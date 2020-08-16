package org.cuiyang.assistant.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.cuiyang.assistant.control.AddressItem;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 地址计算控制器
 *
 * @author cuiyang
 */
public class AddressController extends BaseController implements Initializable {

    /** 基址地址 */
    public TextField baseTextField;
    /** 进制 */
    public ComboBox<String> radixComboBox;
    /** 地址父节点 */
    public VBox addressParent;

    @Override
    public boolean isCloseable() {
        return false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.addAddressItem(0);
    }

    /**
     * 添加地址项
     */
    private void addAddressItem(int index) {
        AddressItem addressItem = new AddressItem();
        addressItem.setBase(getBaseValue(), getRadix());
        addressItem.setAddOnMouseClicked(event -> {
            int idx = AddressController.this.addressParent.getChildren().indexOf(addressItem);
            AddressController.this.addAddressItem(idx + 1);
        });
        addressItem.setSubOnMouseClicked(event -> {
            if (AddressController.this.addressParent.getChildren().size() > 1) {
                AddressController.this.addressParent.getChildren().remove(addressItem);
            }
        });
        this.addressParent.getChildren().add(index, addressItem);
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
                addressItem.calc(true);
            }
        });
    }
}
