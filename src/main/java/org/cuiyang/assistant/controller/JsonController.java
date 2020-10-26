package org.cuiyang.assistant.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.control.CodeEditor;
import org.cuiyang.assistant.control.KeyValueTreeItem;
import org.cuiyang.assistant.control.searchcodeeditor.SearchCodeEditor;
import org.cuiyang.assistant.file.EditorFileOperation;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

import static com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue;
import static freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;
import static org.cuiyang.assistant.control.searchcodeeditor.SearchCodeEditor.FILE_EVENT;
import static org.cuiyang.assistant.util.WordUtils.firstUpperCase;

/**
 * Json 控制器
 *
 * @author cy48576
 */
public class JsonController extends BaseController implements Initializable, EditorFileOperation {

    private Template pojoTemplate;
    private Template mapTemplate;

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
    /** 联动行号 */
    private int rowIndex = 0;
    public ImageView editZoomImageView;
    public ImageView previewZoomImageView;

    public JsonController() {
        try {
            Configuration config  = new Configuration(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
            config.setClassForTemplateLoading(this.getClass(), "/templates");
            pojoTemplate = config.getTemplate("pojo.ftl", "UTF-8");
            mapTemplate = config.getTemplate("map.ftl", "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
     * 子节点生成pojo
     */
    public void genSubPojo() throws IOException, TemplateException {
        KeyValueTreeItem keyItem = (KeyValueTreeItem) jsonTreeView.getTreeItem(jsonTreeView.getSelectionModel().getSelectedIndex());
        KeyValueTreeItem valueItem = (KeyValueTreeItem) jsonTreeView.getTreeItem(jsonTreeView.getSelectionModel().getSelectedIndex());
        if (valueItem.getValue2() instanceof JSON) {
            genPojo(JSON.toJSONString(valueItem.getValue2(), WriteMapNullValue), keyItem.getKey());
        }
    }

    /**
     * 子节点生成map
     */
    public void genSubMap() throws Exception {
        KeyValueTreeItem keyItem = (KeyValueTreeItem) jsonTreeView.getTreeItem(jsonTreeView.getSelectionModel().getSelectedIndex());
        KeyValueTreeItem valueItem = (KeyValueTreeItem) jsonTreeView.getTreeItem(jsonTreeView.getSelectionModel().getSelectedIndex());
        if (valueItem.getValue2() instanceof JSON) {
            genMap(JSON.toJSONString(valueItem.getValue2(), WriteMapNullValue), keyItem.getKey());
        }
    }

    /**
     * 切换视图
     */
    public void switchEdit() {
        if (viewType == 0) {
            splitPane.getItems().removeIf(item -> true);
            splitPane.getItems().add(splitLeft);
            this.editZoomImageView.setImage(new Image("/view/image/zoom-down.png"));
            viewType = 1;
        } else {
            splitPane.getItems().removeIf(item -> true);
            splitPane.getItems().add(splitLeft);
            splitPane.getItems().add(splitRight);
            this.editZoomImageView.setImage(new Image("/view/image/zoom-up.png"));
            viewType = 0;
        }
    }

    /**
     * 切换视图
     */
    public void switchPreview() {
        if (viewType == 0) {
            splitPane.getItems().removeIf(item -> true);
            splitPane.getItems().add(splitRight);
            this.previewZoomImageView.setImage(new Image("/view/image/zoom-down.png"));
            viewType = 2;
        } else {
            splitPane.getItems().removeIf(item -> true);
            splitPane.getItems().add(splitLeft);
            splitPane.getItems().add(splitRight);
            this.previewZoomImageView.setImage(new Image("/view/image/zoom-up.png"));
            viewType = 0;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.editor.setType(CodeEditor.Type.JSON);
        this.editor.setSupportSave(true);
        this.editor.addEventHandler(FILE_EVENT, event -> {
            File file = this.editor.getFile();
            this.setTitle(file);
        });
        this.editor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtils.isBlank(newValue)) {
                jsonTreeView.setRoot(null);
                return;
            }
            try {
                Object jsonObject = JSON.parse(newValue, Feature.OrderedField);
                KeyValueTreeItem root = new KeyValueTreeItem("Root", null);
                this.rowIndex = 0;
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
            item.setRow(this.rowIndex++);
            parent.getChildren().add(item);

            JSONObject jsonObject = (JSONObject) value;
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                buildJsonTreeItem(entry.getKey(), entry.getValue(), item);
            }
            this.rowIndex++;
        } else if (value instanceof JSONArray) {
            KeyValueTreeItem item = new KeyValueTreeItem(key, value);
            item.setRow(this.rowIndex++);
            parent.getChildren().add(item);

            JSONArray jsonArray = (JSONArray) value;
            for (int i = 0; i < jsonArray.size(); i++) {
                buildJsonTreeItem("[" + i + "]", jsonArray.get(i), item);
            }
            this.rowIndex++;
        } else {
            KeyValueTreeItem item = new KeyValueTreeItem(key, value);
            item.setRow(this.rowIndex++);
            parent.getChildren().add(item);
        }
    }

    @Override
    public boolean isCloseable() {
        return StringUtils.isBlank(editor.getText());
    }

    /**
     * 生成pojo
     */
    public void genPojo() throws IOException, TemplateException {
        String text = this.editor.getText();
        genPojo(text, "JSON");
    }

    /**
     * 生成pojo
     */
    public void genPojo(String text, String className) throws IOException, TemplateException {
        if (StringUtils.isEmpty(text)) {
            return;
        }
        clearLog();
        Object object = JSON.parse(text, Feature.OrderedField);
        if (object instanceof JSONObject) {
            genPojo((JSONObject) object, className);
        } else if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) object;
            if (!jsonArray.isEmpty() && jsonArray.get(0) instanceof JSONObject) {
                genPojo((JSONObject) jsonArray.get(0), className);
            }
        }
    }

    /**
     * 生成pojo
     */
    private void genPojo(JSONObject jsonObject, String className) throws IOException, TemplateException {
        List<Map<String, String>> fields = new ArrayList<>();
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            Map<String, String> field = new HashMap<>();
            String fieldType = fieldType(entry.getKey(), entry.getValue());
            field.put("fieldName", entry.getKey());
            field.put("fieldType", fieldType);
            fields.add(field);
        }
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("className", className);
        dataModel.put("fields", fields);
        StringWriter stringWriter = new StringWriter();
        Writer out = new BufferedWriter(stringWriter);
        pojoTemplate.process(dataModel, out);
        out.flush();
        out.close();
        log(stringWriter.toString());
    }

    /**
     * 获取字段类型
     */
    private String fieldType(String key, Object value) throws IOException, TemplateException {
        if (key == null || value == null) {
            return "String";
        }
        if (value instanceof Boolean) {
            return "Boolean";
        } else if (value instanceof Integer) {
            return "Integer";
        } else if (value instanceof BigDecimal) {
            return "BigDecimal";
        } else if (value instanceof JSONObject) {
            String clazz = firstUpperCase(key);
            genPojo((JSONObject) value, clazz);
            return clazz;
        } else if (value instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) value;
            if (jsonArray.isEmpty()) {
                return "List<?>";
            } else {
                return "List<" + fieldType(key, jsonArray.get(0)) + ">";
            }
        } else {
            return "String";
        }
    }

    /**
     * 生成map
     */
    public void genMap() throws Exception {
        genMap(this.editor.getText(), "map");
    }

    /**
     * 生成map
     */
    public void genMap(String text, String name) throws Exception {
        if (StringUtils.isEmpty(text)) {
            return;
        }
        clearLog();
        Object o = JSON.parse(text, Feature.OrderedField);
        JSONObject jsonObject;
        if (o instanceof JSONObject) {
            jsonObject = (JSONObject) o;
        } else if (o instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) o;
            if (jsonArray.isEmpty()) {
                return;
            }
            jsonObject = jsonArray.getJSONObject(0);
        } else {
            return;
        }
        Map<String, String> data = new LinkedHashMap<>();
        jsonObject.forEach((key, value) -> {
            String val;
            if (value == null) {
                val = null;
            } else if (value instanceof JSONObject) {
                val = "{}";
            } else if (value instanceof JSONArray) {
                val = "[]";
            } else {
                val = value.toString();
            }
            data.put(key, val);
        });
        StringWriter stringWriter = new StringWriter();
        Writer out = new BufferedWriter(stringWriter);
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("data", data);
        dataModel.put("mapName", name);
        mapTemplate.process(dataModel, out);
        out.flush();
        out.close();
        log(stringWriter.toString());
    }

    /**
     * 联动
     */
    public void link() {
        KeyValueTreeItem treeItem = (KeyValueTreeItem) jsonTreeView.getTreeItem(jsonTreeView.getSelectionModel().getSelectedIndex());
        if (treeItem != null) {
            this.editor.move(treeItem.getRow());
        }
    }
}
