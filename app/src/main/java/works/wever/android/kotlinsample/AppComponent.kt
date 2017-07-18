package works.wever.android.kotlinsample

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    fun inject(activity: MainActivity)

    fun presenter(): Presenter

}