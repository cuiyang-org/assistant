package org.cuiyang.assistant.controller;

import com.alibaba.fastjson.JSON;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.control.InputDialog;
import org.cuiyang.assistant.model.Cmd;
import org.cuiyang.assistant.util.ConfigUtils;
import org.cuiyang.assistant.util.FileUtils;
import org.cuiyang.assistant.util.ThreadUtils;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.cuiyang.assistant.AssistantApplication.primaryStage;
import static org.cuiyang.assistant.constant.ConfigConstant.CMD;
import static org.cuiyang.assistant.constant.ConfigConstant.LAST_DEX_DIRECTORY;
import static org.cuiyang.assistant.util.ThreadUtils.run;

/**
 * ToolController
 *
 * @author cy48576
 */
public class ToolController extends BaseController implements Initializable {
    public TextField timestampTextField;
    public TextField datetimeTextField;
    public TextField dateFormatterTextField;
    public ComboBox<String> timezoneComboBox;
    public FlowPane cmdParent;

    public void timestamp2datetime() {
        if (timestampTextField.getText().isEmpty()) {
            return;
        }
        try {
            datetimeTextField.setText(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(timestampTextField.getText())), ZoneId.of(timezoneComboBox.getValue())).format(DateTimeFormatter.ofPattern(dateFormatterTextField.getText())));
        } catch (Exception ignore) {
            datetimeTextField.setText("");
        }
    }

    public void datetime2timestamp() {
        if (datetimeTextField.getText().isEmpty()) {
            return;
        }
        try {
            timestampTextField.setText(String.valueOf(Date.from(LocalDateTime.parse(datetimeTextField.getText(), DateTimeFormatter.ofPattern(dateFormatterTextField.getText())).atZone(ZoneId.of(timezoneComboBox.getValue())).toInstant()).getTime()));
        } catch (Exception ignore) {
            timestampTextField.setText("");
        }
    }

    /**
     * uuid
     */
    public void uuid() {
        log(UUID.randomUUID().toString());
    }

    /**
     * uuid2
     */
    public void uuid2() {
        log(UUID.randomUUID().toString().replaceAll("-", ""));
    }

    /**
     * uuid
     */
    public void uuidUpper() {
        log(UUID.randomUUID().toString().toUpperCase());
    }

    /**
     * uuid2
     */
    public void uuid2Upper() {
        log(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
    }

    /**
     * 时间戳
     */
    public void timestamp() {
        log(String.valueOf(System.currentTimeMillis()));
    }

    /**
     * 日期
     */
    public void datetime() {
        log(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
    }

    public void merge() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择要合并的DEX");
        if (StringUtils.isNotEmpty(ConfigUtils.get(LAST_DEX_DIRECTORY))) {
            chooser.setInitialDirectory(new File(ConfigUtils.get(LAST_DEX_DIRECTORY)));
        }
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("android dex", "*.dex"));
        List<File> files = chooser.showOpenMultipleDialog(primaryStage());
        if (CollectionUtils.isEmpty(files)) {
            log("没有选择要合并的dex");
            return;
        }
        ConfigUtils.setAndSave(LAST_DEX_DIRECTORY, files.get(0).getParent());

        run(() -> {
            try {
                String outFile = files.get(0).getParent() + File.separator + "out.zip";
                try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFile))) {
                    for (int i = 0; i < files.size(); i++) {
                        String classes = String.format("classes%s.dex", i > 0 ? (i + 1) : "");
                        int finalI = i;
                        Platform.runLater(() -> log(files.get(finalI).getName() + " -> " + classes));
                        out.putNextEntry(new ZipEntry(classes));
                        try (FileInputStream in = new FileInputStream(files.get(i))) {
                            IOUtils.copy(in, out);
                        }
                    }
                }
                Platform.runLater(() -> log("合并dex成功，输出目录：" + outFile));
            } catch (Exception e) {
                Platform.runLater(() -> log("合并dex失败", e));
            }
        });
    }

    @Override
    public boolean isCloseable() {
        return true;
    }

    public void addCmd() {
        File file = FileUtils.chooserOpenFile();
        if (file == null || file.isDirectory()) {
            return;
        }
        Cmd cmd = Cmd.builder().name(file.getName()).path(file.getAbsolutePath()).build();
        cmdParent.getChildren().add(0, getCmdNode(cmd));
        saveCmd();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            String cmd = ConfigUtils.get(CMD, "");
            List<Cmd> cmdList = JSON.parseArray(cmd, Cmd.class);
            cmdList.forEach(c -> cmdParent.getChildren().add(getCmdNode(c)));
        } catch (Exception ignore) {
        }
    }

    private Node getCmdNode(Cmd cmd) {
        Button button = new Button(cmd.getName());
        button.setPrefWidth(100);
        button.setUserData(cmd);
        button.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            ThreadUtils.run(() -> {
                try {
                    Process process = Runtime.getRuntime().exec(cmd.getPath());

                    InputStream is = process.getInputStream();
                    InputStream es = process.getErrorStream();
                    String line;
                    BufferedReader br;
                    br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    while ((line = br.readLine()) != null) {
                        String finalLine = line;
                        Platform.runLater(() -> log(finalLine));
                    }
                    br = new BufferedReader(new InputStreamReader(es, StandardCharsets.UTF_8));
                    while ((line = br.readLine()) != null) {
                        String finalLine1 = line;
                        Platform.runLater(() -> log(finalLine1));
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> log(e.getMessage()));
                }
            });
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem rename = new MenuItem("重命名");
        rename.setOnAction(event -> {
            InputDialog dialog = new InputDialog(cmd.getName());
            dialog.setTitle("重命名");
            Optional<String> optional = dialog.showAndWait();
            optional.ifPresent(name -> {
                cmd.setName(name);
                button.setText(name);
                saveCmd();
            });
        });
        contextMenu.getItems().add(rename);

        MenuItem delete = new MenuItem("删除");
        delete.setOnAction(event -> {
            cmdParent.getChildren().remove(button);
            saveCmd();
        });
        contextMenu.getItems().add(delete);

        MenuItem top = new MenuItem("置顶");
        top.setOnAction(event -> {
            cmdParent.getChildren().remove(button);
            cmdParent.getChildren().add(0, button);
            saveCmd();
        });
        contextMenu.getItems().add(top);

        MenuItem dir = new MenuItem("打开文件位置");
        dir.setOnAction(event -> {
            try {
                Desktop.getDesktop().open(new File(cmd.getPath()).getParentFile());
            } catch (IOException e) {
                Platform.runLater(() -> log(e.getMessage()));
            }
        });
        contextMenu.getItems().add(dir);

        button.setContextMenu(contextMenu);
        return button;
    }

    private void saveCmd() {
        List<Object> collect = cmdParent.getChildren().stream().map(Node::getUserData).collect(Collectors.toList());
        ConfigUtils.setAndSave(CMD, JSON.toJSONString(collect));
    }
}
