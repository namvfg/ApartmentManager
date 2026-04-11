package com.and.apartmentmanager.helper;

import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;

import com.and.apartmentmanager.R;

public final class PasswordVisibilityHelper {

    private PasswordVisibilityHelper() {}

    public static void bind(ImageView toggle, EditText passwordField) {
        if (toggle == null || passwordField == null) {
            return;
        }
        toggle.setClickable(true);
        toggle.setFocusable(true);
        updateEyeIcon(toggle, passwordField);
        toggle.setOnClickListener(v -> {
            int type = passwordField.getInputType();
            boolean plain = isPlainPasswordVariation(type);
            int selStart = passwordField.getSelectionStart();
            int selEnd = passwordField.getSelectionEnd();
            int base = type & ~InputType.TYPE_MASK_VARIATION;
            passwordField.setInputType(base | (plain
                    ? InputType.TYPE_TEXT_VARIATION_PASSWORD
                    : InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD));
            passwordField.setTypeface(passwordField.getTypeface());
            restoreSelection(passwordField, selStart, selEnd);
            updateEyeIcon(toggle, passwordField);
        });
    }

    private static boolean isPlainPasswordVariation(int inputType) {
        return (inputType & InputType.TYPE_MASK_VARIATION) == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
    }

    private static void updateEyeIcon(ImageView toggle, EditText passwordField) {
        boolean plain = isPlainPasswordVariation(passwordField.getInputType());
        toggle.setImageResource(plain ? R.drawable.ic_auth_eye_off_outline : R.drawable.ic_auth_eye_outline);
    }

    private static void restoreSelection(EditText passwordField, int selStart, int selEnd) {
        int len = passwordField.getText() != null ? passwordField.getText().length() : 0;
        if (selStart >= 0 && selEnd >= 0) {
            int a = clamp(selStart, 0, len);
            int b = clamp(selEnd, 0, len);
            passwordField.setSelection(Math.min(a, b), Math.max(a, b));
        } else if (selStart >= 0) {
            passwordField.setSelection(clamp(selStart, 0, len));
        }
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
