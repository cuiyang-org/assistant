package org.cuiyang.assistant.file;

import org.cuiyang.assistant.constant.FileTypeEnum;
import org.cuiyang.assistant.util.FileUtils;

import java.io.File;

public interface FileOperation {

    FileTypeEnum fileType();

    /**
     * 打开文件
     */
    default void openFile() {
        File file = FileUtils.chooserOpenFile();
        if (file != null) {
            openFile(file);
        }
    }

    /**
     * 另存为
     */
    default void saveAs() {
        File file = FileUtils.chooserSaveFile(fileType());
        if (file != null) {
            saveAs(file);
        }
    }

    /**
     * 打开文件
     */
    void openFile(File file);

    /**
     * 另存为
     */
    void saveAs(File file);
}
