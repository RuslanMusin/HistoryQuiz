package com.example.historyquiz.ui.tests.test_list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.tests.add_test.main.AddMainTestFragment
import com.example.historyquiz.ui.tests.test_item.main.TestFragment
import com.example.historyquiz.utils.Const.NEW_ONES
import com.example.historyquiz.utils.Const.OLD_ONES
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.TEST_ITEM
import com.example.historyquiz.utils.Const.TIME_TYPE
import com.example.historyquiz.utils.Const.USER_ID
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.fragment_test_list.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class TestListFragment : BaseFragment(), TestListView, View.OnClickListener {

    lateinit var userId: String
    lateinit var type: String
    private lateinit var adapter: TestAdapter

    lateinit var skills: MutableList<Test>

    @InjectPresenter
    lateinit var presenter: TestListPresenter
    @Inject
    lateinit var presenterProvider: Provider<TestListPresenter>
    @ProvidePresenter
    fun providePresenter(): TestListPresenter = presenterProvider.get()

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
            presenter.loadOfficialTests(userId, type)
        }
    }

    private fun initViews() {
        setToolbar()
        initRecycler()
        setListeners()
    }

    private fun setToolbar() {
        setActionBar(toolbar)
        setActionBarTitle(R.string.menu_tests)
        if(!type.equals(OLD_ONES)) {
            toolbar.setNavigationIcon(null)
        } else {
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
            floating_button.visibility = View.GONE
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


    override fun changeDataSet(tests: List<Test>) {
        adapter.changeDataSet(tests)
        hideListLoading()
        hideLoading()
        Log.d(TAG_LOG, "test loaded")
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
        if(type.equals(NEW_ONES)) {
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

            floating_button.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    Log.d(TAG_LOG, "act float btn")
                    val fragment = AddMainTestFragment.newInstance()
                    pushFragments(fragment, true)
//                AddTestActivity.start(activity as Activity)
                }
            })
        }
    }

    override fun onItemClick(item: Test) {
        val args = Bundle()
        args.putString(TEST_ITEM, gson.toJson(item))
        val fragment = TestFragment.newInstance(args)
        showLoading()
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
                presenter.loadOfficialTestsByQUery(userId, newText, type)
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
