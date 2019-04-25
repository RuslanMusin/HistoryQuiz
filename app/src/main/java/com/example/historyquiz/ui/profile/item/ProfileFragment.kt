package com.example.historyquiz.ui.profile.item

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.epoch.Epoch
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.epoch.EpochRepository
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.USER_ITEM
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject
import javax.inject.Provider

class ProfileFragment: BaseFragment(), ProfileView {

    @InjectPresenter
    lateinit var profilePresenter: ProfilePresenter
    @Inject
    lateinit var presenterProvider: Provider<ProfilePresenter>
    @ProvidePresenter
    fun providePresenter(): ProfilePresenter = presenterProvider.get()

    @Inject
    lateinit var epochRepository: EpochRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setUserData()
        hideLoading()
    }

    private fun initViews() {
//        setBottomVisibility(true)
        setActionBar(toolbar)
        setActionBarTitle(R.string.menu_profile)
        setListeners()
    }

    private fun setListeners() {
        tv_add_friend.visibility = View.GONE
        tv_play_game.visibility = View.GONE
        tv_add_epoches.setOnClickListener { addEpoches() }
    }

    fun addEpoches() {
        val list = resources.getStringArray(R.array.epoches).toList()
        for (item in list) {
            epochRepository.createEpoch(Epoch(item.toString())).subscribe()
        }
    }

    private fun setUserData() {
        arguments?.let {
            val userJson = it.getString(USER_ITEM)
            val user = gson.fromJson(userJson, User::class.java)
            tv_name.text = user.username
            AppHelper.loadUserPhoto(iv_profile, user.photoUrl)
        }
    }

    override fun handleError(throwable: Throwable) {
        Log.d(TAG_LOG, "handle error")
    }

    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment {
            return ProfileFragment()
        }
    }

}