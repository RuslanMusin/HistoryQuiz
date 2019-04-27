package com.example.historyquiz.ui.profile.item

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.epoch.Epoch
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.epoch.EpochRepository
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.epoch.EpochListFragment
import com.example.historyquiz.ui.profile.list.MemberTabFragment
import com.example.historyquiz.ui.statists.StatListFragment
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.ADD_FRIEND
import com.example.historyquiz.utils.Const.ADD_REQUEST
import com.example.historyquiz.utils.Const.ONLINE_STATUS
import com.example.historyquiz.utils.Const.OWNER_TYPE
import com.example.historyquiz.utils.Const.REMOVE_FRIEND
import com.example.historyquiz.utils.Const.REMOVE_REQUEST
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.USER_ITEM
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.fragment_add_game.*
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject
import javax.inject.Provider

class ProfileFragment: BaseFragment(), ProfileView, View.OnClickListener {

    @InjectPresenter
    lateinit var presenter: ProfilePresenter
    @Inject
    lateinit var presenterProvider: Provider<ProfilePresenter>
    @ProvidePresenter
    fun providePresenter(): ProfilePresenter = presenterProvider.get()

    var mProgressDialog: ProgressDialog? = null
    lateinit var gameDialog: MaterialDialog
    lateinit var user: User
    val lobby: Lobby = Lobby()
    var type: String? = null


    @Inject
    lateinit var epochRepository: EpochRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatus(ONLINE_STATUS)
        setWaitStatus(true)
        arguments?.let {
            user = gson.fromJson(it.getString(USER_ITEM), User::class.java)
            presenter.setUserRelationAndView(user)
        }
        initViews()
        hideLoading()
    }

    override fun initViews() {
//        setBottomVisibility(true)
        setActionBar(toolbar)
        if(type.equals(OWNER_TYPE)) {
            setActionBarTitle(R.string.menu_profile)
        } else {
            toolbar.title = user?.username
        }
        setListeners()
        setUserData()
    }

    private fun setListeners() {
        tv_add_friend.setOnClickListener(this)
        tv_play_game.setOnClickListener(this)
        li_friends.setOnClickListener(this)
        li_statists.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.tv_add_friend -> actWithUser()

            R.id.tv_play_game -> presenter.playGameClick(user.id)

            R.id.li_friends -> showFriends()

            R.id.li_statists -> showStatists()
        }
    }

    fun showFriends() {
        val fragment = MemberTabFragment.newInstance()
        pushFragments(fragment, true)
    }

    fun showStatists() {
        val fragment = StatListFragment.newInstance()
        pushFragments(fragment, true)
    }

    fun addEpoches() {
        val list = resources.getStringArray(R.array.epoches).toList()
        for (item in list) {
            epochRepository.createEpoch(Epoch(item.toString())).subscribe()
        }
    }

    private fun setUserData() {
        arguments?.let {
            tv_name.text = user.username
            tv_level.text = getString(R.string.level, user.level)

            if(type.equals(OWNER_TYPE)) {
                progressBar.max = user.nextLevel.toInt()
                progressBar.progress = user.points.toInt()
                progressBar.visibility = View.VISIBLE
            }
            AppHelper.loadUserPhoto(iv_profile, user.photoUrl)
            when (type) {
                ADD_FRIEND -> tv_add_friend.setText(R.string.add_friend)

                ADD_REQUEST -> tv_add_friend!!.setText(R.string.add_friend)

                REMOVE_FRIEND -> tv_add_friend!!.setText(R.string.remove_friend)

                REMOVE_REQUEST -> tv_add_friend!!.setText(R.string.remove_request)

                OWNER_TYPE -> {
                    tv_add_friend.visibility = View.GONE
                    tv_play_game.visibility = View.GONE
                    li_friends.visibility = View.VISIBLE
                    li_statists.visibility = View.VISIBLE
                }
            }

        }
    }

    override fun handleError(throwable: Throwable) {
        Log.d(TAG_LOG, "handle error")
    }

    override fun showGameDialog() {
                gameDialog = MaterialDialog.Builder(this.activity!!)
                    .customView(R.layout.dialog_fast_game, false)
                    .onNeutral { dialog, which ->
                        dialog.cancel()
                        changePlayButton(true)
                    }
                    .build()

                gameDialog.btn_create_game.setOnClickListener{ createGame() }
                gameDialog.li_choose_epoch.setOnClickListener {
                    val fragment = EpochListFragment.newInstance()
                    fragment.setTargetFragment(this, Const.ADD_EPOCH_CODE)
                    showFragment(this, fragment)
                }

                gameDialog.seekBarCards.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        val strProgress: String = seekBar?.progress.toString()
                        gameDialog.tvCards.text = strProgress
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }

                })


                gameDialog.show()

    }

    fun createGame() {
        lobby.cardNumber = gameDialog.seekBarCards.progress
        presenter.createGame(lobby, user)

    }

    override fun changePlayButton(isClickable: Boolean) {
        tv_play_game.isClickable = isClickable
    }

    private fun actWithUser() {
        when (type) {
            ADD_FRIEND -> {
                user!!.id?.let { presenter.addFriend(it) }
                type = REMOVE_FRIEND
                tv_add_friend.setText(R.string.remove_friend)
            }

            ADD_REQUEST -> {
                user!!.id?.let { presenter.addFriendRequest(it) }
                type = REMOVE_REQUEST
                tv_add_friend!!.setText(R.string.remove_request)
            }

            REMOVE_FRIEND -> {
                user!!.id?.let { presenter.removeFriend(it) }
                type = ADD_REQUEST
                tv_add_friend!!.setText(R.string.add_friend)
            }

            REMOVE_REQUEST -> {
                user!!.id?.let { presenter.removeFriendRequest(it) }
                type = ADD_REQUEST
                tv_add_friend!!.setText(R.string.add_friend)
            }
        }
    }

    override fun changeType(type: String) {
        this.type = type
    }

    override fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this.activity!!)
            mProgressDialog?.let {
                it.setMessage(getString(R.string.loading))
                it.isIndeterminate = true
                it.setCancelable(false)
            }

        }

        mProgressDialog!!.show()
    }

    override fun hideProgressDialog() {
        showSnackBar("Противник не принял приглашение")
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    override fun hideGameDialog() {
        gameDialog.hide()
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            if (reqCode == Const.ADD_EPOCH_CODE) {
                val epoch = gson.fromJson(data!!.getStringExtra(Const.EPOCH_KEY), Epoch::class.java)
                gameDialog.tv_epoch!!.text = epoch.name
                lobby.epoch = epoch
                lobby.epochId = epoch.id
            }
        }

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