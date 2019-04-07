package com.example.historyquiz.ui.game.play

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.example.historyquiz.R
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.test.Question
import com.example.historyquiz.model.user.User
import com.example.historyquiz.repository.game.GameRepository
import com.example.historyquiz.repository.game.GameRepositoryImpl
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.game.game_list.GameListFragment
import com.example.historyquiz.ui.game.play.change_list.GameChangeListAdapter
import com.example.historyquiz.ui.game.play.list.GameCardsListAdapter
import com.example.historyquiz.ui.game.play.question.GameQuestionFragment
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const.MODE_CARD_VIEW
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.getRandom
import com.example.historyquiz.widget.CenterZoomLayoutManager
import com.ms.square.android.expandabletextview.ExpandableTextView
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.dialog_end_game.view.*
import kotlinx.android.synthetic.main.item_game_card_medium.view.*
import kotlinx.android.synthetic.main.layout_change_card.*
import kotlinx.android.synthetic.main.toolbar_game.*
import kotlinx.android.synthetic.main.toolbar_game.view.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class PlayGameFragment : BaseFragment(), PlayGameView {

    var mode: String = MODE_PLAY_GAME

    @InjectPresenter
    lateinit var presenter: PlayGamePresenter
    @Inject
    lateinit var presenterProvider: Provider<PlayGamePresenter>
    @ProvidePresenter
    fun providePresenter(): PlayGamePresenter = presenterProvider.get()

    @Inject
    lateinit var gameRepository: GameRepository

    lateinit var myCard: Card
    lateinit var enemyCard:Card

    var enemyChoosed: Boolean = false
    var myChoosed: Boolean = false
    var enemyAnswered: Boolean = false
    var myAnswered: Boolean = false
    var isQuestionMode: Boolean = false

    lateinit var myCards: MutableList<Card>

    var choosingEnabled = false

    var round: Int = 1

    lateinit var timer: CountDownTimer
    var disconnectTimer: CountDownTimer? = null

    lateinit var toolbar: Toolbar


    lateinit var adapter: GameCardsListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        setStatus(IN_GAME_STATUS)
        val view = inflater.inflate(R.layout.layout_change_card, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar)
        btn_cancel.setOnClickListener{quitGameBeforeGameStart()}
        rv_game_start_cards.layoutManager = CenterZoomLayoutManager(this.activity!!, LinearLayoutManager.HORIZONTAL,false)
        AppHelper.currentUser?.gameLobby?.let { presenter.setInitState(it) }

    }

    override fun performBackPressed() {
        if(mode.equals(MODE_CHANGE_CARDS)) {
            mode = MODE_PLAY_GAME
            stopChange(20000)()
        } else {
            quitGame()
        }
    }

    fun quitGameBeforeGameStart() {
        Log.d(TAG_LOG,"quit game before game start")
        MaterialDialog.Builder(this.activity!!)
            .title(R.string.question_dialog_title)
            .content(R.string.question_dialog_content)
            .positiveText(R.string.agree)
            .negativeText(R.string.disagree)
            .onPositive(object : MaterialDialog.SingleButtonCallback {
                override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                    timer.cancel()
                    gameRepository.disconnectMe().subscribe{ e ->
                        goToFindGameActivity()
                    }
                }

            })
            .show()
    }

    fun quitGame() {
        Log.d(TAG_LOG,"quit game")
        MaterialDialog.Builder(this.activity!!)
            .title(R.string.question_dialog_title)
            .content(R.string.question_dialog_content)
            .positiveText(R.string.agree)
            .negativeText(R.string.disagree)
            .onPositive(object : MaterialDialog.SingleButtonCallback {
                override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                    timer.cancel()
                    disconnectTimer?.cancel()
                    gameRepository.disconnectMe().subscribe()
                }

            })
            .show()
    }

    fun stopChange(time: Long): () -> Unit {
        return {
            timer.cancel()
            presenter.changeGameMode(MODE_PLAY_GAME)
            presenter.setCardList((rv_game_start_cards.adapter as GameChangeListAdapter).items as ArrayList<Card>, time)
        }
    }

    override fun waitEnemyTimer(time: Long) {
        toolbar.tv_time_title.text = getString(R.string.wait_enemy)
        timer = object : CountDownTimer(time, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                toolbar.tv_time.text =  "${millisUntilFinished / 1000}"
                Log.d(TAG_LOG,"Wait Change Time = ${millisUntilFinished / 1000}")
            }

            override fun onFinish() {
                Log.d(TAG_LOG,"stop change cards")
                enemyDisconnectedBeforeGame()
            }
        }.start()
    }

    fun enemyDisconnectedBeforeGame() {
        MaterialDialog.Builder(this.activity!!)
            .title(R.string.game_ended)
            .content(R.string.enemy_disconnected)
            .positiveText("OK")
            .onPositive(object : MaterialDialog.SingleButtonCallback {
                override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                    timer.cancel()
                    gameRepository.disconnectMe().subscribe{ e ->
                        goToFindGameActivity()
                    }
                }

            })
            .show()
    }

    override fun setEnemyUserData(user: User) {
        tv_game_enemy_name.text = user.username

        //TODO image, но еще нет Url в БД
    }

    override fun changeCards(cards: MutableList<Card>, mutCards: MutableList<Card>) {
        Log.d(TAG_LOG,"changeCards")
        mode = MODE_CHANGE_CARDS
        rv_game_start_cards.adapter = GameChangeListAdapter(cards,mutCards,mutCards.size,stopChange(15000))

        toolbar.tv_time_title.text = "Замена карт"
        timer = object : CountDownTimer(10000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                toolbar.tv_time.text =  "${millisUntilFinished / 1000}"
                Log.d(TAG_LOG,"Card Change Time = ${millisUntilFinished / 1000}")
            }

            override fun onFinish() {
                Log.d(TAG_LOG,"stop change cards")
                stopChange(10000)()
            }
        }.start()
    }

    override fun setCardsList(cards: ArrayList<Card>) {
        timer.cancel()
        myCards = cards
        Log.d(TAG_LOG,"set cards")
        LayoutInflater.from(context).inflate(R.layout.activity_game, container, false)
        enemy_selected_card.visibility = View.INVISIBLE
        my_selected_card.visibility = View.INVISIBLE
        game_questions_container.visibility = View.GONE
        Log.d(TAG_LOG,"set game adapter")
        rv_game_my_cards.adapter = GameCardsListAdapter(
            cards,
            this.activity!!,
            {
                if (choosingEnabled) {
                    presenter.chooseCard(it)
                }
//                    showYouCardChoose(it)
            }
        )
        adapter = rv_game_my_cards.adapter as GameCardsListAdapter
        rv_game_my_cards.layoutManager = CenterZoomLayoutManager(this.activity!!, LinearLayoutManager.HORIZONTAL,false)

        setActionBar(game_toolbar)
        btn_cancel.setOnClickListener{quitGame()}
        toolbar_title.text = "Round $round"
        Log.d(TAG_LOG,"Round $round")
        startTimer()

        val listener: View.OnClickListener = View.OnClickListener {

            when(it.id) {

                R.id.enemy_selected_card -> {
                    if(enemy_selected_card.visibility == View.VISIBLE) {
                        showDialogCard(enemyCard)
                    }
                }

                R.id.my_selected_card -> {
                    if(my_selected_card.visibility == View.VISIBLE) {
                        showDialogCard(myCard)
                    }
                }
            }

        }

        enemy_selected_card.setOnClickListener(listener)
        my_selected_card.setOnClickListener(listener)


    }

    private fun showDialogCard(card: Card) {
        val dialog: MaterialDialog = MaterialDialog.Builder(this.activity!!)
            .customView(R.layout.fragment_test_card, false)
            .build()

        val view: View? = dialog.customView
        view?.findViewById<ExpandableTextView>(R.id.expand_text_view)?.text = card.abstractCard.description
        view?.findViewById<TextView>(R.id.tv_name)?.text = card.abstractCard.name

        view?.findViewById<ImageView>(R.id.iv_portrait)?.let { it1 ->
            Glide.with(it1.context)
                .load(card.abstractCard.photoUrl)
                .into(it1)
        }
        dialog.show()
    }


    override fun setCardChooseEnabled(enabled: Boolean) {
        choosingEnabled = enabled
        rv_game_my_cards.isEnabled = enabled
        if (enabled) {
            rv_game_my_cards.alpha = 1f
        } else {
            rv_game_my_cards.alpha = 0.5f
        }
//        rv_game_my_cards.isClickable = enabled
//        rv_game_my_cards.touc

    }

    override fun onAnswer(correct: Boolean) {
        presenter.answer(correct)
    }

    override fun showEnemyCardChoose(card: Card) {
        enemyCard = card
        enemyChoosed = true
        updateTime()
        setCard(enemy_selected_card, card)
        enemy_selected_card.visibility = View.VISIBLE

        val a_anim = AlphaAnimation(0f, 1f)
        a_anim.duration = 700;
        a_anim.fillAfter = true
        enemy_selected_card.startAnimation(a_anim)

        //TODO?
//        val m_anim = object : Animation() {
//            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
////                super.applyTransformation(interpolatedTime, t)
//                val params=enemy_selected_card.layoutParams as ConstraintLayout.LayoutParams
//                params.
//            }
//        }
    }

    private fun updateTime() {
        Log.d(TAG_LOG,"updateTime")
        if(enemyAnswered and myAnswered) {
            Log.d(TAG_LOG,"choose card mode")
            enemyAnswered = false
            myAnswered = false
            isQuestionMode = false
            round++
            toolbar_title.text = "Round $round"
            disconnectTimer?.cancel()
            timer.cancel()
            startTimer()
            adapter.isClickable = true
        }
        /* if(!enemyAnswered && myAnswered) {
             disconnectTimer?.cancel()
             Log.d(TAG_LOG,"Disconnect Answer Time")
             disconnectTimer = object : CountDownTimer(30000, 1000) {

                 override fun onTick(millisUntilFinished: Long) {
                     Log.d(TAG_LOG,"Disconnect Answer Time = ${millisUntilFinished / 1000}")
                 }

                 override fun onFinish() {
                     Log.d(TAG_LOG,"enemy not asnwered dis")
                     presenter.enemyDisconnected()
                 }
             }
             disconnectTimer?.start()
         }*/
        if(enemyChoosed and myChoosed) {
            Log.d(TAG_LOG,"show question mode")
            enemyChoosed = false
            myChoosed = false
            isQuestionMode = true
            disconnectTimer?.cancel()
            timer.cancel()
            presenter.changeGameMode(MODE_CARD_VIEW)
            presenter.waitEnemyGameMode(MODE_CARD_VIEW).subscribe { e ->
                tv_time_title.text = getString(R.string.view_time)
                timer = object : CountDownTimer(10000, 1000) {

                    override fun onTick(millisUntilFinished: Long) {
                        tv_time.text = "${millisUntilFinished / 1000}"
                        Log.d(TAG_LOG,"View Time = ${millisUntilFinished / 1000}")
                    }

                    override fun onFinish() {
                        presenter.changeGameMode(MODE_PLAY_GAME)
                        presenter.waitEnemyGameMode(MODE_PLAY_GAME).subscribe { e ->
                            presenter.showQuestion()
                            startTimer()
                        }
                    }
                }.start()
            }
        }
        /*if(!enemyChoosed && myChoosed) {
            Log.d(TAG_LOG,"Disconnect Choose Time")
            disconnectTimer?.cancel()
            disconnectTimer = object : CountDownTimer(30000, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    Log.d(TAG_LOG,"Disconnect Choose Time = ${millisUntilFinished / 1000}")
                }

                override fun onFinish() {
                    Log.d(TAG_LOG,"enemy not choosed dis")
                    presenter.enemyDisconnected()
                }
            }
            disconnectTimer?.start()
        }*/
    }

    private fun startTimer() {
        disconnectTimer?.cancel()
        timer.cancel()
        tv_time_title.text = getString(R.string.time)
        timer = object : CountDownTimer(30000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                tv_time.text =  "${millisUntilFinished / 1000}"
                Log.d(TAG_LOG,"Card/Answer time = ${millisUntilFinished / 1000}")
            }

            override fun onFinish() {
                Log.d(TAG_LOG, "onFinish")
                if (isQuestionMode) {
                    if (!enemyAnswered) {
                        Log.d(TAG_LOG, "Disconnect Answer Time")
                        tv_time_title.text = getString(R.string.wait_enemy)
                        disconnectTimer = object : CountDownTimer(10000, 1000) {

                            override fun onTick(millisUntilFinished: Long) {
                                tv_time.text = "${millisUntilFinished / 1000}"
                                Log.d(TAG_LOG, "Disconnect Answer Time = ${millisUntilFinished / 1000}")
                            }

                            override fun onFinish() {
                                if (!enemyAnswered) {
                                    Log.d(TAG_LOG, "enemy not asnwered dis")
                                    presenter.enemyDisconnected()
                                } else {
                                    Log.d(TAG_LOG, "enemy answered successfully")
                                }
                            }
                        }
                        disconnectTimer?.start()
                    }
                    if (!myAnswered) {
                        Log.d(TAG_LOG, "notAnswered")
                        myAnswered = true
                        onAnswer(false)
                        updateTime()

                    }
                } else {
                    if (!enemyChoosed) {
                        tv_time_title.text = getString(R.string.wait_enemy)
                        disconnectTimer = object : CountDownTimer(10000, 1000) {

                            override fun onTick(millisUntilFinished: Long) {
                                tv_time.text = "${millisUntilFinished / 1000}"
                                Log.d(TAG_LOG, "Disconnect Choose Time = ${millisUntilFinished / 1000}")
                            }

                            override fun onFinish() {
                                if (!enemyChoosed) {
                                    Log.d(TAG_LOG, "enemy not choosed dis")
                                    presenter.enemyDisconnected()
                                } else {
                                    Log.d(TAG_LOG, "enemy choosed successfully")
                                }
                            }
                        }
                        disconnectTimer?.start()
                    }
                    if (!myChoosed) {
                        Log.d(TAG_LOG, "notChoosed")
                        myChoosed = true
                        val card: Card? = myCards.getRandom()
                        card?.let { adapter.removeElement(it) }
                        updateTime()
                    }
                }
            }
        }
        timer.start()
    }

    override fun showQuestionForYou(question: Question) {
        game_questions_container.visibility = View.VISIBLE

        childFragmentManager
            .beginTransaction()
            .replace(
                R.id.game_questions_container,
                GameQuestionFragment.newInstance(question)
            )
            .commit()

    }

    override fun hideQuestionForYou() {
        game_questions_container.visibility = View.GONE
    }

    override fun showYouCardChoose(card: Card) {
        myCard = card
        myChoosed = true
        updateTime()
        setCard(my_selected_card, card)
        my_selected_card.visibility = View.VISIBLE

        val a_anim = AlphaAnimation(0f, 1f)
        a_anim.duration = 200;
        a_anim.fillAfter = true
        my_selected_card.startAnimation(a_anim)
    }

    override fun hideEnemyCardChoose() {
        enemy_selected_card.clearAnimation()
        enemy_selected_card.visibility = View.INVISIBLE
    }

    override fun hideYouCardChoose() {
        my_selected_card.clearAnimation()
        my_selected_card.visibility = View.INVISIBLE
    }

    override fun showEnemyAnswer(correct: Boolean) {
        enemyAnswered = true
        updateTime()
        if (correct) {
            tv_enemy_score.text = (tv_enemy_score.text.toString().toInt() + 1).toString()
        }
    }

    override fun showYourAnswer(correct: Boolean) {
        myAnswered = true
        updateTime()
        if (correct) {
            tv_my_score.text = (tv_my_score.text.toString().toInt() + 1).toString()
        }
    }

    fun setCard(view: View, card: Card) {
        view.tv_card_person_name.text = card.abstractCard.name?.let { AppHelper.cutLongDescription(it, MAX_LENGTH) }

        Glide.with(this)
            .load(card.abstractCard.photoUrl)
            .into(view.iv_card)

        setWeight(view.ll_card_params.view_card_intelligence, card.intelligence!!.toFloat())
        setWeight(view.ll_card_params.view_card_support, card.support!!.toFloat())
        setWeight(view.ll_card_params.view_card_prestige, card.prestige!!.toFloat())
        setWeight(view.ll_card_params.view_card_hp, card.hp!!.toFloat())
        setWeight(view.ll_card_params.view_card_strength, card.strength!!.toFloat())
    }




    override fun showGameEnd(type: GameRepositoryImpl.GameEndType, card: Card) {

        timer.cancel()
        disconnectTimer?.cancel()

        if (type == GameRepositoryImpl.GameEndType.DRAW) {
            MaterialDialog.Builder(this.activity!!)
                .title("Draw")
                .titleGravity(GravityEnum.CENTER)
//                    .content("draw")
                .neutralText("ok")
                .buttonsGravity(GravityEnum.END)
                .onNeutral { dialog, which ->
                    goToFindGameActivity()
                }
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show()
        } else {
            val dialog = MaterialDialog.Builder(this.activity!!)
                .title(when (type) {
                    GameRepositoryImpl.GameEndType.YOU_WIN,
                    GameRepositoryImpl.GameEndType.ENEMY_DISCONNECTED_AND_YOU_WIN -> "You win"

                    GameRepositoryImpl.GameEndType.YOU_LOSE,
                    GameRepositoryImpl.GameEndType.YOU_DISCONNECTED_AND_LOSE -> "You lose"

                    GameRepositoryImpl.GameEndType.DRAW -> "Draw"//never
                })
                .titleGravity(GravityEnum.CENTER)
                .customView(R.layout.dialog_end_game, false)

                .neutralText("ok")
                .buttonsGravity(GravityEnum.END)
                .onNeutral { dialog, which ->
                    goToFindGameActivity()
                }
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show()

            setCard(dialog.view.card_in_end_dialog, card)

            dialog.view.tv_get_lose_card.text = when (type) {
                GameRepositoryImpl.GameEndType.YOU_WIN,
                GameRepositoryImpl.GameEndType.ENEMY_DISCONNECTED_AND_YOU_WIN -> "You get card:"

                GameRepositoryImpl.GameEndType.YOU_LOSE,
                GameRepositoryImpl.GameEndType.YOU_DISCONNECTED_AND_LOSE -> "You lose card:"

                GameRepositoryImpl.GameEndType.DRAW -> "Draw"//never
            }

            if (type == GameRepositoryImpl.GameEndType.ENEMY_DISCONNECTED_AND_YOU_WIN) {
                dialog.view.tv_game_end_reason.text = "Enemy disconnected"
                dialog.view.tv_game_end_reason.visibility = View.VISIBLE
            }

            if (type == GameRepositoryImpl.GameEndType.YOU_DISCONNECTED_AND_LOSE) {
                dialog.view.tv_game_end_reason.text = "You disconnected"
                dialog.view.tv_game_end_reason.visibility = View.VISIBLE
            }

        }

    }

    private fun goToFindGameActivity() {
        val fragment = GameListFragment.newInstance()
        pushFragments(fragment, true)
    }


    companion object {

        const val MAX_LENGTH = 30

        const val MODE_CHANGE_CARDS = "change_cards"
        const val MODE_PLAY_GAME = "play_game"
        const val GAME_MODE = "game_mode"

        const val ACCEPTED_GAME = "accepted"
        const val LOBBY_JSON = "lobby_json"

        fun newInstance(args: Bundle): Fragment {
            val fragment = PlayGameFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment {
            val fragment = PlayGameFragment()
            return fragment
        }

        fun setWeight(view: View, w: Float) {
            val params = view.layoutParams as LinearLayout.LayoutParams
            params.weight = w
            view.layoutParams = params
        }
    }
}