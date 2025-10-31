package cz.eventboard.eventboard_scanner

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

