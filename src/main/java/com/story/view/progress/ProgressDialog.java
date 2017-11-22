package com.story.view.progress;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.story.view.R;

/**
 * 类名称：ProgressDialog
 * 类描述：自定义进度框，用于网络请求等延时操作的等待对话框
 * 创建人：story
 * 创建时间：2017/11/21 16:46
 */

public class ProgressDialog extends Dialog {
    private Context context;
    private TextView textView;//加载中提示文字
    private TextView timeTv;//倒计时数字
    private TimeCount timeCount;//倒计时
    private boolean couldCancel = false;//是否可以取消，默认不可取消

    @SuppressLint("InflateParams")
    public ProgressDialog(Context context) {
        super(context, R.style.ProgressDialogStyle);
        this.context = context;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(context).inflate(R.layout.progress_progress_dialog_main,null);
        textView = view.findViewById(R.id.tv_progress_show);
        timeTv = view.findViewById(R.id.tv_progress_time);
        setContentView(view,layoutParams);
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    /**
     * 显示对话框
     * @param message 提示内容
     */
    public void show(String message) {
        textView.setText(message == null ? context.getResources().getString(R.string.progress_progress_dialog_loading) : message);
        if (timeCount != null) {
            timeCount.cancel();
            timeTv.setVisibility(View.GONE);
        }
        super.show();
    }

    /**
     * 显示对话框和倒计时
     * @param message 提示内容
     * @param time 倒计时(秒)
     */
    public void show(String message, int time) {
        textView.setText(message == null ? context.getResources().getString(R.string.progress_progress_dialog_loading) : message);
        timeCount = new TimeCount(time * 1000, 1000);
        timeTv.setVisibility(View.VISIBLE);
        timeTv.setText(String.format(context.getResources().getString(R.string.progress_progress_dialog_time), time));
        timeCount.start();
        super.show();
    }

    /**
     * 显示对话框和倒计时
     * @param time 倒计时(秒)
     */
    public void show(int time) {
        timeCount = new TimeCount(time * 1000, 1000);
        timeTv.setVisibility(View.VISIBLE);
        timeTv.setText(String.format(context.getResources().getString(R.string.progress_progress_dialog_time), time));
        timeCount.start();
        super.show();
    }

    @Override
    public void cancel() {
        super.cancel();
        if (timeCount != null) {
            timeCount.cancel();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (timeCount != null) {
            timeCount.cancel();
        }
    }

    public void delayCancel() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cancel();
            }
        }, 1000);
    }

    /**
     * 是否可以取消
     */
    public void setCouldCancel(boolean could) {
        this.couldCancel = could;
    }


    /**
     * 自定义倒计时
     */
    private class TimeCount extends CountTimerStory {
        /**
         * 计时结束
         */
        @Override
        public void onFinish() {
            dismiss();
        }

        /**
         * 计时过程
         * @param millisUntilFinished 完成时间
         */
        @Override
        public void onTick(long millisUntilFinished) {
            timeTv.setText(String.format(context.getResources().getString(R.string.progress_progress_dialog_time), millisUntilFinished/1000));
        }

        /**
         * @param millisInFuture 总时长
         * @param countDownInterval 计时间隔
         */
        TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        return ((keyCode == KeyEvent.KEYCODE_BACK && !couldCancel) || super.onKeyDown(keyCode, event));
    }
}
