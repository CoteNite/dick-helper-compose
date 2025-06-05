package cn.cotenite.dickhelper

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform