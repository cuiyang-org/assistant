package org.cuiyang.assistant.controller;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.util.ConfigUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.cuiyang.assistant.constant.ConfigConstant.LAST_DEX_DIRECTORY;
import static org.cuiyang.assistant.util.ThreadUtils.run;

/**
 * ToolController
 *
 * @author cy48576
 */
public class ToolController extends BaseController {
    public VBox rootPane;
    public TextField timestampTextField;
    public TextField datetimeTextField;
    public TextField dateFormatterTextField;
    public ComboBox<String> timezoneComboBox;

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
        List<File> files = chooser.showOpenMultipleDialog(rootPane.getScene().getWindow());
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
                log("合并dex成功，输出目录：" + outFile);
            } catch (Exception e) {
                log("合并dex失败", e);
            }
        });
    }

    @Override
    public boolean isCloseable() {
        return true;
    }
}
