package com.example.historyquiz.ui.statists

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.base.interfaces.ReloadableView
import com.example.historyquiz.ui.profile.list.list_item.MemberListFragment
import com.example.historyquiz.utils.Const
import com.example.historyquiz.widget.FragViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_member_tabs.*
import javax.inject.Inject
import javax.inject.Provider

class StatListFragment : BaseFragment(), StatListView {

    @InjectPresenter
    lateinit var presenter: StatListPresenter
    @Inject
    lateinit var presenterProvider: Provider<StatListPresenter>
    @ProvidePresenter
    fun providePresenter(): StatListPresenter = presenterProvider.get()

    private var fragments: MutableList<Fragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        setActionBarTitle(R.string.menu_profile)
        setupViewPager(viewpager)
        tab_layout.setupWithViewPager(viewpager)
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout))
        setTabListener()
    }

    private fun setTabListener() {
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.d(Const.TAG_LOG, "on tab selected")
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
        fragments.add(MemberListFragment.newInstance())
        fragments.add(MemberListFragment.newInstance())
        fragments.add(MemberListFragment.newInstance())
        adapter.addFragment(fragments[0], getString(R.string.menu_profile))
        adapter.addFragment(fragments[1], getString(R.string.menu_cards))
        adapter.addFragment(fragments[2], getString(R.string.menu_tests))
        viewPager.adapter = adapter
    }

    companion object {

        fun newInstance(): Fragment {
            val fragment = StatListFragment()
            return fragment
        }
    }
}