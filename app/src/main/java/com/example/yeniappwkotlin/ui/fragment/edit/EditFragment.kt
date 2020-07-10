package com.example.yeniappwkotlin.ui.fragment.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.UserRepository
import com.example.yeniappwkotlin.databinding.EditFragmentBinding
import com.example.yeniappwkotlin.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.edit_fragment.*

class EditFragment : Fragment(), EditListener {

    private lateinit var viewModel: EditViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        //User information
        val userId = PrefUtils.with(requireContext()).getInt("user_id",0)
        val userPhotoUrl = PrefUtils.with(requireContext()).getString("user_image", "")
        val isSocial = PrefUtils.with(requireContext()).getInt("is_social_account", 0)
        loadImage(fragment_edit_photo, userPhotoUrl, isSocial)

        val networkConnectionInterceptor = NetworkConnectionInterceptor(requireContext())
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(requireContext())
        val repository = UserRepository(api, db)
        val factory = EditViewModelFactory(this.requireActivity(), repository, userId)
        viewModel = ViewModelProvider(this, factory).get(EditViewModel::class.java)

        viewModel.editListener = this
        openCloseSoftKeyboard(requireContext(), fragment_edit_etText, true)
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        val asd = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        fragment_edit_close.setOnClickListener {
            navController.navigate(
                R.id.action_editFragment_to_homeFragment )
            asd.checkItem(R.id.nav_tab_home)
        }
        tvShare.setOnClickListener {
            viewModel.onSavePostButtonClick(fragment_edit_etText.text.toString())
        }
    }

    override fun onStarted() {
        edit_progress_bar.show()
    }

    override fun onSuccess(message: String) {
        edit_progress_bar.hide()
    }

    override fun onFailure(message: String) {
        edit_progress_bar.hide()
        if (message == "check_internet"){
            requireContext().toast(getString(R.string.check_internet))
        }else{
            requireContext().toast(message)
        }
    }
}