package org.cuiyang.assistant.controller;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.cuiyang.assistant.util.BrowseUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则控制器
 *
 * @author cy48576
 */
public class RegexController {
    /** 输入 */
    public TextArea inputTextArea;
    /** 输出 */
    public TextArea outputTextArea;
    /** 正则表达式 */
    public TextField regexTextField;
    /** 要替换的文本 */
    public TextField replaceTextField;
    /** 是否忽略大小写 */
    public CheckBox ignoreCaseCheckBox;

    /**
     * 匹配
     */
    public void match() {
        try {
            Matcher matcher = matcher();
            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                sb.append(matcher.group()).append(System.lineSeparator());
            }
            outputTextArea.setText(sb.toString());
        } catch (Exception ignore) {
        }
    }

    /**
     * 替换
     */
    public void replace() {
        try {
            outputTextArea.setText(matcher().replaceAll(replaceTextField.getText()));
        } catch (Exception ignore) {
        }
    }

    /**
     * 打开正则手册
     */
    public void openRegexManual() {
        BrowseUtils.open("http://www.runoob.com/regexp/regexp-metachar.html");
    }

    private Matcher matcher() {
        Pattern pattern;
        if (ignoreCaseCheckBox.isSelected()) {
            pattern = Pattern.compile(regexTextField.getText(), Pattern.CASE_INSENSITIVE);
        } else {
            pattern = Pattern.compile(regexTextField.getText());
        }
        return pattern.matcher(inputTextArea.getText());
    }
}
