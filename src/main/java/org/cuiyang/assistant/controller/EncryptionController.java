package org.cuiyang.assistant.controller;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.util.DesUtils;

import java.util.Base64;

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
    /** 算法 */
    public ComboBox algorithmComboBox;
    /** 模式 */
    public ComboBox modeComboBox;
    /** 填充 */
    public ComboBox paddingComboBox;
    /** 输出 */
    public ComboBox outputComboBox;
    /** 密码 */
    public TextField keyTextField;
    /** 偏移量 */
    public TextField ivTextField;
    /** des/aes选项 */
    public HBox desAndAesOptionHBox;

    /**
     * 加解密
     */
    public void encryptionAndDecryption() {
        try {
            switch (algorithmComboBox.getValue().toString()) {
                case "MD2" :
                    desAndAesOptionHBox.setVisible(false);
                    if (StringUtils.isNotEmpty(encryptionInput.getText())) {
                        encryptionOutput.setText(DigestUtils.md2Hex(encryptionInput.getText()));
                    } else {
                        encryptionOutput.setText("");
                    }
                    break;
                case "MD5" :
                    desAndAesOptionHBox.setVisible(false);
                    if (StringUtils.isNotEmpty(encryptionInput.getText())) {
                        encryptionOutput.setText(DigestUtils.md5Hex(encryptionInput.getText()));
                    } else {
                        encryptionOutput.setText("");
                    }
                    break;
                case "SHA-1" :
                    desAndAesOptionHBox.setVisible(false);
                    if (StringUtils.isNotEmpty(encryptionInput.getText())) {
                        encryptionOutput.setText(DigestUtils.sha1Hex(encryptionInput.getText()));
                    } else {
                        encryptionOutput.setText("");
                    }
                    break;
                case "SHA-256" :
                    desAndAesOptionHBox.setVisible(false);
                    if (StringUtils.isNotEmpty(encryptionInput.getText())) {
                        encryptionOutput.setText(DigestUtils.sha256Hex(encryptionInput.getText()));
                    } else {
                        encryptionOutput.setText("");
                    }
                    break;
                case "SHA-384" :
                    desAndAesOptionHBox.setVisible(false);
                    if (StringUtils.isNotEmpty(encryptionInput.getText())) {
                        encryptionOutput.setText(DigestUtils.sha384Hex(encryptionInput.getText()));
                    } else {
                        encryptionOutput.setText("");
                    }
                    break;
                case "SHA-512" :
                    desAndAesOptionHBox.setVisible(false);
                    if (StringUtils.isNotEmpty(encryptionInput.getText())) {
                        encryptionOutput.setText(DigestUtils.sha512Hex(encryptionInput.getText()));
                    } else {
                        encryptionOutput.setText("");
                    }
                    break;
                case "DES" :
                    desAndAesOptionHBox.setVisible(true);
                    if (StringUtils.isNotEmpty(encryptionInput.getText()) && StringUtils.isNotEmpty(keyTextField.getText())) {
                        DesUtils.DEFAULT_TRANSFORMATION = algorithmComboBox.getValue() + "/" + modeComboBox.getValue() + "/" + paddingComboBox.getValue();
                        byte[] iv = null;
                        if (StringUtils.isNotEmpty(ivTextField.getText())) {
                            iv = ivTextField.getText().getBytes();
                        }
                        byte[] encrypt = DesUtils.encrypt(encryptionInput.getText().getBytes(), keyTextField.getText().getBytes(), iv);
                        if ("base64".equals(outputComboBox.getValue())) {
                            encryptionOutput.setText(Base64.getEncoder().encodeToString(encrypt));
                        } else if ("hex".equals(outputComboBox.getValue())) {
                            encryptionOutput.setText(Hex.encodeHexString(encrypt));
                        }
                    } else {
                        encryptionOutput.setText("");
                    }
                    break;
                case "AES" :
                    desAndAesOptionHBox.setVisible(true);
                    encryptionOutput.setText("");
                    break;
                default:
            }
        } catch (Exception e) {
            encryptionOutput.setText(e.getMessage());
        }
    }

}
