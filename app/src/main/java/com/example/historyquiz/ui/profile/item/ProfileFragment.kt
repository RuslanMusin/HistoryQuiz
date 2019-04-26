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
import com.example.historyquiz.model.game.LobbyPlayerData
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.epoch.EpochRepository
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const.ADD_FRIEND
import com.example.historyquiz.utils.Const.ADD_REQUEST
import com.example.historyquiz.utils.Const.CARD_NUMBER
import com.example.historyquiz.utils.Const.EPOCH_KEY
import com.example.historyquiz.utils.Const.ONLINE_STATUS
import com.example.historyquiz.utils.Const.OWNER_TYPE
import com.example.historyquiz.utils.Const.REMOVE_FRIEND
import com.example.historyquiz.utils.Const.REMOVE_REQUEST
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.USER_ID
import com.example.historyquiz.utils.Const.USER_ITEM
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject
import javax.inject.Provider

class ProfileFragment: BaseFragment(), ProfileView, View.OnClickListener {

    @InjectPresenter
    lateinit var profilePresenter: ProfilePresenter
    @Inject
    lateinit var presenterProvider: Provider<ProfilePresenter>
    @ProvidePresenter
    fun providePresenter(): ProfilePresenter = presenterProvider.get()

    var mProgressDialog: ProgressDialog? = null

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
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.tv_add_friend -> actWithUser()

            R.id.tv_play_game -> playGame()
        }
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
                    btnAddFriend!!.visibility = View.GONE
                    btn_play_game.visibility = View.GONE
                }
            }

        }
    }

    override fun handleError(throwable: Throwable) {
        Log.d(TAG_LOG, "handle error")
    }

    private fun playGame() {
        changePlayButton(false)
        userRepository.checkUserStatus(user.id).subscribe { isOnline ->
            if (isOnline) {
                gameDialog = MaterialDialog.Builder(this)
                    .customView(R.layout.dialog_fast_game, false)
                    .onNeutral { dialog, which ->
                        dialog.cancel()
                        changePlayButton(true)
                    }
                    .build()

                gameDialog.btn_create_game.setOnClickListener{ createGame() }
                gameDialog.li_choose_epoch.setOnClickListener {
                    val intent = Intent(this, EpochListActivity::class.java)
                    startActivityForResult(intent, AddTestFragment.ADD_EPOCH)
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

            } else {
                showSnackBar(R.string.enemy_not_online)
                changePlayButton(true)
            }
        }

    }

    fun createGame() {
        lobby.cardNumber = gameDialog.seekBarCards.progress
        if(lobby.cardNumber >= CARD_NUMBER) {
            /*if (types[gameDialog.spinner.selectedIndex].equals(getString(R.string.official_type))) {
                lobby.type = Const.OFFICIAL_TYPE
            }*/
            cardRepository.findCardsByType(user.id,lobby.type, lobby.epochId).subscribe{ cards ->
                val cardNumber = cards.size
                if(cardNumber >= lobby.cardNumber) {
                    cardRepository.findCardsByType(UserRepository.currentId, lobby.type, lobby.epochId).subscribe { myCards ->
                        val mySize = myCards.size
                        if (mySize >= lobby.cardNumber) {
                            gameDialog.hide()
                            showProgressDialog()
                            lobby.isFastGame = true
                            val playerData = LobbyPlayerData()
                            playerData.playerId = UserRepository.currentId
                            playerData.online = true
                            lobby.creator = playerData
                            user?.id?.let { presenter.playGame(it, lobby) }
                        } else {
                            showSnackBar(R.string.you_dont_have_card_min)
                        }
                    }
                } else {
                    showSnackBar(R.string.enemy_doesnt_have_card_min)
                }
            }

        } else {
            showSnackBar(R.string.set_card_min)
        }
    }

    fun changePlayButton(isClickable: Boolean) {
        btn_play_game.isClickable = isClickable
    }

    private fun showTests() {
        val intent: Intent = Intent(this,OneTestListActivity::class.java)
        intent.putExtra(TEST_LIST_TYPE, USER_TESTS)
        intent.putExtra(USER_ID,user?.id)
        OneTestListActivity.start(this,intent)
    }

    private fun showCards() {
        val intent: Intent = Intent(this,OneCardListActivity::class.java)
        intent.putExtra(USER_ID,user?.id)
        OneCardListActivity.start(this,intent)
    }

    private fun actWithUser() {
        when (type) {
            ADD_FRIEND -> {
                user!!.id?.let { UserRepository().addFriend(UserRepository.currentId, it) }
                type = REMOVE_FRIEND
                btnAddFriend!!.setText(R.string.remove_friend)
            }

            ADD_REQUEST -> {
                user!!.id?.let { UserRepository().addFriendRequest(UserRepository.currentId, it) }
                type = REMOVE_REQUEST
                btnAddFriend!!.setText(R.string.remove_request)
            }

            REMOVE_FRIEND -> {
                user!!.id?.let { UserRepository().removeFriend(UserRepository.currentId, it) }
                type = ADD_REQUEST
                btnAddFriend!!.setText(R.string.add_friend)
            }

            REMOVE_REQUEST -> {
                user!!.id?.let { UserRepository().removeFriendRequest(UserRepository.currentId, it) }
                type = ADD_REQUEST
                btnAddFriend!!.setText(R.string.add_friend)
            }
        }
    }

    private fun changeData() {
        //        startActivity(ChangeUserDataActivity.makeIntent(TestActivity.this));
    }

    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog?.let {
                it.setMessage(getString(R.string.loading))
                it.isIndeterminate = true
                it.setCancelable(false)
            }

        }

        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        showSnackBar("Противник не принял приглашение")
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            if (reqCode == ADD_EPOCH) {
                val epoch = gsonConverter.fromJson(data!!.getStringExtra(EPOCH_KEY), Epoch::class.java)
                gameDialog.tv_epoch.text = epoch.name
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