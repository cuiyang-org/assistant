package org.cuiyang.assistant.file;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.cuiyang.assistant.control.searchcodeeditor.SearchCodeEditor;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface EditorFileOperation extends FileOperation {

    @SneakyThrows
    default SearchCodeEditor editor() {
        Field editor = this.getClass().getDeclaredField("editor");
        return (SearchCodeEditor) editor.get(this);
    }

    @SneakyThrows
    default void setTitle(File file) {
        Method setTitle = this.getClass().getDeclaredMethod("setTitle", File.class);
        setTitle.invoke(this, file);
    }

    @Override
    default void openFile(File file) {
        editor().openFile(file);
        setTitle(file);
    }

    @SneakyThrows
    @Override
    default void saveAs(File file) {
        editor().setFile(file);
        setTitle(file);
        FileUtils.writeStringToFile(file, editor().getText(), "utf-8");
    }

    @Override
    default void save() {
        if (file() == null) {
            saveAs();
        } else {
            editor().save();
        }
    }

    @Override
    default File file() {
        return editor().getFile();
    }
}
