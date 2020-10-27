package org.cuiyang.assistant.util;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

import static javafx.scene.input.KeyCombination.*;

public class ShortcutKeyUtils {

    public static Modifier ctrl() {
        return OSUtils.isMac() ? META_DOWN : CONTROL_DOWN;
    }

    public static KeyCodeCombination ctrl(KeyCode code) {
        return new KeyCodeCombination(code, ctrl());
    }

    public static KeyCodeCombination ctrlAlt(KeyCode code) {
        return new KeyCodeCombination(code, ctrl(), SHIFT_DOWN, ALT_DOWN);
    }

    public static KeyCodeCombination ctrlShift(KeyCode code) {
        return new KeyCodeCombination(code, ctrl(), SHIFT_DOWN);
    }

    public static KeyCodeCombination ctrlAltShift(KeyCode code) {
        return new KeyCodeCombination(code, ctrl(), ALT_DOWN, SHIFT_DOWN);
    }
}
