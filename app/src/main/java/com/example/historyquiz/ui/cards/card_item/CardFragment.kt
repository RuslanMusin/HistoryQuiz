package com.example.historyquiz.ui.cards.card_item

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.bumptech.glide.Glide
import com.example.historyquiz.R
import com.example.historyquiz.model.card.AbstractCard
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.cards.wiki_page.WikiPageFragment
import com.example.historyquiz.ui.cards.wiki_page.WikiPagePresenter
import com.example.historyquiz.ui.comment.CommentFragment
import com.example.historyquiz.ui.comment.CommentPresenter
import com.example.historyquiz.ui.navigation.NavigationView
import com.example.historyquiz.ui.tests.add_test.TestViewModel
import com.example.historyquiz.ui.tests.test_item.main.TestPresenter
import com.example.historyquiz.ui.tests.test_item.main.TestView
import com.example.historyquiz.ui.tests.test_item.question.QuestionFragment
import com.example.historyquiz.ui.tests.test_list.TestListFragment
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.ABS_CARD
import com.example.historyquiz.utils.Const.CARD_COMMENT_TYPE
import com.google.gson.Gson
import kotlinx.android.synthetic.main.layout_add_comment.*
import kotlinx.android.synthetic.main.layout_card.*
import kotlinx.android.synthetic.main.layout_expandable_text_view.*
import kotlinx.android.synthetic.main.toolbar_back.*
import javax.inject.Inject

class CardFragment : CommentFragment(), CardView, View.OnClickListener {

    @Inject
    lateinit var gson: Gson

    lateinit var card: AbstractCard

    @InjectPresenter
    lateinit var presenter: CardPresenter

    @InjectPresenter
    override lateinit var commentPresenter: CommentPresenter

    override var type: String = CARD_COMMENT_TYPE
    override lateinit var elemId: String

    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = CardFragment()
            fragment.arguments = args
            return fragment
        }
    }

    /*  override fun onBackPressed() {
          *//* val args: Bundle = Bundle()
         args.putString(TEST_JSON, gsonConverter.toJson(test))
         val fragment = FinishFragment.newInstance(args)
         (activity as BaseBackActivity).changeFragment(fragment)*//*

        TestListActivity.start(activity as Activity)
    }*/

    override fun showBottomNavigation(navigationView: NavigationView) {
        navigationView.hideBottomNavigation()
        navigationView.changeWindowsSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_card, container, false)

        val testStr: String? = arguments?.getString(Const.ABS_CARD)
        card = gson.fromJson(testStr, AbstractCard::class.java)
        card.id?.let { elemId = it }
        /*(activity as BaseBackActivity).currentTag = TEST_FRAGMENT
        test.title?.let { (activity as ChangeToolbarListener).changeToolbar(TEST_FRAGMENT, it) }*/

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setListeners()
        setData()
        card.id?.let { commentPresenter.loadComments(CARD_COMMENT_TYPE, it) }
    }

    fun setData() {
        tv_name.text = card.name
        expand_text_view.text = card.description
        Glide.with(iv_portrait.context)
            .load(card.photoUrl)
            .into(iv_portrait)
    }

    private fun initViews(view: View) {
        setActionBar(toolbar_back)
        card.name?.let { setToolbarTitle(toolbar_title, it) }
    }

    private fun setListeners() {
        btn_back.setOnClickListener(this)
        li_wiki.setOnClickListener(this)
    }

    private fun getWikiUrl() {
        if(card.wikiUrl != null){
            val args = Bundle()
            args.putString(ABS_CARD, gson.toJson(card))
            pushFragments(WikiPageFragment.newInstance(args), true)
//            WebViewActivity.start(this, card)
        }else{
            showSnackBar(getString(R.string.no_wiki_page))
        }
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.btn_back -> performBackPressed()

            R.id.li_wiki -> getWikiUrl()

        }
    }

    override fun clearAfterSendComment() {
        et_comment.setText(null)
        et_comment.clearFocus()
    }


}


