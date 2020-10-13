package org.cuiyang.assistant.control;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.control.MenuItem;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.cuiyang.assistant.util.ClipBoardUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.EditableStyledDocument;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isAllUpperCase;

/**
 * 代码编辑器
 *
 * @author cuiyang
 */
public class CodeEditor extends CodeArea {

    /** 关键字 */
    private static final String[] KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };

    /** 关键字 */
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    /** 小括号 */
    private static final String PAREN_PATTERN = "\\(|\\)";
    /** 大括号 */
    private static final String BRACE_PATTERN = "\\{|\\}";
    /** 中括号 */
    private static final String BRACKET_PATTERN = "\\[|\\]";
    /** 分号 */
    private static final String SEMICOLON_PATTERN = "\\;";
    /** 字符串 */
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    /** 评论 */
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    /** json key */
    private static final String KEY_PATTERN = "\"[^\"]*\" *:";
    /** json value */
    private static final String VALUE_PATTERN = "\"[^\"]*\"";

    /** 代码高亮正则 */
    private static final Pattern CODE_PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    /** json高亮正则 */
    private static final Pattern JSON_PATTERN = Pattern.compile(
            "(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<KEY>" + KEY_PATTERN + ")"
                    + "|(?<VALUE>" + VALUE_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    private static final Pattern XML_TAG = Pattern.compile("(?<ELEMENT>(</?\\h*)(\\w+)([^<>]*)(\\h*/?>))"
            +"|(?<COMMENT><!--[^<>]+-->)");

    private static final Pattern ATTRIBUTES = Pattern.compile("(\\w+\\h*)(=)(\\h*\"[^\"]+\")");

    private static final int GROUP_OPEN_BRACKET = 2;
    private static final int GROUP_ELEMENT_NAME = 3;
    private static final int GROUP_ATTRIBUTES_SECTION = 4;
    private static final int GROUP_CLOSE_BRACKET = 5;
    private static final int GROUP_ATTRIBUTE_NAME = 1;
    private static final int GROUP_EQUAL_SYMBOL = 2;
    private static final int GROUP_ATTRIBUTE_VALUE = 3;

    private String tab = "    ";
    private Pattern whiteSpace = Pattern.compile("^\\s+");
    private Pattern tabWhiteSpace = Pattern.compile("^\\s{1,4}");
    private ChangeListener<String> listener;

    public enum Type {
        CODE, JSON, XML
    }

    public CodeEditor(EditableStyledDocument<Collection<String>, String, Collection<String>> document) {
        super(document);
        init();
    }

    public CodeEditor() {
        init();
    }

    public CodeEditor(String text) {
        super(text);
        init();
    }

    public void setText(String text) {
        this.clear();
        this.replaceText(text);
        this.requestFollowCaret();
        this.moveTo(0, 0);
    }

    /**
     * 设置代码类型
     * @param type 代码类型
     */
    public void setType(Type type) {
        if (listener != null) {
            this.textProperty().removeListener(listener);
        }
        switch (type) {
            case CODE:
                listener = (obs, oldText, newText) -> this.setStyleSpans(0, codeComputeHighlighting(newText));
                break;
            case JSON:
                listener = (obs, oldText, newText) -> this.setStyleSpans(0, jsonComputeHighlighting(newText));
                break;
            case XML:
                listener = (obs, oldText, newText) -> this.setStyleSpans(0, xmlComputeHighlighting(newText));
                break;
            default:
        }
        this.textProperty().addListener(listener);
    }

    private void init() {
        // 解决中文输入问题
        this.setInputMethodRequests(new InputMethodRequestsObject(this));
        this.setOnInputMethodTextChanged(e -> {
            if (!e.getCommitted().equals("")) {
                IndexRange selection = this.getSelection();
                this.replaceText(selection, e.getCommitted());
            }
        });
        this.setWrapText(false);
        // 设置行号
        this.setParagraphGraphicFactory(LineNumberFactory.get(this));
        this.setContextMenu(contextMenu());
        // 监听按键
        this.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                // 换行对齐
                int caretPosition = this.getCaretPosition();
                int currentParagraph = this.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher(this.getParagraph(currentParagraph - 1).getSegments().get(0));
                if (m0.find()) Platform.runLater(() -> this.insertText(caretPosition, m0.group()));
            } else if (keyEvent.getCode() == KeyCode.TAB && !(keyEvent.isControlDown() || keyEvent.isMetaDown())) {
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
            } else if (keyEvent.getCode() == KeyCode.D && (keyEvent.isControlDown() || keyEvent.isMetaDown())) {
                // 复制当前行
                int index = this.getCurrentParagraph();
                this.insertText(index, this.getParagraphLength(index), "\n" + this.getParagraph(index).getText());
            } else if (keyEvent.getCode() == KeyCode.Y && (keyEvent.isControlDown() || keyEvent.isMetaDown())) {
                // 剪贴当前行
                int index = this.getCurrentParagraph();
                String content = this.getParagraph(index).getText();
                ClipBoardUtils.setSysClipboardText(content);
                if (index == this.getParagraphs().size() - 1) {
                    // 末行
                    if (this.getParagraphs().size() == 1) {
                        this.deleteText(index, 0, index, this.getParagraphLength(index));
                    } else {
                        this.deleteText(index - 1, this.getParagraphLength(index - 1), index, this.getParagraphLength(index));
                    }
                } else {
                    this.deleteText(index, 0, index + 1, 0);
                }
            } else if (keyEvent.getCode() == KeyCode.U && (keyEvent.isControlDown() || keyEvent.isMetaDown()) && keyEvent.isShiftDown()) {
                // 大小写转换
                IndexRange selection = this.getSelection();
                String selectedText = this.getSelectedText();
                this.replaceText(selection, isAllUpperCase(selectedText) ? selectedText.toLowerCase() : selectedText.toUpperCase());
                this.selectRange(selection.getStart(), selection.getEnd());
            }
        });
    }

    private StyleSpans<Collection<String>> codeComputeHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        try {
            int lastKwEnd = 0;
            Matcher matcher = CODE_PATTERN.matcher(text);
            while(matcher.find()) {
                String styleClass =
                        matcher.group("KEYWORD") != null ? "keyword" :
                                matcher.group("PAREN") != null ? "paren" :
                                        matcher.group("BRACE") != null ? "brace" :
                                                matcher.group("BRACKET") != null ? "bracket" :
                                                        matcher.group("SEMICOLON") != null ? "semicolon" :
                                                                matcher.group("STRING") != null ? "string" :
                                                                        matcher.group("COMMENT") != null ? "comment" :
                                                                                null; /* never happens */ assert styleClass != null;
                spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                lastKwEnd = matcher.end();
            }
            spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return spansBuilder.create();
    }

    private StyleSpans<Collection<String>> jsonComputeHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        try {
            int lastKwEnd = 0;
            Matcher matcher = JSON_PATTERN.matcher(text);
            while(matcher.find()) {
                String styleClass =
                        matcher.group("BRACE") != null ? "brace" :
                                matcher.group("BRACKET") != null ? "bracket" :
                                        matcher.group("KEY") != null ? "key" :
                                                matcher.group("VALUE") != null ? "value" :
                                                        matcher.group("COMMENT") != null ? "comment" :
                                                                null; /* never happens */ assert styleClass != null;
                spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                lastKwEnd = matcher.end();
            }
            spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return spansBuilder.create();
    }

    /**
     * 代码高亮
     */
    private StyleSpans<Collection<String>> xmlComputeHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        try {
            Matcher matcher = XML_TAG.matcher(text);
            int lastKwEnd = 0;
            while(matcher.find()) {

                spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                if(matcher.group("COMMENT") != null) {
                    spansBuilder.add(Collections.singleton("comment"), matcher.end() - matcher.start());
                }
                else {
                    if(matcher.group("ELEMENT") != null) {
                        String attributesText = matcher.group(GROUP_ATTRIBUTES_SECTION);

                        spansBuilder.add(Collections.singleton("tagmark"), matcher.end(GROUP_OPEN_BRACKET) - matcher.start(GROUP_OPEN_BRACKET));
                        spansBuilder.add(Collections.singleton("anytag"), matcher.end(GROUP_ELEMENT_NAME) - matcher.end(GROUP_OPEN_BRACKET));

                        if(!attributesText.isEmpty()) {

                            lastKwEnd = 0;

                            Matcher amatcher = ATTRIBUTES.matcher(attributesText);
                            while(amatcher.find()) {
                                spansBuilder.add(Collections.emptyList(), amatcher.start() - lastKwEnd);
                                spansBuilder.add(Collections.singleton("attribute"), amatcher.end(GROUP_ATTRIBUTE_NAME) - amatcher.start(GROUP_ATTRIBUTE_NAME));
                                spansBuilder.add(Collections.singleton("tagmark"), amatcher.end(GROUP_EQUAL_SYMBOL) - amatcher.end(GROUP_ATTRIBUTE_NAME));
                                spansBuilder.add(Collections.singleton("avalue"), amatcher.end(GROUP_ATTRIBUTE_VALUE) - amatcher.end(GROUP_EQUAL_SYMBOL));
                                lastKwEnd = amatcher.end();
                            }
                            if(attributesText.length() > lastKwEnd)
                                spansBuilder.add(Collections.emptyList(), attributesText.length() - lastKwEnd);
                        }

                        lastKwEnd = matcher.end(GROUP_ATTRIBUTES_SECTION);

                        spansBuilder.add(Collections.singleton("tagmark"), matcher.end(GROUP_CLOSE_BRACKET) - lastKwEnd);
                    }
                }
                lastKwEnd = matcher.end();
            }
            spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return spansBuilder.create();
    }

    /**
     * 右键菜单
     */
    private ContextMenu contextMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem close = new MenuItem("切换自动换行");
        menu.getItems().add(close);
        close.setOnAction(event -> this.setWrapText(!this.isWrapText()));
        return menu;
    }

    private static class InputMethodRequestsObject implements InputMethodRequests {

        private CodeArea area;

        public InputMethodRequestsObject(CodeArea area) {
            this.area = area;
        }

        @Override
        public Point2D getTextLocation(int offset) {
            Optional<Bounds> caretPositionBounds = area.getCaretBounds();
            if (caretPositionBounds.isPresent()) {
                Bounds bounds = caretPositionBounds.get();
                return new Point2D(bounds.getMaxX() - 5, bounds.getMaxY());
            }
            throw new NullPointerException();
        }

        @Override
        public int getLocationOffset(int x, int y) {
            return 0;
        }

        @Override
        public void cancelLatestCommittedText() {

        }

        @Override
        public String getSelectedText() {
            return "";
        }
    }
}
