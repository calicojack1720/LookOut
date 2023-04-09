package com.example.lologin

interface AlarmScheduler {
    fun schedule(item: AlarmItem, daysOfWeek: List<Int>)
    fun cancel(item: AlarmItem)

}