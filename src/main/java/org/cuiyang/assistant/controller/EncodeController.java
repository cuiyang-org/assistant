package org.cuiyang.assistant.controller;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.cuiyang.assistant.util.UnicodeUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * 编码控制器
 *
 * @author cy48576
 */
public class EncodeController {

    /** 编码输入 */
    public TextArea encodeInput;
    /** 编码输出 */
    public TextArea encodeOutput;

    /**
     * 编码转换
     */
    public void encodeExchange() {
        String temp = encodeInput.getText();
        encodeInput.setText(encodeOutput.getText());
        encodeOutput.setText(temp);
    }

    /**
     * 编码或解码
     * @param mouseEvent MouseEvent
     */
    public void encodeOrDecode(MouseEvent mouseEvent) {
        try {
            Button source = (Button) mouseEvent.getSource();
            switch (source.getText()) {
                case "URLEncode" :
                    encodeOutput.setText(URLEncoder.encode(encodeInput.getText(), "UTF-8"));
                    break;
                case "URLDecode" :
                    encodeOutput.setText(URLDecoder.decode(encodeInput.getText(), "UTF-8"));
                    break;
                case "Base64Encode" :
                    encodeOutput.setText(new String(Base64.getEncoder().encode(encodeInput.getText().getBytes())));
                    break;
                case "Base64Decode" :
                    encodeOutput.setText(new String(Base64.getDecoder().decode(encodeInput.getText().getBytes())));
                    break;
                case "UnicodeEncode" :
                    encodeOutput.setText(UnicodeUtils.stringToUnicode(encodeInput.getText()));
                    break;
                case "UnicodeDecode" :
                    encodeOutput.setText(UnicodeUtils.unicodeToString(encodeInput.getText()));
                    break;
                default:
            }
        } catch (Exception e) {
            encodeOutput.setText(ExceptionUtils.getStackTrace(e));
        }
    }

}
