package cn.cotenite.dickhelper.viewModle.timing

import javax.swing.text.StyledEditorKit.BoldAction

sealed class TimingState{
    data object Default:TimingState()

    data class Normal(
        val elapsedTime: Long,
        val isRunning: Boolean
    ):TimingState()

    data class Challenge(
        val remainingTime: Long, // 剩余时间 (倒计时)
        val totalDuration: Long, // 挑战总时长
        val isRunning: Boolean
    ):TimingState()

}

enum class RecordMode(val docs: String) {
    NONE("null"),       // 默认/未选择模式
    NORMAL("正常模式"),     // 正向计时
    CHALLENGE("挑战模式")   // 挑战计时 (倒计时)
}
