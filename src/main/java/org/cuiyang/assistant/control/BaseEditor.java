package org.cuiyang.assistant.control;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.EditableStyledDocument;
import org.fxmisc.richtext.model.StyleSpans;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码编辑器
 *
 * @author cuiyang
 */
public abstract class BaseEditor extends CodeArea {

    private String tab = "    ";
    private Pattern whiteSpace = Pattern.compile("^\\s+");
    private Pattern tabWhiteSpace = Pattern.compile("^\\s{1,4}");

    public BaseEditor(EditableStyledDocument<Collection<String>, String, Collection<String>> document) {
        super(document);
        init();
    }

    public BaseEditor() {
        init();
    }

    public BaseEditor(String text) {
        super(text);
        init();
    }

    public void setText(String text) {
        this.clear();
        this.replaceText(text);
    }

    private void init() {
        // 设置行号
        this.setParagraphGraphicFactory(LineNumberFactory.get(this));
        // 监听按键
        this.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                // 换行对齐
                int caretPosition = this.getCaretPosition();
                int currentParagraph = this.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher(this.getParagraph(currentParagraph - 1).getSegments().get(0));
                if (m0.find()) Platform.runLater(() -> this.insertText(caretPosition, m0.group()));
            } else if (keyEvent.getCode() == KeyCode.TAB) {
                // tab转空格
                int caretPosition = this.getCaretPosition();
                if (keyEvent.isShiftDown()) {
                    String str = this.getParagraph(this.getCurrentParagraph()).getSegments().get(0);
                    Matcher matcher = tabWhiteSpace.matcher(str);
                    if (matcher.find()) {
                        String group = matcher.group();
                        int start = caretPosition - this.getCaretColumn();
                        int end = start + group.length();
                        this.deleteText(start, end);
                        this.insertText(caretPosition - group.length(), "");
                    }
                } else {
                    this.replaceText(caretPosition - 1, caretPosition, tab);
                }
            }
        });
        // 代码高亮
        this.textProperty().addListener((obs, oldText, newText) -> this.setStyleSpans(0, computeHighlighting(newText)));
    }

    /**
     * 代码高亮
     */
    abstract protected StyleSpans<Collection<String>> computeHighlighting(String text);

}
