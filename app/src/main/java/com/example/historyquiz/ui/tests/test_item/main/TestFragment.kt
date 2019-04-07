package com.example.historyquiz.ui.tests.test_item.main

import android.support.v7.app.AppCompatActivity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.bumptech.glide.Glide
import com.example.historyquiz.R
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.tests.add_test.TestViewModel
import com.example.historyquiz.ui.tests.test_item.question.QuestionFragment
import com.example.historyquiz.ui.tests.test_list.TestListFragment
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const.AFTER_TEST
import com.example.historyquiz.utils.Const.QUESTION_NUMBER
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.TEST_ITEM
import com.example.historyquiz.utils.Const.WIN_GAME
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.layout_test.*
import kotlinx.android.synthetic.main.toolbar_back.*
import javax.inject.Inject

class TestFragment : BaseFragment(), TestView, View.OnClickListener {

    @Inject
    lateinit var gson: Gson

    lateinit var test: Test
    lateinit var model: TestViewModel

    @InjectPresenter
    lateinit var presenter: TestPresenter

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

    override fun performBackPressed() {
        removeStackDownTo()
        val fragment = TestListFragment.newInstance()
        pushFragments(fragment, true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_test, container, false)


        val testStr: String? = arguments?.getString(TEST_ITEM)
        test = gson.fromJson(testStr,Test::class.java)


        /*(activity as BaseBackActivity).currentTag = TEST_FRAGMENT
        test.title?.let { (activity as ChangeToolbarListener).changeToolbar(TEST_FRAGMENT, it) }*/
        presenter.readCardForTest(test)

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
        val relation: String? = test.testRelation?.relation
        Log.d(TAG_LOG,"has card = $relation and has test = ${test.testDone}")
        if(relation.equals(AFTER_TEST) || relation.equals(WIN_GAME)) {
            tv_card_done.text = getText(R.string.test_was_done)
        } else {
            tv_card_done.text = getText(R.string.test_wasnt_done)
        }
        expand_text_view.text = test.desc
        tv_name.text = test.title
        test.card?.abstractCard?.photoUrl?.let {
            Glide.with(iv_portrait.context)
                .load(it)
                .into(iv_portrait)
        }
        hideLoading()
    }

    private fun initViews(view: View) {
        setActionBar(toolbar_back)
        test.title?.let { setToolbarTitle(toolbar_title, it) }
    }

    private fun setListeners() {
        tv_do_test.setOnClickListener(this)
        btn_back.setOnClickListener(this)
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

            R.id.btn_back -> performBackPressed()
        }
    }


}


