package org.cuiyang.assistant.controller;

import javafx.scene.control.TextArea;
import org.cuiyang.assistant.util.ClipBoardUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * IdController
 *
 * @author cy48576
 */
public class IdController {
    public TextArea idOutput;

    /**
     * 复制到粘贴板
     */
    private void copy() {
        ClipBoardUtils.setSysClipboardText(idOutput.getText());
    }

    /**
     * uuid
     */
    public void uuid() {
        idOutput.setText(UUID.randomUUID().toString());
        copy();
    }

    /**
     * uuid2
     */
    public void uuid2() {
        idOutput.setText(UUID.randomUUID().toString().replaceAll("-", ""));
        copy();
    }

    /**
     * 时间戳
     */
    public void timestamp() {
        idOutput.setText(String.valueOf(System.currentTimeMillis()));
        copy();
    }

    /**
     * 日期
     */
    public void datetime() {
        idOutput.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        copy();
    }
}
