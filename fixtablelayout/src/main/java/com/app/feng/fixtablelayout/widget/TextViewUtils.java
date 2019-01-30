package com.app.feng.fixtablelayout.widget;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

public class TextViewUtils {

    public static TextView generateTextView(
            Context context, String text, int gravity, int minWidth, int padding) {
        TextView textView = new TextView(context);

        setTextView(textView, text, gravity, minWidth, padding);
        return textView;
    }

    public static void setTextView(
            TextView textView, String text, int gravity, int minWidth, int padding) {
        textView.setText(text);
        textView.setWidth(minWidth);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        textView.setPadding(0, padding, 0, padding);
    }
}
