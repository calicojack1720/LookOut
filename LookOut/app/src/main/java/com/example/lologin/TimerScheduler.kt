package com.example.lologin

interface TimerScheduler {
    fun schedule(item: TimerItem)
    fun cancel(item: TimerItem)
}