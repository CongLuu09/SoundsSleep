package com.kenhtao.site.soundssleep.Timer;

public interface TimerCallBack {
    void onTimerSet(long durationMillis);
    void onTimerCancelled();
    default void onTimerFinished() {}

}
