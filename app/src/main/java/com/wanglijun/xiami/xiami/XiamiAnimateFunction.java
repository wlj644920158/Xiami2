package com.wanglijun.xiami.xiami;

import android.animation.Animator;
import android.animation.ValueAnimator;

public interface XiamiAnimateFunction {

    void onAnimationUpdate(ValueAnimator animation);

    void onAnimationEnd(Animator animation);

}
