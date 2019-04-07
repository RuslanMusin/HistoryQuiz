package com.example.historyquiz.ui.auth.fragments.signup

import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.bumptech.glide.Glide
import com.example.historyquiz.R
import com.example.historyquiz.model.db_dop_models.PhotoItem
import com.example.historyquiz.model.user.User
import com.example.historyquiz.ui.base.BaseFragment
import com.example.historyquiz.ui.navigation.NavigationView
import com.example.historyquiz.utils.Const.PHOTO_ITEM
import com.example.historyquiz.utils.Const.STUB_PATH
import com.example.historyquiz.utils.Const.USER_ITEM
import com.google.gson.Gson
import java.io.InputStream
import javax.inject.Inject

class SignUpFragment: BaseFragment(), SignUpView, View.OnClickListener {

    @InjectPresenter
    lateinit var signUpPresenter: SignUpPresenter

    @Inject
    lateinit var gson: Gson

    lateinit var photoDialog: MaterialDialog

    var imageUri: Uri? = null
    var photoUrl: String = STUB_PATH

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun showBottomNavigation(navigationView: NavigationView) {
        navigationView.hideBottomNavigation()
    }

    private fun initViews() {
//        setBottomVisibility(false)
        setListeners()
    }

    private fun setListeners() {
        btn_sign_up.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        li_add_photo.setOnClickListener(this)

        iv_cover.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sign_up -> {
                val user = buildUser()
                signUpPresenter.createAccount(user, imageUri)
            }

            R.id.btn_login -> goToLogin()

            R.id.li_add_photo -> addPhoto()

            R.id.iv_cover -> setFillerData()
        }
    }

    private fun setFillerData() {
        et_username.setText("rust@ma.ru")
        et_password.setText("rustamka")
        et_email.setText("rust@ma.ru")
    }

    private fun buildUser(): User {
        val user = User()
        user.username = et_username.text.toString()
        user.email = et_email.text.toString()
        user.password = et_password.text.toString()
        user.photoUrl = photoUrl
        user.lowerUsername = user.username?.toLowerCase()

        return user
    }

    override fun showEmailError(hasError: Boolean) {
        if(hasError) {
            ti_email.error = getString(R.string.enter_correct_name)
        } else {
            ti_email.error = null
        }

    }

    override fun showPasswordError(hasError: Boolean) {
        if(hasError) {
            ti_password.error = getString(R.string.enter_correct_password)
        } else {
            ti_password.error = null
        }

    }

    private fun goToLogin() {
        openLoginPage()
//        Navigation.findNavController(btn_login).navigate(R.id.action_signUpFragment_to_loginFragment2)
    }

    override fun goToProfile(user: User) {
        val args = Bundle()
        args.putString(USER_ITEM, gson.toJson(user))
        openNavigationPage()
       /* Navigation.findNavController(btn_sign_up)
            .navigate(R.id.action_signUpFragment_to_profileFragment,args)*/
    }

    private fun addPhoto() {
        activity?.let {
            photoDialog = MaterialDialog.Builder(it)
                .customView(R.layout.dialog_pick_image, false).build()

            photoDialog.btn_choose_gallery.setOnClickListener{ showGallery() }
            photoDialog.btn_choose_standart.setOnClickListener{ showStandarts()}

            photoDialog.show()
        }


    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if (resultCode == android.support.v7.app.AppCompatActivity.RESULT_OK) {
            if(reqCode == GALLERY_PHOTO) {
                imageUri = data?.data
                activity?.let {
                    val imageStream: InputStream = it.contentResolver.openInputStream(imageUri)
                    val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
                    iv_cover.setImageBitmap(selectedImage)
                }

            }
            if(reqCode == STANDART_PHOTO) {
                val photoItem = gson.fromJson(data?.getStringExtra(PHOTO_ITEM), PhotoItem::class.java)
                photoUrl = photoItem.photoUrl
                Glide.with(this)
                    .load(photoUrl)
                    .into(iv_cover)
            }
        } else {
            imageUri = null
            Toast.makeText(activity!!, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }
    }

    fun showGallery() {
        photoDialog.hide()
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_PHOTO)
    }

    fun showStandarts() {
      /*  photoDialog.hide()
        val intent = Intent(this, AddPhotoActivity::class.java)
        intent.putExtra(USER_ID, BOT_ID)
        startActivityForResult(intent, STANDART_PHOTO)*/
    }

    companion object {

        private val GALLERY_PHOTO = 0

        private val STANDART_PHOTO = 1

            fun newInstance(args: Bundle): Fragment {
                val fragment = SignUpFragment()
                fragment.arguments = args
                return fragment
            }

            fun newInstance(): Fragment {
                return SignUpFragment()
            }
    }
}