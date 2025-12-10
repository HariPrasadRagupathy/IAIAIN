package com.hp.iaiain.util

import platform.Foundation.*

actual fun getCurrentSystemDateTime(): SystemDateTime {
    val now = NSDate()
    val calendar = NSCalendar.currentCalendar

    // Use the correct NSCalendarUnit flags
    val unitFlags = NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or
                    NSCalendarUnitHour or NSCalendarUnitMinute or NSCalendarUnitSecond

    val components = calendar.components(unitFlags, now)

    return SystemDateTime(
        year = components.year.toInt(),
        month = components.month.toInt(),
        day = components.day.toInt(),
        hour = components.hour.toInt(),
        minute = components.minute.toInt(),
        second = components.second.toInt()
    )
}

