package org.cuiyang.assistant.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.control.texteditor.TextEditor;
import org.cuiyang.assistant.util.BrowseUtils;
import org.cuiyang.assistant.util.XmlUtils;

/**
 * Html 控制器
 *
 * @author cy48576
 */
public class XmlController implements Initializable {

    /** xml文本框 */
    public TextEditor xmlTextArea;
    /** xpath */
    public TextField xpathTextField;
    /** xpath */
    public TextArea xpathTextArea;

    /**
     * xml 格式化
     */
    public void xmlFormat() {
        try {
            this.xmlTextArea.textArea.setText(XmlUtils.format(this.xmlTextArea.textArea.getText()));
        } catch (Exception ignore) {
        }
    }

    /**
     * xpath
     */
    public void xpath() {
        try {
            if (StringUtils.isEmpty(this.xpathTextField.getText())) {
                this.xpathTextArea.setText("");
            } else {
                this.xpathTextArea.setText(XmlUtils.xpath(this.xmlTextArea.textArea.getText(), this.xpathTextField.getText()));
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * 打开xpath选择器手册
     */
    public void openCssManual() {
        BrowseUtils.open("http://www.w3school.com.cn/xpath/index.asp");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.xmlTextArea.textArea.setPromptText("请输入xml");
    }
}
