package works.wever.android.kotlinsample

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnItemSelected
import butterknife.OnTextChanged
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card_repository.view.*
import javax.inject.Inject
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class MainActivity : AppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    val presenter: Presenter by lazy { ViewModelProviders.of(this, viewModelFactory).get(Presenter::class.java) }
    val adapter by lazy { RepositoryAdapter() }
    var snackbar: Snackbar? = null
    val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MainApplication).component.inject(this)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        disposables.add(presenter.stateSubject.subscribe({ render(it) }))

        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)

        val spinnerAdapter = ArrayAdapter<RepositorySortMode>(this, android.R.layout.simple_spinner_item, RepositorySortMode.values())
        spinner.adapter = spinnerAdapter
    }

    @OnItemSelected(R.id.spinner)
    fun onSpinnerItemSelected(spinner: Spinner, position: Int) {
        presenter.onSortModeChanged(position)
    }

    @OnTextChanged(value = R.id.username, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    fun onUsernameChanged(editable: Editable) {
        presenter.onUsernameChanged(editable.toString())
    }

    @OnClick(R.id.getReposButton)
    fun onGetReposClicked() {
        presenter.onGetReposTapped()
        root.hideKeyboard()
    }

    fun render(state: Presenter.ViewModelState) {
        adapter.repositories = state.repositories

        when (state.loadingState) {
            is LoadingState.Idle -> {
                progressBar.visibility = View.GONE
                snackbar?.dismiss()
            }
            is LoadingState.Loading -> {
                progressBar.visibility = View.VISIBLE
                snackbar?.dismiss()
            }
            is LoadingState.Error -> {
                progressBar.visibility = View.GONE
                snackbar = Snackbar.make(root, state.loadingState.message, Snackbar.LENGTH_INDEFINITE)
                snackbar?.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    inner class RepositoryAdapter : RecyclerView.Adapter<RepositoryViewHolder>() {
        var repositories: List<Repository> by Delegates.observable(emptyList()) {
            _: KProperty<*>, _: List<Repository>, _: List<Repository> ->
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = repositories.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.card_repository, parent, false)
            return RepositoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: RepositoryViewHolder?, position: Int) {
            val repo = repositories[position]
            holder?.bindRepository(repo)
        }
    }

    inner class RepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindRepository(repo: Repository) {
            itemView.title.text = repo.name
            itemView.description.text = repo.description
        }
    }

}
