package com.example.historyquiz.ui.epoch

import android.content.Intent
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
import com.example.historyquiz.model.epoch.Epoch
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.HAS_DEFAULT
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.fragment_test_list.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class EpochListFragment : BaseFragment(), EpochListView {

    lateinit var adapter: EpochAdapter

    private var isLoaded = false

    @InjectPresenter
    lateinit var presenter: EpochListPresenter
    @Inject
    lateinit var presenterProvider: Provider<EpochListPresenter>
    @ProvidePresenter
    fun providePresenter(): EpochListPresenter = presenterProvider.get()

    var isClickable: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_test_list, container, false)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        initRecycler()
        arguments?.let {
            val hasDefault: Boolean = it.getBoolean(HAS_DEFAULT)
            presenter.loadEpoches(hasDefault)
            setStatus(Const.EDIT_STATUS)
            setWaitStatus(false)
        }
    }

    private fun initViews(view: View) {
        setActionBar(toolbar)
        toolbar.title = getString(R.string.choose_epoch)
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }


    private fun initRecycler() {
        adapter = EpochAdapter(ArrayList())
        val manager = LinearLayoutManager(this.activity)
        rv_list!!.layoutManager = manager
        rv_list!!.setEmptyView(tv_empty)
        adapter!!.attachToRecyclerView(rv_list!!)
        adapter!!.setOnItemClickListener(this)
        rv_list!!.adapter = adapter

        floating_button.visibility = View.GONE
    }

    override fun handleError(error: Throwable) {
        Log.d(Const.TAG_LOG, "error = " + error.message)
        error.printStackTrace()
    }

    override fun changeDataSet(tests: List<Epoch>) {
        Log.d(Const.TAG_LOG, "changeDataSet")
        adapter!!.changeDataSet(tests)
        hideLoading()
        hideListLoading()
    }

    override fun setNotLoading() {
//        isLoading = false
    }

    override fun hideListLoading() {
        pg_list.visibility = View.GONE
    }

    override fun showListLoading() {
        pg_list.visibility = View.GONE
    }

    override fun onItemClick(item: Epoch) {
        val intent = Intent()
        val cardJson = gson.toJson(item)
        intent.putExtra(Const.EPOCH_KEY, cardJson)
        targetFragment?.onActivityResult(Const.ADD_EPOCH_CODE, android.support.v7.app.AppCompatActivity.RESULT_OK, intent)
        hideFragment()
    }

    override fun hideProgressDialog() {
        showSnackBar("Противник не принял приглашение")
        isClickable = true
        /*if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
        isClickable = true*/
    }

    override fun loadNextElements(i: Int) {
//        presenter!!.loadNextElements(i)
    }

    companion object {

        fun newInstance(): Fragment {
            val fragment = EpochListFragment()
            return fragment
        }

        fun newInstance(args: Bundle): Fragment {
            val fragment = EpochListFragment()
            fragment.arguments = args
            return fragment
        }
    }

}