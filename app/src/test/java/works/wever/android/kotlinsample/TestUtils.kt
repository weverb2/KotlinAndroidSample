package works.wever.android.kotlinsample

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers

class TestUtils {

    companion object {
        fun setupRxTest() {
            RxJavaPlugins.reset()
            RxAndroidPlugins.reset()

            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        }
    }
}