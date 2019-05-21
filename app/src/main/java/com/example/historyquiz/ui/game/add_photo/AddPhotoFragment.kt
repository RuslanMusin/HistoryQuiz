package com.example.historyquiz.ui.game.add_photo

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.historyquiz.R
import com.example.historyquiz.model.db_dop_models.PhotoItem
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.utils.Const
import com.example.historyquiz.utils.Const.BOT_ID
import com.example.historyquiz.utils.Const.gson
import kotlinx.android.synthetic.main.fragment_search_card.*
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class AddPhotoFragment: BaseFragment(), AddPhotoView {

    @InjectPresenter
    lateinit var presenter: AddPhotoPresenter
    @Inject
    lateinit var presenterProvider: Provider<AddPhotoPresenter>
    @ProvidePresenter
    fun providePresenter(): AddPhotoPresenter = presenterProvider.get()

    lateinit var adapter: AddPhotoAdapter
    lateinit var userId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_card, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinner.visibility = View.GONE
        setStatus(Const.EDIT_STATUS)
        setWaitStatus(false)
        initRecycler()
        hideLoading()
        hideListLoading()
        setActionBar(toolbar)
        toolbar.setNavigationOnClickListener { hideFragment() }
        setActionBarTitle(R.string.choose_avatar)
        presenter.loadPhotos(BOT_ID)
       /* arguments?.let {
            userId = it.getString(BOT_ID)
            presenter.loadPhotos(userId)
        }*/

    }

    override fun handleError(throwable: Throwable) {

    }

    override fun showListLoading() {
        pg_list.visibility = View.VISIBLE
    }

    override fun hideListLoading() {
        pg_list.visibility = View.GONE
    }

    override fun loadNextElements(i: Int) {
    }

    override fun setNotLoading() {
    }

    override fun changeDataSet(tests: List<PhotoItem>) {
        adapter!!.changeDataSet(tests)
        hideListLoading()
        hideLoading()
    }

    private fun initRecycler() {
        adapter = AddPhotoAdapter(ArrayList())
        val manager = GridLayoutManager(this.activity, 4)
        rv_list.layoutManager = manager
        rv_list.setEmptyView(tv_empty)
        adapter!!.attachToRecyclerView(rv_list)
        adapter!!.setOnItemClickListener(this)
        rv_list.adapter = adapter
        rv_list.setHasFixedSize(true)
    }


    override fun onItemClick(item: PhotoItem) {
        val intent = Intent()
        intent.putExtra(Const.PHOTO_ITEM, gson.toJson(item))
        targetFragment?.onActivityResult(Const.ADD_CARD_CODE, android.support.v7.app.AppCompatActivity.RESULT_OK, intent)
        hideFragment()
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