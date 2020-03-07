package org.cuiyang.assistant.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.control.XmlEditor;
import org.cuiyang.assistant.util.BrowseUtils;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Html 控制器
 *
 * @author cy48576
 */
public class HtmlController implements Initializable {

    /** html文本框 */
    public XmlEditor htmlTextArea;
    /** css选择器 */
    public TextField cssQueryTextField;
    /** css选择器 */
    public XmlEditor cssQueryTextArea;

    /**
     * html 格式化
     */
    public void htmlFormat() {
        try {
            this.htmlTextArea.setText(Jsoup.parse(this.htmlTextArea.getText()).toString());
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
                this.cssQueryTextArea.setText(Jsoup.parse(this.htmlTextArea.getText()).select(this.cssQueryTextField.getText()).toString());
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

    /**
     * 打开浏览器
     */
    public void openBrowser() throws IOException {
        File file = new File("temp.html");
        FileUtils.writeStringToFile(file, this.htmlTextArea.getText(), "UTF-8");
        BrowseUtils.open(file.getPath());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
