package com.example.historyquiz.ui.game.game_list

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.historyquiz.R
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.cards.add_card_list.AddCardListFragment
import com.example.historyquiz.ui.game.add_game.AddGameFragment
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.TAG_LOG
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.fragment_test_list.*
import java.util.ArrayList
import javax.inject.Inject

class GameListFragment : BaseFragment(), GameListView {

    lateinit var adapter: GameAdapter

    private var isLoaded = false

    @Inject
    lateinit var presenter: GameListPresenter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_test_list, container, false)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        initRecycler()
        presenter.loadOfficialTests()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViews(view: View) {

    }


    private fun initRecycler() {
        adapter = GameAdapter(ArrayList<Lobby>())
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

    override fun changeDataSet(users: List<Lobby>) {
        adapter!!.changeDataSet(users)
    }

    override fun loadOfficialTests() {
        Log.d(Const.TAG_LOG, "load requests")
        presenter!!.loadOfficialTests()
    }

    override fun setNotLoading() {
//        isLoading = false
    }

    override fun showDetails(lobby: Lobby) {
        Log.d(Const.TAG_LOG,"show test act")
        presenter.findGame(lobby)

    }

    override fun onGameFinded(){
        Log.d(TAG_LOG, "start usual game")
//            hideProgressDialog()
//        PlayGameActivity.start(this)
    }

    override fun hideProgressDialog() {
        showSnackBar("Противник не принял приглашение")
        /*if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
        isClickable = true*/
    }

    override fun onBotGameFinded() {
        Log.d(TAG_LOG,"start bot")
//        BotGameActivity.start(this)
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