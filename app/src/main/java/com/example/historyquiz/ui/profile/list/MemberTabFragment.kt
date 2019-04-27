package com.example.historyquiz.ui.profile.list

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.base.interfaces.ReloadableView
import com.example.historyquiz.ui.base.interfaces.SearchListener
import com.example.historyquiz.ui.profile.list.list_item.MemberListFragment
import com.example.historyquiz.utils.Const.ADD_FRIEND
import com.example.historyquiz.utils.Const.DEFAULT_USERS_TYPE
import com.example.historyquiz.utils.Const.REMOVE_FRIEND
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.widget.FragViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_member_tabs.*
import javax.inject.Inject
import javax.inject.Provider

class MemberTabFragment : BaseFragment(), MembersTabView {

    @InjectPresenter
    lateinit var presenter: MembersTabPresenter
    @Inject
    lateinit var presenterProvider: Provider<MembersTabPresenter>
    @ProvidePresenter
    fun providePresenter(): MembersTabPresenter = presenterProvider.get()

    private var fragments: MutableList<Fragment> = ArrayList()
    lateinit private var currentFragment: SearchListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_member_tabs, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        setActionBar(toolbar)
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        setupViewPager(viewpager)
        tab_layout.setupWithViewPager(viewpager)
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout))
        setTabListener()
    }

    private fun setTabListener() {
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.d(TAG_LOG, "on tab selected")
                viewpager.currentItem = tab.position
                (fragments[tab.position] as ReloadableView).reloadList()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }


    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = FragViewPagerAdapter(childFragmentManager)
        fragments.add(MemberListFragment.newInstance(DEFAULT_USERS_TYPE))
        fragments.add(MemberListFragment.newInstance(REMOVE_FRIEND))
        fragments.add(MemberListFragment.newInstance(ADD_FRIEND))
        adapter.addFragment(fragments[0], getString(R.string.tab_all_users))
        adapter.addFragment(fragments[1], getString(R.string.tab_friends))
        adapter.addFragment(fragments[2], getString(R.string.tab_requests))
        viewPager.adapter = adapter
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
//                presenter.loadOfficialTestsByQUery(query)

                if (!finalSearchView.isIconified) {
                    finalSearchView.isIconified = true
                }
                searchItem.collapseActionView()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val pos = viewpager.currentItem
                currentFragment = fragments[pos] as SearchListener
                currentFragment.findByQuery(newText)
                return false
            }
        })

    }

    companion object {

        fun newInstance(): Fragment {
            val fragment = MemberTabFragment()
            return fragment
        }
    }

}