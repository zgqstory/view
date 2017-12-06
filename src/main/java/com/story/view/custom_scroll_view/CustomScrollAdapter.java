package com.story.view.custom_scroll_view;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名称：CustomScrollAdapter
 * 类描述：自定义滑动View适配器
 * 创建人：story
 * 创建时间：2017/11/29 10:53
 */

public abstract class CustomScrollAdapter<T> extends PagerAdapter implements ViewPager.OnPageChangeListener {

    private List<T> data = new ArrayList<>();
    private CustomScrollView scrollView;

    protected CustomScrollAdapter(List<T> data, CustomScrollView customScrollView) {
        this.data = data;
        this.scrollView = customScrollView;
        init();
    }

    private void init() {
        int position = Integer.MAX_VALUE/2 - (Integer.MAX_VALUE/2) % getRealCount();
        scrollView.getViewPager().setAdapter(this);
        scrollView.getViewPager().setOnPageChangeListener(this);
        scrollView.getViewPager().setCurrentItem(position);

        scrollView.setDotItemView(getRealCount());
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int itemPosition = position % getRealCount();
        if(data.size() > itemPosition) {
            T t = data.get(itemPosition);
            View view = loadItemView(container, itemPosition, t);
            if (view != null) {
                container.addView(view);
                return view;
            }
        }
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return data.size() == 0 ? 0 : Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    int getRealCount() {
        return data.size();
    }

    /**
     * 需要实现方法，加载Item布局
     * @param container container
     * @param position position
     * @param t t
     */
    public abstract View loadItemView(ViewGroup container, int position, T t);

    /**
     * 定义方法：设置Adapter数据
     * @param data data
     */
    public void setData(List<T> data) {
        if (data != null) {
            this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        scrollView.updateDotLayout(position % getRealCount());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
