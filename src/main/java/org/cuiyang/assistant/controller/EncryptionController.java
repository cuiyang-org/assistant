package org.cuiyang.assistant.controller;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * 加解密控制器
 *
 * @author cy48576
 */
public class EncryptionController {

    /** 加密输入 */
    public TextArea encryptionInput;
    /** 加密输出 */
    public TextArea encryptionOutput;


    /**
     * 加解密互换
     */
    public void encryptionExchange() {
        String temp = encryptionInput.getText();
        encryptionInput.setText(encryptionOutput.getText());
        encryptionOutput.setText(temp);
    }

    /**
     * 摘要
     * @param mouseEvent MouseEvent
     */
    public void digest(MouseEvent mouseEvent) {
        try {
            Button source = (Button) mouseEvent.getSource();
            switch (source.getText()) {
                case "MD2" :
                    encryptionOutput.setText(DigestUtils.md2Hex(encryptionInput.getText()));
                    break;
                case "MD5" :
                    encryptionOutput.setText(DigestUtils.md5Hex(encryptionInput.getText()));
                    break;
                case "SHA-1" :
                    encryptionOutput.setText(DigestUtils.sha1Hex(encryptionInput.getText()));
                    break;
                case "SHA-256" :
                    encryptionOutput.setText(DigestUtils.sha256Hex(encryptionInput.getText()));
                    break;
                case "SHA-384" :
                    encryptionOutput.setText(DigestUtils.sha384Hex(encryptionInput.getText()));
                    break;
                case "SHA-512" :
                    encryptionOutput.setText(DigestUtils.sha512Hex(encryptionInput.getText()));
                    break;
                default:
            }
        } catch (Exception e) {
            encryptionOutput.setText(ExceptionUtils.getStackTrace(e));
        }
    }

}
