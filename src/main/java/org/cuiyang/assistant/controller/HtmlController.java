package org.cuiyang.assistant.controller;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.util.BrowseUtils;
import org.jsoup.Jsoup;

/**
 * Html 控制器
 *
 * @author cy48576
 */
public class HtmlController {

    /** html文本框 */
    public TextArea htmlTextArea;
    /** css选择器 */
    public TextField cssQueryTextField;
    /** css选择器 */
    public TextArea cssQueryTextArea;

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
}
