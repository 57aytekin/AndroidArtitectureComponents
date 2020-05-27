package com.example.yeniappwkotlin.ui.fragment.edit

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.db.entities.User
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.UserRepository
import com.example.yeniappwkotlin.databinding.EditFragmentBinding
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
        val networkConnectionInterceptor = NetworkConnectionInterceptor(requireContext())
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(requireContext())
        val repository = UserRepository(api, db)

        val factory = EditViewModelFactory(this.requireActivity(),repository)
        viewModel = ViewModelProvider(this, factory).get(EditViewModel::class.java)
        binding?.edit = viewModel
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
