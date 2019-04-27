package com.example.historyquiz.ui.statists.tab_fragment.game_stats

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.epoch.UserEpoch
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.statists.StatListPresenter
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import javax.inject.Inject
import javax.inject.Provider

class GameStatsFragment : BaseFragment(), GameStatsView {

    private lateinit var adapter: GameStatsAdapter

    lateinit var themes: MutableList<UserEpoch>
    lateinit var userId: String

    @InjectPresenter
    lateinit var presenter: GameStatsPresenter
    @Inject
    lateinit var presenterProvider: Provider<GameStatsPresenter>
    @ProvidePresenter
    fun providePresenter(): GameStatsPresenter = presenterProvider.get()

    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = GameStatsFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment {
            val fragment = GameStatsFragment()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        presenter.loadStats()
    }

    override fun showStats(themes: List<UserEpoch>) {
        this.themes = themes.toMutableList()
        changeDataSet(this.themes)
    }

    private fun initViews() {
        initRecycler()
        setListeners()
    }

    private fun setListeners() {
    }

    override fun setNotLoading() {

    }

    override fun showListLoading(disposable: Disposable) {
        pg_list.visibility = View.VISIBLE
    }

    override fun hideListLoading() {
        pg_list.visibility = View.GONE
    }

    override fun loadNextElements(i: Int) {
    }


    override fun changeDataSet(tests: List<UserEpoch>) {
        adapter.changeDataSet(tests)
        hideLoading()
    }

    override fun handleError(throwable: Throwable) {

    }

    override fun reloadList() {
        presenter.loadStats()
    }

    private fun initRecycler() {
        adapter = GameStatsAdapter(ArrayList())
        val manager = LinearLayoutManager(activity as Activity)
        rv_list.layoutManager = manager
        rv_list.setEmptyView(tv_empty)
        adapter.attachToRecyclerView(rv_list)
        adapter.setOnItemClickListener(this)
    }

    override fun onItemClick(item: UserEpoch) {

    }
}