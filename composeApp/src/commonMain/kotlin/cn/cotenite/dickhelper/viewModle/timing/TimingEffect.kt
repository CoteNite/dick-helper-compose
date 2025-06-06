package cn.cotenite.dickhelper.viewModle.timing

sealed class TimingEffect {
    data class ShowNotesDialog(val sessionDuration: Long, val sessionMode: RecordMode) : TimingEffect()
}