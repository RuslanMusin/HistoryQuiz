package com.example.historyquiz.ui.game.play.question

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.example.historyquiz.R
import com.example.historyquiz.model.test.Answer
import com.example.historyquiz.model.test.Question
import com.example.historyquiz.ui.game.play.PlayView
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.layout_question.*
import java.util.*

class GameQuestionFragment : Fragment(), View.OnClickListener {

    private lateinit var question: Question

    private var textViews: MutableList<TextView>? = null
    private var checkBoxes: MutableList<CheckBox> = ArrayList()

    var clickable = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_question, container, false)

        question = gson.fromJson(arguments?.getString(QUESTION_JSON)!!, Question::class.java)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        setListeners()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViews(view: View) {
        textViews = ArrayList()
        checkBoxes = ArrayList()

        tv_question.text = question.question

        setStartAnswers()
    }

    private fun setStartAnswers() {
        for (answer in question.answers) {
            addAnswer(answer)
        }
    }


    private fun setListeners() {
        btn_next_question!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_next_question -> {
                if (clickable) {
                    clickable = false
                    checkAnswers()
                    (parentFragment as PlayView).onAnswer(question.userRight)
                }
            }
        }
    }

    private fun checkAnswers() {
        Log.d(Const.TAG_LOG,"questioin = ${question.question}")
        question.userRight = true
        for(i in question.answers.indices) {
            val answer: Answer = question.answers[i]
            if(checkBoxes.get(i).isChecked) {
                answer.userClicked = true
                Log.d(Const.TAG_LOG,"checked answer = ${answer.text}")
            }
            if(answer.userClicked != answer.isRight) {
                question.userRight = false
                Log.d(Const.TAG_LOG, "wrong i = $i and answer = " + question.answers[i])
            }
            Log.d(Const.TAG_LOG,"userclick = ${answer.userClicked} and q right = ${answer.isRight} and content = ${answer.text}")
        }
    }

    private fun addAnswer(answer: Answer) {
        val view: LinearLayout = layoutInflater.inflate(R.layout.layout_item_question, li_answers, false) as LinearLayout
        val tvAnswer: TextView = view.findViewWithTag("tv_answer")
        tvAnswer.text = answer.text
        textViews?.add(tvAnswer)
        val checkBox: CheckBox = view.findViewWithTag("checkbox")
        checkBoxes?.add(checkBox)
        li_answers.addView(view)
    }

    companion object {

        const val QUESTION_JSON = "queston_json"

        //нужно где-то в другой части приложения...
        const val QUESTION_NUMBER = "queston_number"

        fun newInstance(question: Question): Fragment {
            val fragment = GameQuestionFragment()
            val args: Bundle = Bundle()
            args.putString(QUESTION_JSON, gson.toJson(question))
            fragment.arguments = args
            return fragment
        }
    }
}
