package org.cuiyang.assistant.controller;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.MultiLineReceiver;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.adb.AdbClient;
import org.cuiyang.assistant.util.AlertUtils;
import org.cuiyang.assistant.util.ConfigUtils;
import org.cuiyang.assistant.util.FileUtils;
import org.cuiyang.assistant.util.ThreadUtils;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.cuiyang.assistant.constant.ConfigConstant.ADB_PATH;

/**
 * AdbController
 *
 * @author cy48576
 */
public class AdbController extends BaseController implements Initializable {

    public static final String CURRENT_ACTIVITY = "dumpsys activity top";
    public static final String REMOTE_CONNECT_CMD = "setprop service.adb.tcp.port 5555";
    public static final String DEVELOPMENT_SETTINGS_CMD = "am start com.android.settings/com.android.settings.DevelopmentSettings";
    public static final String IF_CONFIG_CMD = "ifconfig";
    public static final String FRIDA_SERVER = "su -c '/data/local/tmp/frida-server'";
    public static final String ANDROID_SERVER = "su -c '/data/local/tmp/cy -p6789'";

    private static final Pattern IP_PATTERN = Pattern.compile("addr:(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})");

    public ComboBox<IDevice> deviceComboBox;
    public Button androidServerBtn;
    public Button fridaServerBtn;
    private AtomicBoolean androidServerRunning = new AtomicBoolean(false);
    private AtomicBoolean fridaServerRunning = new AtomicBoolean(false);

    @Override
    public boolean isCloseable() {
        return true;
    }

    @Override
    public boolean close() {
        if (androidServerRunning.get()) {
            AlertUtils.info("请先关闭AndroidServer");
            return false;
        }
        if (fridaServerRunning.get()) {
            AlertUtils.info("请先关闭FridaServer");
            return false;
        }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deviceComboBox.setConverter(new StringConverter<IDevice>() {
            @Override
            public String toString(IDevice object) {
                return object.getName();
            }

            @Override
            public IDevice fromString(String string) {
                return null;
            }
        });
        flush();
    }

    /**
     * 刷新设备
     */
    public void flush() {
        String adbPath = ConfigUtils.get(ADB_PATH);
        if (StringUtils.isEmpty(adbPath)) {
            selectAdb();
            return;
        }
        deviceComboBox.getItems().removeIf(item -> true);
        ThreadUtils.run(() -> {
            List<IDevice> devices = AdbClient.getInstance().getDevices();
            devices.forEach(item -> deviceComboBox.getItems().add(item));
            if (!deviceComboBox.getItems().isEmpty()) {
                Platform.runLater(() -> deviceComboBox.setValue(deviceComboBox.getItems().get(0)));
            }
        });
    }

    /**
     * 选择adb
     */
    public void selectAdb() {
        File file = FileUtils.chooserOpenFile();
        if (file != null) {
            String adbPath = file.getAbsolutePath();
            ConfigUtils.setAndSave(ADB_PATH, adbPath);
            AdbClient.setAdbPath(adbPath);
        }
    }

    /**
     * 当前Activity
     */
    @SneakyThrows
    public void currentActivity() {
        String out = cmd(CURRENT_ACTIVITY);
        if (out.contains("ACTIVITY")) {
            List<String> lines = IOUtils.readLines(new StringReader(out));
            for (String line : lines) {
                if (line.contains("ACTIVITY")) {
                    String[] tokens = StringUtils.split(line, " ");
                    log(tokens[1]);
                }
            }
        }
    }

    /**
     * 远程连接
     */
    public void remoteConnect() {
        cmd(REMOTE_CONNECT_CMD);
        developmentSettings();
        AlertUtils.info("远程连接已开启，请关闭再重新打开\"USB调试\"选项即可");
    }

    /**
     * 开发者选项
     */
    public void developmentSettings() {
        cmd(DEVELOPMENT_SETTINGS_CMD);
    }

    /**
     * ip地址
     */
    public void ipAddr() {
        String ret = cmd(IF_CONFIG_CMD);
        Matcher matcher = IP_PATTERN.matcher(ret);
        while (matcher.find()) {
            String ip = matcher.group(1);
            if ("127.0.0.1".equals(ip)) {
                continue;
            }
            log(ip);
        }
    }

    /**
     * 启动Frida Server
     */
    public void fridaServer() {
        if (fridaServerRunning.get()) {
            fridaServerRunning.set(false);
            fridaServerBtn.setText("启动FridaServer");
        } else {
            fridaServerRunning.set(true);
            fridaServerBtn.setText("暂停FridaServer");
            cmd(FRIDA_SERVER, "FridaServer", fridaServerRunning);
        }
    }

    /**
     * 启动Android Server
     */
    @SneakyThrows
    public void androidServer() {
        if (androidServerRunning.get()) {
            androidServerRunning.set(false);
            androidServerBtn.setText("启动AndroidServer");
        } else {
            androidServerRunning.set(true);
            androidServerBtn.setText("暂停AndroidServer");
            currentDevice().createForward(6789, 6789);
            cmd(ANDROID_SERVER, "AndroidServer", androidServerRunning);
        }
    }

    private IDevice currentDevice() {
        IDevice device = deviceComboBox.getValue();
        if (device == null) {
            AlertUtils.info("请先连接设备");
            throw new IllegalStateException("请先连接设备");
        }
        return device;
    }

    @SneakyThrows
    private String cmd(String cmd) {
        IDevice device = currentDevice();
        StringBuilder sb = new StringBuilder();
        device.executeShellCommand(cmd, new MultiLineReceiver() {
            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public void processNewLines(String[] strings) {
                for (String line : strings) {
                    sb.append(line).append("\n");
                }
            }
        });
        return sb.toString();
    }

    private void cmd(String cmd, String name, AtomicBoolean running) {
        log("启动" + name + "...");
        ThreadUtils.run(() -> {
            try {
                currentDevice().executeShellCommand(cmd, new MultiLineReceiver() {
                    @Override
                    public boolean isCancelled() {
                        if (!running.get()) {
                            Platform.runLater(() -> log("暂停" + name));
                        }
                        return !running.get();
                    }

                    @Override
                    public void processNewLines(String[] strings) {
                        Arrays.asList(strings).forEach(item -> Platform.runLater(() -> log(item)));
                    }
                }, Integer.MAX_VALUE, TimeUnit.DAYS);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }
}
