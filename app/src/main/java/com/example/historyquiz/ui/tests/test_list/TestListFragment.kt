package com.example.historyquiz.ui.tests.test_list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.BaseFragment
import com.google.gson.reflect.TypeToken
import com.summer.itis.cardsproject.ui.tests.test_list.TestAdapter
import io.reactivex.disposables.Disposable
import java.util.ArrayList
import java.util.regex.Pattern

class TestListFragment : BaseFragment(), TestListView, View.OnClickListener {

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
        val view = inflater.inflate(R.layout.fragment_skills, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun showSkills(skills: List<Skill>) {
        this.skills = skills.toMutableList()
        changeDataSet(this.skills)
    }

    private fun initViews() {
        setToolbarData()
        initRecycler()
        setListeners()
    }

    private fun setToolbarData() {
        mainListener.setToolbar(toolbar_edit)
        toolbar_title.text = getString(R.string.skills)
        btn_back.visibility = View.VISIBLE
        if(type.equals(WATCHER_TYPE)) {
            btn_edit.visibility = View.GONE
        }
    }

    private fun setListeners() {
        if(!type.equals(WATCHER_TYPE)) {
            btn_edit.setOnClickListener(this)
        }
        btn_back.setOnClickListener(this)
    }

    override fun setNotLoading() {

    }

    override fun showLoading(disposable: Disposable) {
        pb_list.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        pb_list.visibility = View.GONE
    }

    override fun loadNextElements(i: Int) {
    }


    override fun changeDataSet(tests: List<Skill>) {
        adapter.changeDataSet(tests)
        hideLoading()
    }

    override fun handleError(throwable: Throwable) {

    }

    private fun initRecycler() {
        adapter = SkillAdapter(ArrayList())
        val manager = LinearLayoutManager(activity as Activity)
        rv_list.layoutManager = manager
        rv_list.setEmptyView(tv_empty)
        adapter.attachToRecyclerView(rv_list)
        adapter.setOnItemClickListener(this)
    }

    override fun onItemClick(item: Skill) {
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.btn_edit -> editSkills()

            R.id.btn_back -> backFragment()

        }
    }

    private fun editSkills() {
        mainListener.showLoading()
        val fragment = EditSkillsFragment.newInstance(mainListener)
        fragment.setTargetFragment(this, EDIT_SKILLS)
        mainListener.showFragment(this, fragment)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {

                EDIT_SKILLS -> {
                    data?.getStringExtra(SKILL_KEY)?.let {
                        val founderListType = object : TypeToken<ArrayList<Skill>>(){}.type
                        val skills: List<Skill> = gsonConverter.fromJson(it, founderListType)
                        AppHelper.currentCurator.skills = skills.toMutableList()
                        this.skills = skills.toMutableList()
                        changeDataSet(skills)
                    }
                }
            }
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
                findFromList(newText)
                return false
            }
        })

    }

    private fun findFromList(query: String) {
        val pattern: Pattern = Pattern.compile("${query.toLowerCase()}.*")
        val list: MutableList<Test> = java.util.ArrayList()
        for(skill in skills) {
            if(pattern.matcher(skill.name.toLowerCase()).matches()) {
                list.add(skill)
            }
        }
        changeDataSet(list)
    }

    companion object {

        const val EDIT_SKILLS = 1

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
