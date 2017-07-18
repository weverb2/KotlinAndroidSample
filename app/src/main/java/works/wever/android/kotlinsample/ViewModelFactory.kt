package works.wever.android.kotlinsample

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val appComponent = (application as MainApplication).component
        if (modelClass == Presenter::class.java) {
            return appComponent.presenter() as T
        }
        return modelClass.newInstance()
    }
}