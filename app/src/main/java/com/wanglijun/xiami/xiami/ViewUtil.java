package com.wanglijun.xiami.xiami;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by wanglijun on 2018/4/27.
 */

public class ViewUtil {
    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }
}
