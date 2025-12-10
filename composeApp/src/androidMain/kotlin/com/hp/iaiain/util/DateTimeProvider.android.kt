package com.hp.iaiain.util

import java.time.LocalDateTime

actual fun getCurrentSystemDateTime(): SystemDateTime {
    val now = LocalDateTime.now()
    return SystemDateTime(
        year = now.year,
        month = now.monthValue,
        day = now.dayOfMonth,
        hour = now.hour,
        minute = now.minute,
        second = now.second
    )
}

