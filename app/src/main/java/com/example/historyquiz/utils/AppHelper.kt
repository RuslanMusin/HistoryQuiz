package com.example.historyquiz.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.historyquiz.model.user.User
import com.example.historyquiz.utils.Const.MAX_LENGTH
import com.example.historyquiz.utils.Const.MORE_TEXT
import com.example.historyquiz.utils.Const.STUB_PATH
import com.example.historyquiz.utils.Const.TAG_LOG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

//ОСНОВНОЙ КЛАСС HELPER приложения. ОТСЮДА БЕРЕМ ТЕКУЩЕГО ЮЗЕРА ИЗ БД, ГРУЗИМ ФОТКУ ЮЗЕРА В ПРОФИЛЬ,
//ПОЛУЧАЕМ ССЫЛКУ НА ПУТЬ ФАЙЛОГО ХРАНИЛИЩА И СОЗДАЕМ СЕССИЮ. ПОКА ТАК ПУСТЬ БУДЕТ
class AppHelper {

    companion object {

        lateinit var currentUser: User

        val storageReference: StorageReference
            get() = FirebaseStorage.getInstance().reference

        fun loadUserPhoto(photoView: ImageView) {
            if(!currentUser?.photoUrl.equals(STUB_PATH)) {
                val storageReference = currentUser!!.photoUrl?.let { FirebaseStorage.getInstance().reference.child(it) }

                Glide.with(photoView.context)
                    .load(storageReference)
                    .into(photoView)
            }
        }

        fun loadUserPhoto(photoView: ImageView, photoUrl: String) {
            if(photoUrl.startsWith(Const.IMAGE_START_PATH)) {
                val imageReference = storageReference.child(photoUrl)

                Log.d(TAG_LOG, "name " + (imageReference?.path ?: ""))

                Glide.with(photoView.context)
                    .load(imageReference)
                    .into(photoView)
            } else {
                Glide.with(photoView.context)
                    .load(photoUrl)
                    .into(photoView)
            }
        }

        fun readFileFromAssets(fileName: String, context: Context): List<String> {
            var reader: BufferedReader? = null
            var names: MutableList<String> = ArrayList()
            try {
                reader = BufferedReader(
                    InputStreamReader(context.assets.open(fileName), "UTF-8")
                )
                var mLine: String? = reader.readLine()
                while (mLine != null && !"".equals(mLine)) {
                    names.add(mLine)
                    mLine = reader.readLine()
                }
                return names
            } catch (e: IOException) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        //log the exception
                    }

                }
            }
            return names
        }

        fun convertDpToPx(dp: Float, context: Context): Int {
            val r = context.getResources()
            val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
            ).toInt()
            return px
        }

        fun hideKeyboardFrom(context: Context, view: View) {
            Log.d(TAG_LOG,"hide keyboard")
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }

        fun showKeyboard(context: Context, editText: EditText) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }

        fun cutLongDescription(description: String, maxLength: Int): String {
            return if (description.length < MAX_LENGTH) {
                description
            } else {
                description.substring(0, MAX_LENGTH - MORE_TEXT.length) + MORE_TEXT
            }
        }
    }
}
