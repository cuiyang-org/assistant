package org.cuiyang.assistant.controller;

import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.util.ConfigUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.cuiyang.assistant.constant.ConfigConstant.LAST_DEX_DIRECTORY;
import static org.cuiyang.assistant.util.ThreadUtils.run;

/**
 * 逆向控制器
 *
 * @author cuiyang
 */
public class ReverseController extends BaseController {

    public VBox rootPane;

    public void merge() throws IOException {
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
                        log(files.get(i).getName() + " -> " + classes);
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
        return false;
    }
}
