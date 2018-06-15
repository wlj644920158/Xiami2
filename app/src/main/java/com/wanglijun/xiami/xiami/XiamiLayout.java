package com.wanglijun.xiami.xiami;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ScrollView;

import com.wanglijun.xiami.R;


/**
 * 外层的布局
 * Created by wanglijun on 2018/4/28.
 */

public class XiamiLayout extends ViewGroup implements ViewPager.OnPageChangeListener, XiamiTitleIndicator.OnTitleSelectListener, NestedScrollingParent {
    private static final String TAG = "XiamiLayout";

    /**
     * 上面的四个标题标签
     */
    private XiamiTitleIndicator titleIndicator;
    /**
     * 这个应该都知道吧
     */
    private ViewPager viewPager;
    /**
     * 整个界面能滑动的最大范围,这里默认就是上面的搜索框的高度
     */
    private int maxOffset;
    /**
     * 搜索框
     */
    private View searchView;
    private int layoutWidth;

    /**
     * 当前是否是展开状态
     */
    private boolean isExpanded = true;



    /**
     * 松手时执行的动画,目的是为了松开手时平滑的过度到某个状态
     */
    private ValueAnimator upValueAnimator;

    /**
     * 松手时当前搜索框的alpha
     */
    private float upSearchAlpha;


    private NestedScrollingParentHelper nestedScrollingParentHelper;

    public XiamiLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.WHITE);
        nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    }


    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }


    @Override
    public void onStopNestedScroll(View target) {
        nestedScrollingParentHelper.onStopNestedScroll(target);
        /**
         * 状态矫正
         */

        final MarginLayoutParams titleParams = (MarginLayoutParams) titleIndicator.getLayoutParams();
        final MarginLayoutParams viewPagerParams = (MarginLayoutParams) viewPager.getLayoutParams();
        /**
         * 利用标签的位置来判断松开手的时候是什么状态,这边主要是在松手的时候,处理掉展开还是折叠的问题
         */
        final int titleRealTop = (int) (titleIndicator.getY() - titleParams.topMargin);
        upSearchAlpha = searchView.getAlpha();
        if (titleRealTop == 0) {
            isExpanded = false;
        } else if (titleRealTop > 0 && titleRealTop < maxOffset / 2) {
            upValueAnimator = createUpAnimator(titleRealTop, 0, new XiamiAnimateFunction() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    int titleTop = value + titleParams.topMargin;
                    int titleBottom = titleTop + titleIndicator.getMeasuredHeight();
                    int viewPagerTop = titleBottom + titleParams.bottomMargin + viewPagerParams.topMargin;
                    int viewPagerBottom = viewPagerTop + viewPager.getMeasuredHeight();
                    titleIndicator.layout(titleIndicator.getLeft(), titleTop, titleIndicator.getRight(), titleBottom);
                    viewPager.layout(titleIndicator.getLeft(), viewPagerTop, titleIndicator.getRight(), viewPagerBottom);
                    searchView.setAlpha(upSearchAlpha * (1 - animation.getAnimatedFraction()));
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isExpanded = false;

                }
            });
            upValueAnimator.start();
        } else if (titleRealTop >= maxOffset / 2 && titleRealTop < maxOffset) {
            upValueAnimator = createUpAnimator(titleRealTop, maxOffset, new XiamiAnimateFunction() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    int titleTop = value + titleParams.topMargin;
                    int titleBottom = titleTop + titleIndicator.getMeasuredHeight();
                    int viewPagerTop = titleBottom + titleParams.bottomMargin + viewPagerParams.topMargin;
                    int viewPagerBottom = viewPagerTop + XiamiLayout.this.viewPager.getMeasuredHeight();
                    titleIndicator.layout(titleIndicator.getLeft(), titleTop, titleIndicator.getRight(), titleBottom);
                    XiamiLayout.this.viewPager.layout(titleIndicator.getLeft(), viewPagerTop, titleIndicator.getRight(), viewPagerBottom);
                    searchView.setAlpha(upSearchAlpha + (1 - upSearchAlpha) * animation.getAnimatedFraction());
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isExpanded = true;

                }
            });
            upValueAnimator.start();
        } else {
            isExpanded = true;
        }
    }


    private ValueAnimator createUpAnimator(int start, int end, final XiamiAnimateFunction function) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                function.onAnimationUpdate(animation);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                function.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        return animator;
    }


    @Override
    public int getNestedScrollAxes() {
        return nestedScrollingParentHelper.getNestedScrollAxes();
    }


    /**
     * 先于子view开始滚动
     *
     * @param target
     * @param dx
     * @param dy
     * @param consumed
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        final MarginLayoutParams titleParams = (MarginLayoutParams) titleIndicator.getLayoutParams();

        float supposeY = titleIndicator.getY() - dy;

        /**
         * 向上滑动
         */
        if (dy > 0) {
            /**
             * 如果本次滑动越过边界
             */
            if (supposeY < titleParams.topMargin) {
                dy = (int) (titleIndicator.getY() - titleParams.topMargin);
            }
            offset(dy, consumed);
        }


    }

    private void offset(int dy, int[] consumed) {
        ViewCompat.offsetTopAndBottom(titleIndicator, -dy);
        ViewCompat.offsetTopAndBottom(viewPager, -dy);
        final MarginLayoutParams titleParams = (MarginLayoutParams) titleIndicator.getLayoutParams();
        float scale = (titleIndicator.getTop() - titleParams.topMargin) * 1.f / maxOffset;
        searchView.setAlpha(scale);

        consumed[0] = 0;
        consumed[1] = dy;
    }


    //后于child滚动
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        /**
         * 向下滑动
         */
        float supposeY = titleIndicator.getY() - dyUnconsumed;
        final MarginLayoutParams titleParams = (MarginLayoutParams) titleIndicator.getLayoutParams();
        if (dyUnconsumed < 0) {
            /**
             * 如果子view不能继续向下滑动的时候,那么如果还有可用的距离,我们就开始向下展开
             */
            if (!target.canScrollHorizontally(dyUnconsumed)) {
                if (supposeY > maxOffset + titleParams.topMargin) {
                    dyUnconsumed = (int) (titleIndicator.getY() - maxOffset - titleParams.topMargin);
                }
                ViewCompat.offsetTopAndBottom(titleIndicator, -dyUnconsumed);
                ViewCompat.offsetTopAndBottom(viewPager, -dyUnconsumed);
                float scale = (titleIndicator.getTop() - titleParams.topMargin) * 1.f / maxOffset;
                searchView.setAlpha(scale);

            }
        }
    }

    //返回值：是否消费了fling
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    //返回值：是否消费了fling
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    /**
     * 获取当前是否是展开状态
     *
     * @return
     */
    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        /**
         * 为了有些人可能顺序写反了,我们采用findViewById来绑定控件
         */
        searchView = findViewById(R.id.cv_xiami_search);
        titleIndicator = findViewById(R.id.xti_xiami_title);
        viewPager = findViewById(R.id.vp_xiami_content);
        titleIndicator.setOnTitleSelectListener(this);
        viewPager.addOnPageChangeListener(this);

        if (searchView == null) {
            throw new RuntimeException("XiamiLayout keep no searchView!!!");
        }
        if (titleIndicator == null || !(titleIndicator instanceof XiamiTitleIndicator)) {
            throw new RuntimeException("XiamiLayout keep no xiamiTitleIndicator(XiamiTitleIndicator)!!!");
        }
        if (viewPager == null || !(viewPager instanceof ViewPager)) {
            throw new RuntimeException("XiamiLayout keep no viewPager(android.support.v4.view.ViewPager)!!!");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * 虽然自定义控件的时候一定要考虑AT_MOST和EXACTLY两种情况,但是这里我们在高度上都是要把父控件给的参考高度全部用完的,就是这么霸气,所以我们只需要考虑宽度的情况
         */
        int width_size = MeasureSpec.getSize(widthMeasureSpec);
        int height_size = MeasureSpec.getSize(heightMeasureSpec);
        int width_mode = MeasureSpec.getMode(widthMeasureSpec);
        int new_width = 0;

        /**
         * 2.测量searchView
         */
        measureChild(searchView, widthMeasureSpec, heightMeasureSpec);
        MarginLayoutParams searchParams = (MarginLayoutParams) searchView.getLayoutParams();
        if (new_width < searchView.getMeasuredWidth() + searchParams.leftMargin + searchParams.rightMargin) {
            new_width = searchView.getMeasuredWidth() + searchParams.leftMargin + searchParams.rightMargin;
        }
        /**
         * 3.测量标签
         */
        measureChild(titleIndicator, widthMeasureSpec, heightMeasureSpec);
        MarginLayoutParams titleParams = (MarginLayoutParams) titleIndicator.getLayoutParams();
        if (new_width < titleIndicator.getMeasuredWidth() + titleParams.leftMargin + titleParams.rightMargin) {
            new_width = titleIndicator.getMeasuredWidth() + titleParams.leftMargin + titleParams.rightMargin;
        }
        /**
         * 4.最后处理ViewPager,先确定一下,整个控件能够滑动的范围,我们默认是searchView的区域
         */
        maxOffset = searchView.getMeasuredHeight() + searchParams.topMargin + searchParams.bottomMargin;
        /**
         * ViewPager 的高度就是总的高度减去搜索框的区域高度,减去标签控件的区域高度
         */
        int titleViewHeight = titleIndicator.getMeasuredHeight() + titleParams.topMargin + titleParams.bottomMargin;
        int viewPagerHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height_size - titleViewHeight, MeasureSpec.EXACTLY);
        measureChild(viewPager, widthMeasureSpec, viewPagerHeightMeasureSpec);
        MarginLayoutParams viewPagerParams = (MarginLayoutParams) viewPager.getLayoutParams();
        if (new_width < viewPager.getMeasuredWidth() + viewPagerParams.leftMargin + viewPagerParams.rightMargin) {
            new_width = viewPager.getMeasuredWidth() + viewPagerParams.leftMargin + viewPagerParams.rightMargin;
        }
        new_width = getPaddingLeft() + getPaddingRight() + new_width;
        if (width_mode == MeasureSpec.EXACTLY) {
            new_width = width_size;
        }
        setMeasuredDimension(new_width, height_size);
    }


    /**
     * 拿到控件的宽度和高度
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        layoutWidth = w;
    }

    @Override
    protected void onLayout(boolean changed, int ll, int tt, int rr, int bb) {
        int l = getPaddingLeft();
        int t = getPaddingTop();
        int r = layoutWidth - getPaddingRight();

        /**
         * 布局搜索框
         */
        MarginLayoutParams searchParams = (MarginLayoutParams) searchView.getLayoutParams();

        int searchTop = t + searchParams.topMargin;
        int searchBottom = searchTop + searchView.getMeasuredHeight();
        int searchLeft = l + searchParams.leftMargin;
        int searchRight = r - searchParams.rightMargin;

        searchView.layout(searchLeft, searchTop, searchRight, searchBottom);


        /**
         * 布局标签
         */
        MarginLayoutParams titleParams = (MarginLayoutParams) titleIndicator.getLayoutParams();
        int titleTop;
        int titleBottom;
        int titleLeft;
        int titleRight;


        /**
         * 区分展开和折叠时候布局的不同
         */
        if (isExpanded) {
            titleTop = searchBottom + searchParams.bottomMargin + titleParams.topMargin;
        } else {
            titleTop = titleParams.topMargin;
        }

        titleBottom = titleTop + titleIndicator.getMeasuredHeight();
        titleLeft = l + titleParams.leftMargin;
        titleRight = r - titleParams.rightMargin;
        titleIndicator.layout(titleLeft, titleTop, titleRight, titleBottom);
        /**
         * 布局ViewPager
         */
        MarginLayoutParams viewPagerParams = (MarginLayoutParams) viewPager.getLayoutParams();
        int viewPagerTop = titleBottom + titleParams.bottomMargin + viewPagerParams.topMargin;
        int viewPagerBottom = viewPagerTop + viewPager.getMeasuredHeight();
        int viewPagerLeft = l - viewPagerParams.leftMargin;
        int viewPagerRight = r - viewPagerParams.rightMargin;
        viewPager.layout(viewPagerLeft, viewPagerTop, viewPagerRight, viewPagerBottom);


    }


    /**
     * 外部调用的设置适配器的方法,适配器封装过,一把返回标签和视图
     *
     * @param adapter
     */
    public void setAdapter(XiamiPagerAdapter adapter, int position) {
        if (adapter == null) {
            throw new RuntimeException("adapter is null!!");
        }
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        titleIndicator.setTitles(adapter.getTitles(), position);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int p) {
        titleIndicator.setCurrentItem(p);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onTitleSelect(int position) {
        viewPager.setCurrentItem(position);
    }




    /* 生成默认的LayoutParams */

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }


    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }


    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }


}
