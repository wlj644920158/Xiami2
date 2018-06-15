package com.wanglijun.xiami.xiami;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * ViewPager上面的标签选项卡
 * <p>
 * Created by wanglijun on 2018/4/27.
 */

public class XiamiTitleIndicator extends RelativeLayout {
    public interface OnTitleSelectListener {
        void onTitleSelect(int position);
    }

    private OnTitleSelectListener onTitleSelectListener;

    public void setOnTitleSelectListener(OnTitleSelectListener onTitleSelectListener) {
        this.onTitleSelectListener = onTitleSelectListener;
    }

    private static final String TAG = "XiamiTitleIndicator";

    /**
     * 标签数组
     */
    private List<String> titles;
    /**
     * 最大状态下文字的默认大小
     */
    private static final float MAX_TITLE_TEXT_SIZE = 30;//sp
    /**
     * 最小状态下文字的默认大小
     */
    private static final float MIN_TITLE_TEXT_SIZE = 12;//sp

    /**
     * 当前的索引值
     */
    private int titleIndex = 0;//当前索引


    private int maxTextViewHeight;


    /**
     * 文字的margin
     */
    private int margin;


    public XiamiTitleIndicator(Context context) {
        super(context, null);
        init(context);
    }

    public XiamiTitleIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        margin = ViewUtil.dp2px(context, XiamiConfig.XIAMI_TITLE_MARGIN);

    }


    /**
     * 设置当前的标题以及选中的位置
     *
     * @param title
     * @param position
     */
    public void setTitles(List<String> title, int position) {
        this.titles = title;

        removeAllViews();
        titleIndex = position;
        for (int i = 0; i < titles.size(); i++) {
            TextView textView = new TextView(getContext());
            /**
             * 为什么设置的id不直接用i,因为-1到22倍RelativeLayout使用作常量了,在addRule的时候就会有意想不到的效果,所以只要不使用这几个整型就行了
             */
            textView.setId(i + 100);
            textView.setText(titles.get(i));
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(titleIndex == i ? MAX_TITLE_TEXT_SIZE : MIN_TITLE_TEXT_SIZE);
            /**
             * 所有TextView包裹内容
             */
            RelativeLayout.LayoutParams params = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            /**
             * 靠底部排列
             */
            params.addRule(ALIGN_PARENT_BOTTOM);


            /**
             * 后一个在前一个的右边
             */
            if (i > 0) {
                params.addRule(RIGHT_OF, i - 1 + 100);
            }

            params.setMargins(margin, margin, margin, margin);
            textView.setLayoutParams(params);
            if (i != titleIndex) {
                textView.setTypeface(Typeface.DEFAULT);
            } else {
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                /**
                 * 这里先用一个伪造的足够大的MeasureSpec来测量下这个最大状态下的TextView看看它有多大,为什么要右移2位,因为MeasureSpec前两位是决定模式使用的
                 */
                int w_m = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
                int h_m = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
                textView.measure(w_m, h_m);
                maxTextViewHeight = textView.getMeasuredHeight();
            }



            final int finalI = i;
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTitleClick(finalI);
                }
            });
            addView(textView);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * 当前是保证AT_MOST情况下的,不要使用EXACTLY.AT_MOST看起来效果是最好的
         */
        int h_m = MeasureSpec.makeMeasureSpec(maxTextViewHeight+ margin * 2, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, h_m);
    }


    /**
     * 用于选中某个标签后文字放大的动画
     */
    private ValueAnimator timeValueAnimator;

    /**
     * TextView被点击的时候调用
     *
     * @param position
     */
    public void onTitleClick(int position) {
        setCurrentItem(position);
        if (onTitleSelectListener != null) {
            onTitleSelectListener.onTitleSelect(titleIndex);
        }
    }

    /**
     * ViewPager选中一页的时候调用
     *
     * @param position
     */
    public void setCurrentItem(int position) {
        if (getChildCount() == 0) {
            return;
        }
        /**
         * 之前的动画如果没执行完,让它结束
         */
        if (timeValueAnimator != null && timeValueAnimator.isRunning()) {
            timeValueAnimator.end();
        }
        if (position == titleIndex) {
            return;
        }
        int lastIndex = titleIndex;
        final TextView preTextView = (TextView) getChildAt(lastIndex);
        final TextView curTextView = (TextView) getChildAt(position);
        timeValueAnimator = ValueAnimator.ofFloat(0.f, 1.f);
        timeValueAnimator.setDuration(300);
        timeValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                /**
                 * 选中的这个放大,之前的那个缩小
                 */
                float preSize = MIN_TITLE_TEXT_SIZE + (MAX_TITLE_TEXT_SIZE - MIN_TITLE_TEXT_SIZE) * (1 - f);
                float curSize = MIN_TITLE_TEXT_SIZE + (MAX_TITLE_TEXT_SIZE - MIN_TITLE_TEXT_SIZE) * f;

                if (f >= 0.5f) {
                    preTextView.setTypeface(Typeface.DEFAULT);
                } else {
                    curTextView.setTypeface(Typeface.DEFAULT_BOLD);
                }
                preTextView.setTextSize(preSize);
                curTextView.setTextSize(curSize);
            }
        });
        timeValueAnimator.start();
        titleIndex = position;
    }
}
