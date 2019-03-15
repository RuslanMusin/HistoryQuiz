package com.example.historyquiz.ui.cards.add_card_list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.wiki_api.opensearch.Item
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const.ADD_CARD
import com.example.historyquiz.utils.Const.CARD_ITEM
import com.example.historyquiz.utils.Const.TAG_LOG
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import java.util.ArrayList
import java.util.regex.Pattern

class AddCardListFragment: BaseFragment(), AddCardListView {

    private var card: Card? = null

    @InjectPresenter
    lateinit var presenter: AddCardListPresenter
    private var adapter: AddCardListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_question, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun handleError(throwable: Throwable) {

    }

    override fun showLoading(disposable: Disposable) {
        pg_list.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        pg_list.visibility = View.GONE
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

        val names: List<String> = AppHelper.readFileFromAssets("regular.txt",this)
        for(name in names) {
            Log.d(TAG_LOG,"name = " + name)
        }
        itemList = Stream.of(itemList)
            .filter { e ->
                var flag: Boolean = false
                e.description?.let {
                    flag = true
                    val text = it.content
                    Log.d(TAG_LOG, "text = " + text)
//                    val pattern = Pattern.compile(".*\\(.*[0-9]{1,4}.*(\\s*-\\s*[0-9]{1,4}.*)?\\).*")
                    val mainPattern = Pattern.compile(".*\\(.*(([0-9]{1,4})|(век|др\\.)).*\\).*")
                    val secondPattern = Pattern.compile("\\(.*\\)\\s*—")
                    val thirdPattern = Pattern.compile("\\s+|,|\\.")
                    flag = mainPattern.matcher(text!!).matches()
                    if (flag) {
                        Log.d(TAG_LOG, "text true = " + text)
                        val partsOne = text.split(secondPattern)
                        val parts: MutableList<String> = ArrayList()
                        for(part in partsOne) {
                            Log.d(TAG_LOG,"big_part = $part")
                            val partsMin: List<String> = part.split(thirdPattern)
                            for(partMin in partsMin) {
                                Log.d(TAG_LOG,"partMin = $partMin")
                            }
                            parts.addAll(partsMin)
                        }
                        /* val partOne = parts[0]
                         var partTwo = ""
                         if (parts.size > 1) {
                             partTwo = parts[1]
                         }
                         Log.d(TAG_LOG, "part = " + partOne)
                         Log.d(TAG_LOG, "partTwo = " + partTwo)*/
                        for (name in names) {
                            for(part in parts) {
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

    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)

        var searchView: android.support.v7.widget.SearchView? = null
        if (searchItem != null) {
            searchView = searchItem.actionView as android.support.v7.widget.SearchView
        }
        if (searchView != null) {
            val finalSearchView = searchView
            searchView.setOnQueryTextListener(object : android.support.v7.widget.SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.d(TAG_LOG,"opensearch")
                    if(checkSearch(query)) {
                        presenter.opensearch(query)
                        if (!finalSearchView.isIconified) {
                            finalSearchView.isIconified = true
                        }
                        searchItem!!.collapseActionView()
                    } else {
                        showSnackBar("Поиск возможен только на русском с использованием цифр и тире")
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun checkSearch(query: String): Boolean {
        val pattern: Pattern = Pattern.compile("[А-я0-9_\\-,.]+")
        return (pattern.matcher(query).matches())
    }

    private fun initRecycler() {
        adapter = AddCardListAdapter(ArrayList())
        val manager = LinearLayoutManager(this)
        rv_list.layoutManager = manager
        rv_list.setEmptyView(tv_empty)
        adapter!!.attachToRecyclerView(rv_list)
        adapter!!.setOnItemClickListener(this)
        rv_list.adapter = adapter
        rv_list.setHasFixedSize(true)
    }


    override fun onItemClick(item: Item) {
     /*   val intent = Intent(this, AddCardActivity::class.java)
        val itemJson = gsonConverter.toJson(item)
        intent.putExtra(ITEM_JSON, itemJson)
        startActivityForResult(intent, ADD_CARD)*/
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if (reqCode == ADD_CARD && resultCode == Activity.RESULT_OK) {
            val card = data?.getStringExtra(CARD_ITEM)
            targetFragment?.onActivityResult(reqCode, resultCode, data)
            /*val intent = Intent()
            intent.putExtra(CARD_EXTRA, card)
            setResult(Activity.RESULT_OK, intent)*/
        } else {
            onBackPressed()
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