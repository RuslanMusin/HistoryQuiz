package com.example.historyquiz.ui.game.add_game

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.arellomobile.mvp.presenter.InjectPresenter
import com.bumptech.glide.Glide
import com.example.historyquiz.R
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.db_dop_models.PhotoItem
import com.example.historyquiz.model.epoch.Epoch
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.model.game.LobbyPlayerData
import com.example.historyquiz.model.test.Test
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.cards.add_card_list.AddCardListFragment
import com.example.historyquiz.ui.game.add_photo.AddPhotoFragment
import com.example.historyquiz.ui.game.game_list.GameListFragment
import com.example.historyquiz.ui.tests.add_test.TestViewModel
import com.example.historyquiz.ui.tests.add_test.main.AddMainTestPresenter
import com.example.historyquiz.ui.tests.add_test.main.AddMainTestView
import com.example.historyquiz.ui.tests.add_test.question.AddQuestionTestFragment
import com.example.historyquiz.ui.tests.test_item.check_answers.AnswersFragment
import com.example.historyquiz.utils.AppHelper.Companion.currentId
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.ADD_CARD_CODE
import com.example.historyquiz.utils.Const.ADD_EPOCH_CODE
import com.example.historyquiz.utils.Const.CARD_NUMBER
import com.example.historyquiz.utils.Const.EDIT_STATUS
import com.example.historyquiz.utils.Const.ONLINE_STATUS
import com.example.historyquiz.utils.Const.PHOTO_ITEM
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.USER_ID
import com.example.historyquiz.utils.Const.gson
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_add_game.*
import kotlinx.android.synthetic.main.toolbar_back.*
import javax.inject.Inject

class AddGameFragment : BaseFragment(), AddGameView, View.OnClickListener {

    @InjectPresenter
    lateinit var presenter: AddGamePresenter

    lateinit var lobby: Lobby


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_game, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setStatus(EDIT_STATUS)
        initViews(view)
        lobby = Lobby()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViews(view: View) {
        setToolbar()
        setListeners()
    }

    private fun setToolbar() {
        setActionBar(toolbar_back)
        setToolbarTitle(toolbar_title, getString(R.string.add_test))
    }

    private fun setListeners() {
        btn_add_game_photo.setOnClickListener(this)
        li_choose_epoch.setOnClickListener(this)
        btn_create_game.setOnClickListener(this)
        seekBarCards.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val strProgress: String = seekBar?.progress.toString()
                tvCards.text = strProgress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.btn_create_game -> {
                lobby.cardNumber = seekBarCards.progress
                if(lobby.cardNumber >= CARD_NUMBER) {
                    /* if (types[spinner.selectedIndex].equals(getString(R.string.official_type))) {
                         lobby.type = OFFICIAL_TYPE
                     } else {
                         lobby.type = USER_TYPE
                     }*/
                    Log.d(TAG_LOG,"lobby type = ${lobby.type}")
                    lobby.title = et_game_name.text.toString()
                    lobby.lowerTitle = lobby.title?.toLowerCase()
                    lobby.status = Const.ONLINE_STATUS
                    lobby.isFastGame = false
                    val playerData = LobbyPlayerData()
                    playerData.playerId = currentId
                    playerData.online = true
                    lobby.creator = playerData
                    presenter.createGame(lobby)
                } else {
                    showSnackBar(R.string.set_card_min)
                }

            }

            R.id.btn_add_game_photo -> {
                val fragment = AddPhotoFragment.newInstance()
                fragment.setTargetFragment(this, ADD_CARD_CODE)
                showFragment(this, fragment)
            }

            R.id.li_choose_epoch -> {
                /*val fragment = EpochListFragment.newInstance()
                fragment.setTargetFragment(this, ADD_EPOCH_CODE)
                showFragment(this, fragment)*/
            }
        }
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            if (reqCode == ADD_CARD_CODE ) {
                val photoItem = gson.fromJson(data!!.getStringExtra(PHOTO_ITEM), PhotoItem::class.java)
                Glide.with(iv_cover.context)
                    .load(photoItem.photoUrl)
                    .into(iv_cover)
                lobby.photoUrl = photoItem.photoUrl


            }

            if (reqCode == ADD_EPOCH_CODE) {
                val epoch = gson.fromJson(data!!.getStringExtra(Const.EPOCH_KEY), Epoch::class.java)
                tv_epoch!!.text = epoch.name
                lobby.epoch = epoch
                lobby.epochId = epoch.id
            }
        }
    }

    override fun onGameCreated() {
        val fragment = GameListFragment.newInstance()
        pushFragments(fragment, true)
    }

    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = AddGameFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment {
            val fragment = AddGameFragment()
            return fragment
        }
    }
}
