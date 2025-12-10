package com.hp.iaiain.util

// Expected platform-specific function to get current date/time
expect fun getCurrentSystemDateTime(): SystemDateTime

data class SystemDateTime(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val second: Int
)

