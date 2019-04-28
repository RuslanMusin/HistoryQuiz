package com.example.historyquiz.ui.cards.add_card

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
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.wiki_api.opensearch.Item
import com.example.historyquiz.model.wiki_api.query.Page
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.utils.Const.ADD_CARD_CODE
import com.example.historyquiz.utils.Const.CARD_ITEM
import com.example.historyquiz.utils.Const.ITEM_ITEM
import com.example.historyquiz.utils.Const.TAG_LOG
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.layout_add_card.*
import kotlinx.android.synthetic.main.toolbar_back_done.*
import javax.inject.Inject
import javax.inject.Provider

class AddCardFragment : BaseFragment(), AddCardView, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private var card: Card? = null

    private lateinit var seekBars: List<SeekBar>
    var seekChanged: SeekBar? = null
    private lateinit var seeksChanges: MutableList<SeekBar>
    private var numberSeek: Int = 0

    private var balance: Int = 50

    @InjectPresenter
    lateinit var presenter: AddCardPresenter
    @Inject
    lateinit var presenterProvider: Provider<AddCardPresenter>
    @ProvidePresenter
    fun providePresenter(): AddCardPresenter = presenterProvider.get()

    private var item: Item? = null

    private var isBalanced: Boolean = true

    /*override fun showBottomNavigation(navigationView: NavigationView) {
        navigationView.hideBottomNavigation()
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_add_card, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setStatus(EDIT_STATUS)
//        setHasOptionsMenu(true)
        initViews()
        hideLoading()
    }

    fun setBalance() {
        var newBalance = 0
        for(seek in seekBars) {
            newBalance += seek.progress
        }
        balance = newBalance
        Log.d(TAG_LOG,"set balance = $balance")
    }

    private fun initViews() {
        setActionBar(toolbar_back_done)
        setToolbarTitle(toolbar_title, getString(R.string.card_stats))
        card = Card()
        seekBars = listOf<SeekBar>(seekBarSupport, seekBarIntelligence, seekBarPrestige, seekBarHp, seekBarStrength)
        arguments?.let {
            seeksChanges = ArrayList()
            item = gson.fromJson(it.getString(ITEM_ITEM), Item::class.java)
            card?.abstractCard?.wikiUrl = item!!.url!!.content
            card?.abstractCard?.description = item!!.description!!.content
            item!!.text!!.content?.let { presenter.query(it) }
            setBalance()
            setListeners()
        }

    }

   /* override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }*/

    private fun setListeners() {
        seekBarStrength!!.setOnSeekBarChangeListener(this)
        seekBarHp!!.setOnSeekBarChangeListener(this)
        seekBarPrestige!!.setOnSeekBarChangeListener(this)
        seekBarIntelligence!!.setOnSeekBarChangeListener(this)
        seekBarSupport!!.setOnSeekBarChangeListener(this)

        btn_ok.setOnClickListener(this)
        btn_back.setOnClickListener(this)

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        changeTvSeekbar(seekBar)
//        Log.d(TAG_LOG,"isBalanced = $isBalanced")
        if(fromUser) {
            Log.d(TAG_LOG, "from user")
            Log.d(TAG_LOG,"set balance")
            setBalance()
            Log.d(TAG_LOG,"balance with otheres")
            balanceWithOthers(seekBar)
        }
    }

    private fun changeTvSeekbar(seekBar: SeekBar?) {
        val strProgress: String = seekBar?.progress.toString()
        when (seekBar?.id) {
            R.id.seekBarHp -> {
                tvHp!!.setText(strProgress)
                Log.d(TAG_LOG,"seek changed = hp" )
            }

            R.id.seekBarPrestige -> {
                tvPrestige!!.setText(strProgress)
                Log.d(TAG_LOG,"seek changed = prestige" )
            }

            R.id.seekBarIntelligence -> {
                tvIntelligence!!.setText(strProgress)
                Log.d(TAG_LOG,"seek changed = intel" )
            }

            R.id.seekBarSupport -> {
                tvSupport!!.setText(strProgress)
                Log.d(TAG_LOG,"seek changed = support" )
            }

            R.id.seekBarStrength -> {
                tvStrength!!.setText(strProgress)
                Log.d(TAG_LOG,"seek changed = strenght" )
            }
        }
    }

    private fun balanceWithOthers(seekBar: SeekBar?) {
        if(seekChanged == null || seekBar?.id != seekChanged?.id) {
            seeksChanges = ArrayList()
            numberSeek = 0
            for(seek in seekBars) {
                if (!(seek.id == seekBar?.id)){
                    seeksChanges.add(seek)
                }
            }
            Log.d(TAG_LOG,"seekChanged  = " + seekChanged)
        }
        seekChanged = seekBar
        while(balance != 50) {
            for (numb in seeksChanges.indices) {
                val changeSeek = seeksChanges[numberSeek]
                numberSeek = if(numberSeek != (seeksChanges.size-1)) (numberSeek+1) else 0
//                Log.d(TAG_LOG,"numberSeek = " + numberSeek)
                if(balance == 50) {
                    return
                }
                if(balance > 50 && changeSeek.progress > 0) {
                    changeSeek.progress--
                    balance--
                } else if(balance < 50) {
                    changeSeek.progress++
                    balance++
                }
                Log.d(TAG_LOG,"balance = " + balance)
            }

        }
    }


    override fun setQueryResults(list: List<Page>) {
        val page = list[0]
        card!!.abstractCard?.name = page.title
        card!!.abstractCard?.photoUrl = page.original!!.source
        card!!.abstractCard?.extract = page.extract!!.content

    }

    override fun handleError(throwable: Throwable) {

    }


   /* override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.edit_menu, menu)
        val checkItem = menu?.findItem(R.id.action_check)
        checkItem?.setOnMenuItemClickListener {


            true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }*/

    private fun prepareCard() {
        card?.hp = seekBarHp.progress
        card?.strength = seekBarStrength.progress
        card?.support = seekBarSupport.progress
        card?.prestige = seekBarPrestige.progress
        card?.intelligence = seekBarIntelligence.progress
    }

     override fun onClick(v: View) {
            when(v.id) {

                R.id.btn_ok -> addCard()

                R.id.btn_back -> performBackPressed()
         }
     }

    private fun addCard() {
        prepareCard()
        val intent = Intent()
        Log.d(TAG_LOG,"wiki url = " + card?.abstractCard?.wikiUrl)
        val cardJson = gson.toJson(card)
        intent.putExtra(CARD_ITEM, cardJson)
        targetFragment?.onActivityResult(ADD_CARD_CODE, android.support.v7.app.AppCompatActivity.RESULT_OK, intent)
        hideFragment()
    }

    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = AddCardFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment {
            val fragment = AddCardFragment()
            return fragment
        }
    }


}
