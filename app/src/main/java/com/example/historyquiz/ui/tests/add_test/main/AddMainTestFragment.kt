package com.example.historyquiz.ui.tests.add_test.main

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.example.historyquiz.R
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.epoch.Epoch
import com.example.historyquiz.model.test.Link
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.cards.add_card_list.AddCardListFragment
import com.example.historyquiz.ui.epoch.EpochListFragment
import com.example.historyquiz.ui.tests.add_test.TestViewModel
import com.example.historyquiz.ui.tests.add_test.add_link.AddLinkFragment
import com.example.historyquiz.ui.tests.add_test.question.AddQuestionTestFragment
import com.example.historyquiz.ui.tests.test_item.check_answers.AnswersFragment.Companion.QUESTION_NUMBER
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.ADD_CARD_CODE
import com.example.historyquiz.utils.Const.ADD_EPOCH_CODE
import com.example.historyquiz.utils.Const.ADD_LINK_CODE
import com.example.historyquiz.utils.Const.CARD_ITEM
import com.example.historyquiz.utils.Const.EPOCH_KEY
import com.example.historyquiz.utils.Const.HAS_DEFAULT
import com.example.historyquiz.utils.Const.LINK_ITEM
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
    private lateinit var checkListener: View.OnClickListener

    private var links: MutableList<Link> = ArrayList()

    private var imageViews: MutableList<ImageView> = ArrayList()
    private var liViews: MutableList<LinearLayout> = ArrayList()

 /*   override fun showBottomNavigation(navigationView: NavigationView) {
        navigationView.hideBottomNavigation()
    }*/

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
        hideLoading()
        if(arguments == null) {
            test = Test()
        } else {
            test = gson.fromJson(arguments?.getString(TEST_ITEM),Test::class.java)
            setTestData()
        }
        setStatus(Const.EDIT_STATUS)
        setWaitStatus(false)
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
        tv_add_epoch.setOnClickListener(this)
        tv_add_link.setOnClickListener(this)

        checkListener = object: View.OnClickListener{
            override fun onClick(v: View?) {
                val ivRemove = v as ImageView
                val index = imageViews.indexOf(ivRemove)
                val liSkill = liViews[index]
                li_added_links.removeView(liSkill)
                liViews.removeAt(index)
                imageViews.removeAt(index)
                links.removeAt(index)
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.btn_create_questions -> {
                test!!.title = et_test_name.text.toString()
                test!!.desc = et_test_desc.text.toString()
                test.links = links

                if(checkTest()) {//
                    val args: Bundle = Bundle()
                    args.putString(TEST_ITEM, gson.toJson(test))
                    args.putInt(QUESTION_NUMBER, 0)
                    Log.d(TAG_LOG, "Create quesitons")
                    val fragment = AddQuestionTestFragment.newInstance(args)
                    pushFragments(fragment, true)
                } else {

                }
            }

            R.id.tv_add_card -> {
               val fragment = AddCardListFragment.newInstance()
                fragment.setTargetFragment(this, ADD_CARD_CODE)
                showFragment(this, fragment)

            }

            R.id.tv_add_epoch -> {
                val args = Bundle()
                args.putBoolean(HAS_DEFAULT, false)
                val fragment = EpochListFragment.newInstance(args)
                fragment.setTargetFragment(this, ADD_EPOCH_CODE)
                showFragment(this, fragment)
            }

            R.id.tv_add_link -> {
                val fragment = AddLinkFragment.newInstance()
                fragment.setTargetFragment(this, ADD_LINK_CODE)
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
            hideLoading()
        }

        if (reqCode == ADD_EPOCH_CODE && resultCode == RESULT_OK) {
            val epoch = gson.fromJson(data!!.getStringExtra(EPOCH_KEY), Epoch::class.java)
            test.epochId = epoch.id
            test.epoch = epoch
            li_added_epoch.visibility = View.VISIBLE
            tv_added_epoch!!.text = epoch.name
            tv_test_epoch_name.setError(null);
        }

        if (reqCode == ADD_LINK_CODE && resultCode == RESULT_OK) {
            val link = gson.fromJson(data!!.getStringExtra(LINK_ITEM), Link::class.java)
            links.add(link)
            addLinkView(link)
        }
    }

    private fun addLinkView(link: Link) {
        val view: View = layoutInflater.inflate(R.layout.item_link_clear, li_added_links,false)
        val ivRemoveSkill: ImageView = view.findViewById(R.id.iv_remove_skill)
        val tvAddedSkill: TextView = view.findViewById(R.id.tv_added_skill_name)

        ivRemoveSkill.setOnClickListener(checkListener)
        tvAddedSkill.text = link.name
        imageViews.add(ivRemoveSkill)
        liViews.add(view as LinearLayout)
        li_added_links.addView(view)
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
