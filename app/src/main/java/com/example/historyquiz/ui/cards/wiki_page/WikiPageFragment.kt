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
import com.example.historyquiz.model.card.AbstractCard
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.fragment_wiki_page.*
import kotlinx.android.synthetic.main.toolbar_back.*
import javax.inject.Inject
import javax.inject.Provider

class WikiPageFragment : BaseFragment(), WikiPageView, View.OnClickListener {

    lateinit var card: AbstractCard

    @InjectPresenter
    lateinit var presenter: WikiPagePresenter
    @Inject
    lateinit var presenterProvider: Provider<WikiPagePresenter>
    @ProvidePresenter
    fun providePresenter(): WikiPagePresenter = presenterProvider.get()

    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = WikiPageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_wiki_page, container, false)

        val testStr: String? = arguments?.getString(Const.ABS_CARD)
        card = gson.fromJson(testStr, AbstractCard::class.java)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setData()
        setListeners()
    }

    private fun setListeners() {
        btn_back.setOnClickListener(this)
    }

    private fun setToolbar() {
        setActionBar(toolbar_back)
        card.name?.let { setToolbarTitle(toolbar_title, it) }
    }

    private fun setData() {
        webView.getSettings().setJavaScriptEnabled(true)
        webView.loadUrl(card.wikiUrl)

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