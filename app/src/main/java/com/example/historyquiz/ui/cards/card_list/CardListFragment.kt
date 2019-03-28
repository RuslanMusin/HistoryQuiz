package com.example.historyquiz.ui.cards.card_list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.card.AbstractCard
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.cards.card_item.CardFragment
import com.example.historyquiz.ui.navigation.NavigationView
import com.example.historyquiz.ui.tests.add_test.main.AddMainTestFragment
import com.example.historyquiz.ui.tests.test_item.main.TestFragment
import com.example.historyquiz.ui.tests.test_list.TestAdapter
import com.example.historyquiz.ui.tests.test_list.TestListPresenter
import com.example.historyquiz.ui.tests.test_list.TestListView
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.USER_ID
import com.google.gson.Gson
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.fragment_test_list.*
import java.util.ArrayList
import javax.inject.Inject

class CardListFragment : BaseFragment(), CardListView, View.OnClickListener {

    @Inject
    lateinit var gson: Gson

    lateinit var userId: String
    private lateinit var adapter: CardAdapter

    lateinit var skills: MutableList<Card>

    @InjectPresenter
    lateinit var presenter: CardListPresenter

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
        initViews()
        arguments?.let {
            userId = it.getString(USER_ID)
        }
        presenter.loadUserCards(userId)
    }

    private fun initViews() {
        setToolbar()
        initRecycler()
        setListeners()
    }

    private fun setToolbar() {
        setActionBar(toolbar)
        setActionBarTitle(R.string.menu_cards)
    }

    private fun setListeners() {
    }

    override fun setNotLoading() {

    }

    override fun showListLoading(disposable: Disposable) {
//        pb_list.visibility = View.VISIBLE
    }

    override fun hideListLoading() {
        pg_list.visibility = View.GONE
    }

    override fun loadNextElements(i: Int) {
    }


    override fun changeDataSet(cards: List<AbstractCard>) {
        /*val abstractCards: MutableList<AbstractCard> = ArrayList()
        for(card in cards) {
            abstractCards.add(card.abstractCard)
        }*/
        adapter!!.changeDataSet(cards)
        hideListLoading()
        hideLoading()
        Log.d(Const.TAG_LOG, "test loaded")
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
        menu?.let { setSearchMenuItem(it) }
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
