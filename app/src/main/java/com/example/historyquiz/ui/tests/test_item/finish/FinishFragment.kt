package com.example.historyquiz.ui.tests.test_item.finish

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
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
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.NEW_ONES
import com.example.historyquiz.utils.Const.OLD_ONES
import com.example.historyquiz.utils.Const.QUESTION_NUMBER
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.TEST_ITEM
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.fragment_finish_test.*
import javax.inject.Inject
import javax.inject.Provider

class FinishFragment : BaseFragment(), FinishView, View.OnClickListener {

    lateinit var model: TestViewModel

    @InjectPresenter
    lateinit var presenter: FinishPresenter
    @Inject
    lateinit var presenterProvider: Provider<FinishPresenter>
    @ProvidePresenter
    fun providePresenter(): FinishPresenter = presenterProvider.get()

    lateinit var test: Test
    lateinit var rightQuestions: MutableList<Question>
    lateinit var wrongQuestions: MutableList<Question>
    var procent: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       val view = inflater.inflate(R.layout.fragment_finish_test, container, false)
       test = gson.fromJson(arguments?.getString(TEST_ITEM), Test::class.java)
        Log.d(TAG_LOG, "createFinishView")
       return view
   }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setToolbar()
        btn_finish_test.setOnClickListener(this)
        li_wrong_answers.setOnClickListener(this)
        li_right_answers.setOnClickListener(this)
        li_winned_card.setOnClickListener(this)
        if (OLD_ONES.equals(test.type)) {
            Log.d(TAG_LOG, "findUserAnswers")
            presenter.findUserAnswers(test)
        } else {
            checkAnswers(test)
        }
        setStatus(Const.EDIT_STATUS)
        setWaitStatus(false)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun checkAnswers(test: Test) {
        Log.d(TAG_LOG, "checkAnswers")
        rightQuestions = ArrayList()
        wrongQuestions = ArrayList()
        for (question in test.questions) {
            if (question.userRight) {
                rightQuestions.add(question)
            } else {
                wrongQuestions.add(question)
            }
        }
        test.rightQuestions = rightQuestions
        test.wrongQuestions = wrongQuestions
        setFinishData()

    }

    private fun setToolbar() {
        setActionBar(toolbar)
        setActionBarTitle(R.string.test_result)
    }

    fun setFinishData() {
        tv_right_answers.text = rightQuestions.size.toString()
        tv_wrong_answers.text = wrongQuestions.size.toString()
        procent = Math.round((test.rightQuestions.size.toDouble() / test.questions.size.toDouble()) * 100)
        Log.d(TAG_LOG, "procent = $procent")
        tv_procent.text = procent.toString()
        test.testDone = true
        test.procent = procent
        presenter.checkUserCard(test)
    }

    override fun setCardData(hasCard: Boolean) {
        if (NEW_ONES.equals(test.type)) {
            Log.d(TAG_LOG, "finish it")
            if(!hasCard && procent >= 70) {
                tv_winned_card.text = test.card?.abstractCard?.name
                li_winned_card.visibility = View.VISIBLE
            }
            presenter.finishTest(test)
            presenter.updateAfterTest(test)
        }
        hideLoading()
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btn_finish_test -> {
              /*  for(question in test.questions) {
                    question.userRight = false
                    for(answer in question.answers) {
                        answer.userClicked = false
                    }
                }*/
                removeStackDownTo(2)
                test.testDone = true
                test.type = OLD_ONES
                val args: Bundle = Bundle()
                args.putString(TEST_ITEM, gson.toJson(test))
                val fragment = TestFragment.newInstance(args)
                pushFragments(fragment, true)
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
                /*if(procent >= 80) {
                    val args: Bundle = Bundle()
                    args.putString(TEST_ITEM, gson.toJson(test))
                    val fragment = TestCardFragment.newInstance(args)
                    pushFragments(fragment, true)
                }*/

            }
        }
    }

    fun prepareAnswers(type: String) {
       /* model = activity?.run {
            ViewModelProviders.of(this).get(TestViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        model.selectNumber(0)
        model.selectTest(test)*/
        val args: Bundle = Bundle()
        args.putInt(QUESTION_NUMBER, 0)
        args.putString(ANSWERS_TYPE, type)
        args.putString(TEST_ITEM, gson.toJson(test))
        val fragment = AnswersFragment.newInstance(args)
        pushFragments(fragment, true)
    }

    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = FinishFragment()
            fragment.arguments = args
            return fragment
        }
    }
}