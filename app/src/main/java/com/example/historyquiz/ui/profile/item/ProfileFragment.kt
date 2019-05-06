package com.example.historyquiz.ui.profile.item

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.epoch.Epoch
import com.example.historyquiz.model.game.Lobby
import com.example.historyquiz.model.user.User
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.cards.card_list.CardListFragment
import com.example.historyquiz.ui.profile.list.MemberTabFragment
import com.example.historyquiz.ui.statists.StatListFragment
import com.example.historyquiz.ui.tests.test_list.TestListFragment
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.ADD_FRIEND
import com.example.historyquiz.utils.Const.ADD_REQUEST
import com.example.historyquiz.utils.Const.OWNER_TYPE
import com.example.historyquiz.utils.Const.REMOVE_FRIEND
import com.example.historyquiz.utils.Const.REMOVE_REQUEST
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.USER_DATA_PREFERENCES
import com.example.historyquiz.utils.Const.USER_ID
import com.example.historyquiz.utils.Const.USER_ITEM
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.dialog_help.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_expandable.*
import kotlinx.android.synthetic.main.toolbar_help.*
import javax.inject.Inject
import javax.inject.Provider

class ProfileFragment: BaseFragment(), ProfileView, View.OnClickListener {

    @InjectPresenter
    lateinit var presenter: ProfilePresenter
    @Inject
    lateinit var presenterProvider: Provider<ProfilePresenter>
    @ProvidePresenter
    fun providePresenter(): ProfilePresenter = presenterProvider.get()

    lateinit var helpDialog: MaterialDialog
    lateinit var user: User
    val lobby: Lobby = Lobby()
    var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            user = gson.fromJson(it.getString(USER_ITEM), User::class.java)
            Log.d(TAG_LOG, "userEmail = ${user.email}")
            presenter.setUserRelationAndView(user)
            setStatus(Const.ONLINE_STATUS)
            setWaitStatus(true)
        }
        hideLoading()
    }

    override fun initViews() {
//        setBottomVisibility(true)
        setActionBar(toolbar)
        if(type.equals(OWNER_TYPE)) {
            toolbar.setNavigationIcon(null)
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
        li_tests.setOnClickListener(this)
        li_cards.setOnClickListener(this)
        li_logout.setOnClickListener(this)
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.tv_add_friend -> actWithUser()

            R.id.tv_play_game -> presenter.playGameClick(user)

            R.id.li_friends -> showFriends()

            R.id.li_statists -> showStatists()

            R.id.li_cards -> showCards()

            R.id.li_tests -> showTests()

            R.id.li_logout -> presenter.logout()
        }
    }

    override fun deleteUserPrefs() {
        activity?.getSharedPreferences(USER_DATA_PREFERENCES, Context.MODE_PRIVATE)?.let {
            val editor: SharedPreferences.Editor = it.edit()
            editor.remove(Const.USER_USERNAME)
            editor.remove(Const.USER_PASSWORD)
            editor.apply()
        }
    }

    fun showCards() {
        val args = Bundle()
        args.putString(Const.TIME_TYPE, Const.OLD_ONES)
        args.putString(USER_ID, user.id)
        val fragment = CardListFragment.newInstance(args)
        pushFragments(fragment, true)
    }

    fun showTests() {
        val args = Bundle()
        args.putString(Const.TIME_TYPE, Const.OLD_ONES)
        args.putString(USER_ID, user.id)
        val fragment = TestListFragment.newInstance(args)
        pushFragments(fragment, true)
    }

    fun showDescription() {
        expandable_layout.toggle()
        if(expandable_layout.isExpanded) {
            iv_arrow.rotation = 180f
        } else {
            iv_arrow.rotation = 0f
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

    private fun setUserData() {
        arguments?.let {
            tv_name.text = user.username
            tv_email.text = user.email
            tv_username.text = user.username
            tv_level.text = getString(R.string.level, user.level)
            progressBar.max = user.nextLevel.toInt()
            progressBar.progress = user.points.toInt()
            if(!type.equals(OWNER_TYPE)) {
                li_friends.visibility = View.GONE
                li_statists.visibility = View.GONE
                li_tests.visibility = View.GONE
                li_logout.visibility = View.GONE
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
                }
            }

        }
    }

    override fun handleError(throwable: Throwable) {
        Log.d(TAG_LOG, "handle error")
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.help_menu, menu)
        if (type.equals(OWNER_TYPE)) {
            helpDialog = MaterialDialog.Builder(this.activity!!)
                .customView(R.layout.dialog_help, false)
                .onNeutral { dialog, which ->
                    dialog.cancel()
                }
                .build()

            helpDialog.btn_cancel.setOnClickListener { helpDialog.cancel() }
            helpDialog.tv_help_content.text = getString(R.string.profile_text)
            menu?.let {
                val helpItem = menu.findItem(R.id.action_help)
                helpItem.setOnMenuItemClickListener {
                    helpDialog.show()
                    true
                }
            }
        } else {
            menu?.findItem(R.id.action_help)?.setVisible(false)
        }
        super.onCreateOptionsMenu(menu, inflater)
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



    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            if (reqCode == Const.ADD_EPOCH_CODE) {
                val epoch = gson.fromJson(data!!.getStringExtra(Const.EPOCH_KEY), Epoch::class.java)
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