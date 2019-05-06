package com.example.historyquiz.ui.game.add_game

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.example.historyquiz.R
import com.example.historyquiz.model.db_dop_models.PhotoItem
import com.example.historyquiz.model.epoch.Epoch
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.model.game.LobbyPlayerData
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.epoch.EpochListFragment
import com.example.historyquiz.ui.game.add_photo.AddPhotoFragment
import com.example.historyquiz.ui.game.game_list.GameListFragment
import com.example.historyquiz.utils.AppHelper.Companion.currentId
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.ADD_CARD_CODE
import com.example.historyquiz.utils.Const.ADD_EPOCH_CODE
import com.example.historyquiz.utils.Const.CARD_NUMBER
import com.example.historyquiz.utils.Const.PHOTO_ITEM
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.fragment_add_game.*
import kotlinx.android.synthetic.main.toolbar_back.*
import javax.inject.Inject
import javax.inject.Provider

class AddGameFragment : BaseFragment(), AddGameView, View.OnClickListener {

    @InjectPresenter
    lateinit var presenter: AddGamePresenter
    @Inject
    lateinit var presenterProvider: Provider<AddGamePresenter>
    @ProvidePresenter
    fun providePresenter(): AddGamePresenter = presenterProvider.get()

    lateinit var lobby: Lobby


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_game, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        lobby = Lobby()
        setStatus(Const.EDIT_STATUS)
        setWaitStatus(false)
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
        btn_back.setOnClickListener(this)
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
                val args = Bundle()
                args.putBoolean(Const.HAS_DEFAULT, true)
                val fragment = EpochListFragment.newInstance(args)
                fragment.setTargetFragment(this, ADD_EPOCH_CODE)
                showFragment(this, fragment)
            }

            R.id.btn_back -> activity?.onBackPressed()
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

    override fun showTitleError(b: Boolean) {
        if(b) {
            ti_game_name.error = getString(R.string.input_name)
        } else {
            ti_game_name.error = null
        }
    }

    override fun showEpochError(b: Boolean) {
        if(b) {
            showSnackBar(R.string.choose_epoch_please)
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
