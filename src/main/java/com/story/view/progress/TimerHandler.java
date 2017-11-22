package com.story.view.progress;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 类名称：TimerHandler
 * 类描述：定时器Handler
 * 创建人：story
 * 创建时间：2017/11/21 18:46
 */

public class TimerHandler extends Handler {

    private final WeakReference<CountTimerStory> storyTimerWeak;

    TimerHandler(CountTimerStory countTimerStory) {
        storyTimerWeak = new WeakReference<>(countTimerStory);
    }

    @Override
    public void handleMessage(Message msg) {
        CountTimerStory storyTimer = storyTimerWeak.get();
        synchronized (storyTimerWeak) {
            if (storyTimer.ismCancelled()) {
                return;
            }
            final long millisRest = (long) msg.obj;
            if (millisRest < storyTimer.getmCountdownInterval() && millisRest > 0) {
                // 剩余时间小于mCountdownInterval，不执行onTick延迟millisLeft
                sendMessageDelayed(obtainMessage(CountTimerStory.MSG, millisRest - storyTimer.getmCountdownInterval()), millisRest);
            } else if (millisRest <= 0) {
                // 剩余时间小于等于0，结束
                storyTimer.onFinish();
            } else {
                //剩余时间大于mCountdownInterval，执行一次onTick然后延迟mCountdownInterval
                storyTimer.onTick(millisRest);
                sendMessageDelayed(obtainMessage(CountTimerStory.MSG, millisRest - storyTimer.getmCountdownInterval()), storyTimer.getmCountdownInterval());
            }
        }
    }
}
