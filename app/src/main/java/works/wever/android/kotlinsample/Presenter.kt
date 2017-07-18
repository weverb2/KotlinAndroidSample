package works.wever.android.kotlinsample

import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class Presenter @Inject constructor(val githubService: GithubService) : ViewModel() {

    val stateSubject: BehaviorSubject<ViewModelState> = BehaviorSubject.createDefault(ViewModelState())

    var state: ViewModelState
        get() = stateSubject.value
        set(value) = stateSubject.onNext(value)

    val disposables = CompositeDisposable()

    data class ViewModelState(
            val username: String = "",
            val repositories: List<Repository> = emptyList(),
            val loadingState: LoadingState = LoadingState.Idle(),
            val sortMode: RepositorySortMode = RepositorySortMode.FULL_NAME) {

        fun isFormComplete() = username.isNotBlank()
    }

    fun onGetReposTapped() {
        if (state.isFormComplete()) {
            state = state.copy(loadingState = LoadingState.Loading())
            disposables.add(githubService.fetchRepositories(username = state.username, sortMode = state.sortMode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ onReposReceived(it) }, { onError(it) }))
        } else {
            state = state.copy(loadingState = LoadingState.Error("Please enter a username"))
        }
    }

    fun onReposReceived(repos: List<Repository>) {
        state = state.copy(repositories = repos, loadingState = LoadingState.Idle())
    }

    fun onError(throwable: Throwable) {
        state = state.copy(loadingState = LoadingState.Error("Unable to Load Repositories for ${state.username}"))
    }

    fun onUsernameChanged(username: String) {
        state = state.copy(username = username, loadingState = LoadingState.Idle())
    }

    fun onSortModeChanged(position: Int) {
        state = state.copy(sortMode = RepositorySortMode.values()[position])
    }
}