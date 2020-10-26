package org.cuiyang.assistant.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.cuiyang.assistant.control.AddressItem;
import org.cuiyang.assistant.file.FileOperation;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * 地址计算控制器
 *
 * @author cuiyang
 */
public class AddressController extends BaseController implements Initializable, FileOperation {

    /** 基址地址 */
    public TextField baseTextField;
    /** 进制 */
    public ComboBox<String> radixComboBox;
    /** 地址父节点 */
    public VBox addressParent;
    /** 文件 */
    private File file;

    @Override
    public boolean isCloseable() {
        return baseTextField.getText().isEmpty();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.addAddressItem(0);
    }

    /**
     * 添加地址项
     */
    private void addAddressItem(int index) {
        AddressItem addressItem = newAddAddressItem();
        this.addressParent.getChildren().add(index, addressItem);
    }

    /**
     * 创建一个AddressItem
     */
    private AddressItem newAddAddressItem() {
        AddressItem addressItem = new AddressItem();
        addressItem.setBase(getBaseValue(), getRadix());
        addressItem.setAddOnMouseClicked(event -> {
            int idx = AddressController.this.addressParent.getChildren().indexOf(addressItem);
            AddressController.this.addAddressItem(idx + 1);
        });
        addressItem.setSubOnMouseClicked(event -> {
            if (AddressController.this.addressParent.getChildren().size() > 1) {
                AddressController.this.addressParent.getChildren().remove(addressItem);
                this.saveAs(file);
            }
        });
        addressItem.setOnChangeEvent(event -> this.saveAs(file));
        return addressItem;
    }

    /**
     * 获取基地址
     */
    private long getBaseValue() {
        try {
            return Long.parseLong(this.baseTextField.getText(), getRadix());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取进制
     */
    private int getRadix() {
        switch (this.radixComboBox.getValue()) {
            case "二进制":
                return 2;
            case "八进制":
                return 8;
            case "十六进制":
                return 16;
            default:
                return 10;
        }
    }

    /**
     * 计算
     */
    public void calc() {
        long baseValue = getBaseValue();
        int radix = getRadix();
        this.addressParent.getChildren().forEach(item -> {
            if (item instanceof AddressItem) {
                AddressItem addressItem = (AddressItem) item;
                addressItem.setBase(baseValue, radix);
                addressItem.calc(true, false);
            }
        });
        if (file != null) {
            saveAs(file);
        }
    }

    @SneakyThrows
    @Override
    public void openFile(File file) {
        this.file = file;
        String data = FileUtils.readFileToString(file, "utf-8");
        JSONObject addr = JSON.parseObject(data);
        baseTextField.setText(addr.getString("base"));
        radixComboBox.setValue(addr.getString("radix"));
        addressParent.getChildren().removeIf(node -> true);
        addressParent.getChildren().addAll(addr.getJSONArray("offsets").stream().map(o -> deserialize(o.toString())).collect(Collectors.toList()));
        setTitle(file);
    }

    @SneakyThrows
    @Override
    public void saveAs(File file) {
        this.file = file;
        List<Map<String, Object>> offsets = this.addressParent.getChildren().stream().map(item -> serialize((AddressItem) item)).collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("base", baseTextField.getText());
        result.put("radix", radixComboBox.getValue());
        result.put("offsets", offsets);
        FileUtils.writeStringToFile(file, JSON.toJSONString(result), "utf-8");
        setTitle(file);
    }

    public Map<String, Object> serialize(AddressItem addressItem) {
        Map<String, Object> result = new HashMap<>();
        result.put("offset", addressItem.getOffset());
        result.put("remark", addressItem.getRemark());
        return result;
    }

    public AddressItem deserialize(String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        AddressItem addressItem = newAddAddressItem();
        addressItem.setOffset(jsonObject.getString("offset"));
        addressItem.setRemark(jsonObject.getString("remark"));
        return addressItem;
    }
}
