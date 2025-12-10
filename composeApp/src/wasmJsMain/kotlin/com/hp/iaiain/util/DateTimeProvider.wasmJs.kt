package com.hp.iaiain.util

@JsFun("() => new Date().getFullYear()")
private external fun jsGetFullYear(): Int

@JsFun("() => new Date().getMonth()")
private external fun jsGetMonth(): Int

@JsFun("() => new Date().getDate()")
private external fun jsGetDate(): Int

@JsFun("() => new Date().getHours()")
private external fun jsGetHours(): Int

@JsFun("() => new Date().getMinutes()")
private external fun jsGetMinutes(): Int

@JsFun("() => new Date().getSeconds()")
private external fun jsGetSeconds(): Int

actual fun getCurrentSystemDateTime(): SystemDateTime {
    return SystemDateTime(
        year = jsGetFullYear(),
        month = jsGetMonth() + 1, // JS months are 0-indexed
        day = jsGetDate(),
        hour = jsGetHours(),
        minute = jsGetMinutes(),
        second = jsGetSeconds()
    )
}

