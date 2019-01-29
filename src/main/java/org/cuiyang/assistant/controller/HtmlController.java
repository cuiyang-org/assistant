package org.cuiyang.assistant.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.control.texteditor.TextEditor;
import org.cuiyang.assistant.util.BrowseUtils;
import org.jsoup.Jsoup;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Html 控制器
 *
 * @author cy48576
 */
public class HtmlController implements Initializable {

    /** html文本框 */
    public TextEditor htmlTextArea;
    /** css选择器 */
    public TextField cssQueryTextField;
    /** css选择器 */
    public TextArea cssQueryTextArea;

    /**
     * html 格式化
     */
    public void htmlFormat() {
        try {
            this.htmlTextArea.textArea.setText(Jsoup.parse(this.htmlTextArea.textArea.getText()).toString());
        } catch (Exception ignore) {
        }
    }

    /**
     * css选择器
     */
    public void cssQuery() {
        try {
            if (StringUtils.isEmpty(this.cssQueryTextField.getText())) {
                this.cssQueryTextArea.setText("");
            } else {
                this.cssQueryTextArea.setText(Jsoup.parse(this.htmlTextArea.textArea.getText()).select(this.cssQueryTextField.getText()).toString());
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * 打开css选择器手册
     */
    public void openCssManual() {
        BrowseUtils.open("http://www.w3school.com.cn/cssref/css_selectors.asp");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.htmlTextArea.textArea.setPromptText("请输入html");
    }
}
