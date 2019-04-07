package com.example.historyquiz.ui.tests.test_item.check_answers

import android.arch.lifecycle.ViewModelProviders
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.CompoundButtonCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.test.Answer
import com.example.historyquiz.model.test.Question
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.tests.add_test.TestViewModel
import com.example.historyquiz.ui.tests.test_item.finish.FinishFragment
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.TEST_ITEM
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.fragment_question.*
import kotlinx.android.synthetic.main.toolbar_back_cancel_forward.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class AnswersFragment : BaseFragment(), AnswersView, View.OnClickListener {

    @InjectPresenter
    lateinit var presenter: AnswersPresenter
    @Inject
    lateinit var presenterProvider: Provider<AnswersPresenter>
    @ProvidePresenter
    fun providePresenter(): AnswersPresenter = presenterProvider.get()

    lateinit var model: TestViewModel
    private lateinit var question: Question
    private lateinit var test: Test
    private lateinit var type: String
    private var listSize: Int = 0
    private var number: Int = 0

    private lateinit var colorStateList: ColorStateList
    private lateinit var rightStateList: ColorStateList

    private var textViews: MutableList<TextView>? = null
    private var checkBoxes: MutableList<CheckBox>? = null
    private var radioButtons: MutableList<RadioButton>? = null

    override fun performBackPressed() {
        beforeQuestion()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_question, container, false)

        type = arguments?.getString(ANSWERS_TYPE)!!
        model = activity?.run {
            ViewModelProviders.of(this).get(TestViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        model.test.value?.let {
            test = it
        }
        model.number.value?.let {
            number = it
        }
        /*val testStr: String = arguments?.getString(TEST_ITEM)!!
        number = arguments?.getInt(QUESTION_NUMBER)!!
        test = gson.fromJson(testStr, Test::class.java)*/
        if(type.equals(RIGHT_ANSWERS)) {
            question =  test.rightQuestions[number]
            listSize = test.rightQuestions.size
        } else {
           question = test.wrongQuestions[number]
            listSize = test.wrongQuestions.size

        }

        /*(activity as BaseBackActivity).currentTag = TestActivity.ANSWERS_FRAGMENT + number
        (activity as ChangeToolbarListener).changeToolbar(ANSWERS_FRAGMENT,"Вопрос ${number+1}/${listSize}")*/
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        setListeners()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setToolbar() {
        setActionBar(toolbar_back_cancel_forward)
        setToolbarTitle(toolbar_title, getString(R.string.question_number, number + 1, listSize))
    }

    private fun initViews(view: View) {
        setToolbar()
        textViews = ArrayList()
        radioButtons = ArrayList()
        checkBoxes = ArrayList()

        if(number == (listSize-1)) {
            btn_next_question.visibility = View.GONE
            btn_forward.visibility = View.GONE
            btn_cancel.visibility = View.GONE
            btn_finish_questions.visibility = View.VISIBLE
            btn_ok.visibility = View.VISIBLE
//            (activity as ChangeToolbarListener).showOk(true)
        }

        tv_question.text = question.question

        setStartAnswers()
    }

    private fun setStartAnswers() {
        colorStateList = ColorStateList(
                arrayOf(intArrayOf(-android.R.attr.state_checked), // unchecked
                        intArrayOf(android.R.attr.state_checked))// checked
                ,
                intArrayOf(Color.parseColor("#FFFFFF"), Color.parseColor("#DC143C"))
        )

        rightStateList = ColorStateList(
                arrayOf(intArrayOf(-android.R.attr.state_checked), // unchecked
                        intArrayOf(android.R.attr.state_checked))// checked
                ,
                intArrayOf(Color.parseColor("#FFFFFF"), Color.parseColor("#00cc00"))
        )

        for (answer in question.answers) {
            addAnswer(answer)
        }
        for(tv in textViews!!) {
            Log.d(TAG_LOG,"content = " + tv.text)
        }


    }


    private fun setListeners() {
        btn_finish_questions!!.setOnClickListener(this)
        btn_next_question!!.setOnClickListener(this)

        btn_ok.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
        btn_forward.setOnClickListener(this)
        btn_back.setOnClickListener(this)

        btn_next_question.text = getString(R.string.next_question)
    }

    private fun beforeQuestion() {
        if(number > 0) {
            val args: Bundle = Bundle()
            args.putString(TEST_ITEM, gson.toJson(test))
            args.putString(ANSWERS_TYPE, type)
            args.putInt(QUESTION_NUMBER, --number)
            val fragment = AnswersFragment.newInstance(args)
//            (activity as BaseBackActivity).changeFragment(fragment, ANSWERS_FRAGMENT + number)
        } else {
            finishQuestions()
        }
    }

    private fun finishQuestions() {
        removeStackDownTo()
        val args: Bundle = Bundle()
        args.putString(TEST_ITEM, gson.toJson(test))
        val fragment = FinishFragment.newInstance(args)
        pushFragments(fragment, true)
//        (activity as BaseBackActivity).changeFragment(fragment, FINISH_FRAGMENT)
    }

    private fun nextQuestion() {
        model.selectTest(test)
        model.selectNumber(++number)
        val args: Bundle = Bundle()
        args.putString(TEST_ITEM, gson.toJson(test))
        args.putString(ANSWERS_TYPE,type)
        args.putInt(QUESTION_NUMBER, ++number)
        val fragment = AnswersFragment.newInstance(args)
        pushFragments(fragment, true)
//        (activity as BaseBackActivity).changeFragment(fragment, ANSWERS_FRAGMENT + number)
    }

    override fun onClick(v: View) {


        when (v.id) {

            R.id.btn_finish_questions -> {
                finishQuestions()
               /* val args: Bundle = Bundle()
                args.putString(TEST_JSON, gsonConverter.toJson(test))
                activity!!.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, TestFragment.newInstance(args))
                        .addToBackStack("AddQuestionFragment")
                        .commit()        */
            }

            R.id.btn_next_question -> {
                nextQuestion()
              /*  val args: Bundle = Bundle()
                args.putString(TEST_JSON, gsonConverter.toJson(test))
                args.putInt(QUESTION_NUMBER, ++number)
                args.putString(ANSWERS_TYPE, type)
                activity!!.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, AnswersFragment.newInstance(args))
                        .addToBackStack("AddQuestionFragment")
                        .commit()*/
            }

            R.id.btn_cancel -> finishQuestions()

            R.id.btn_back -> beforeQuestion()

            R.id.btn_ok -> finishQuestions()

            R.id.btn_forward -> nextQuestion()

        }
    }

    private fun addAnswer(answer: Answer) {
        val view: LinearLayout = layoutInflater.inflate(R.layout.layout_item_question,li_answers,false) as LinearLayout
        val tvAnswer: TextView = view.findViewWithTag("tv_answer")
        tvAnswer.text = answer.text
        textViews?.add(tvAnswer)
        val checkBox: CheckBox = view.findViewWithTag("checkbox")
        if(answer.isRight) {
            CompoundButtonCompat.setButtonTintList(checkBox, rightStateList)
            checkBox.isChecked = true
        }
        checkBoxes?.add(checkBox)
        li_answers.addView(view)
        if(type.equals(WRONG_ANSWERS) && !answer.isRight && answer.userClicked != answer.isRight) {
            Log.d(TAG_LOG,"change checkbox color")
            Log.d(Const.TAG_LOG,"content tv = ${tvAnswer.text}")
            Log.d(TAG_LOG,"answer.isRight = ${answer.isRight} and userClick = ${answer.userClicked}")
            CompoundButtonCompat.setButtonTintList(checkBox, colorStateList)
            checkBox.isChecked = true
        }
        checkBox.isEnabled = false
    }

    companion object {

        private val RESULT_LOAD_IMG = 0

        const val QUESTION_NUMBER = "queston_number"

        const val RIGHT_ANSWERS = "right_answers"
        const val WRONG_ANSWERS = "wrong_answers"
        const val ANSWERS_TYPE = "type_answers"
        const val CARD_JSON = "card_json"


        fun newInstance(args: Bundle): Fragment {
            val fragment = AnswersFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
