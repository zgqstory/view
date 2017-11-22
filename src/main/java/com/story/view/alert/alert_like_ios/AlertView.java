package com.story.view.alert.alert_like_ios;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.story.view.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 类名称：AlertView
 * 类描述：仿IOS弹出框控件
 * 创建人：story
 * 创建时间：2017/11/22 18:08
 */

public class AlertView {
    private final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM
    );
    private static final int HORIZONTAL_BUTTONS_MAXCOUNT = 2;
    private static final int CANCELPOSITION = -1;//点击取消按钮返回 －1，其他按钮从0开始算

    private String title;
    private String msg;
    private List<String> mDestructive;
    private String cancel;
    private List<String> mDatas = new ArrayList<>();//存放所有显示按钮数据

    private Context context;
    private ViewGroup contentContainer;
    private ViewGroup decorView;//activity的根View
    private ViewGroup rootView;//AlertView 的 根View
    private int rootId;//AlertView根View的ID

    private Style style = Style.Alert;

    private OnDismissListener onDismissListener;
    private OnItemClickListener onItemClickListener;
    private boolean isDismissing;

    private Animation outAnim;
    private Animation inAnim;
    private int gravity = Gravity.CENTER;

    /**
     * 弹出对话框
     * @param title 标题
     * @param msg 内容
     * @param cancel 确定/取消键
     * @param destructive 红色选项
     * @param others 蓝色选项
     * @param context 上下文
     * @param style 样式
     * @param onItemClickListener 监听
     */
    public AlertView(String title, String msg, String cancel, String[] destructive, String[] others, Context context, Style style, OnItemClickListener onItemClickListener){
        this.context = context;
        if(style != null) {
            this.style = style;
        }
        this.onItemClickListener = onItemClickListener;

        initData(title, msg, cancel, destructive, others);
        initViews();
        init();
        initEvents();
        //默认可取消
        setCancelable(true);
    }

    /**
     * 获取数据
     */
    private void initData(String title, String msg, String cancel, String[] destructive, String[] others) {
        this.title = title;
        this.msg = msg;
        if (destructive != null){
            this.mDestructive = Arrays.asList(destructive);
            this.mDatas.addAll(mDestructive);
        }
        if (others != null){
            List<String> mOthers = Arrays.asList(others);
            this.mDatas.addAll(mOthers);
        }
        if (cancel != null){
            this.cancel = cancel;
            if(style == Style.Alert && mDatas.size() < HORIZONTAL_BUTTONS_MAXCOUNT){
                this.mDatas.add(0,cancel);
            }
        }
    }

    /**
     * 初始化对话框布局，中间布局和底部布局两种样式
     */
    private void initViews() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        decorView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        rootView = (ViewGroup) layoutInflater.inflate(R.layout.alert_like_ios_main, decorView, false);
        rootId = (int) System.currentTimeMillis();
        rootView.setId(rootId);
        rootView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));
        contentContainer = rootView.findViewById(R.id.content_container);
        int margin_alert_left_right;
        switch (style){
            case ActionSheet:
                params.gravity = Gravity.BOTTOM;
                margin_alert_left_right = context.getResources().getDimensionPixelSize(R.dimen.alert_like_ios_sheet_left_right);
                params.setMargins(margin_alert_left_right,0,margin_alert_left_right,margin_alert_left_right);
                contentContainer.setLayoutParams(params);
                gravity = Gravity.BOTTOM;
                initActionSheetViews(layoutInflater);
                break;
            case Alert:
                params.gravity = Gravity.CENTER;
                margin_alert_left_right = context.getResources().getDimensionPixelSize(R.dimen.alert_like_ios_alert_left_right);
                params.setMargins(margin_alert_left_right,0,margin_alert_left_right,0);
                contentContainer.setLayoutParams(params);
                gravity = Gravity.CENTER;
                initAlertViews(layoutInflater);
                break;
        }
    }

    /**
     * 初始化标题和内容
     */
    private void initHeaderView(ViewGroup viewGroup){
        //标题和消息
        TextView tvAlertTitle = viewGroup.findViewById(R.id.tv_title);
        TextView tvAlertMsg = viewGroup.findViewById(R.id.tv_msg);
        if(title != null && !title.equals("")) {
            tvAlertTitle.setText(title);
            if (style == Style.Alert && (msg == null || msg.equals(""))) {
                tvAlertTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.alert_like_ios_alert_txt_header_size_when_msg_null));
            }
        }else{
            tvAlertTitle.setVisibility(View.GONE);
        }
        if(msg != null && !msg.equals("")) {
            tvAlertMsg.setText(msg);
        }else{
            tvAlertMsg.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化竖直排列的按钮
     */
    private void initListView(){
        ListView alertButtonListView = contentContainer.findViewById(R.id.list_buttons);
        //把cancel作为footerView
        if(cancel != null && style == Style.Alert){
            View itemView = LayoutInflater.from(context).inflate(R.layout.alert_like_ios_button, null);
            TextView tvAlert = itemView.findViewById(R.id.tv_button);
            tvAlert.setText(cancel);
            tvAlert.setClickable(true);
            tvAlert.setTypeface(Typeface.DEFAULT_BOLD);
            tvAlert.setTextColor(ContextCompat.getColor(context, R.color.alert_like_ios_txt_cancel));
            tvAlert.setBackgroundResource(R.drawable.alert_like_ios_bg_button_bottom);
            tvAlert.setOnClickListener(new OnTextClickListener(CANCELPOSITION));
            alertButtonListView.addFooterView(itemView);
        }
        AlertViewAdapter adapter = new AlertViewAdapter(mDatas,mDestructive);
        alertButtonListView.setAdapter(adapter);
        alertButtonListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(onItemClickListener != null) {
                    onItemClickListener.onItemClick(AlertView.this,position);
                }
                dismiss();
            }
        });
    }

    /**
     * 初始化底部弹出布局
     */
    private void initActionSheetViews(LayoutInflater layoutInflater) {
        ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.alert_like_ios_sheet,contentContainer);
        initHeaderView(viewGroup);
        initListView();
        TextView tvAlertCancel = contentContainer.findViewById(R.id.tv_cancel);
        if(cancel != null && !cancel.equals("")){
            tvAlertCancel.setVisibility(View.VISIBLE);
            tvAlertCancel.setText(cancel);
        }
        tvAlertCancel.setOnClickListener(new OnTextClickListener(CANCELPOSITION));
    }

    /**
     * 初始化中间弹出布局
     */
    private void initAlertViews(LayoutInflater layoutInflater) {
        ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.alert_like_ios_alert, contentContainer);
        initHeaderView(viewGroup);
        int position = 0;
        //如果总数据小于等于HORIZONTAL_BUTTONS_MAXCOUNT，则是横向button
        if(mDatas.size()<=HORIZONTAL_BUTTONS_MAXCOUNT){
            ViewStub viewStub = contentContainer.findViewById(R.id.view_horizontal);
            viewStub.inflate();
            LinearLayout loAlertButtons = contentContainer.findViewById(R.id.lay_buttons);
            for (int i = 0; i < mDatas.size(); i ++) {
                //如果不是第一个按钮
                if (i != 0){
                    //添加上按钮之间的分割线
                    View divider = new View(context);
                    divider.setBackgroundColor(ContextCompat.getColor(context, R.color.alert_like_ios_divider));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)context.getResources().getDimension(R.dimen.alert_like_ios_divider), LinearLayout.LayoutParams.MATCH_PARENT);
                    loAlertButtons.addView(divider,params);
                }
                View itemView = LayoutInflater.from(context).inflate(R.layout.alert_like_ios_button, null);
                TextView tvAlert = itemView.findViewById(R.id.tv_button);
                tvAlert.setClickable(true);

                //设置点击效果
                if(mDatas.size() == 1) {
                    tvAlert.setBackgroundResource(R.drawable.alert_like_ios_bg_button_bottom);
                } else if(i == 0) {//设置最左边的按钮效果
                    tvAlert.setBackgroundResource(R.drawable.alert_like_ios_bg_button_left);
                } else if(i == mDatas.size() - 1) {//设置最右边的按钮效果
                    tvAlert.setBackgroundResource(R.drawable.alert_like_ios_bg_button_right);
                }
                String data = mDatas.get(i);
                tvAlert.setText(data);

                if (data == cancel){//取消按钮的样式，使用==而不是equals确保是取消按钮
                    tvAlert.setTypeface(Typeface.DEFAULT_BOLD);
                    tvAlert.setTextColor(ContextCompat.getColor(context, R.color.alert_like_ios_txt_cancel));
                    tvAlert.setOnClickListener(new OnTextClickListener(CANCELPOSITION));
                    position = position - 1;
                } else if (mDestructive!= null && mDestructive.contains(data)){//消极项字体颜色
                    tvAlert.setTextColor(ContextCompat.getColor(context, R.color.alert_like_ios_txt_destructive));
                }

                tvAlert.setOnClickListener(new OnTextClickListener(position));
                position++;
                loAlertButtons.addView(itemView,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            }
        } else{
            ViewStub viewStub = contentContainer.findViewById(R.id.view_vertical);
            viewStub.inflate();
            initListView();
        }
    }

    /**
     * 划入动画
     */
    private Animation getInAnimation() {
        int res = AlertAnimateUtil.getAnimationResource(this.gravity, true);
        return AnimationUtils.loadAnimation(context, res);
    }

    /**
     * 划出动画
     */
    private Animation getOutAnimation() {
        int res = AlertAnimateUtil.getAnimationResource(this.gravity, false);
        return AnimationUtils.loadAnimation(context, res);
    }

    /**
     * 初始化动画数据
     */
    private void init() {
        inAnim = getInAnimation();
        outAnim = getOutAnimation();
    }

    private void initEvents() {
    }

//    public AlertView addExtView(View extView){
//        loAlertHeader.addView(extView);
//        return this;
//    }

    /**
     * show的时候调用
     */
    private void onAttached(View view) {
        decorView.addView(view);
        contentContainer.startAnimation(inAnim);
    }
    /**
     * 添加这个View到Activity的根视图
     */
    public void show() {
        if (isShowing()) {
            return;
        }
        onAttached(rootView);
    }
    /**
     * 检测该View是不是已经添加到根视图
     */
    private boolean isShowing() {
        View view = decorView.findViewById(rootId);
        return view != null;
    }

    private void dismiss() {
        if (isDismissing) {
            return;
        }

        //消失动画
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                decorView.post(new Runnable() {
                    @Override
                    public void run() {
                        //从activity根视图移除
                        decorView.removeView(rootView);
                        isDismissing = false;
                        if (onDismissListener != null) {
                            onDismissListener.onDismiss(AlertView.this);
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        contentContainer.startAnimation(outAnim);
        isDismissing = true;
    }

    /**
     * 设置是否可取消
     */
    public AlertView setCancelable(boolean isCancelable) {
        View view = rootView;
        if (isCancelable) {
            view.setOnTouchListener(onCancelableTouchListener);
        } else{
            view.setOnTouchListener(null);
        }
        return this;
    }

    /**
     * 定义取消事件
     */
    public AlertView setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    /**
     * 点击按钮监听
     */
    private class OnTextClickListener implements View.OnClickListener{
        private int position;
        public OnTextClickListener(int position){
            this.position = position;
        }
        @Override
        public void onClick(View view) {
            if(onItemClickListener != null){
                onItemClickListener.onItemClick(AlertView.this,position);
            }
            dismiss();
        }
    }

    /**
     * Called when the user touch on black overlay in order to dismiss the dialog
     */
    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dismiss();
            }
            return false;
        }
    };
}
