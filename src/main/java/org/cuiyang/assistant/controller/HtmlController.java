package org.cuiyang.assistant.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.control.CodeEditor;
import org.cuiyang.assistant.util.BrowseUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

/**
 * Html 控制器
 *
 * @author cy48576
 */
@Slf4j
public class HtmlController implements Initializable {

    /** html文本框 */
    public CodeEditor htmlTextArea;
    /** css选择器 */
    public TextField cssQueryTextField;
    /** css选择器 */
    public CodeEditor cssQueryTextArea;

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
        Document document = Jsoup.parse(this.htmlTextArea.getText());
        FileUtils.writeStringToFile(file, this.htmlTextArea.getText(), charset(document));
        BrowseUtils.open(file.getPath());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.htmlTextArea.setType(CodeEditor.Type.XML);
        this.cssQueryTextArea.setType(CodeEditor.Type.XML);
    }

    /**
     * 获取编码
     */
    private Charset charset(Document document) {
        try {
            String charset = document.select("meta[charset]").attr("charset");
            if (StringUtils.isNotEmpty(charset)) {
                return Charset.forName(charset.trim());
            }
            String content = document.select("meta[http-equiv=Content-Type]").attr("content");
            String[] split = content.split("=");
            if (split.length == 2) {
                return Charset.forName(split[1].trim());
            }
        } catch (Exception e) {
            log.error("获取编码失败", e);
        }
        return Charset.defaultCharset();
    }

}
