package works.wever.android.kotlinsample

sealed class LoadingState {
    data class Loading(val message: String = "") : LoadingState()
    data class Idle(val message: String = "") : LoadingState()
    data class Error(val message: String) : LoadingState()
}