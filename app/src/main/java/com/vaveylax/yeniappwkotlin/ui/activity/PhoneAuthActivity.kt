package com.vaveylax.yeniappwkotlin.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.vaveylax.yeniappwkotlin.R
import com.vaveylax.yeniappwkotlin.ui.activity.auth.RegisterActivity
import com.vaveylax.yeniappwkotlin.util.GenericKeyEvent
import com.vaveylax.yeniappwkotlin.util.GenericTextWatcher
import com.vaveylax.yeniappwkotlin.util.toast
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_phone_auth.*
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationId = ""
    var responseCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)

        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val phoneNumber = intent.getStringExtra("phone_number")

        val text = "+90 $phoneNumber${getString(R.string.phone_send_code)}"
        phone_auth_text.text = text

        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val code = credential.smsCode
                if (code != null) {
                    responseCode = code
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                toast(p0.message!!)
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationId = p0
            }
        }

        sendVerificationCode(phoneNumber!!)
        mAuth.setLanguageCode("tr")

        btn_phone_auth.setOnClickListener {
            val code =
                verify1.text.toString() + verify2.text.toString() + verify3.text.toString() + verify4.text.toString() + verify5.text.toString() + verify6.text.toString()
            //authenticate()
            if (code.isEmpty() || code.length < 6) {
                toast(getString(R.string.six_character))
            } else {
                verifyCode(code)
            }
        }
        //Edittexts listener
        edittextsListener(verify1!! , verify2, verify3, verify4, verify5, verify6)
    }

    private fun edittextsListener(
        verify1: EditText,
        verify2: EditText,
        verify3: EditText,
        verify4: EditText,
        verify5: EditText,
        verify6: EditText
    ) {
        //GenericTextWatcher here works only for moving to next EditText when a number is entered
        //first parameter is the current EditText and second parameter is next EditText
        verify1.addTextChangedListener(GenericTextWatcher(verify1, verify2))
        verify2.addTextChangedListener(GenericTextWatcher(verify2, verify3))
        verify3.addTextChangedListener(GenericTextWatcher(verify3, verify4))
        verify4.addTextChangedListener(GenericTextWatcher(verify4, verify5))
        verify5.addTextChangedListener(GenericTextWatcher(verify5, verify6))
        verify6.addTextChangedListener(GenericTextWatcher(verify6, null))

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        //first parameter is the current EditText and second parameter is previous EditText
        verify1.setOnKeyListener(GenericKeyEvent(verify1, null))
        verify2.setOnKeyListener(GenericKeyEvent(verify2, verify1))
        verify3.setOnKeyListener(GenericKeyEvent(verify3, verify2))
        verify4.setOnKeyListener(GenericKeyEvent(verify4,verify3))
        verify5.setOnKeyListener(GenericKeyEvent(verify5,verify4))
        verify6.setOnKeyListener(GenericKeyEvent(verify6,verify5))
    }

    private fun verifyCode(verifyNo: String) {
        val credential: PhoneAuthCredential =
            PhoneAuthProvider.getCredential(verificationId, verifyNo)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val intent = Intent(this, RegisterActivity::class.java)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    toast(task.exception?.message.toString())
                }
            }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val phone = "+90$phoneNumber"
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone, // Phone number to verify
            120, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            mCallbacks
        ) // OnVerificationStateChangedCallbacks
    }

}