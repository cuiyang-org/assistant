package org.cuiyang.assistant.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.control.texteditor.TextEditor;
import org.cuiyang.assistant.util.ClipBoardUtils;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import static com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue;

/**
 * Json 控制器
 *
 * @author cy48576
 */
public class JsonController implements Initializable {

    /** json文本框 */
    public TextEditor jsonTextArea;
    /** json树 */
    public TreeView<String> jsonTreeView;

    /**
     * json格式化
     */
    public void jsonFormat() {
        try {
            String jsonStr = JSON.toJSONString(JSON.parse(this.jsonTextArea.textArea.getText(), Feature.OrderedField), WriteMapNullValue, PrettyFormat);
            jsonStr = jsonStr.replaceAll("\t", "    ");
            this.jsonTextArea.textArea.setText(jsonStr);
        } catch (Exception ignore) {
        }
    }

    /**
     * json去格式化
     */
    public void jsonSimple() {
        try {
            this.jsonTextArea.textArea.setText(JSON.toJSONString(JSON.parse(this.jsonTextArea.textArea.getText(), Feature.OrderedField), WriteMapNullValue));
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.jsonTextArea.textArea.setPromptText("请输入json串");
        this.jsonTextArea.textArea.textProperty().addListener((observable, oldValue, newValue) -> {
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
        } else if (o instanceof JSONArray) {
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

}
