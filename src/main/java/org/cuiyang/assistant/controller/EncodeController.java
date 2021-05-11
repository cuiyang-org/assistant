package org.cuiyang.assistant.controller;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
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
public class EncodeController extends BaseController {

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
            Label source = (Label) mouseEvent.getSource();
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
                    encodeOutput.setText(new String(Base64.getDecoder().decode(encodeInput.getText().replaceAll("\\s", "").getBytes())));
                    break;
                case "HexEncode" :
                    encodeOutput.setText(Hex.encodeHexString(encodeInput.getText().getBytes()));
                    break;
                case "HexDecode" :
                    encodeOutput.setText(new String(Hex.decodeHex(encodeInput.getText().replaceAll("\\s", "").toCharArray())));
                    break;
                case "UnicodeEncode" :
                    encodeOutput.setText(UnicodeUtils.stringToUnicode(encodeInput.getText()));
                    break;
                case "UnicodeDecode" :
                    encodeOutput.setText(UnicodeUtils.unicodeToString(encodeInput.getText()));
                case "去空格" :
                    encodeOutput.setText(encodeInput.getText().replace(" ", ""));
                    break;
                case "去空行" :
                    encodeOutput.setText(encodeInput.getText().replace("\n", ""));
                    break;
                case "去空白" :
                    encodeOutput.setText(encodeInput.getText().replaceAll("\\s", ""));
                    break;
                default:
            }
        } catch (Exception e) {
            encodeOutput.setText(ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public boolean isCloseable() {
        return StringUtils.isBlank(encodeInput.getText());
    }
}
