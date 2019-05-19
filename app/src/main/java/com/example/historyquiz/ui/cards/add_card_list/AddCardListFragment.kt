package com.example.historyquiz.ui.cards.add_card_list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.SeekBar
import com.annimon.stream.Stream
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.wiki_api.opensearch.Item
import com.example.historyquiz.model.wiki_api.query.Page
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.cards.add_card.AddCardFragment
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.ADD_CARD_CODE
import com.example.historyquiz.utils.Const.CARD_ITEM
import com.example.historyquiz.utils.Const.ITEM_ITEM
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.gson
import com.jaredrummler.materialspinner.MaterialSpinner
import kotlinx.android.synthetic.main.fragment_search_card.*
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.layout_add_card.*
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Provider
import kotlin.random.Random

class AddCardListFragment: BaseFragment(), AddCardListView {

    private var card: Card? = null

    @InjectPresenter
    lateinit var presenter: AddCardListPresenter
    @Inject
    lateinit var presenterProvider: Provider<AddCardListPresenter>
    @ProvidePresenter
    fun providePresenter(): AddCardListPresenter = presenterProvider.get()

    private var adapter: AddCardListAdapter? = null

    var personFlag = true
    var lastSearch = ""
    /*override fun showBottomNavigation(navigationView: NavigationView) {
        navigationView.hideBottomNavigation()
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_card, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        hideLoading()
        hideListLoading()
        setToolbar()
        setListeners()
        setStatus(Const.EDIT_STATUS)
        setWaitStatus(false)
        setHasOptionsMenu(true)
    }

    private fun setListeners() {
        spinner.setItems(getString(R.string.search_person), getString(R.string.common_search))
        spinner?.setOnItemSelectedListener(object : MaterialSpinner.OnItemSelectedListener<Any> {
            override fun onItemSelected(view: MaterialSpinner?, position: Int, id: Long, item: Any?) {
                when (position) {
                    0 -> {
                        personFlag = true

                    }

                    1 -> {
                        personFlag =  false
                    }
                }
                presenter.opensearch(lastSearch)
            }

        })
    }

    private fun setToolbar() {
        setActionBar(toolbar)
        setActionBarTitle(R.string.search_card)
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }

    override fun handleError(throwable: Throwable) {

    }

    override fun showListLoading() {
        pg_list.visibility = View.VISIBLE
    }

    override fun hideListLoading() {
        pg_list.visibility = View.GONE
    }

    override fun changeDataSet(tests: List<Item>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadNextElements(i: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setNotLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setOpenSearchList(list: List<Item>) {
        var itemList = list

        Log.d(TAG_LOG,"setResult = " + itemList.size)

        /* for (item in itemList) {
             Log.d(TAG_LOG, "text " + item.text!!.content!!)
             Log.d(TAG_LOG, "desc " + item.description!!.content!!)
             Log.d(TAG_LOG, "url " + item.url!!.content!!)
         }*/
        val sep = "-----------"
        Log.d(TAG_LOG, sep)

        if(personFlag) {
            val names: List<String> = AppHelper.readFileFromAssets("regular.txt", this.requireContext())
            for (name in names) {
                Log.d(TAG_LOG, "name = " + name)
            }
            itemList = Stream.of(itemList)
                .filter { e ->
                    var flag: Boolean = false
                    e.description?.let {
                        flag = true
                        val text = it.content
                        Log.d(TAG_LOG, "text = " + text)
//                    val pattern = Pattern.compile(".*\\(.*[0-9]{1,4}.*(\\setBottomNavigationStatus*-\\setBottomNavigationStatus*[0-9]{1,4}.*)?\\).*")
                        val mainPattern = Pattern.compile(".*\\(.*(([0-9]{1,4})|(век|др\\.)).*\\).*")
                        val secondPattern = Pattern.compile("\\(.*\\)\\s*—")
                        val thirdPattern = Pattern.compile("\\s+|,|\\.")
                        flag = mainPattern.matcher(text!!).matches()
                        if (flag) {
                            Log.d(TAG_LOG, "text true = " + text)
                            val partsOne = text.split(secondPattern)
                            val parts: MutableList<String> = ArrayList()
                            for (part in partsOne) {
                                Log.d(TAG_LOG, "big_part = $part")
                                val partsMin: List<String> = part.split(thirdPattern)
                                for (partMin in partsMin) {
                                    Log.d(TAG_LOG, "partMin = $partMin")
                                }
                                parts.addAll(partsMin)
                            }
                            val partOne = parts[0]
                            var partTwo = ""
                            if (parts.size > 1) {
                                partTwo = parts[1]
                            }
                            Log.d(TAG_LOG, "part = " + partOne)
                            Log.d(TAG_LOG, "partTwo = " + partTwo)
                            for (name in names) {
                                for (part in parts) {
                                    if (part.equals(name)) {
                                        flag = false
                                        Log.d(TAG_LOG, "flag = $flag and name = $name")
                                        break
                                    }
                                }
                            }
                        }
                    }

                    flag
                }
                .toList()
        }
        for (item in itemList) {
            /*  Log.d(TAG_LOG, "text " + item.text!!.content!!)
              Log.d(TAG_LOG, "desc " + item.description!!.content!!)
              Log.d(TAG_LOG, "url " + item.url!!.content!!)*/
        }

        if(itemList.size == 0) {
            showSnackBar("Ничего не найдено")
        } else {
            adapter!!.changeDataSet(itemList)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)

        var searchView: android.support.v7.widget.SearchView? = null
        if (searchItem != null) {
            searchView = searchItem.actionView as android.support.v7.widget.SearchView
        }
        if (searchView != null) {
            val finalSearchView = searchView
            searchView.setOnQueryTextListener(object : android.support.v7.widget.SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.d(TAG_LOG, "opensearch")
                        if (!finalSearchView.isIconified) {
                            finalSearchView.isIconified = true
                        }
                        searchItem!!.collapseActionView()

                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    if(!newText.equals("")) {
                        if (checkSearch(newText)) {
                            lastSearch = newText
                            presenter.opensearch(newText)
                        } else {
                            showSnackBar("Поиск возможен только на русском с использованием цифр, пробелов и тире")
                        }
                    }

                    return false
                }
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun checkSearch(query: String): Boolean {
        val pattern: Pattern = Pattern.compile("([А-я0-9_\\-,.]+\\s*)+")
        return (pattern.matcher(query).matches())
    }

    private fun initRecycler() {
        adapter = AddCardListAdapter(ArrayList())
        val manager = LinearLayoutManager(this.activity)
        rv_list.layoutManager = manager
        rv_list.setEmptyView(tv_empty)
        adapter!!.attachToRecyclerView(rv_list)
        adapter!!.setOnItemClickListener(this)
        rv_list.adapter = adapter
        rv_list.setHasFixedSize(true)
    }


    override fun onItemClick(item: Item) {
        showLoading()
        buildCard(item)
       /* val args = Bundle()
        val itemJson = gson.toJson(item)
        args.putString(ITEM_ITEM, itemJson)
        val fragment = AddCardFragment.newInstance(args)
        fragment.setTargetFragment(this, ADD_CARD_CODE)
        showFragment(this, fragment)*/
    }

    private fun buildCard(item: Item) {
        card = Card()
        card?.abstractCard?.wikiUrl = item!!.url!!.content
        card?.abstractCard?.description = item!!.description!!.content
        item!!.text!!.content?.let { presenter.query(it) }
    }

    override fun setQueryResults(list: List<Page>) {
        val page = list[0]
        card!!.abstractCard?.name = page.title
        card!!.abstractCard?.photoUrl = page.original!!.source
        card!!.abstractCard?.extract = page.extract!!.content
        prepareStats()
        prepareBackResult()
    }

    private fun prepareBackResult() {
        val intent = Intent()
        val cardJson = gson.toJson(card)
        intent.putExtra(CARD_ITEM, cardJson)
        targetFragment?.onActivityResult(ADD_CARD_CODE, Activity.RESULT_OK, intent)
        hideFragment()
    }

    private fun prepareStats() {
        var maxValue = 51
        val hp = Random.nextInt(0, maxValue)
        maxValue -= hp
        val strength = Random.nextInt(0, maxValue)
        maxValue -= strength
        val support = Random.nextInt(0, maxValue)
        maxValue -= support
        val prestige = Random.nextInt(0, maxValue)
        maxValue -= prestige
        val intelligence = Random.nextInt(0, maxValue)
        card?.hp = hp
        card?.strength = strength
        card?.support = support
        card?.prestige = prestige
        card?.intelligence = intelligence
        Log.d(TAG_LOG, "$hp $strength $support $prestige $intelligence")
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if (reqCode == ADD_CARD_CODE && resultCode == android.support.v7.app.AppCompatActivity.RESULT_OK) {
            val card = data?.getStringExtra(CARD_ITEM)
            targetFragment?.onActivityResult(reqCode, resultCode, data)
            hideFragment()
            /*val intent = Intent()
            intent.putExtra(CARD_EXTRA, card)
            setResult(Activity.RESULT_OK, intent)*/
        } else {
            hideFragment()
        }
    }

    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = AddCardListFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment {
            val fragment = AddCardListFragment()
            return fragment
        }
    }

}