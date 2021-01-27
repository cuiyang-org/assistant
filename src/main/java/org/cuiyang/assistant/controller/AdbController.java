package org.cuiyang.assistant.controller;

import com.android.ddmlib.MultiLineReceiver;
import com.github.cosysoft.device.android.AndroidDevice;
import com.github.cosysoft.device.android.impl.AndroidDeviceStore;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import org.cuiyang.assistant.util.AlertUtils;
import org.cuiyang.assistant.util.ThreadUtils;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AdbController
 *
 * @author cy48576
 */
public class AdbController extends BaseController implements Initializable {

    public static final String REMOTE_CONNECT_CMD = "setprop service.adb.tcp.port 5555";
    public static final String DEVELOPMENT_SETTINGS_CMD = "am start com.android.settings/com.android.settings.DevelopmentSettings";
    public static final String PROXY_SELECTOR_CMD = "am start com.android.settings/com.android.settings.ProxySelector";
    public static final String IF_CONFIG_CMD = "ifconfig";
    public static final String FRIDA_SERVER = "su -c '/data/local/tmp/frida-server'";
    public static final String ANDROID_SERVER = "su -c '/data/local/tmp/cy -p6789'";

    private static final Pattern IP_PATTERN = Pattern.compile("addr:(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})");

    public ComboBox<AndroidDevice> deviceComboBox;
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
        deviceComboBox.setConverter(new StringConverter<AndroidDevice>() {
            @Override
            public String toString(AndroidDevice object) {
                return object.getName();
            }

            @Override
            public AndroidDevice fromString(String string) {
                return null;
            }
        });
        TreeSet<AndroidDevice> devices = AndroidDeviceStore.getInstance().getDevices();
        devices.forEach(item -> deviceComboBox.getItems().add(item));
        if (!deviceComboBox.getItems().isEmpty()) {
            deviceComboBox.setValue(deviceComboBox.getItems().get(0));
        }
    }

    /**
     * 当前Activity
     */
    public void currentActivity() {
        AndroidDevice device = currentDevice();
        log(device.currentActivity());
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
     * 代理设置
     */
    public void proxySettings() {
        cmd(PROXY_SELECTOR_CMD);
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
    public void androidServer() {
        if (androidServerRunning.get()) {
            androidServerRunning.set(false);
            androidServerBtn.setText("启动AndroidServer");
        } else {
            androidServerRunning.set(true);
            androidServerBtn.setText("暂停AndroidServer");
            cmd(ANDROID_SERVER, "AndroidServer", androidServerRunning);
        }
    }

    private AndroidDevice currentDevice() {
        AndroidDevice device = deviceComboBox.getValue();
        if (device == null) {
            AlertUtils.info("请先连接设备");
            throw new IllegalStateException("请先连接设备");
        }
        return device;
    }

    private String cmd(String cmd) {
        AndroidDevice device = currentDevice();
        return device.runAdbCommand("shell " + cmd);
    }

    private void cmd(String cmd, String name, AtomicBoolean running) {
        log("启动" + name + "...");
        ThreadUtils.run(() -> {
            try {
                currentDevice().getDevice().executeShellCommand(cmd, new MultiLineReceiver() {
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
