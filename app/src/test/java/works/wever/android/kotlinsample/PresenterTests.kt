package works.wever.android.kotlinsample

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PresenterTests {

    lateinit var presenter: Presenter
    lateinit var githubService: GithubService
    lateinit var testObserver: TestObserver<Presenter.ViewModelState>

    @Before
    fun setup() {
        TestUtils.setupRxTest()
        githubService = mock()
        presenter = Presenter(githubService)
        testObserver = TestObserver<Presenter.ViewModelState>()
    }

    @Test
    fun testStateFormComplete() {
        val formIncomplete = Presenter.ViewModelState()
        Assert.assertFalse(formIncomplete.isFormComplete())

        val formComplete = Presenter.ViewModelState("validUsername")
        Assert.assertTrue(formComplete.isFormComplete())
    }

    @Test
    fun testUpdateUsername() {
        val initialState = presenter.state
        val expectedState = initialState.copy(username = "user")

        presenter.stateSubject.subscribe(testObserver)

        presenter.onUsernameChanged("user")

        testObserver.assertValues(initialState, expectedState)
    }

    @Test
    fun testGetReposSuccess() {
        whenever(githubService.fetchRepositories("user", sortMode = RepositorySortMode.FULL_NAME)).thenReturn(Single.just(listOf(Repository())))
        val initialState = presenter.state.copy(username = "user")
        val loadingState = initialState.copy(loadingState = LoadingState.Loading())
        val finalState = initialState.copy(repositories = listOf(Repository()))

        presenter.onUsernameChanged("user")

        presenter.stateSubject.subscribe(testObserver)

        presenter.onGetReposTapped()

        testObserver.assertValues(initialState, loadingState, finalState)
    }
}
