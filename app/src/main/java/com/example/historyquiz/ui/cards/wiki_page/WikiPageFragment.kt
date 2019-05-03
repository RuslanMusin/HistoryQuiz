package com.example.historyquiz.ui.cards.wiki_page

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.PAGE_TITLE
import com.example.historyquiz.utils.Const.PAGE_URL
import kotlinx.android.synthetic.main.fragment_wiki_page.*
import kotlinx.android.synthetic.main.toolbar_back.*
import javax.inject.Inject
import javax.inject.Provider

class WikiPageFragment : BaseFragment(), WikiPageView, View.OnClickListener {

    @InjectPresenter
    lateinit var presenter: WikiPagePresenter
    @Inject
    lateinit var presenterProvider: Provider<WikiPagePresenter>
    @ProvidePresenter
    fun providePresenter(): WikiPagePresenter = presenterProvider.get()

    lateinit var title: String
    lateinit var url: String

    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = WikiPageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_wiki_page, container, false)

        arguments?.let {
            title = it.getString(PAGE_TITLE)
            url = it.getString(PAGE_URL)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setData()
        setListeners()
        setStatus(Const.ONLINE_STATUS)
        setWaitStatus(true)
    }

    private fun setListeners() {
        btn_back.setOnClickListener(this)
    }

    private fun setToolbar() {
        setActionBar(toolbar_back)
        setToolbarTitle(toolbar_title, title)
    }

    private fun setData() {
        webView.getSettings().setJavaScriptEnabled(true)
        webView.loadUrl(url)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                view?.loadUrl(url)
                return true
            }
        }

    }

    override fun onClick(v: View) {

        when(v.id) {

            R.id.btn_back -> performBackPressed()
        }
    }
}