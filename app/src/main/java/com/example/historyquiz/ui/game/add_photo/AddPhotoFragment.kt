package com.example.historyquiz.ui.game.add_photo

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import com.annimon.stream.Stream
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.card.Card
import com.example.historyquiz.model.db_dop_models.PhotoItem
import com.example.historyquiz.model.wiki_api.opensearch.Item
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.cards.add_card.AddCardFragment
import com.example.historyquiz.ui.cards.add_card_list.AddCardListAdapter
import com.example.historyquiz.ui.cards.add_card_list.AddCardListPresenter
import com.example.historyquiz.ui.cards.add_card_list.AddCardListView
import com.example.historyquiz.utils.AppHelper
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.EDIT_STATUS
import com.example.historyquiz.utils.Const.USER_ID
import com.example.historyquiz.utils.Const.gson
import com.google.gson.Gson
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_add_list.*
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import java.util.ArrayList
import java.util.regex.Pattern
import javax.inject.Inject

class AddPhotoFragment: BaseFragment(), AddPhotoView {

    @InjectPresenter
    lateinit var presenter: AddPhotoPresenter
    lateinit var adapter: AddPhotoAdapter
    lateinit var userId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_add_list, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatus(EDIT_STATUS)
        initRecycler()
        hideLoading()
        hideListLoading()
        setActionBar(toolbar)
        setActionBarTitle(R.string.search_card)
        arguments?.let {
            userId = it.getString(USER_ID)
            presenter.loadPhotos(userId)
        }

    }

    override fun handleError(throwable: Throwable) {

    }

    override fun showListLoading(disposable: Disposable) {
        pg_list.visibility = View.VISIBLE
    }

    override fun hideListLoading() {
        pg_list.visibility = View.GONE
    }

    override fun changeDataSet(tests: List<PhotoItem>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadNextElements(i: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setNotLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun initRecycler() {
        adapter = AddPhotoAdapter(ArrayList())
        val manager = LinearLayoutManager(this.activity)
        rv_list.layoutManager = manager
        rv_list.setEmptyView(tv_empty)
        adapter!!.attachToRecyclerView(rv_list)
        adapter!!.setOnItemClickListener(this)
        rv_list.adapter = adapter
        rv_list.setHasFixedSize(true)
    }


    override fun onItemClick(item: PhotoItem) {
        val args = Bundle()
        val itemJson = gson.toJson(item)
        args.putString(Const.ITEM_ITEM, itemJson)
        val fragment = AddCardFragment.newInstance(args)
        fragment.setTargetFragment(this, Const.ADD_CARD_CODE)
        showFragment(this, fragment)
    }

    companion object {

        fun newInstance(args: Bundle): Fragment {
            val fragment = AddPhotoFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment {
            val fragment = AddPhotoFragment()
            return fragment
        }
    }

}