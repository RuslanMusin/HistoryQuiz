package com.example.historyquiz.ui.cards.card_list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.card.AbstractCard
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.cards.card_item.CardFragment
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.OLD_ONES
import com.example.historyquiz.utils.Const.ONLINE_STATUS
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.TIME_TYPE
import com.example.historyquiz.utils.Const.USER_ID
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.dialog_help.*
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.fragment_test_list.*
import kotlinx.android.synthetic.main.toolbar_help.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class CardListFragment : BaseFragment(), CardListView, View.OnClickListener {

    lateinit var userId: String
    lateinit var type: String
    private lateinit var adapter: CardAdapter

    lateinit var skills: MutableList<Card>

    @InjectPresenter
    lateinit var presenter: CardListPresenter
    @Inject
    lateinit var presenterProvider: Provider<CardListPresenter>
    @ProvidePresenter
    fun providePresenter(): CardListPresenter = presenterProvider.get()

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
        arguments?.let {
            userId = it.getString(USER_ID)
            type = it.getString(TIME_TYPE)
            initViews()
            Log.d(TAG_LOG, "user load cards/userId = $userId")
            presenter.loadUserCards(userId)
            setStatus(Const.ONLINE_STATUS)
            setWaitStatus(true)
        }
    }

    private fun initViews() {
        setToolbar()
        initRecycler()
        setListeners()
    }

    private fun setToolbar() {
        setActionBar(toolbar)
        setActionBarTitle(R.string.menu_cards)
        if(!type.equals(OLD_ONES)) {
            toolbar.setNavigationIcon(null)
        } else {
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        }
    }

    private fun setListeners() {
    }

    override fun setNotLoading() {

    }

    override fun showListLoading() {
//        pb_list.visibility = View.VISIBLE
    }

    override fun hideListLoading() {
        pg_list.visibility = View.GONE
    }

    override fun loadNextElements(i: Int) {
    }


    override fun changeDataSet(cards: List<AbstractCard>) {
        adapter!!.changeDataSet(cards)
        hideListLoading()
        hideLoading()
        Log.d(Const.TAG_LOG, "cards loaded and size = ${cards.size}")
    }

    override fun handleError(throwable: Throwable) {

    }

    private fun initRecycler() {
        floating_button.visibility = View.GONE
        adapter = CardAdapter(ArrayList())
        val manager = LinearLayoutManager(this.activity)
        rv_list.layoutManager = manager
        rv_list.setEmptyView(tv_empty)
        adapter.attachToRecyclerView(rv_list)
        adapter.setOnItemClickListener(this)
        rv_list.adapter = adapter
    }

    override fun onItemClick(item: AbstractCard) {
        val args = Bundle()
        args.putString(Const.ABS_CARD, gson.toJson(item))
        val fragment = CardFragment.newInstance(args)
        pushFragments(fragment, true)
    }

    override fun onClick(v: View) {
        when (v.id) {

            /*  R.id.btn_edit -> editSkills()

              R.id.btn_back -> backFragment()*/

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.search_menu, menu)
        helpDialog = MaterialDialog.Builder(this.activity!!)
            .customView(R.layout.dialog_help, false)
            .onNeutral { dialog, which ->
                dialog.cancel()
            }
            .build()

        helpDialog.btn_cancel.setOnClickListener{ helpDialog.cancel() }
        helpDialog.tv_help_content.text = getString(R.string.card_text)
        menu?.let {
            val helpItem = menu.findItem(R.id.action_help)
            helpItem.setOnMenuItemClickListener {
                helpDialog.show()
                true
            }
            setSearchMenuItem(it)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setSearchMenuItem(menu: Menu) {
        val searchItem = menu.findItem(R.id.action_search)

        val searchView: SearchView = searchItem.actionView as SearchView
        val finalSearchView = searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                if (!finalSearchView.isIconified) {
                    finalSearchView.isIconified = true
                }
                searchItem.collapseActionView()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                presenter.loadCardsByQuery(newText, userId)
//                findFromList(newText)
                return false
            }
        })

    }

    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = CardListFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment {
            val fragment = CardListFragment()
            return fragment
        }
    }
}
