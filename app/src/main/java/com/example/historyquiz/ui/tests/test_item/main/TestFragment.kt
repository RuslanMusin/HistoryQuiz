package com.example.historyquiz.ui.tests.test_item.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.example.historyquiz.R
import com.example.historyquiz.model.test.Link
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.cards.wiki_page.WikiPageFragment
import com.example.historyquiz.ui.tests.add_test.TestViewModel
import com.example.historyquiz.ui.tests.test_item.finish.FinishFragment
import com.example.historyquiz.ui.tests.test_item.question.QuestionFragment
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.AppHelper.Companion.currentId
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.OLD_ONES
import com.example.historyquiz.utils.Const.PAGE_TITLE
import com.example.historyquiz.utils.Const.PAGE_URL
import com.example.historyquiz.utils.Const.QUESTION_NUMBER
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.TEST_ITEM
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.layout_expandable.*
import kotlinx.android.synthetic.main.layout_links.*
import kotlinx.android.synthetic.main.layout_test.*
import kotlinx.android.synthetic.main.toolbar_back.*
import javax.inject.Inject
import javax.inject.Provider

class TestFragment : BaseFragment(), TestView, View.OnClickListener {

    lateinit var test: Test
    lateinit var model: TestViewModel

    @InjectPresenter
    lateinit var presenter: TestPresenter
    @Inject
    lateinit var presenterProvider: Provider<TestPresenter>
    @ProvidePresenter
    fun providePresenter(): TestPresenter = presenterProvider.get()

    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = TestFragment()
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

    /*override fun performBackPressed() {
        removeStackDownTo()
        val type = if(test.testDone) OLD_ONES else NEW_ONES
        val userId = if(test.testDone) currentId else BOT_ID
        val args = Bundle()
        args.putString(Const.TIME_TYPE, type)
        args.putString(Const.USER_ID, userId)
        val fragment = TestListFragment.newInstance(args)
        pushFragments(fragment, true)
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_test, container, false)


        val testStr: String? = arguments?.getString(TEST_ITEM)
        test = gson.fromJson(testStr,Test::class.java)


        /*(activity as BaseBackActivity).currentTag = TEST_FRAGMENT
        test.title?.let { (activity as ChangeToolbarListener).changeToolbar(TEST_FRAGMENT, it) }*/
        presenter.readCardForTest(test)
        setStatus(Const.ONLINE_STATUS)
        setWaitStatus(true)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AppHelper.hideKeyboardFrom(activity as Context,view)

        initViews(view)
        setListeners()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setData() {
        if (test.testDone == false) {
            tv_done.text = getText(R.string.test_wasnt_done)
        } else {
            tv_done.text = getText(R.string.test_was_done)
        }
        if(OLD_ONES.equals(test.type)) {
            tv_do_test.visibility = View.GONE
            tv_check_result.visibility = View.VISIBLE
        }
        if(currentId.equals(test.authorId)) {
            tv_do_test.visibility = View.GONE
        }
        val relation: String? = test.testRelation?.relation
        Log.d(TAG_LOG,"has card = $relation and has test = ${test.testDone}")
       /* if(relation.equals(AFTER_TEST) || relation.equals(WIN_GAME)) {
            tv_card_done.text = getText(R.string.test_was_done)
        } else {
            tv_card_done.text = getText(R.string.test_wasnt_done)
        }*/
        tv_content.text = test.desc
        tv_name.text = test.title
        tv_test_name.text = test.title
        tv_questions.text = test.questions.size.toString()
        tv_epoch.text = test.epoch?.name
        test.card?.abstractCard?.photoUrl?.let {
            Glide.with(iv_portrait.context)
                .load(it)
                .into(iv_portrait)
        }
        if(test.links.size == 0) {
            li_links.visibility = View.GONE
        } else {
            for(skill in test.links) {
                addLinkView(skill)
            }
        }
        hideLoading()
    }

    private fun addLinkView(skill: Link) {
        val view: View = layoutInflater.inflate(R.layout.item_link, li_added_links,false)
        val tvAddedSkill: TextView = view.findViewById(R.id.tv_link_name)
        val tvContent: TextView = view.findViewById(R.id.tv_link_content)

        tvAddedSkill.text = skill.name
        tvAddedSkill.setOnClickListener { loadPage(skill) }
        tvContent.text = skill.content
        li_added_links.addView(view)
    }

    private fun loadPage(link: Link) {
        val args = Bundle()
        args.putString(PAGE_TITLE, link.name)
        args.putString(PAGE_URL, link.url)
        pushFragments(WikiPageFragment.newInstance(args), true)

    }

    fun showDescription() {
        expandable_layout.toggle()
        if(expandable_layout.isExpanded) {
            iv_arrow.rotation = 180f
//            iv_arrow.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp)
        } else {
//            iv_arrow.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp)
            iv_arrow.rotation = 0f
        }
    }

    fun showLinks() {
        expandable_link_layout.toggle()
        if(expandable_link_layout.isExpanded) {
            iv_link_arrow.rotation = 180f
        } else {
            iv_link_arrow.rotation = 0f
        }
    }

    private fun initViews(view: View) {
        setActionBar(toolbar_back)
        test.title?.let { setToolbarTitle(toolbar_title, it) }
    }

    private fun setListeners() {
        tv_do_test.setOnClickListener(this)
        tv_check_result.setOnClickListener(this)
        btn_back.setOnClickListener(this)
        li_description.setOnClickListener(this)
        li_links.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.tv_do_test -> {
                model = activity?.run {
                    ViewModelProviders.of(this).get(TestViewModel::class.java)
                } ?: throw Exception("Invalid Activity")
                model.selectNumber(0)
                model.selectTest(test)
                val args: Bundle = Bundle()
                args.putString(TEST_ITEM, gson.toJson(test))
                args.putInt(QUESTION_NUMBER,0)
                val fragment = QuestionFragment.newInstance(args)
                pushFragments(fragment, true)
//                (activity as BaseBackActivity).changeFragment(fragment, QUESTION_FRAGMENT + 0)

            }

            R.id.tv_check_result -> {
                val args: Bundle = Bundle()
                args.putString(TEST_ITEM, gson.toJson(test))
                val fragment = FinishFragment.newInstance(args)
                pushFragments(fragment, true)
            }

            R.id.btn_back -> performBackPressed()

            R.id.li_description -> showDescription()

            R.id.li_links -> showLinks()

        }
    }


}


