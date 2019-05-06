package com.example.historyquiz.ui.tests.add_test.add_link

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.test.Link
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.ADD_LINK_CODE
import com.example.historyquiz.utils.Const.LINK_ITEM
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.fragment_add_link.*
import kotlinx.android.synthetic.main.toolbar_back_done.*
import javax.inject.Inject
import javax.inject.Provider

class AddLinkFragment : BaseFragment(), AddLinkView, View.OnClickListener {

    lateinit var link: Link

    @InjectPresenter
    lateinit var presenter: AddLinkPresenter
    @Inject
    lateinit var presenterProvider: Provider<AddLinkPresenter>
    @ProvidePresenter
    fun providePresenter(): AddLinkPresenter = presenterProvider.get()

   /* android:descendantFocusability="beforeDescendants"
    android:clickable="true"
    android:focusable="true"
    android:focusableInTouchMode="true"*/

    companion object {

        fun newInstance(): Fragment {
            val fragment = AddLinkFragment()
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        link = Link()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_link, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        setStatus(Const.EDIT_STATUS)
        setWaitStatus(false)
        super.onViewCreated(view, savedInstanceState)
    }

    fun initViews() {
        setToolbarData()
        setListeners()
    }

    private fun setToolbarData() {
        setActionBar(toolbar_back_done)
        toolbar_title.text = getString(R.string.add_link)
    }

    private fun setListeners() {
        btn_ok.setOnClickListener(this)
        btn_back.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        when (v.id) {

            R.id.btn_ok -> changeData()

            R.id.btn_back -> backFragment()
        }
    }

    private fun changeData() {
        link.url = et_url.text.toString()
        link.name = et_name.text.toString()
        link.content = et_description.text.toString()
        val intent = Intent()
        intent.putExtra(LINK_ITEM, gson.toJson(link))
        targetFragment?.onActivityResult(ADD_LINK_CODE, Activity.RESULT_OK, intent)
        hideFragment()
    }
}