package com.example.historyquiz.ui.game.game_list

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.game.add_game.AddGameFragment
import com.example.historyquiz.ui.game.bot_play.BotGameFragment
import com.example.historyquiz.ui.game.play.PlayGameFragment
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.TAG_LOG
import kotlinx.android.synthetic.main.dialog_help.*
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.fragment_test_list.*
import kotlinx.android.synthetic.main.toolbar_help.*
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Provider

class GameListFragment : BaseFragment(), GameListView {

    var games: List<Lobby> = ArrayList()

    lateinit var adapter: GameAdapter

    private var isLoaded = false

    @InjectPresenter
    lateinit var presenter: GameListPresenter
    @Inject
    lateinit var presenterProvider: Provider<GameListPresenter>
    @ProvidePresenter
    fun providePresenter(): GameListPresenter = presenterProvider.get()

    var isClickable: Boolean = true
    lateinit var helpDialog: MaterialDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_test_list, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        presenter.loadOfficialTests()
        setStatus(Const.ONLINE_STATUS)
        setWaitStatus(true)
    }

    private fun initViews(view: View) {
        setToolbar()
        initRecycler()
        isClickable = true

    }

    private fun setToolbar() {
        setActionBar(toolbar)
        setActionBarTitle(R.string.menu_game)
        toolbar.setNavigationIcon(null)
    }

    private fun initRecycler() {
        adapter = GameAdapter(this, ArrayList<Lobby>())
        val manager = LinearLayoutManager(this.activity)
        rv_list!!.layoutManager = manager
        rv_list!!.setEmptyView(tv_empty)
        adapter!!.attachToRecyclerView(rv_list!!)
        adapter!!.setOnItemClickListener(this)
        rv_list!!.adapter = adapter

        rv_list?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv_list: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv_list, dx, dy);
                if (dy > 0 && floating_button.getVisibility() == View.VISIBLE) {
                    floating_button.hide();
                } else if (dy < 0 && floating_button.getVisibility() != View.VISIBLE) {
                    floating_button.show();
                }
            }
        })

        floating_button.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                Log.d(Const.TAG_LOG, "act float btn")
                MaterialDialog.Builder(activity as Activity)
                    .title(R.string.create_new_game)
                    .content(R.string.old_game_will_be_deleted)
                    .positiveText("Создать")
                    .onPositive(object : MaterialDialog.SingleButtonCallback {
                        override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                            AppHelper.currentUser?.let { it.lobbyId?.let { it1 ->
                                adapter.removeItemById(it1)
                            } }
                            val fragment = AddGameFragment.newInstance()
                            pushFragments(fragment, true)
                        }

                    })
                    .negativeText("Отмена")
                    .onNegative{ dialog, action -> dialog.cancel()}
                    .show()


            }
        })
    }

    override fun removeLobby(lobbyId: String) {
        presenter.removeLobby(lobbyId)
    }

    override fun handleError(error: Throwable) {
        Log.d(Const.TAG_LOG, "error = " + error.message)
        error.printStackTrace()
    }

    override fun changeDataSet(tests: List<Lobby>) {
        Log.d(TAG_LOG, "changeDataSet")
        adapter!!.changeDataSet(tests)
        hideLoading()
        hideListLoading()
    }

    override fun showGames(list: List<Lobby>) {
        this.games = list
        changeDataSet(list)
        adapter.attachToRecyclerView(rv_list)
    }

    override fun setNotLoading() {
//        isLoading = false
    }

    override fun showDetails(lobby: Lobby) {
        Log.d(Const.TAG_LOG,"show test act")
        presenter.findGame(lobby)

    }

    override fun hideListLoading() {
        pg_list.visibility = View.GONE
    }

    override fun showListLoading() {
        pg_list.visibility = View.GONE
    }

    override fun onItemClick(item: Lobby) {
        Log.d(TAG_LOG, "onItemClick clickable = $isClickable")
        if(isClickable) {
            setWaitStatus(false)
            presenter!!.onItemClick(item)
            isClickable = false
        }
    }

    override fun onGameFinded(){
        Log.d(TAG_LOG, "start usual game")
        val fragment = PlayGameFragment.newInstance()
        pushFragments(fragment, true)
    }

    override fun hideProgressDialog() {
        super.hideProgressDialog()
        isClickable = true
    }

    override fun setItemClickable(isClickable: Boolean) {
        this.isClickable = isClickable
    }

    override fun onBotGameFinded() {
        Log.d(TAG_LOG,"start bot")
        val fragment = BotGameFragment.newInstance()
        pushFragments(fragment, true)
//        BotGameActivity.start(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.game_list_menu, menu)
        helpDialog = MaterialDialog.Builder(this.activity!!)
            .customView(R.layout.dialog_help, false)
            .onNeutral { dialog, which ->
                dialog.cancel()
            }
            .build()

        helpDialog.btn_cancel.setOnClickListener{ helpDialog.cancel() }
        helpDialog.tv_help_content.text = getString(R.string.game_text)
        menu?.let {
            val helpItem = menu.findItem(R.id.action_help)
            helpItem.setOnMenuItemClickListener {
                helpDialog.show()
                true
            }

            val searchItem = menu.findItem(R.id.action_search)

            var searchView: SearchView? = null
            if (searchItem != null) {
                searchView = searchItem.actionView as SearchView
            }
            if (searchView != null) {
                val finalSearchView = searchView
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String): Boolean {
                        if (!finalSearchView.isIconified) {
                            finalSearchView.isIconified = true
                        }
                        searchItem!!.collapseActionView()
                        return false
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
//                        presenter.loadOfficialTestsByQuery(newText)
                        findFromList(newText)
                        return false
                    }
                })
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun findFromList(query: String) {
        val pattern: Pattern = Pattern.compile("(\\w*\\s+)*${query.toLowerCase()}.*")
        val list: MutableList<Lobby> = java.util.ArrayList()
        for(skill in games) {
            if (pattern.matcher(skill.title?.toLowerCase()).matches()) {
                list.add(skill)
            }
        }

        Log.d(TAG_LOG, "list.size = ${list.size}")
        changeDataSet(list)
    }

    override fun loadNextElements(i: Int) {
//        presenter!!.loadNextElements(i)
    }

    companion object {

        fun newInstance(): Fragment {
            val fragment = GameListFragment()
            return fragment
        }
    }

}