package com.example.historyquiz.ui.tests.test_item.question


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.test.Answer
import com.example.historyquiz.model.test.Question
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.tests.add_test.TestViewModel
import com.example.historyquiz.ui.tests.test_item.finish.FinishFragment
import com.example.historyquiz.utils.Const.QUESTION_NUMBER
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.TEST_ITEM
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.layout_question.*
import kotlinx.android.synthetic.main.toolbar_back_cancel_forward.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider


class QuestionFragment : BaseFragment(), QuestionView, View.OnClickListener {

    @InjectPresenter
    lateinit var presenter: QuestionPresenter
    @Inject
    lateinit var presenterProvider: Provider<QuestionPresenter>
    @ProvidePresenter
    fun providePresenter(): QuestionPresenter = presenterProvider.get()

    lateinit var model: TestViewModel

    private lateinit var question: Question
    private lateinit var test: Test
    private var number: Int = 0

    private var textViews: MutableList<TextView> = ArrayList()
    private var checkBoxes: MutableList<CheckBox> = ArrayList()

   /* override fun onBackPressed() {
        shouldCancel()
    }

    override fun onCancel() {
        shouldCancel()
    }

    override fun onForward() {
        nextQuestion()
    }

    override fun onOk() {
        finishQuestions()
    }*/

    override fun performBackPressed() {
        shouldCancel()
    }

    fun shouldCancel() {
        MaterialDialog.Builder(activity as Context)
                .title(R.string.question_dialog_title)
                .content(R.string.question_dialog_content)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .onPositive(object :MaterialDialog.SingleButtonCallback {
                    override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                        for(question in test.questions) {
                            question.userRight = false
                            for(answer in question.answers) {
                                answer.userClicked = false
                            }
                        }
                        removeStackDownTo(number + 1)
                        performBackPressed()
                        /*val args = Bundle()
                        args.putString(TEST_ITEM, gson.toJson(test))
                        val fragment = TestFragment.newInstance(args)
                        pushFragments(fragment, true)*/
                    }

                })
                .show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_question, container, false)

        val testStr: String = arguments?.getString(TEST_ITEM)!!
        number = arguments?.getInt(QUESTION_NUMBER)!!
        test = gson.fromJson(testStr, Test::class.java)
        question = test.questions[number]
        /* model = activity?.run {
             ViewModelProviders.of(this).get(TestViewModel::class.java)
         } ?: throw Exception("Invalid Activity")
         model.test.value?.let {
             test = it
         }
         model.number.value?.let {
             number = it
         }*/

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        setListeners()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViews(view: View) {
        setToolbar()
        if(number + 1 == test.questions.size) {
            btn_ok.visibility = View.VISIBLE
        } else {
            btn_ok.visibility = View.GONE
        }
        tv_question.text = question.question

        setStartAnswers()
    }

    private fun setToolbar() {
        setActionBar(toolbar_back_cancel_forward)
        setToolbarTitle(toolbar_title, getString(R.string.question_number, number + 1, test.questions.size))
    }

    private fun setStartAnswers() {

        for (answer in question.answers) {
            addAnswer(answer)
        }
        for(tv in textViews!!) {
            Log.d(TAG_LOG,"content = " + tv.text)
        }

        if(number == (test.questions.size-1)) {
            btn_next_question.visibility = View.GONE
            btn_finish_questions.visibility = View.VISIBLE
        }
    }


    private fun setListeners() {
        btn_finish_questions!!.setOnClickListener(this)
        btn_next_question!!.setOnClickListener(this)
        btn_ok.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
        btn_forward.setOnClickListener(this)

        btn_back.visibility = View.GONE
    }

    private fun finishQuestions() {
        removeStackDownTo(test.questions.size)
        checkAnswers()
        val args: Bundle = Bundle()
        args.putString(TEST_ITEM, gson.toJson(test))
        val fragment = FinishFragment.newInstance(args)
        pushFragments(fragment, true)
    }

    private fun nextQuestion() {
        checkAnswers()
        val args: Bundle = Bundle()
        args.putString(TEST_ITEM, gson.toJson(test))
        args.putInt(QUESTION_NUMBER, ++number)
        val fragment = QuestionFragment.newInstance(args)
        pushFragments(fragment, true)
    }
    override fun onClick(v: View) {


        when (v.id) {

            R.id.btn_finish_questions -> {
               finishQuestions()
            }

            R.id.btn_next_question -> {
                nextQuestion()
            }

            R.id.btn_ok -> finishQuestions()

            R.id.btn_cancel -> shouldCancel()

            R.id.btn_back -> shouldCancel()

            R.id.btn_forward -> nextQuestion()

        }
    }

    private fun checkAnswers() {
        Log.d(TAG_LOG,"questioin = ${question.question}")
        question.userRight = true
        for(i in question.answers.indices) {
            val answer: Answer = question.answers[i]
                if(checkBoxes.get(i).isChecked) {
                    answer.userClicked = true
                    Log.d(TAG_LOG,"checked answer = ${answer.text}")
                }
            if(answer.userClicked != answer.isRight) {
                question.userRight = false
                Log.d(TAG_LOG, "wrong i = $i and answer = " + question.answers[i])
            }
            Log.d(TAG_LOG,"userclick = ${answer.userClicked} and q right = ${answer.isRight} and content = ${answer.text}")
        }
    }

    private fun addAnswer(answer: Answer) {
        val view: LinearLayout = layoutInflater.inflate(R.layout.layout_item_question,li_answers,false) as LinearLayout
        val tvAnswer: TextView = view.findViewWithTag("tv_answer")
        tvAnswer.text = answer.text
        textViews?.add(tvAnswer)
        Log.d(TAG_LOG,"content tv = ${tvAnswer.text}")
        val checkBox: CheckBox = view.findViewWithTag("checkbox")
        checkBoxes?.add(checkBox)
        Log.d(TAG_LOG,"checkboxes size = ${checkBoxes?.size}")
        li_answers.addView(view)
    }

    companion object {

        private val RESULT_LOAD_IMG = 0


        const val RIGHT_ANSWERS = "right_answers"
        const val WRONG_ANSWERS = "wrong_answers"
        const val ANSWERS_TYPE = "type_answers"
        const val CARD_JSON = "card_json"


        fun newInstance(args: Bundle): Fragment {
            val fragment = QuestionFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
