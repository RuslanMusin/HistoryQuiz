package com.example.historyquiz.ui.tests.test_list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.tests.test_item.main.TestFragment
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.TEST_ID
import com.example.historyquiz.utils.Const.TEST_ITEM
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.fragment_test_list.*
import java.util.ArrayList
import java.util.regex.Pattern
import javax.inject.Inject

class TestListFragment : BaseFragment(), TestListView, View.OnClickListener {

    @Inject
    lateinit var gson: Gson

    lateinit var userId: String
    private lateinit var adapter: TestAdapter

    lateinit var skills: MutableList<Test>

    @InjectPresenter
    lateinit var presenter: TestListPresenter

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
        presenter.loadOfficialTests()
    }

    private fun initViews() {
        setToolbarData()
        initRecycler()
        setListeners()
    }

    private fun setToolbarData() {

    }

    private fun setListeners() {
    }

    override fun setNotLoading() {

    }

    override fun showLoading(disposable: Disposable) {
//        pb_list.visibility = View.VISIBLE
    }

    override fun hideLoading() {
//        pb_list.visibility = View.GONE
    }

    override fun loadNextElements(i: Int) {
    }


    override fun changeDataSet(tests: List<Test>) {
        adapter.changeDataSet(tests)
        hideLoading()
    }

    override fun handleError(throwable: Throwable) {

    }

    private fun initRecycler() {
        adapter = TestAdapter(ArrayList<Test>())
        val manager = LinearLayoutManager(this.activity)
        rv_list.layoutManager = manager
        rv_list.setEmptyView(tv_empty)
        adapter.attachToRecyclerView(rv_list)
        adapter.setOnItemClickListener(this)
        rv_list.adapter = adapter

        rv_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && floating_button.getVisibility() == View.VISIBLE) {
                    floating_button.hide();
                } else if (dy < 0 && floating_button.getVisibility() != View.VISIBLE) {
                    floating_button.show();
                }
            }
        })

        floating_button.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                Log.d(TAG_LOG,"act float btn")
//                AddTestActivity.start(activity as Activity)
            }
        })
    }

    override fun onItemClick(item: Test) {
        val args = Bundle()
        args.putString(TEST_ITEM, gson.toJson(item))
        val fragment = TestFragment.newInstance(args)
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
                presenter.loadOfficialTestsByQUery(newText)
//                findFromList(newText)
                return false
            }
        })

    }

    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = TestListFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment {
            val fragment = TestListFragment()
            return fragment
        }
    }
}
