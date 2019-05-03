package com.example.historyquiz.ui.profile.list.list_item

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.user.User
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.profile.item.ProfileFragment
import com.example.historyquiz.utils.AppHelper.Companion.currentId
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.USERS_LIST_TYPE
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Provider

class MemberListFragment : BaseFragment(), MemberListView {

    private lateinit var adapter: MemberAdapter

    var tests: MutableList<User> = ArrayList()
    lateinit var userId: String

    @InjectPresenter
    lateinit var presenter: MemberListPresenter
    @Inject
    lateinit var presenterProvider: Provider<MemberListPresenter>
    @ProvidePresenter
    fun providePresenter(): MemberListPresenter = presenterProvider.get()

    lateinit var type: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_member_list, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        arguments?.let {
            type = it.getString(USERS_LIST_TYPE)
            presenter.loadUsers(type)

        }
    }

    override fun reloadList() {
        if(tests.size > 0) {
            presenter.loadUsers(type)
        }
    }

    private fun initViews() {
        initRecycler()
        setListeners()
    }

    private fun setListeners() {
    }

    override fun setNotLoading() {

    }

    override fun showListLoading() {
        pg_list.visibility = View.VISIBLE
    }

    override fun hideListLoading() {
        pg_list.visibility = View.GONE
    }

    override fun loadNextElements(i: Int) {
    }


    override fun changeDataSet(tests: List<User>) {
        adapter.changeDataSet(tests)
        hideLoading()
    }

    override fun showItems(tests: List<User>) {
        this.tests = tests.toMutableList()
        changeDataSet(tests)
    }

    override fun handleError(throwable: Throwable) {

    }

    private fun initRecycler() {
        adapter = MemberAdapter(ArrayList())
        val manager = LinearLayoutManager(activity as Activity)
        rv_list.layoutManager = manager
        rv_list.setEmptyView(tv_empty)
        adapter.attachToRecyclerView(rv_list)
        adapter.setOnItemClickListener(this)

    }

    override fun onItemClick(item: User) {
        if(currentId.equals(item.id)) {
            performBackPressed()
        } else {
            val args = Bundle()
            args.putString(Const.USER_ITEM, gson.toJson(item))
            val fragment = ProfileFragment.newInstance(args)
            pushFragments(fragment, true)
        }
    }

    override fun findByQuery(query: String) {
        val pattern: Pattern = Pattern.compile("${query.toLowerCase()}.*")
        val list: MutableList<User> = java.util.ArrayList()
        for(skill in tests) {
            if(pattern.matcher(skill.username?.toLowerCase()).matches()) {
                list.add(skill)
            }
        }
        Log.d(TAG_LOG, "list.size = ${list.size}")
        changeDataSet(list)
    }

    companion object {

        fun newInstance(): Fragment {
            val fragment = MemberListFragment()
            return fragment
        }

        fun newInstance(type: String): Fragment {
            val fragment = MemberListFragment()
            val args = Bundle()
            args.putString(USERS_LIST_TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }
}
