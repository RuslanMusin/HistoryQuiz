package com.example.historyquiz.ui.tests.add_test.main

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.example.historyquiz.R
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.epoch.Epoch
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.cards.add_card_list.AddCardListFragment
import com.example.historyquiz.ui.epoch.EpochListFragment
import com.example.historyquiz.ui.tests.add_test.TestViewModel
import com.example.historyquiz.ui.tests.add_test.question.AddQuestionTestFragment
import com.example.historyquiz.ui.tests.test_item.check_answers.AnswersFragment.Companion.QUESTION_NUMBER
import com.example.historyquiz.utils.Const.ADD_CARD_CODE
import com.example.historyquiz.utils.Const.ADD_EPOCH_CODE
import com.example.historyquiz.utils.Const.CARD_ITEM
import com.example.historyquiz.utils.Const.EPOCH_KEY
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.TEST_ITEM
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.fragment_add_test.*
import kotlinx.android.synthetic.main.toolbar_back.*
import javax.inject.Inject
import javax.inject.Provider

class AddMainTestFragment : BaseFragment(), AddMainTestView, View.OnClickListener {

    @InjectPresenter
    lateinit var presenter: AddMainTestPresenter
    @Inject
    lateinit var presenterProvider: Provider<AddMainTestPresenter>
    @ProvidePresenter
    fun providePresenter(): AddMainTestPresenter = presenterProvider.get()

    lateinit var model: TestViewModel

    private var imageUri: Uri? = null

    lateinit var test: Test

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_test, container, false)
        return view
    }

    private fun setTestData() {
        et_test_name.setText(test?.title)
        et_test_desc.setText(test?.desc)
        tv_added_card.text =test?.card?.abstractCard?.name
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        setListeners()

        model = activity?.run {
            ViewModelProviders.of(this).get(TestViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        test = Test()
        model.test.value?.let {
            test = it
        }
        hideLoading()
     /*   if(arguments == null) {
            test = Test()
        } else {
            test = gson.fromJson(arguments?.getString(TEST_ITEM),Test::class.java)
            setTestData()
        }*/

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViews(view: View) {
        setToolbar()
    }

    private fun setToolbar() {
        setActionBar(toolbar_back)
        setToolbarTitle(toolbar_title, getString(R.string.add_test))
    }

    private fun setListeners() {
        btn_create_questions.setOnClickListener(this)
        tv_add_card.setOnClickListener(this)
        btn_back.setOnClickListener(this)
        li_choose_epoch.setOnClickListener(this)
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
                    model.selectNumber(0)
                    model.selectTest(test)
                    Log.d(TAG_LOG, "Create quesitons")
                    val fragment = AddQuestionTestFragment.newInstance(args)
//                    showFragment(this, fragment)
                    pushFragments(fragment, true)
//                    (activity as BaseBackActivity).changeFragment(fragment, ADD_QUESTION_FRAGMENT + 0)
                } else {

                }
            }

            R.id.tv_add_card -> {
               val fragment = AddCardListFragment.newInstance()
                fragment.setTargetFragment(this, ADD_CARD_CODE)
                showFragment(this, fragment)

            }

            R.id.li_choose_epoch -> {
                val fragment = EpochListFragment.newInstance()
                fragment.setTargetFragment(this, ADD_EPOCH_CODE)
                showFragment(this, fragment)
            }

            R.id.btn_back -> performBackPressed()
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

        if (reqCode == ADD_CARD_CODE && resultCode == android.support.v7.app.AppCompatActivity.RESULT_OK) {
            li_added_card.visibility = View.VISIBLE
            val card = gson.fromJson(data!!.getStringExtra(CARD_ITEM), Card::class.java)
            tv_added_card.text = card.abstractCard.name
            test!!.card = card
            Glide.with(this)
                .load(card.abstractCard.photoUrl)
                .into(iv_cover)
            tv_test_card_name.setError(null);
        }

        if (reqCode == ADD_EPOCH_CODE && resultCode == RESULT_OK) {
            val epoch = gson.fromJson(data!!.getStringExtra(EPOCH_KEY), Epoch::class.java)
            tv_epoch!!.text = epoch.name
            test!!.epoch = epoch
            test!!.epochId = epoch.id
            test!!.card?.epochId = epoch.id
        }
    }

    companion object {

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
