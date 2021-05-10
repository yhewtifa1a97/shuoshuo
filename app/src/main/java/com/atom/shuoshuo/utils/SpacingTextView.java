package com.atom.shuoshuo.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.utils
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/8/1  11:24
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class SpacingTextView extends TextView {

    public class LetterSpacing {
        public final static float NORMAL = 0f;
        public final static float NORMALBIG = 0.025f;
        public final static float BIG = 0.05f;
        public final static float BIGGEST = 0.2f;
    }

    private float letterSpacing = LetterSpacing.BIG;
    private CharSequence originalText = "";

    public SpacingTextView(Context context) {
        this(context, null);
    }

    public SpacingTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpacingTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        originalText = super.getText();
        applyLetterSpacing();
        this.invalidate();
    }

    public float getLetterSpacing() {
        return letterSpacing;
    }

    public void setLetterSpacing(float letterSpacing) {
        this.letterSpacing = letterSpacing;
        applyLetterSpacing();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        applyLetterSpacing();
    }

    @Override
    public CharSequence getText() {
        return originalText;
    }


    private void applyLetterSpacing() {
        if (this.originalText == null)
            return;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < originalText.length(); i++) {
            String c = "" + originalText.charAt(i);
            builder.append(c.toLowerCase());
            if (i + 1 < originalText.length()) {
                builder.append("\u00A0");
            }
        }

        SpannableString finalText = new SpannableString(builder.toString());
        if (builder.toString().length() > 1) {
            for (int i = 1; i < builder.toString().length(); i += 2) {
                finalText.setSpan(new ScaleXSpan((letterSpacing + 1) / 10), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        super.setText(finalText, BufferType.SPANNABLE);
    }
}
