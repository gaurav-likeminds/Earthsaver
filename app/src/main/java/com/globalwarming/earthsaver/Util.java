package com.globalwarming.earthsaver;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getTime(Long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
        return simpleDateFormat.format(new Date(time));
    }

}
