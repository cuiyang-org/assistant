package org.cuiyang.assistant.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.control.CodeEditor;
import org.cuiyang.assistant.control.KeyValueTreeItem;
import org.cuiyang.assistant.control.searchcodeeditor.SearchCodeEditor;
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
public class JsonController extends BaseController implements Initializable {

    /** json文本框 */
    public SearchCodeEditor editor;
    /** json树 */
    public TreeView<String> jsonTreeView;
    /** split */
    public SplitPane splitPane;
    /** left */
    public Node splitLeft;
    /** right */
    public Node splitRight;
    /** 视图类型 0左右 1 编辑 2 折叠 */
    public int viewType = 0;

    /**
     * json格式化
     */
    public void jsonFormat() {
        String text = this.editor.getText();
        if (StringUtils.isEmpty(text)) {
            return;
        }
        try {
            String jsonStr = JSON.toJSONString(JSON.parse(text, Feature.OrderedField), WriteMapNullValue, PrettyFormat);
            jsonStr = jsonStr.replaceAll("\t", "    ");
            this.editor.setText(jsonStr);
        } catch (Exception e) {
            log(e.getMessage());
        }
    }

    /**
     * json去格式化
     */
    public void jsonSimple() {
        String text = this.editor.getText();
        if (StringUtils.isEmpty(text)) {
            return;
        }
        try {
            this.editor.setText(JSON.toJSONString(JSON.parse(text, Feature.OrderedField), WriteMapNullValue));
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
    private void expanded(TreeItem<?> root, boolean expanded) {
        if (root == null) {
            return;
        }
        root.setExpanded(expanded);
        for (TreeItem<?> item : root.getChildren()) {
            expanded(item, expanded);
        }
    }

    /**
     * copyJson
     */
    public void copyJson() {
        KeyValueTreeItem treeItem = (KeyValueTreeItem) jsonTreeView.getTreeItem(jsonTreeView.getSelectionModel().getSelectedIndex());
        ClipBoardUtils.setSysClipboardText(treeItem.getKey() + ":" + treeItem.getValue2());
    }

    /**
     * copyJsonValue
     */
    public void copyJsonValue() {
        KeyValueTreeItem treeItem = (KeyValueTreeItem) jsonTreeView.getTreeItem(jsonTreeView.getSelectionModel().getSelectedIndex());
        ClipBoardUtils.setSysClipboardText(String.valueOf(treeItem.getValue2()));
    }

    /**
     * copyJsonName
     */
    public void copyJsonName() {
        KeyValueTreeItem treeItem = (KeyValueTreeItem) jsonTreeView.getTreeItem(jsonTreeView.getSelectionModel().getSelectedIndex());
        ClipBoardUtils.setSysClipboardText(treeItem.getKey());
    }

    /**
     * 切换视图
     */
    public void switchView() {
        if (viewType == 0) {
            splitPane.getItems().removeIf(item -> true);
            splitPane.getItems().add(splitLeft);
            viewType = 1;
        } else if (viewType == 1) {
            splitPane.getItems().removeIf(item -> true);
            splitPane.getItems().add(splitRight);
            viewType = 2;
        } else {
            splitPane.getItems().removeIf(item -> true);
            splitPane.getItems().add(splitLeft);
            splitPane.getItems().add(splitRight);
            viewType = 0;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.editor.setType(CodeEditor.Type.JSON);
        this.editor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtils.isBlank(newValue)) {
                jsonTreeView.setRoot(null);
                return;
            }
            try {
                Object jsonObject = JSON.parse(newValue, Feature.OrderedField);
                KeyValueTreeItem root = new KeyValueTreeItem("Root", null);
                buildJsonTreeItem("JSON", jsonObject, root);
                jsonTreeView.setRoot(root);
                jsonTreeView.setShowRoot(false);
                jsonExpanded();
            } catch (Exception e) {
                jsonTreeView.setRoot(null);
            }
        });
    }

    /**
     * 构建json树
     */
    private void buildJsonTreeItem(String key, Object value, KeyValueTreeItem parent) {
        if (value instanceof JSONObject) {
            KeyValueTreeItem item = new KeyValueTreeItem(key, value);
            parent.getChildren().add(item);

            JSONObject jsonObject = (JSONObject) value;
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                buildJsonTreeItem(entry.getKey(), entry.getValue(), item);
            }
        } else if (value instanceof JSONArray) {
            KeyValueTreeItem item = new KeyValueTreeItem(key, value);
            parent.getChildren().add(item);

            JSONArray jsonArray = (JSONArray) value;
            for (int i = 0; i < jsonArray.size(); i++) {
                buildJsonTreeItem("[" + i + "]", jsonArray.get(i), item);
            }
        } else {
            KeyValueTreeItem item = new KeyValueTreeItem(key, value);
            parent.getChildren().add(item);
        }
    }

}
