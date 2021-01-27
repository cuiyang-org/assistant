package org.cuiyang.assistant.control;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import org.cuiyang.assistant.control.searchcodeeditor.SearchCodeEditor;

public class LogOutPane extends SplitPane {

    private SearchCodeEditor out = new SearchCodeEditor();

    public LogOutPane() {
        this.setOrientation(Orientation.VERTICAL);
        out.setPadding(new Insets(5, 0, 0, 0));
    }

    /**
     * 显示日志输出
     */
    public void showLogOut(boolean show) {
        if (show && this.getItems().size() == 1) {
            this.getItems().add(out);
            this.setDividerPositions(0.8);
            this.getStyleClass().remove("no-divider");
        } else if (!show && this.getItems().size() == 2) {
            this.getItems().remove(out);
            this.setDividerPositions(1);
            this.getStyleClass().add("no-divider");
        }
    }

    /**
     * 判断是否显示日志输出
     */
    public boolean isShowLogOut() {
        return this.getItems().size() == 2;
    }

    public void appendText(String text) {
        out.appendText(text);
    }

    public void clear() {
        out.clear();
    }
}
