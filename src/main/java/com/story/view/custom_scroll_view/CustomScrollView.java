package com.story.view.custom_scroll_view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 类名称：CustomScrollView
 * 类描述：自定义页面左右滑动控件，可自动轮播，可显示dot
 * 创建人：story
 * 创建时间：2017/11/23 16:36
 */

public class CustomScrollView extends RelativeLayout {

    private boolean isAutoScroll;//是否自动滑动
    private boolean isShowDot;//是否显示dot
    private boolean hasSetDotParams;//使用者是否设置过Params
    private int dotImageSelected;//Dot选中图片
    private int dotImageNormal;//Dot默认图片
    private int currentItem;//当前显示item下标
    private int autoInterval = 3;//自动滑动时间间隔
    private Timer mTimer;

    private ViewPager viewPager;//承载页面主要内容
    private LinearLayout dotLayout;//页面dot
    private Context context;

    public CustomScrollView(Context context) {
        this(context, null);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        initViewPager();
        initDotLayout();
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        viewPager = new ViewPager(context);
        addView(viewPager);
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
    }

    /**
     * 开始自动滑动
     */
    public void start() {
        stop();
        mTimer = new Timer();
        mTimer.schedule(new AutoTask(), autoInterval, autoInterval);
    }

    /**
     * 停止自动滑动
     */
    private void stop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 通过Params设置Dot的位置（只修改位置、边距、大小，且方法值执行一次）
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
     * @param itemSelected item选择时图片
     * @param itemNormal item默认状态
     * @param count item数量
     * @param size item长宽(单位是px)
     */
    public void setDotItemView(@DrawableRes int itemSelected, @DrawableRes int itemNormal, int count, int size) {
        if (count > 0 && size > 0) {
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
    private void updateDotLayout(int position) {
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
        stop();
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 控件被点击时停止任务
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                stop();
                break;
            case MotionEvent.ACTION_UP:
                start();
                break;
        }
        return super.onTouchEvent(event);
    }

    /************************************** 定时代码 *****************************************/
    private Handler mHandler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            currentItem = viewPager.getCurrentItem();
            if (viewPager.getAdapter() != null) {
                if (currentItem == viewPager.getAdapter().getCount() - 1) {
                    currentItem = 0;
                } else {
                    currentItem++;
                }
            }
            viewPager.setCurrentItem(currentItem);
            updateDotLayout(currentItem);
        }
    };

    private class AutoTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(runnable);
        }
    }
}
