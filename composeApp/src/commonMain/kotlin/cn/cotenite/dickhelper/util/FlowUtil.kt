package cn.cotenite.dickhelper.util

import kotlinx.coroutines.flow.MutableStateFlow

fun <T> MutableStateFlow<T>.update(function: (T) -> T) {
    this.value = function(this.value)
}
