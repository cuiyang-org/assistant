package org.cuiyang.assistant.util;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.cuiyang.assistant.javafx.InvalidKeyCodeCombination;

import static javafx.scene.input.KeyCombination.*;

public class ShortcutKeyUtils {

    public static Modifier ctrl() {
        return OSUtils.isMac() ? META_DOWN : CONTROL_DOWN;
    }

    public static KeyCodeCombination ctrl(KeyCode code) {
        return new KeyCodeCombination(code, ctrl());
    }

    public static InvalidKeyCodeCombination invalidCtrl(KeyCode code) {
        return new InvalidKeyCodeCombination(code, ctrl());
    }

    public static KeyCodeCombination alt(KeyCode code) {
        return new KeyCodeCombination(code, ALT_DOWN);
    }

    public static InvalidKeyCodeCombination invalidAlt(KeyCode code) {
        return new InvalidKeyCodeCombination(code, ALT_DOWN);
    }

    public static KeyCodeCombination ctrlAlt(KeyCode code) {
        return new KeyCodeCombination(code, ctrl(), ALT_DOWN);
    }

    public static InvalidKeyCodeCombination invalidCtrlAlt(KeyCode code) {
        return new InvalidKeyCodeCombination(code, ctrl(), ALT_DOWN);
    }

    public static KeyCodeCombination ctrlShift(KeyCode code) {
        return new KeyCodeCombination(code, ctrl(), SHIFT_DOWN);
    }

    public static InvalidKeyCodeCombination invalidCtrlShift(KeyCode code) {
        return new InvalidKeyCodeCombination(code, ctrl(), SHIFT_DOWN);
    }

    public static KeyCodeCombination ctrlAltShift(KeyCode code) {
        return new KeyCodeCombination(code, ctrl(), ALT_DOWN, SHIFT_DOWN);
    }

    public static InvalidKeyCodeCombination invalidCtrlAltShift(KeyCode code) {
        return new InvalidKeyCodeCombination(code, ctrl(), ALT_DOWN, SHIFT_DOWN);
    }
}
