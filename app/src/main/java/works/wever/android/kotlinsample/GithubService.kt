package works.wever.android.kotlinsample

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

open class GithubService(val api: GithubApi) {

    @JvmOverloads
    open fun fetchRepositories(username: String,
                               sortMode: RepositorySortMode? = null): Single<List<Repository>> {
        return api.fetchRepositories(username, sortMode)
    }

}

interface GithubApi {

    @GET("/users/{user}/repos")
    fun fetchRepositories(@Path("user") username: String,
                          @Query("sort") sortMode: RepositorySortMode?): Single<List<Repository>>

}
