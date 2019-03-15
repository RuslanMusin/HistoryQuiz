package com.example.historyquiz.ui.tests.add_test.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.bumptech.glide.Glide
import com.example.historyquiz.R
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.tests.add_test.question.AddQuestionTestFragment
import com.example.historyquiz.ui.tests.test_item.check_answers.AnswersFragment.Companion.QUESTION_NUMBER
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.TEST_ITEM
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_add_test.*
import javax.inject.Inject

class AddMainTestFragment : BaseFragment(), AddMainTestView, View.OnClickListener {

    @Inject
    lateinit var gson: Gson

    @InjectPresenter
    lateinit var presenter: AddMainTestPresenter

    private var imageUri: Uri? = null

    private var test: Test? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_test, container, false)


        return view
    }

    private fun setTestData() {
        et_test_name.setText(test?.title)
        et_test_desc.setText(test?.desc)
        tv_added_cards.text =test?.card?.abstractCard?.name
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        setListeners()

        if(arguments == null) {
            test = Test()
        } else {
            test = gson.fromJson(arguments?.getString(TEST_ITEM),Test::class.java)
            setTestData()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViews(view: View) {

    }

    private fun setListeners() {
        btn_create_questions.setOnClickListener(this)
        btn_add_card.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.btn_create_questions -> {
                test!!.title = et_test_name.text.toString()
                test!!.desc = et_test_desc.text.toString()

//                addTestView!!.setTest(test!!)

                if(checkTest()) {//
                    val args: Bundle = Bundle()
                    args.putString(TEST_ITEM, gson.toJson(test))
                    args.putInt(QUESTION_NUMBER, 0)
                    val fragment = AddQuestionTestFragment.newInstance(args)
                    pushFragments(fragment, true)
//                    (activity as BaseBackActivity).changeFragment(fragment, ADD_QUESTION_FRAGMENT + 0)
                } else {

                }
            }

            R.id.btn_add_card -> {
                val intent = Intent(activity, AddCardListActivity::class.java)
                startActivityForResult(intent, ADD_CARD)

            }
        }
    }

    private fun checkTest(): Boolean {
        var flag: Boolean = if(test == null) false else true
        test?.let {
            if(it.title == null  || it.title?.trim().equals("")) {
                ti_test_name.error = "Введите название теста!"
                flag = false
            } else {
                ti_test_name.error = null
            }
            if(it.desc == null  || it.desc?.trim().equals("")) {
                ti_test_desc.error = "Введите описание теста!"

                flag = false

            } else {
                ti_test_desc?.error = null
            }
            if(it.card == null) {
                tv_test_card_name.requestFocus();
                tv_test_card_name.setError("Добавьте карту!");
                flag = false
            } else {
                tv_test_card_name.setError(null);
            }
        }
        Log.d(TAG_LOG, "flag = $flag")
        return flag
    }
    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if (reqCode == ADD_CARD && resultCode == Activity.RESULT_OK) {
            val card = gson.fromJson(data!!.getStringExtra(CARD_EXTRA), Card::class.java)
            tv_added_cards.text = card.abstractCard.name
            test!!.card = card
            Glide.with(this)
                .load(card.abstractCard.photoUrl)
                .into(iv_cover)
            tv_test_card_name.setError(null);
        }
    }

    companion object {

        const val ADD_CARD: Int = 1

        fun newInstance(args: Bundle): Fragment {
            val fragment = AddMainTestFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment {
            val fragment = AddMainTestFragment()
            return fragment
        }
    }
}
