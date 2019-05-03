package com.example.historyquiz.ui.tests.add_test.question

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
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
import com.example.historyquiz.ui.tests.add_test.main.AddMainTestFragment
import com.example.historyquiz.ui.tests.test_item.main.TestFragment
import com.example.historyquiz.ui.tests.test_list.TestListFragment
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.QUESTION_NUMBER
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.TEST_ITEM
import com.example.historyquiz.utils.Const.TEST_MANY_TYPE
import com.example.historyquiz.utils.Const.TEST_ONE_TYPE
import com.example.historyquiz.utils.Const.gson
import com.jaredrummler.materialspinner.MaterialSpinner
import kotlinx.android.synthetic.main.fragment_add_question.*
import kotlinx.android.synthetic.main.toolbar_add_test.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class AddQuestionTestFragment : BaseFragment(), AddQuestionTestView, View.OnClickListener {

    @InjectPresenter
    lateinit var presenter: AddQuestionTestPresenter
    @Inject
    lateinit var presenterProvider: Provider<AddQuestionTestPresenter>
    @ProvidePresenter
    fun providePresenter(): AddQuestionTestPresenter = presenterProvider.get()

    lateinit var model: TestViewModel

    private var imageUri: Uri? = null

    lateinit var test: Test
    private lateinit var question: Question
    private var number: Int = 0

    private var answers: MutableList<Answer> = ArrayList()
    private var answerSize: Int = 0

    private var editTexts: MutableList<EditText> = ArrayList()
    private var checkBoxes: MutableList<CheckBox> = ArrayList()
    private var radioButtons: MutableList<RadioButton> = ArrayList()

    private var testType: String = TEST_ONE_TYPE

    private lateinit var liParams: LinearLayout.LayoutParams
    private lateinit var tiParams: LinearLayout.LayoutParams
    private lateinit var etParams: LinearLayout.LayoutParams
    private lateinit var rbParams: LinearLayout.LayoutParams

    private lateinit var checkListener: View.OnClickListener

    /*override fun showBottomNavigation(navigationView: NavigationView) {
        navigationView.hideBottomNavigation()
    }*/

    override fun performBackPressed() {
        beforeQuestion()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_question, container, false)
        hideLoading()
        arguments?.let {
            test = gson.fromJson(it.getString(TEST_ITEM), Test::class.java)
            number = it.getInt(QUESTION_NUMBER)
        }
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        setListeners()
        if(test.questions.size > number) {
            question = test.questions[number]
            setQuestionData()
        } else {
            question = Question()
            test.questions.add(question)
            setStartFields()
            setStartAnswers()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setQuestionData() {
        Log.d(TAG_LOG,"set question data")
        testType = question.type
        selectIndex()
        et_question.setText(question.question)
        for(i in question.answers.indices) {
            if(i >= checkBoxes.size) {
                addAnswer()
            }
            checkBoxes[i].isChecked = question.answers?.get(i)?.isRight ?: false
            editTexts[i].setText(question.answers?.get(i)?.text)
        }
        if(editTexts.size == 0) {
            setStartParams()
        }
        setStatus(Const.EDIT_STATUS)
        setWaitStatus(false)
    }

    private fun setStartParams() {
        setStartFields()
        setStartAnswers()
    }

    private fun selectIndex() {
        Log.d(TAG_LOG, "type = ${question.type}")
        when {

            TEST_ONE_TYPE.equals(testType) -> spinner.selectedIndex = 0

            TEST_MANY_TYPE.equals(testType) -> spinner.selectedIndex = 1
        }
    }


    private fun initViews(view: View) {
        setActionBar(toolbar_add_test)
        setToolbarTitle(toolbar_title, getString(R.string.add_question_number, number + 1))
        spinner.setItems(getString(R.string.test_type_one), getString(R.string.test_type_many))

        answers = ArrayList()
        editTexts = ArrayList()
        radioButtons = ArrayList()
        checkBoxes = ArrayList()
    }

    private fun setStartAnswers() {
        for (i in 0..2) {
            addAnswer()

        }
    }

    private fun setStartFields() {
        et_question.setText(getString(R.string.add_question_number, number + 1))
    }


    private fun setListeners() {
        btn_add_answer.setOnClickListener(this)
        btn_ok.setOnClickListener(this)
        btn_back.setOnClickListener(this)
        btn_forward.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
        spinner?.setOnItemSelectedListener(object : MaterialSpinner.OnItemSelectedListener<Any> {
            override fun onItemSelected(view: MaterialSpinner?, position: Int, id: Long, item: Any?) {
                when (position) {
                    0 -> {
                        changeToOneType(null)
                    }

                    1 -> {
                        changeToManyType()
                    }
                }
            }

        })
        checkListener = object: View.OnClickListener{
            override fun onClick(v: View?) {
                if(testType.equals(TEST_ONE_TYPE)) {
                    Log.d(TAG_LOG,"change on one type")
                    changeToOneType(v as CheckBox)

                }
            }

        }


    }

    private fun changeToManyType() {
        testType = TEST_MANY_TYPE

    }

    private fun changeToOneType(check: CheckBox?) {
        Log.d(TAG_LOG,"change to one type")
        testType = TEST_ONE_TYPE
        var count = if (check == null) 0 else 1
        val boxes: MutableList<CheckBox> = ArrayList()
        for (checkBox in checkBoxes) {
            if (checkBox.isChecked && check != checkBox) {
                Log.d(TAG_LOG,"add to box")
                boxes.add(checkBox)
            }
        }
        for (checkBox in boxes) {
            if (checkBox.isChecked) {
                count++
                checkBox.isChecked = if (count > 1 ) false else true
                Log.d(TAG_LOG,"checkbox is checked = ${checkBox.isChecked}")
            }
        }

    }

    private fun finishQuestions() {
        prepareQuestion()
        Log.d(TAG_LOG, "after preparing")
        if(checkQuestion()) {
            Log.d(TAG_LOG, "create test")
            presenter.createTest(test)
        }
    }

    override fun navigateToTest() {
        removeStackDownTo(number + 2)
        val args = Bundle()
        args.putString(TEST_ITEM, gson.toJson(test))
        val fragment = TestFragment.newInstance(args)
        pushFragments(fragment, true)
    }

    private fun nextQuestion() {
        if(number <= 9) {
            prepareQuestion()
            if(checkQuestion()) {
                val args: Bundle = Bundle()
                args.putString(TEST_ITEM, gson.toJson(test))
                args.putInt(QUESTION_NUMBER, ++number)
                val fragment = AddQuestionTestFragment.newInstance(args)
                pushFragments(fragment, true)
            }
        }
    }

    private fun checkQuestion(): Boolean {
        var flag: Boolean = true
        if(question.question == null || question.question?.trim().equals("")) {
            ti_question.error = "Введите вопрос!"
            flag = false
        } else {
            ti_question.error = null
        }
        var count: Int = 0
        for(i in question.answers.indices) {
            if(question.answers[i].isRight) {
                count++
            }
            if(question.answers[i].text == null || question.answers[i].text?.trim().equals("")) {
                editTexts[i].error = "Напишите вариант ответа"
                flag = false
            }else {
                editTexts[i].error = null
            }
        }
        if(count == 0) {
            flag = false
            showSnackBar("Выберите хотя бы один ответ!")
        }

        answers.clear()

        return flag
    }

    private fun beforeQuestion() {
        val args = Bundle()
        args.putString(TEST_ITEM, gson.toJson(test))
        var toQuestion = true
        if(number == 0) {
            toQuestion = false
        }
        args.putInt(QUESTION_NUMBER, --number)
        if(toQuestion) {
            removeStackDownTo(2)
            val fragment = AddQuestionTestFragment.newInstance(args)
            pushFragments(fragment, true)
        } else {
            removeStackDownTo(2)
            val fragment = AddMainTestFragment.newInstance(args)
            pushFragments(fragment, true)
        }
    }

    override fun onClick(v: View) {


        when (v.id) {

            R.id.btn_ok -> {
                Log.d(TAG_LOG, "finish quetions")
                finishQuestions()
            }

            R.id.btn_forward -> {
                nextQuestion()
            }

            R.id.btn_back -> {
                beforeQuestion()

                /*if(number != 0) {
                    beforeQuestion()
                } else {
                    val args: Bundle = Bundle()
                    args.putString(TEST_ITEM, gson.toJson(test))
                    val fragment = AddMainTestFragment.newInstance(args)

//                    (activity as BaseBackActivity).changeFragment(fragment, ADD_TEST_FRAGMENT)
                }*/
            }

            R.id.btn_cancel -> {
                MaterialDialog.Builder(activity as Context)
                    .title(R.string.question_dialog_title)
                    .content(R.string.question_dialog_content)
                    .positiveText(R.string.agree)
                    .negativeText(R.string.disagree)
                    .onPositive(object : MaterialDialog.SingleButtonCallback {
                        override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                            val fragment = TestListFragment.newInstance()
                            pushFragments(fragment, true)
//                            TestListActivity.start(activity as Activity)
                        }

                    })
                    .show()
            }

            R.id.btn_add_answer -> {
                if(answerSize < 5) {
                    addAnswer()

                }

            }

        }
    }

    /* fun finishQuestion() {
         prepareQuestion()
         addTestView!!.createTest()
     }
 */
    private fun addAnswer() {
        answerSize++
        val view: View = layoutInflater.inflate(R.layout.layout_item_add_question,li_answers,false)
        val editText: EditText = view.findViewById(R.id.et_answer)
        val checkBox: CheckBox = view.findViewById(R.id.checkbox)

        editText.setText("Answer $answerSize")
        checkBox.setOnClickListener(checkListener)

        editTexts?.add(editText)
        checkBoxes?.add(checkBox)

        li_answers.addView(view)
    }


    private fun prepareQuestion() {

        for (i in checkBoxes!!.indices) {
            val answer = Answer()
            answer.text = editTexts!![i].text.toString()
            if (checkBoxes!![i].isChecked) {
                answer.isRight = true
            }
            answers!!.add(answer)
        }

        question!!.question = et_question.text.toString()
        question!!.answers = answers.toMutableList()
        question.id = number.toString()
        question.type = testType

    }

    private fun addPhoto() {
        val photoPickerIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        photoPickerIntent.addCategory(Intent.CATEGORY_OPENABLE)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if (resultCode == android.support.v7.app.AppCompatActivity.RESULT_OK) {
            imageUri = data!!.data
        }
    }

    companion object {

        private val RESULT_LOAD_IMG = 0

        fun newInstance(args: Bundle): Fragment {
            val fragment = AddQuestionTestFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
