package com.example.historyquiz.ui.tests.test_item.finish

import android.support.v7.app.AppCompatActivity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.test.Question
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.tests.add_test.TestViewModel
import com.example.historyquiz.ui.tests.test_item.check_answers.AnswersFragment
import com.example.historyquiz.ui.tests.test_item.check_answers.AnswersFragment.Companion.ANSWERS_TYPE
import com.example.historyquiz.ui.tests.test_item.check_answers.AnswersFragment.Companion.RIGHT_ANSWERS
import com.example.historyquiz.ui.tests.test_item.check_answers.AnswersFragment.Companion.WRONG_ANSWERS
import com.example.historyquiz.ui.tests.test_item.main.TestFragment
import com.example.historyquiz.ui.tests.test_item.winned_card.TestCardFragment
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.TEST_ITEM
import com.google.gson.Gson
import javax.inject.Inject

class FinishFragment : BaseFragment(), FinishView, View.OnClickListener {

    @Inject
    lateinit var gson: Gson

    lateinit var model: TestViewModel

    @InjectPresenter
    lateinit var presenter: FinishPresenter

    lateinit var test: Test
    var rightQuestions: MutableList<Question> = ArrayList()
    var wrongQuestions: MutableList<Question> = ArrayList()
    var procent: Long = 0

   /* override fun onBackPressed() {
       *//* val args: Bundle = Bundle()
        args.putString(TEST_JSON, gsonConverter.toJson(test))
        val fragment = FinishFragment.newInstance(args)
        (activity as BaseBackActivity).changeFragment(fragment)*//*
    }

    override fun onOk() {
        btn_finish_test.performClick()
    }*/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_finish_test, container, false)
       /* (activity as BaseBackActivity).currentTag = TestActivity.FINISH_FRAGMENT
        (activity as ChangeToolbarListener).changeToolbar(FINISH_FRAGMENT,"Результат")*/

        test = gson.fromJson(arguments?.getString(TEST_ITEM),Test::class.java)
        for(question in test.questions) {
            if(question.userRight) {
                rightQuestions.add(question)
            } else {
                wrongQuestions.add(question)
            }
        }
        test.rightQuestions = rightQuestions
        test.wrongQuestions = wrongQuestions

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setToolbar()
        tv_right_answers.text = rightQuestions.size.toString()
        tv_wrong_answers.text = wrongQuestions.size.toString()
        setCardText()

        btn_finish_test.setOnClickListener(this)
        li_wrong_answers.setOnClickListener(this)
        li_right_answers.setOnClickListener(this)
        li_winned_card.setOnClickListener(this)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setToolbar() {
        setActionBar(toolbar)
        setActionBarTitle(R.string.test_result)
    }

    fun setCardText() {
        procent = Math.round((test.rightQuestions.size.toDouble() / test.questions.size.toDouble()) * 100)
        Log.d(TAG_LOG, "procent = $procent")
        if (procent >= 80) {
            Log.d(TAG_LOG, "finish it")
            tv_winned_card.text = test.card?.abstractCard?.name
            test.testDone = true
            presenter.finishTest(test)


        } else {
            tv_winned_card.text = getText(R.string.test_failed)
        }
    }


    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btn_finish_test -> {
                for(question in test.questions) {
                    question.userRight = false
                    for(answer in question.answers) {
                        answer.userClicked = false
                    }
                }
                removeStackDownTo()
                val args: Bundle = Bundle()
                args.putString(TEST_ITEM, gson.toJson(test))
                val fragment = TestFragment.newInstance(args)
                pushFragments(fragment, true)
//                TestActivity.start(activity as Activity,test)
            }

            R.id.li_wrong_answers -> {
                if(wrongQuestions.size > 0) {
                    prepareAnswers(WRONG_ANSWERS)
                }
            }

            R.id.li_right_answers -> {
                if(rightQuestions.size > 0) {
                    prepareAnswers(RIGHT_ANSWERS)
                }
            }

            R.id.li_winned_card -> {
                if(procent >= 80) {
                    val args: Bundle = Bundle()
                    args.putString(TEST_ITEM, gson.toJson(test))
                    val fragment = TestCardFragment.newInstance(args)
                    pushFragments(fragment, true)
//                    (activity as BaseBackActivity).changeFragment(fragment, WINNED_FRAGMENT)
                }

            }
        }
    }

    fun prepareAnswers(type: String) {
        model = activity?.run {
            ViewModelProviders.of(this).get(TestViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        model.selectNumber(0)
        model.selectTest(test)
        val args: Bundle = Bundle()
        args.putString(ANSWERS_TYPE, type)
        args.putString(TEST_ITEM, gson.toJson(test))
        val fragment = AnswersFragment.newInstance(args)
        pushFragments(fragment, true)
//        (activity as BaseBackActivity).changeFragment(fragment, ANSWERS_FRAGMENT + 0)
      /*  activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, AnswersFragment.newInstance(args))
                .addToBackStack("AddQuestionFragment")
                .commit()*/
    }


    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = FinishFragment()
            fragment.arguments = args
            return fragment
        }
    }
}