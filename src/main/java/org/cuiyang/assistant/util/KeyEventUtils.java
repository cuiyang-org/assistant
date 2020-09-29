package org.cuiyang.assistant.util;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyEventUtils {

    public static boolean isCtrl(KeyEvent event) {
        return event.isControlDown() || event.isMetaDown();
    }

    public static boolean ctrl(KeyEvent event, KeyCode code) {
        return event.getCode() == code && isCtrl(event) && !event.isShiftDown();
    }

    public static boolean ctrlShift(KeyEvent event, KeyCode code) {
        return event.getCode() == code && isCtrl(event) && event.isShiftDown();
    }
}
