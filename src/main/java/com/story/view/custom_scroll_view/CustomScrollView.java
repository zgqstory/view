package com.story.view.custom_scroll_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.story.view.R;
import com.story.view.utils.DensityUtil;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 类名称：CustomScrollView
 * 类描述：自定义页面左右滑动控件，可自动轮播，可显示dot
 * 创建人：story
 * 创建时间：2017/11/23 16:36
 */

public class CustomScrollView extends RelativeLayout {

    private static final int DEFAULT_DOT_SIZE = 10;//单位dp
    private static final int AUTO_INTERVAL = 5;//自动滑动时间间隔

    private boolean isShowDot;// 是否显示Dot
    private boolean hasSetDotParams;//使用者是否设置过Params
    private int dotImageSelected;//Dot选中图片
    private int dotImageNormal;//Dot默认图片
    private int dotImageSize;//Dot图片默认大小

    private Timer mTimer;

    private ViewPager viewPager;//承载页面主要内容
    private LinearLayout dotLayout;//页面dot
    private Context context;

    public CustomScrollView(Context context) {
        this(context, null);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScrollView);
        if (typedArray != null) {
            isShowDot = typedArray.getBoolean(R.styleable.CustomScrollView_isShowDot, false);
            dotImageNormal = typedArray.getResourceId(R.styleable.CustomScrollView_dotImageNormal, R.drawable.custom_scroll_view_dot_unselect_icon);
            dotImageSelected = typedArray.getResourceId(R.styleable.CustomScrollView_dotImageSelected, R.drawable.custom_scroll_view_dot_select_icon);
            dotImageSize = typedArray.getDimensionPixelSize(R.styleable.CustomScrollView_dotImageSize, DensityUtil.changeDpToPx(context, DEFAULT_DOT_SIZE));
            typedArray.recycle();
        }

        init(context);
    }

    private void init(Context context) {
        this.context = context;
        initViewPager();
        setFixedScroller();
        initDotLayout();
    }

    /**
     * 设置ViewPager滑动速度
     */
    private void setFixedScroller() {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);
            Scroller scroller = new Scroller(context, (Interpolator) interpolator.get(null)) {
                @Override
                public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                    super.startScroll(startX, startY, dx, dy, duration * 7);    // 这里是关键，将duration变长或变短
                }
            };
            scrollerField.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
            // Do nothing.
        } catch (IllegalAccessException e) {
            // Do nothing.
        }
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        viewPager = new ViewPager(context);
        addView(viewPager);
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    /**
     * 初始化DotLayout
     */
    private void initDotLayout() {
        dotLayout = new LinearLayout(context);
        dotLayout.setBackgroundColor(Color.TRANSPARENT);
        LayoutParams dotParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        dotParams.addRule(ALIGN_PARENT_BOTTOM);
        dotParams.addRule(ALIGN_PARENT_RIGHT);
        dotParams.setMargins(12,20,12,20);
        dotLayout.setLayoutParams(dotParams);
        addView(dotLayout);
        if (!isShowDot) {
            dotLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 开始自动滑动
     */
    public void beginAutoScroll() {
        stopAutoScroll();
        mTimer = new Timer();
        mTimer.schedule(new AutoTask(), AUTO_INTERVAL*1000, AUTO_INTERVAL*1000);
    }

    /**
     * 停止定时
     */
    private void stopAutoScroll() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 通过Params设置Dot的位置（只修改位置、边距，且方法值执行一次）
     * @param layoutParams layoutParams
     */
    public void setDotParams(LayoutParams layoutParams) {
        if (layoutParams != null && !hasSetDotParams) {
            hasSetDotParams = true;
            LayoutParams nowParams = (LayoutParams) dotLayout.getLayoutParams();
            //取消默认位置数据
            nowParams.addRule(ALIGN_PARENT_BOTTOM, 0);
            nowParams.addRule(ALIGN_PARENT_RIGHT, 0);
            int[] rules = layoutParams.getRules();
            for (int i: rules) {
                nowParams.addRule(i);
            }
            nowParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
            dotLayout.setLayoutParams(nowParams);
        }
    }

    /**
     * 设置Dot的Item
     * @param count item数量
     */
    public void setDotItemView(int count) {
        setDotItemView(dotImageNormal, dotImageSelected, count, dotImageSize);
    }

    /**
     * 设置Dot的Item
     * @param itemSelected item选择时图片
     * @param itemNormal item默认状态
     * @param count item数量
     * @param size item长宽(单位是px)
     */
    public void setDotItemView(@DrawableRes int itemNormal, @DrawableRes int itemSelected, int count, int size) {
        if (count > 0) {
            dotImageSelected = itemSelected;
            dotImageNormal = itemNormal;
            dotLayout.removeAllViews();
            for (int i=0;i<count;i++) {
                ImageView itemView = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                params.leftMargin = 8;
                params.gravity = Gravity.CENTER;
                itemView.setLayoutParams(params);
                if (i == 0) {
                    itemView.setBackgroundResource(dotImageSelected);
                } else {
                    itemView.setBackgroundResource(dotImageNormal);
                }
                dotLayout.addView(itemView);
            }
        }
    }

    /**
     * 更新Dot Item的状态
     * @param position 当前选择item下标
     */
    public void updateDotLayout(int position) {
        int size = dotLayout.getChildCount();
        if (position < size) {
            for (int i=0;i<size;i++) {
                ImageView itemView = (ImageView) dotLayout.getChildAt(i);
                if (i == position) {
                    itemView.setBackgroundResource(dotImageSelected);
                } else {
                    itemView.setBackgroundResource(dotImageNormal);
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        // 控件被移出时取消定时
        stopAutoScroll();
        super.onDetachedFromWindow();
    }

    /************************************** 定时代码 *****************************************/
    private Handler mHandler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = viewPager.getCurrentItem();
            if (viewPager.getAdapter() != null) {
                if (currentItem == viewPager.getAdapter().getCount() - 1) {
                    currentItem = 0;
                } else {
                    currentItem++;
                }
            }
            viewPager.setCurrentItem(currentItem);
            updateDotLayout(currentItem % ((CustomScrollAdapter)viewPager.getAdapter()).getRealCount());
        }
    };

    private class AutoTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(runnable);
        }
    }
}
