package com.example.historyquiz.ui.tests.test_item.winned_card

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.example.historyquiz.R
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.utils.Const.TEST_ITEM
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.fragment_test_card.*
import kotlinx.android.synthetic.main.layout_expandable_text_view.*
import javax.inject.Inject
import javax.inject.Provider


class TestCardFragment: BaseFragment(), TestCardView {

    @InjectPresenter
    lateinit var presenter: TestCardPresenter
    @Inject
    lateinit var presenterProvider: Provider<TestCardPresenter>
    @ProvidePresenter
    fun providePresenter(): TestCardPresenter = presenterProvider.get()

    lateinit var test: Test
    lateinit var card: Card

    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = TestCardFragment()
            fragment.arguments = args
            return fragment
        }
    }

    /*override fun onBackPressed() {
        val args: Bundle = Bundle()
        args.putString(TEST_JSON, gsonConverter.toJson(test))
        val fragment = FinishFragment.newInstance(args)
        (activity as BaseBackActivity).changeFragment(fragment, FINISH_FRAGMENT)
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_test_card, container, false)

        val testStr: String? = arguments?.getString(TEST_ITEM)
        test = gson.fromJson(testStr, Test::class.java)
        card = test.card!!
//        (activity as BaseBackActivity).currentTag = TestActivity.WINNED_FRAGMENT
//        (activity as ChangeToolbarListener).changeToolbar(WINNED_FRAGMENT,"Карта ${card.abstractCard?.name}")
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        expand_text_view.text = card.abstractCard?.description
        tv_name.text = card.abstractCard?.name
        tv_support.text = card.support.toString()
        tv_hp.text = card.hp.toString()
        tv_strength.text = card.strength.toString()
        tv_prestige.text = card.prestige.toString()
        tv_intelligence.text = card.intelligence.toString()

        card.abstractCard?.photoUrl?.let {
            Glide.with(iv_portrait.context)
                    .load(it)
                    .into(iv_portrait)
        }

        super.onViewCreated(view, savedInstanceState)
    }
}