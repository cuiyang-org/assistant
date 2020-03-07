package org.cuiyang.assistant.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.control.XmlEditor;
import org.cuiyang.assistant.util.BrowseUtils;
import org.cuiyang.assistant.util.XmlUtils;

/**
 * Xml 控制器
 *
 * @author cy48576
 */
public class XmlController implements Initializable {

    /** xml文本框 */
    public XmlEditor xmlTextArea;
    /** xpath */
    public TextField xpathTextField;
    /** xpath */
    public XmlEditor xpathTextArea;

    /**
     * xml 格式化
     */
    public void xmlFormat() {
        try {
            this.xmlTextArea.setText(XmlUtils.format(this.xmlTextArea.getText()));
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
                this.xpathTextArea.setText(XmlUtils.xpath(this.xmlTextArea.getText(), this.xpathTextField.getText()));
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
    }
}
