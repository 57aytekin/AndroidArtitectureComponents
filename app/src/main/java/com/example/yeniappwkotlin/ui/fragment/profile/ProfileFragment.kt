package com.example.yeniappwkotlin.ui.fragment.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.UserRepository
import com.example.yeniappwkotlin.ui.activity.auth.LoginActivity
import com.example.yeniappwkotlin.ui.activity.edit_profile.EditProfileActivity
import com.example.yeniappwkotlin.util.PrefUtils
import com.example.yeniappwkotlin.util.loadImage
import com.example.yeniappwkotlin.util.openCloseSoftKeyboard
import com.example.yeniappwkotlin.util.snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile_app_bar.*
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    var navController: NavController? = null
    private lateinit var mAuth : FirebaseAuth
    private var userId : Int? = null

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        openCloseSoftKeyboard(requireContext(),requireView(), false)

        mAuth = FirebaseAuth.getInstance()
        val networkConnectionInterceptor = NetworkConnectionInterceptor(requireContext())
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(requireContext())
        val repository = UserRepository(api, db)
        val factory = ProfileViewModelFactory(repository)
        userId = PrefUtils.with(requireContext()).getInt("user_id",-1)

        viewModel = ViewModelProviders.of(this, factory).get(ProfileViewModel::class.java)

        val userName: String
        val userFirstName: String
        val userImage: String
        val userLastName: String
        val isSocial: Int

        val bundle = arguments
        if (bundle != null){
            if (bundle.getInt("different_user_id") != userId){
                btnProfileDuzenle.text = "Takip Et"
            }
            userName = bundle.getString("different_user_name")!!
            userFirstName = bundle.getString("different_first_name")!!
            userLastName = bundle.getString("different_last_name")!!
            userImage = bundle.getString("different_user_photo")!!
            isSocial = bundle.getInt("different_user_isSocial")

        }else{
            btnProfileDuzenle.text = "Profili Düzenle"
            userName = PrefUtils.with(requireContext()).getString("user_name", "")!!
            userFirstName = PrefUtils.with(requireContext()).getString("user_first_name", "")!!
            userLastName = PrefUtils.with(requireContext()).getString("user_last_name", "")!!
            userImage = PrefUtils.with(requireContext()).getString("user_image", "")!!
            isSocial = PrefUtils.with(requireContext()).getInt("is_social_account",0)
        }

        profileUserName.text = userName
        tvProfileUserName.text = "$userFirstName $userLastName"
        loadImage(ivProfilePhoto, userImage,isSocial)

        btnProfileDuzenle.setOnClickListener {
            if (bundle != null && bundle.getInt("different_user_id") != userId){
                profile_container.snackbar(getString(R.string.level_low))
            }else{
                startActivity(Intent(requireContext(), EditProfileActivity::class.java))
            }
        }
        navController = Navigation.findNavController(requireActivity(), R.id.profile_fragment)
        profiletabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("RESELECT","RESELECT")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.d("UNSELECT","UNSELECT")
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 -> {
                        navController!!.navigate(R.id.profilePaylasimlarFragment)
                    }
                    1 -> {
                        navController!!.navigate(R.id.yakindaFragment)
                    }
                }
            }
        })

        profile_logout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        PrefUtils.with(requireContext()).clear()
        mAuth.signOut()
        Intent(requireContext(), LoginActivity::class.java).also {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.deleteUser()
                viewModel.deletePost()
                viewModel.deleteLikes()
                viewModel.deleteMessageList()
                viewModel.updateIsLogin(userId!!, 0)
                viewModel.updateUserLoginStatu(userId!!, 0)
            }
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        }
    }
}
