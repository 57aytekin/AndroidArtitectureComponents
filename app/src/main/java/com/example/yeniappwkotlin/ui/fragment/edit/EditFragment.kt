package com.example.yeniappwkotlin.ui.fragment.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.yeniappwkotlin.util.PrefUtils
import com.example.yeniappwkotlin.util.loadImage
import com.example.yeniappwkotlin.util.openCloseSoftKeyboard
import com.example.yeniappwkotlin.util.snackbar
import kotlinx.android.synthetic.main.edit_fragment.*

class EditFragment : Fragment(), EditListener {

    private lateinit var viewModel: EditViewModel
    private var binding : EditFragmentBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding  =
            DataBindingUtil.inflate(inflater, R.layout.edit_fragment, container, false)

        binding?.lifecycleOwner = this
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        //User information
        val userId = PrefUtils.with(requireContext()).getInt("user_id",0)
        val userPhotoUrl = PrefUtils.with(requireContext()).getString("user_image", "")
        val isSocial = PrefUtils.with(requireContext()).getInt("is_social_account", 0)
        loadImage(binding!!.fragmentEditPhoto, userPhotoUrl, isSocial)

        val networkConnectionInterceptor = NetworkConnectionInterceptor(requireContext())
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(requireContext())
        val repository = UserRepository(api, db)
        val factory = EditViewModelFactory(this.requireActivity(), repository, userId)
        viewModel = ViewModelProvider(this, factory).get(EditViewModel::class.java)
        binding?.edit = viewModel

        openCloseSoftKeyboard(requireContext(), binding?.fragmentEditEtText!!, true)
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        binding!!.fragmentEditClose.setOnClickListener {
            navController.navigate(
                R.id.action_editFragment_to_homeFragment )
        }
    }

    override fun onStarted() {
        root_edit.snackbar("Paylaşılıyor...")
    }

    override fun onSuccess(message: String) {
        root_edit.snackbar(message)
    }

    override fun onFailure(message: String) {
        root_edit.snackbar(message)
    }
}