package com.example.yeniappwkotlin.ui.fragment.profile

import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation

import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.NoConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.UserRepository
import com.example.yeniappwkotlin.ui.activity.auth.LoginActivity
import com.example.yeniappwkotlin.ui.activity.edit_profile.EditProfileActivity
import com.example.yeniappwkotlin.util.*
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile_app_bar.*
import kotlinx.android.synthetic.main.fragment_profile_app_bar.view.*
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.security.auth.callback.Callback

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
        val userName = PrefUtils.with(requireContext()).getString("user_name", "")
        val userFirstName = PrefUtils.with(requireContext()).getString("user_first_name", "")
        val userLastName = PrefUtils.with(requireContext()).getString("user_last_name", "")
        val userImage = PrefUtils.with(requireContext()).getString("user_image", "")
        val isSocial = PrefUtils.with(requireContext()).getInt("is_social_account",0)
        profileUserName.text = userName
        tvProfileUserName.text = "$userFirstName $userLastName"
        loadImage(ivProfilePhoto, userImage,isSocial)

        btnProfileDuzenle.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
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
