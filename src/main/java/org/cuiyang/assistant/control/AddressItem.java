package org.cuiyang.assistant.control;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.util.ClipBoardUtils;

/**
 * 地址Item
 *
 * @author cuiyang
 */
public class AddressItem extends HBox {

    private long baseValue = 0;
    private int radix = 10;

    private TextField offsetTextField = this.addInput("偏移地址");
    private TextField realTextField = this.addInput("真实地址");
    private TextField remarkTextField = this.addInput("备注信息");
    private Button copyButton = this.addButton("", "/view/image/copy.png");
    private Button addButton = this.addButton("", "/view/image/add.png");
    private Button subButton = this.addButton("", "/view/image/sub.png");
    private EventHandler<Event> changeEventHandler;

    public AddressItem() {
        init();
    }

    public final void setAddOnMouseClicked(EventHandler<? super MouseEvent> value) {
        this.addButton.setOnMouseClicked(value);
    }

    public final void setSubOnMouseClicked(EventHandler<? super MouseEvent> value) {
        this.subButton.setOnMouseClicked(value);
    }

    private void init() {
        setSpacing(20);
        this.offsetTextField.setOnKeyReleased(event -> calc(true, true));
        this.realTextField.setOnKeyReleased(event -> calc(false, true));
        this.remarkTextField.setOnKeyReleased(event -> {
            if (this.changeEventHandler != null) {
                this.changeEventHandler.handle(null);
            }
        });
        copyButton.setOnMouseClicked(event -> ClipBoardUtils.setSysClipboardText(realTextField.getText()));
        copyButton.setTooltip(new Tooltip("复制真实地址"));
        addButton.setTooltip(new Tooltip("添加"));
        subButton.setTooltip(new Tooltip("删除"));
    }

    public void setBase(long baseValue, int radix) {
        this.baseValue = baseValue;
        this.radix = radix;
    }

    /**
     * 计算
     */
    public void calc(boolean byOffset, boolean event) {
        try {
            if (byOffset && StringUtils.isNotEmpty(this.offsetTextField.getText())) {
                // 计算真实地址
                long offsetValue = Long.parseLong(this.offsetTextField.getText(), radix);
                long realValue = baseValue + offsetValue;
                this.realTextField.setText(Long.toString(realValue, radix).toUpperCase());
            } else if (!byOffset && StringUtils.isNotEmpty(this.realTextField.getText())) {
                // 计算偏移地址
                long realValue = Long.parseLong(this.realTextField.getText(), radix);
                long offsetValue = realValue - baseValue;
                this.offsetTextField.setText(Long.toString(offsetValue, radix).toUpperCase());
            }
            if (event && this.changeEventHandler != null) {
                this.changeEventHandler.handle(null);
            }
        } catch (Exception ignore) {
        }
    }

    private TextField addInput(String name) {
        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(name);
        label.setTextFill(Paint.valueOf("gray"));
        hBox.getChildren().add(label);
        TextField textField = new TextField();
        textField.setPromptText(name);
        textField.setPrefWidth(200);
        hBox.getChildren().add(textField);
        this.getChildren().add(hBox);
        return textField;
    }

    private Button addButton(String text, String image) {
        ImageView imageView = null;
        if (image != null) {
            imageView = new ImageView(new Image(image));
            imageView.setFitWidth(16);
            imageView.setFitHeight(16);
        }
        Button button = new Button(text, imageView);
        this.getChildren().add(button);
        return button;
    }

    public void setOnChangeEvent(EventHandler<Event> handler) {
        this.changeEventHandler = handler;
    }

    public void setOffset(String val) {
        this.offsetTextField.setText(val);
        this.calc(true, false);
    }

    public String getOffset() {
        return this.offsetTextField.getText();
    }

    public void setRemark(String val) {
        this.remarkTextField.setText(val);
    }

    public String getRemark() {
        return this.remarkTextField.getText();
    }
}
