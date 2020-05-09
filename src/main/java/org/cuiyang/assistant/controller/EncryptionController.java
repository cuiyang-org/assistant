package org.cuiyang.assistant.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.util.CipherUtils;
import org.cuiyang.assistant.util.ConfigUtils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static org.cuiyang.assistant.constant.ConfigConstant.LAST_DEX_DIRECTORY;
import static org.cuiyang.assistant.util.CommonUtils.parentVisible;
import static org.cuiyang.assistant.util.CommonUtils.visible;

/**
 * 加解密控制器
 *
 * @author cy48576
 */
public class EncryptionController {

    /** 输入 */
    public TextArea input;
    /** 输出 */
    public TextArea output;
    /** 算法 */
    public ComboBox<String> algorithmComboBox;
    /** 模式 */
    public ComboBox<String> modeComboBox;
    /** 填充 */
    public ComboBox<String> paddingComboBox;
    /** 密码 */
    public TextField keyTextField;
    /** 偏移量 */
    public TextField ivTextField;
    /** 秘钥类型 */
    public ComboBox<String> keyTypeComboBox;
    /** des/aes选项 */
    public HBox desAndAesOptionHBox;
    /** 输入类型 */
    public ComboBox<String> inputType;
    /** 输入字符 */
    public ComboBox<String> inputCharset;
    /** 选择文件 */
    public Button inputFileBtn;
    /** 输出类型 */
    public ComboBox<String> outputType;
    /** 输出字符 */
    public ComboBox<String> outputCharset;
    /** 保存文件 */
    public Button outputFileBtn;

    /**
     * 算法切换
     */
    public void algorithmAction() {
        switch (algorithmComboBox.getValue()) {
            case "MD2" :
            case "MD5" :
            case "SHA-1" :
            case "SHA-256" :
            case "SHA-384" :
            case "SHA-512" :
            case "To Hex" :
            case "From Hex" :
            case "To Base64" :
            case "From Base64" :
                if (StringUtils.isEmpty(input.getText())) {
                    inputType.setValue("String");
                }
                outputType.setValue("String");
                visible(desAndAesOptionHBox, false);
                break;
            case "To DES" :
            case "To AES" :
                if (StringUtils.isEmpty(input.getText())) {
                    inputType.setValue("String");
                }
                outputType.setValue("Base64");
                visible(desAndAesOptionHBox, true);
                break;
            case "From DES" :
            case "From AES" :
                if (StringUtils.isEmpty(input.getText())) {
                    inputType.setValue("Base64");
                }
                outputType.setValue("String");
                visible(desAndAesOptionHBox, true);
                break;
            default:
        }
        run();
    }

    /**
     * 运行
     */
    public void run() {
        if (StringUtils.isEmpty(input.getText())) {
            output.setText("");
            return;
        }
        try {
            switch (algorithmComboBox.getValue()) {
                case "MD2" :
                    output.setText(getOutput(DigestUtils.md2(getInput())));
                    break;
                case "MD5" :
                    output.setText(getOutput(DigestUtils.md5(getInput())));
                    break;
                case "SHA-1" :
                    output.setText(getOutput(DigestUtils.sha1(getInput())));
                    break;
                case "SHA-256" :
                    output.setText(getOutput(DigestUtils.sha256(getInput())));
                    break;
                case "SHA-384" :
                    output.setText(getOutput(DigestUtils.sha384(getInput())));
                    break;
                case "SHA-512" :
                    output.setText(getOutput(DigestUtils.sha512(getInput())));
                    break;
                case "To Hex" :
                    output.setText(Hex.encodeHexString(getInput()).toUpperCase());
                    break;
                case "From Hex" :
                    output.setText(getOutput(Hex.decodeHex(input.getText().toCharArray())));
                    break;
                case "To Base64" :
                    output.setText(getOutput(Base64.getEncoder().encode(getInput())));
                    break;
                case "From Base64" :
                    output.setText(getOutput(Base64.getDecoder().decode(getInput())));
                    break;
                case "To DES" :
                    CipherUtils.DEFAULT_ALGORITHM = "DES";
                    CipherUtils.DEFAULT_TRANSFORMATION = "DES/" + modeComboBox.getValue() + "/" + paddingComboBox.getValue();
                    output.setText(getOutput(CipherUtils.encrypt(getInput(), getKey(), getIv())));
                    break;
                case "From DES" :
                    CipherUtils.DEFAULT_ALGORITHM = "DES";
                    CipherUtils.DEFAULT_TRANSFORMATION = "DES/" + modeComboBox.getValue() + "/" + paddingComboBox.getValue();
                    output.setText(getOutput(CipherUtils.decrypt(getInput(), getKey(), getIv())));
                    break;
                case "To AES" :
                    CipherUtils.DEFAULT_ALGORITHM = "AES";
                    CipherUtils.DEFAULT_TRANSFORMATION = "AES/" + modeComboBox.getValue() + "/" + paddingComboBox.getValue();
                    output.setText(getOutput(CipherUtils.encrypt(getInput(), getKey(), getIv())));
                    break;
                case "From AES" :
                    CipherUtils.DEFAULT_ALGORITHM = "AES";
                    CipherUtils.DEFAULT_TRANSFORMATION = "AES/" + modeComboBox.getValue() + "/" + paddingComboBox.getValue();
                    output.setText(getOutput(CipherUtils.decrypt(getInput(), getKey(), getIv())));
                    break;
                default:
            }
        } catch (Exception e) {
            output.setText(e.getMessage());
        }
    }

    /**
     * 输入输出类型切换
     */
    public void inputOutputTypeAction(ActionEvent actionEvent) {
        ComboBox<String> type = inputType;
        ComboBox<String> charset = inputCharset;
        Button file = inputFileBtn;
        TextArea textArea = input;
        //noinspection unchecked
        if (((ComboBox<String>) actionEvent.getSource()).getId().equals("outputType")) {
            type = outputType;
            charset = outputCharset;
            file = outputFileBtn;
            textArea = output;
        }

        textArea.setEditable(true);
        switch (type.getValue()) {
            case "String":
                parentVisible(charset, true);
                parentVisible(file, false);
                break;
            case "Base64":
            case "Hex":
                parentVisible(charset, false);
                parentVisible(file, false);
                break;
            case "Binary":
                parentVisible(charset, false);
                parentVisible(file, true);
                textArea.setEditable(false);
                textArea.setText(null);
                break;
            default:
        }
        run();
    }

    /**
     * 选择输入文件
     */
    public void chooseInputFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择要输入文件");
        if (StringUtils.isNotEmpty(ConfigUtils.get(LAST_DEX_DIRECTORY))) {
            chooser.setInitialDirectory(new File(ConfigUtils.get(LAST_DEX_DIRECTORY)));
        }
        List<File> files = chooser.showOpenMultipleDialog(input.getScene().getWindow());
        if (CollectionUtils.isNotEmpty(files)) {
            ConfigUtils.setAndSave(LAST_DEX_DIRECTORY, files.get(0).getParent());
            input.setText(files.get(0).getAbsolutePath());
        }
    }

    /**
     * 选择输入文件
     */
    public void chooseOutputFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择要输出的文件");
        if (StringUtils.isNotEmpty(ConfigUtils.get(LAST_DEX_DIRECTORY))) {
            chooser.setInitialDirectory(new File(ConfigUtils.get(LAST_DEX_DIRECTORY)));
        }
        File file = chooser.showSaveDialog(input.getScene().getWindow());
        if (file != null) {
            ConfigUtils.setAndSave(LAST_DEX_DIRECTORY, file.getParent());
            output.setText(file.getAbsolutePath());
        }
    }

    /**
     * 获取输入
     */
    private byte[] getInput() throws IOException, DecoderException {
        switch (inputType.getValue()) {
            case "String":
                return input.getText().getBytes(inputCharset.getValue());
            case "Base64":
                return Base64.getDecoder().decode(input.getText());
            case "Hex":
                return Hex.decodeHex(input.getText().toCharArray());
            case "Binary":
                if (StringUtils.isEmpty(input.getText())) {
                    throw new IllegalArgumentException("请选择文件");
                } else {
                    return FileUtils.readFileToByteArray(new File(input.getText()));
                }
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * 获取输出
     */
    private String getOutput(byte[] output) throws IOException {
        switch (outputType.getValue()) {
            case "String":
                return new String(output, outputCharset.getValue());
            case "Base64":
                return Base64.getEncoder().encodeToString(output);
            case "Hex":
                return Hex.encodeHexString(output).toUpperCase();
            case "Binary":
                String outputFile = this.output.getText();
                FileUtils.writeByteArrayToFile(new File(outputFile), output);
                return outputFile;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * 获取秘钥
     */
    private byte[] getKey() throws DecoderException {
        return getBytes(keyTypeComboBox.getValue(), keyTextField.getText());
    }

    /**
     * 获取IV
     */
    private byte[] getIv() throws DecoderException {
        return getBytes(keyTypeComboBox.getValue(), ivTextField.getText());
    }

    /**
     * 获取二进制
     */
    private byte[] getBytes(String type, String value) throws DecoderException {
        if (value == null) {
            return null;
        }
        switch (type) {
            case "String":
                return value.getBytes();
            case "Base64":
                return Base64.getDecoder().decode(value);
            case "Hex":
                return Hex.decodeHex(value.toCharArray());
            default:
                throw new IllegalArgumentException(value);
        }
    }

}
