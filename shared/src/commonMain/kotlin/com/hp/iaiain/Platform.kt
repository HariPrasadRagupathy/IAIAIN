package com.hp.iaiain

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform