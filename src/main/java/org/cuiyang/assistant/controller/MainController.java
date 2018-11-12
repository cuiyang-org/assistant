package org.cuiyang.assistant.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.cuiyang.assistant.util.ClipBoardUtils;
import org.cuiyang.assistant.util.UnicodeUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Map;

import static com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue;

/**
 * MainController
 *
 * @author cy48576
 */
public class MainController {

    /** tab容器 */
    public Pane container;
    /** json文本框 */
    public TextArea jsonTextArea;
    /** json树 */
    public TreeView<String> jsonTreeView;
    /** 编码输入 */
    public TextArea encodeInput;
    /** 编码输出 */
    public TextArea encodeOutput;
    /** 加密输入 */
    public TextArea encryptionInput;
    /** 加密输出 */
    public TextArea encryptionOutput;

    public MainController() {
    }

    /**
     * init
     */
    public void init () {
        this.jsonTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtils.isBlank(newValue)) {
                jsonTreeView.setRoot(null);
                return;
            }
            try {
                Object jsonObject = JSON.parse(newValue, Feature.OrderedField);
                TreeItem<String> root = new TreeItem<>("Root");
                buildJsonTreeItem("JSON", jsonObject, root);
                jsonTreeView.setRoot(root);
                jsonTreeView.setShowRoot(false);
            } catch (Exception e) {
                jsonTreeView.setRoot(null);
            }
        });
    }

    /**
     * 构建json树
     */
    private void buildJsonTreeItem(String key, Object o, TreeItem<String> parent) {
        if (o instanceof JSONObject) {
            TreeItem<String> item = new TreeItem<>(key);
            item.setExpanded(true);
            parent.getChildren().add(item);

            JSONObject jsonObject = (JSONObject) o;
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                buildJsonTreeItem(entry.getKey(), entry.getValue(), item);
            }
        } else if (o instanceof  JSONArray) {
            TreeItem<String> item = new TreeItem<>(key);
            item.setExpanded(true);
            parent.getChildren().add(item);

            JSONArray jsonArray = (JSONArray) o;
            for (int i = 0; i < jsonArray.size(); i++) {
                buildJsonTreeItem("[" + i + "]", jsonArray.get(i), item);
            }
        } else {
            TreeItem<String> item = new TreeItem<>(key + " : " + String.valueOf(o));
            item.setExpanded(true);
            parent.getChildren().add(item);
        }
    }

    /**
     * 切换tab
     */
    public void switchTab(MouseEvent mouseEvent) {
        Node source = (Node) mouseEvent.getSource();
        Pane parent = (Pane) source.getParent();
        ObservableList<Node> children = parent.getChildren();
        for (int i = 0; i < children.size(); i++) {
            children.get(i).getStyleClass().remove("selected");
            if (source == children.get(i)) {
                show(i);
            }
        }
        source.getStyleClass().add("selected");
    }

    /**
     * 显示对应的tab
     */
    private void show(int index) {
        ObservableList<Node> children = container.getChildren();
        for (int i = 0; i < children.size(); i++) {
            children.get(i).setVisible(i == index);
        }
    }

    /**
     * json格式化
     */
    public void jsonFormat() {
        try {
            String jsonStr = JSON.toJSONString(JSON.parse(this.jsonTextArea.getText(), Feature.OrderedField), WriteMapNullValue, PrettyFormat);
            jsonStr = jsonStr.replaceAll("\t", "    ");
            this.jsonTextArea.setText(jsonStr);
        } catch (Exception ignore) {
        }
    }

    /**
     * json去格式化
     */
    public void jsonSimple() {
        try {
            this.jsonTextArea.setText(JSON.toJSONString(JSON.parse(this.jsonTextArea.getText(), Feature.OrderedField), WriteMapNullValue));
        } catch (Exception ignore) {
        }
    }

    /**
     * json折叠
     */
    public void jsonFold() {
        if (jsonTreeView == null || jsonTreeView.getRoot() == null) {
            return;
        }
        expanded(jsonTreeView.getRoot(), false);
        jsonTreeView.getRoot().setExpanded(true);
    }

    /**
     * json展开
     */
    public void jsonExpanded() {
        if (jsonTreeView == null || jsonTreeView.getRoot() == null) {
            return;
        }
        expanded(jsonTreeView.getRoot(), true);
        jsonTreeView.getRoot().setExpanded(true);
    }

    /**
     * json折叠/展开
     */
    private void expanded(TreeItem<String> root, boolean expanded) {
        if (root == null) {
            return;
        }
        root.setExpanded(expanded);
        for (TreeItem<String> item : root.getChildren()) {
            expanded(item, expanded);
        }
    }

    /**
     * copyJson
     */
    public void copyJson() {
        TreeItem<String> treeItem = jsonTreeView.getTreeItem(jsonTreeView.getSelectionModel().getSelectedIndex());
        ClipBoardUtils.setSysClipboardText(treeItem.getValue());
    }

    /**
     * copyJsonValue
     */
    public void copyJsonValue() {
        TreeItem<String> treeItem = jsonTreeView.getTreeItem(jsonTreeView.getSelectionModel().getSelectedIndex());
        String value;
        if (treeItem.getChildren().size() <= 0) {
            value = treeItem.getValue().split(":")[1];
        } else {
            value = treeItem.getValue();
        }
        ClipBoardUtils.setSysClipboardText(value.trim());
    }

    /**
     * copyJsonName
     */
    public void copyJsonName() {
        TreeItem<String> treeItem = jsonTreeView.getTreeItem(jsonTreeView.getSelectionModel().getSelectedIndex());
        ClipBoardUtils.setSysClipboardText(treeItem.getValue().split(":")[0].trim());
    }

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
