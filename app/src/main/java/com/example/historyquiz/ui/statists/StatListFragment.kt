package com.example.historyquiz.ui.statists

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.*
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.base.interfaces.ReloadableView
import com.example.historyquiz.ui.statists.tab_fragment.common_stats.CommonStatsFragment
import com.example.historyquiz.ui.statists.tab_fragment.game_stats.GameStatsFragment
import com.example.historyquiz.ui.statists.tab_fragment.leader_stats.LeaderStatsFragment
import com.example.historyquiz.utils.Const
import com.example.historyquiz.widget.FragViewPagerAdapter
import kotlinx.android.synthetic.main.dialog_help.*
import kotlinx.android.synthetic.main.fragment_member_tabs.*
import kotlinx.android.synthetic.main.toolbar_help.*
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
    lateinit var helpDialog: MaterialDialog


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
        hideLoading()
        setStatus(Const.ONLINE_STATUS)
        setWaitStatus(true)
    }

    private fun initViews() {
        setActionBar(toolbar)
        setActionBarTitle(R.string.statists)
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
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
        fragments.add(CommonStatsFragment.newInstance())
        fragments.add(GameStatsFragment.newInstance())
        fragments.add(LeaderStatsFragment.newInstance())
        adapter.addFragment(fragments[0], getString(R.string.tab_common_stat))
        adapter.addFragment(fragments[1], getString(R.string.tab_game_stat))
        adapter.addFragment(fragments[2], getString(R.string.tab_leader_stat))
        viewPager.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.help_menu, menu)
        helpDialog = MaterialDialog.Builder(this.activity!!)
            .customView(R.layout.dialog_help, false)
            .onNeutral { dialog, which ->
                dialog.cancel()
            }
            .build()

        helpDialog.btn_cancel.setOnClickListener { helpDialog.cancel() }
        helpDialog.tv_help_content.text = getString(R.string.stats_text)
        menu?.let {
            val helpItem = menu.findItem(R.id.action_help)
            helpItem.setOnMenuItemClickListener {
                helpDialog.show()
                true
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {

        fun newInstance(): Fragment {
            val fragment = StatListFragment()
            return fragment
        }
    }
}