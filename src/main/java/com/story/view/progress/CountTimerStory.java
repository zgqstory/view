package com.story.view.progress;

/**
 * 类名称：CountTimerStory
 * 类描述：自定义倒计时基础类，防止系统倒计时类不准确问题(此倒计时要求onTick不能做延时操作)
 * 创建人：story
 * 创建时间：2017/11/21 17:41
 */

public abstract class CountTimerStory {

    static final int MSG = 1;

    private final long mMillisInFuture;//计时总时间
    private final long mCountdownInterval;//每次变化时间
    private boolean mCancelled = false;//计时器开始标志
    private TimerHandler mHandler;

    CountTimerStory(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
        mHandler = new TimerHandler(this);
    }

    /**
     * 取消倒计时
     */
    synchronized final void cancel() {
        mCancelled = true;
        mHandler.removeMessages(MSG);
    }

    /**
     * 开始倒计时
     */
    synchronized final void start() {
        mCancelled = false;
        if (mMillisInFuture <= 0) {
            onFinish();
        } else {
            mHandler.sendMessage(mHandler.obtainMessage(MSG, mMillisInFuture));
        }
    }

    public abstract void onTick(long millisUntilFinished);

    public abstract void onFinish();

    long getmCountdownInterval() {
        return mCountdownInterval;
    }

    boolean ismCancelled() {
        return mCancelled;
    }
}
