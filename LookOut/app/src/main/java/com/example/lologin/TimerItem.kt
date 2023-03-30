package com.example.lologin

import java.time.LocalDateTime

data class TimerItem (
    /*val itemSeconds: Int,
    val itemMinutes: Int,
    val itemHours: Int,
    val itemName: String
     */
    val time: LocalDateTime,
    val message: String
)