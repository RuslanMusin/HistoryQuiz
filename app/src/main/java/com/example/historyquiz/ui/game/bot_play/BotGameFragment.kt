package com.example.historyquiz.ui.game.bot_play

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
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
import com.example.historyquiz.repository.game.GameRepositoryImpl
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.game.game_list.GameListFragment
import com.example.historyquiz.ui.game.play.change_list.GameChangeListAdapter
import com.example.historyquiz.ui.game.play.list.GameCardsListAdapter
import com.example.historyquiz.ui.game.play.question.GameQuestionFragment
import com.example.historyquiz.utils.AppHelper.Companion.currentUser
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.MODE_CHANGE_CARDS
import com.example.historyquiz.utils.Const.MODE_PLAY_GAME
import com.example.historyquiz.utils.getRandom
import com.example.historyquiz.widget.CenterZoomLayoutManager
import com.ms.square.android.expandabletextview.ExpandableTextView
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.dialog_end_game.view.*
import kotlinx.android.synthetic.main.item_game_card_medium.view.*
import kotlinx.android.synthetic.main.layout_change_card.*
import kotlinx.android.synthetic.main.toolbar_game.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class BotGameFragment : BaseFragment(), BotGameView {

    var mode: String = MODE_PLAY_GAME

    @InjectPresenter
    lateinit var presenter: BotGamePresenter
    @Inject
    lateinit var presenterProvider: Provider<BotGamePresenter>
    @ProvidePresenter
    fun providePresenter(): BotGamePresenter = presenterProvider.get()

    lateinit var myCard: Card
    lateinit var enemyCard: Card

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


    lateinit var adapter: GameCardsListAdapter


    override fun performBackPressed() {
        if(mode.equals(MODE_CHANGE_CARDS)) {
            mode = MODE_PLAY_GAME
            stopChange()()
        } else {
            quitGame()
        }    
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_change_card, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatus(Const.IN_GAME_STATUS)
        setWaitStatus(false)
        rv_game_start_cards.layoutManager = CenterZoomLayoutManager(this.activity!!, LinearLayoutManager.HORIZONTAL,false)
        currentUser?.gameLobby?.let { presenter.setInitState(it) }

    }

    fun quitGame() {
        MaterialDialog.Builder(this.activity!!)
            .title(R.string.question_dialog_title)
            .content(R.string.question_dialog_content)
            .positiveText(R.string.agree)
            .negativeText(R.string.disagree)
            .onPositive(object : MaterialDialog.SingleButtonCallback {
                override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                    presenter.onDisconnectAndLose()
                }

            })
            .show()
    }

    fun stopChange(): () -> Unit {
        return {
            timer.cancel()
//            presenter.changeGameMode(MODE_PLAY_GAME)
            presenter.setCardList((rv_game_start_cards.adapter as GameChangeListAdapter).items as ArrayList<Card>)
        }
    }

    override fun setEnemyUserData(user: User) {
        tv_game_enemy_name.text = user.username

        //TODO image, но еще нет Url в БД
    }

    override fun changeCards(cards: MutableList<Card>, mutCards: MutableList<Card>) {
        Log.d(Const.TAG_LOG,"changeCards")
        mode = MODE_CHANGE_CARDS
        rv_game_start_cards.adapter = GameChangeListAdapter(cards,mutCards,mutCards.size,stopChange())
        timer = object : CountDownTimer(10000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                tv_time.text =  "${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                Log.d(Const.TAG_LOG,"stop change cards")
                stopChange()()
            }
        }.start()
    }

    override fun setCardsList(cards: ArrayList<Card>) {
        myCards = cards
        Log.d(Const.TAG_LOG,"set cards")
        val viewGroup = view as ViewGroup
        viewGroup.removeAllViews()
        viewGroup.addView(LayoutInflater.from(context).inflate(R.layout.activity_game, viewGroup, false))
        enemy_selected_card.visibility = View.INVISIBLE
        my_selected_card.visibility = View.INVISIBLE
        game_questions_container.visibility = View.GONE
        Log.d(Const.TAG_LOG,"set game adapter")
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
        Log.d(Const.TAG_LOG,"updateTime")
        if(enemyAnswered and myAnswered) {
            Log.d(Const.TAG_LOG,"choose card mode")
            enemyAnswered = false
            myAnswered = false
            isQuestionMode = false
            round++
            toolbar_title.text = "Round $round"
            timer.cancel()
            startTimer()
            adapter.isClickable = true
        }
        if(enemyChoosed and myChoosed) {
            Log.d(Const.TAG_LOG,"show question mode")
            enemyChoosed = false
            myChoosed = false
            isQuestionMode = true
            timer.cancel()
//            presenter.changeGameMode(Const.MODE_CARD_VIEW)
//            presenter.waitEnemyGameMode(Const.MODE_CARD_VIEW).subscribe { e ->
            tv_time.text = "View Time"
            timer = object : CountDownTimer(10000, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
//                        presenter.changeGameMode(MODE_PLAY_GAME)
//                        presenter.waitEnemyGameMode(MODE_PLAY_GAME).subscribe { e ->
                    timer.cancel()
                    presenter.showQuestion()
                    startTimer()
//                        }
                }
            }.start()
//            }
        }

    }

    private fun startTimer() {
        timer = object : CountDownTimer(30000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                tv_time.text =  "${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                Log.d(Const.TAG_LOG,"onFinish")
                if(isQuestionMode && !myAnswered) {
                    Log.d(Const.TAG_LOG,"notAnswered")
                    myAnswered = true
                    onAnswer(false)
                    updateTime()

                }
                if(!isQuestionMode && !myChoosed) {
                    Log.d(Const.TAG_LOG,"notChoosed")
                    myChoosed = true
                    val card: Card? = myCards.getRandom()
                    card?.let { adapter.removeElement(it) }
                    updateTime()
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
        view.tv_card_person_name.text = card.abstractCard!!.name

        Glide.with(this)
            .load(card.abstractCard!!.photoUrl)
            .into(view.iv_card)

        setWeight(view.ll_card_params.view_card_intelligence, card.intelligence!!.toFloat())
        setWeight(view.ll_card_params.view_card_support, card.support!!.toFloat())
        setWeight(view.ll_card_params.view_card_prestige, card.prestige!!.toFloat())
        setWeight(view.ll_card_params.view_card_hp, card.hp!!.toFloat())
        setWeight(view.ll_card_params.view_card_strength, card.strength!!.toFloat())
    }




    override fun showGameEnd(type: GameRepositoryImpl.GameEndType, card: Card) {

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

        fun newInstance(args: Bundle): Fragment {
            val fragment = BotGameFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment {
            val fragment = BotGameFragment()
            return fragment
        }

        fun setWeight(view: View, w: Float) {
            val params = view.layoutParams as LinearLayout.LayoutParams
            params.weight = w
            view.layoutParams = params
        }
    }


}