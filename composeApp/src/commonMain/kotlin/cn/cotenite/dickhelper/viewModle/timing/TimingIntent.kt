package cn.cotenite.dickhelper.viewModle.timing

sealed class TimingIntent {
    // 模式选择
    data object SelectNormalMode : TimingIntent()
    data object SelectChallengeMode : TimingIntent()

    // 计时器控制
    data object StopTimer : TimingIntent()
    data object ContinueTimer : TimingIntent()
    data object EndTimer : TimingIntent()

    data class SaveNotes(val notes: String, val sessionDuration: Long, val sessionMode: String) : TimingIntent()
    data object CancelNotesDialog : TimingIntent()

}