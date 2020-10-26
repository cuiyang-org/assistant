package org.cuiyang.assistant.util;

import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

import static org.cuiyang.assistant.AssistantApplication.primaryStage;
import static org.cuiyang.assistant.constant.ConfigConstant.LAST_DIRECTORY;
import static org.cuiyang.assistant.constant.SystemConstant.CHOOSER_OPEN_FILE_TITLE;
import static org.cuiyang.assistant.constant.SystemConstant.CHOOSER_SAVE_FILE_TITLE;

/**
 * 文件工具类
 *
 * @author cuiyang
 */
public class FileUtils {

    /**
     * 选择文件
     * @param title 标题
     * @return 选择的文件
     */
    public static File fileChooser(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        if (StringUtils.isNotEmpty(ConfigUtils.get(LAST_DIRECTORY))) {
            chooser.setInitialDirectory(new File(ConfigUtils.get(LAST_DIRECTORY)));
        }
        File file = chooser.showSaveDialog(primaryStage());
        if (file != null) {
            ConfigUtils.setAndSave(LAST_DIRECTORY, file.getParent());
        }
        return file;
    }


    /**
     * 选择要打开的文件
     * @return 选择的文件
     */
    public static File chooserOpenFile() {
        return fileChooser(CHOOSER_OPEN_FILE_TITLE);
    }


    /**
     * 选择要保存的文件
     * @return 选择的文件
     */
    public static File chooserSaveFile() {
        return fileChooser(CHOOSER_SAVE_FILE_TITLE);
    }
}
