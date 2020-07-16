package com.example.yeniappwkotlin.ui.activity.edit_profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.CommentRepository
import com.example.yeniappwkotlin.data.network.repositories.UserRepository
import com.example.yeniappwkotlin.ui.activity.comment.CommentListener
import com.example.yeniappwkotlin.ui.activity.comment.CommentViewModel
import com.example.yeniappwkotlin.ui.activity.comment.CommentViewModelFactory
import com.example.yeniappwkotlin.util.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.lang.Exception

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var viewModel: EditProfileViewModel
    private var IMG_RESULT = 777
    private var bitmap: Bitmap? = null
    private var userId: Int? = null
    private var userName: String? = null
    private var userFirstName: String? = null
    private var userLastName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val networkConnectionInterceptor = NetworkConnectionInterceptor(this)
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(this)
        val repository = UserRepository(api, db)
        val factory = EditProfileFactory(repository)

        viewModel = ViewModelProviders.of(this, factory).get(EditProfileViewModel::class.java)

        userName = PrefUtils.with(this).getString("user_name", "")
        userId = PrefUtils.with(this).getInt("user_id", -1)
        userFirstName = PrefUtils.with(this).getString("user_first_name", "")
        userLastName = PrefUtils.with(this).getString("user_last_name", "")
        val userImage = PrefUtils.with(this).getString("user_image", "")
        val isSocial = PrefUtils.with(this).getInt("is_social_account", 0)

        loadImage(ivEditPhoto, userImage, isSocial)
        etEditUserName.setText(userName)
        etEditName.setText(userFirstName)
        etEditLastName.setText(userLastName)

        ivEditPhoto.setOnClickListener(this)
        tvEditText.setOnClickListener(this)
        ivEditAccept.setOnClickListener(this)
        ivEditBackButton.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tvEditText -> {
                selectImage()
            }
            R.id.ivEditPhoto -> {
                selectImage()
            }
            R.id.ivEditBackButton -> {
                finish()
            }
            R.id.ivEditAccept -> {
                val view = this.currentFocus
                try {
                    openCloseSoftKeyboard(this, view!!, false)
                }catch (e: Exception){
                    Log.d("EXP_",e.printStackTrace().toString())
                }
                if (!isChanged(userName!!, userFirstName!!, userLastName!!)){
                    editContainer.snackbar("Lütfen değişiklik yaptıktan sonra kaydedin")
                    return
                }

                if (!checkValidation()){
                    return
                }
                Coroutines.main {
                    try {
                        edit_progress_bar.show()
                        val response = viewModel.updateUserProfile(
                            userId!!,
                            etEditUserName.text.toString().trim(),
                            etEditName.text.toString().trim(),
                            etEditLastName.text.toString().trim(),
                            encode()
                        )
                        if (response.success == 1) {
                            edit_progress_bar.hide()
                            etEditName.isFocusable = false
                            etEditUserName.isFocusable = false
                            etEditLastName.isFocusable = false
                            if (response.message == "username_taken") {
                                editContainer.snackbar("Girmiş olduğunuz kullanıcı adı kullanılmakta")
                            } else {
                                editContainer.snackbar("Başarılı")
                                updateLocalUser()
                                updateSharedPreferenceValue()
                                check.check()
                                finish()
                            }
                        } else {
                            edit_progress_bar.hide()
                            etEditName.isFocusable = false
                            etEditUserName.isFocusable = false
                            etEditLastName.isFocusable = false
                            editContainer.snackbar("Lütfen değişiklik yaptıktan sonra kaydedin")
                        }
                    }  catch (e: ApiException) {
                        editContainer.snackbar("Sunucu da bir sorun oluştu: ${e.printStackTrace()}")
                    } catch (e: NoInternetException) {
                        editContainer.snackbar(getString(R.string.check_internet))
                    }catch (e: Exception) {
                        edit_progress_bar.hide()
                        editContainer.snackbar("Bir hata ile karşılaştık: ${e.printStackTrace()}")
                    }
                }
            }
        }
    }

    private fun isChanged( username : String, name : String, surname : String) : Boolean{
        return !(encode() == "null" && etEditUserName.text.toString() == username
                && etEditName.text.toString() == name
                && etEditLastName.text.toString() == surname)
    }

    private fun updateSharedPreferenceValue() {
        val paths = "image/${etEditUserName.text}.jpg"
        PrefUtils.with(this).save("user_name", etEditUserName.text.toString())
        PrefUtils.with(this).save("user_first_name", etEditName.text.toString())
        PrefUtils.with(this).save("user_last_name", etEditLastName.text.toString())
        if (encode() != "null") {
            PrefUtils.with(this).save("user_image", paths)
        }
    }

    private fun updateLocalUser() {
        Coroutines.main {
            try {
                viewModel.updateLocalUser(
                    etEditUserName.text.toString(),
                    etEditName.text.toString(),
                    etEditLastName.text.toString(),
                    encode(),
                    userId!!
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkValidation() : Boolean {
        val userName = etEditUserName.text.trim()
        if (etEditUserName.text.isEmpty() || etEditName.text.isEmpty() || etEditLastName.text.isEmpty()) {
            editContainer.snackbar(getString(R.string.fill_fields))
            return false
        }
        else if (etEditUserName.text.length < 5 || etEditUserName.text.length > 20) {
            editContainer.snackbar(getString(R.string.valid_user_name))
            return false
        }
        else if (etEditName.text.length < 3 || etEditName.text.length > 15) {
            editContainer.snackbar(getString(R.string.valid_first_name))
            return false
        }
        else if (etEditLastName.text.length < 2 || etEditLastName.text.length > 15) {
            editContainer.snackbar(getString(R.string.valid_last_name))
            return false
        }
        else if (checkTurkishCharacter(etEditUserName.text.toString().trim())) {
            editContainer.snackbar("Kullanıcı adında türkçe karakter(ı,ş,ç,ö,ü) ve boşluk olamaz ")
            return false
        }else{
            return  true
        }
    }

    private fun checkTurkishCharacter(deger: String): Boolean {
        return (deger.contains(" ") || deger.contains("ş") || deger.contains("Ş") || deger.contains("Ç")
                || deger.contains("ç") || deger.contains("Ğ") || deger.contains("ğ") || deger.contains("ş")
                || deger.contains("ö") || deger.contains("Ö") || deger.contains("ü") || deger.contains("Ü")
                || deger.contains("ı") || deger.contains("İ"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMG_RESULT && resultCode == Activity.RESULT_OK && data != null) {
            val path = data.data
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, path)
                ivEditPhoto.setImageBitmap(bitmap)
            } catch (e: Exception) {

            }
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMG_RESULT)
    }

    private fun encode(): String {
        // Encode image to base64 string
        val baos = ByteArrayOutputStream()
        return if (bitmap != null) {
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageBytes = baos.toByteArray()
            encodeToString(imageBytes, DEFAULT)
        } else {
            "null"
        }
    }
}