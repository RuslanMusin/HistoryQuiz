package com.example.historyquiz.ui.game.fast_game

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
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.epoch.Epoch
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.model.user.User
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.epoch.EpochListFragment
import com.example.historyquiz.ui.game.game_list.GameListFragment
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.USER_ITEM
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.fragment_fast_game.*
import javax.inject.Inject
import javax.inject.Provider

class FastGameFragment : BaseFragment(), FastGameView, View.OnClickListener {

    @InjectPresenter
    lateinit var presenter: FastGamePresenter
    @Inject
    lateinit var presenterProvider: Provider<FastGamePresenter>
    @ProvidePresenter
    fun providePresenter(): FastGamePresenter = presenterProvider.get()

    lateinit var lobby: Lobby
    var dialog: ProgressDialog? = null
    lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_fast_game, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setStatus(Const.EDIT_STATUS)
        initViews(view)
        lobby = Lobby()
        arguments?.let {
            user = gson.fromJson(it.getString(USER_ITEM), User::class.java)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViews(view: View) {
        setToolbar()
        setListeners()
    }

    private fun setToolbar() {
        setActionBar(toolbar)
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }

    private fun setListeners() {
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

   /* override fun showProgressDialog() {
        if (dialog == null) {
            dialog = ProgressDialog(this.activity!!)
            dialog?.let {
                it.setMessage(getString(R.string.loading))
                it.isIndeterminate = true
                it.setCancelable(false)
            }

        }

        dialog!!.show()
    }

    override fun hideProgressDialog() {
        showSnackBar("Противник не принял приглашение")
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }*/

    override fun onClick(v: View) {
        when (v.id) {

            R.id.btn_create_game -> {
                lobby.cardNumber = seekBarCards.progress
                Log.d(TAG_LOG, "lobby.cardNumber = ${lobby.cardNumber}")
                presenter.createGame(lobby, user)
            }

            R.id.li_choose_epoch -> {
                val args = Bundle()
                args.putBoolean(Const.HAS_DEFAULT, true)
                val fragment = EpochListFragment.newInstance(args)
                fragment.setTargetFragment(this, Const.ADD_EPOCH_CODE)
                showFragment(this, fragment)
            }
        }
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {

            if (reqCode == Const.ADD_EPOCH_CODE) {
                val epoch = Const.gson.fromJson(data!!.getStringExtra(Const.EPOCH_KEY), Epoch::class.java)
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
            val fragment = FastGameFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment {
            val fragment = FastGameFragment()
            return fragment
        }
    }
}
