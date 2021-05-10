package com.atom.shuoshuo.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

public class SnackbarUtil {

    public static void showMessage(View view, String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show();
    }

    public static void showMessage(View view, String text, String action, View.OnClickListener listener) {
        Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE).setAction(action, listener).show();
    }
}