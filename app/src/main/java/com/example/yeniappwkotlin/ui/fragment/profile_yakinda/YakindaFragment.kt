package com.example.yeniappwkotlin.ui.fragment.profile_yakinda

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.yeniappwkotlin.R

class YakindaFragment : Fragment() {

    companion object {
        fun newInstance() = YakindaFragment()
    }

    private lateinit var viewModel: YakindaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.yakinda_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(YakindaViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
