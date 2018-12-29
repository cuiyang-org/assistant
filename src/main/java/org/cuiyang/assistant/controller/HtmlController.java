package org.cuiyang.assistant.controller;

import javafx.scene.control.TextArea;
import org.jsoup.Jsoup;

/**
 * Html 控制器
 *
 * @author cy48576
 */
public class HtmlController {

    /** html文本框 */
    public TextArea htmlTextArea;

    /**
     * html 格式化
     */
    public void htmlFormat() {
        try {
            this.htmlTextArea.setText(Jsoup.parse(this.htmlTextArea.getText()).toString());
        } catch (Exception ignore) {
        }
    }

}
