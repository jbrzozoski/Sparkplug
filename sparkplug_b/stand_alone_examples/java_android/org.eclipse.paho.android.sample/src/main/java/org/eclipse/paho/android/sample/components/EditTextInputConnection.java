package org.eclipse.paho.android.sample.components;

import android.util.Log;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import static android.content.ContentValues.TAG;

/**
 * Created by wes on 6/1/17.
 */

public class EditTextInputConnection extends InputConnectionWrapper {

    public EditTextInputConnection(InputConnection target, boolean mutable) {
        super(target, mutable);
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
        // some code which takes the input and manipulates it and calls editText.getText().replace() afterwards
        Log.d(TAG, "commitText");
        return true;
    }
}