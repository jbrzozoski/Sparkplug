package org.eclipse.paho.android.sample.components;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class CustomEditText extends AppCompatEditText {

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        //Log.d(TAG, "onCreateInputConnection");
        InputConnection con = super.onCreateInputConnection(outAttrs);
        EditTextInputConnection connectionWrapper = new EditTextInputConnection(con, true);
        return connectionWrapper;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        //Log.d(TAG, "onKeyPreIme");
        return super.onKeyPreIme(keyCode, event);
    }
}
