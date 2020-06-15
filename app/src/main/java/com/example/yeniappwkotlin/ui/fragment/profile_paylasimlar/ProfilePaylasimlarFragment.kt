package com.example.yeniappwkotlin.ui.fragment.profile_paylasimlar

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.NoConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.PostRepository
import com.example.yeniappwkotlin.ui.fragment.home.HomeViewModelFactory
import com.example.yeniappwkotlin.util.PrefUtils
import kotlinx.android.synthetic.main.profile_paylasimlar_fragment.*

class ProfilePaylasimlarFragment : Fragment() {

    companion object {
        fun newInstance() =
            ProfilePaylasimlarFragment()
    }

    private lateinit var viewModel: ProfilePaylasimlarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_paylasimlar_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val userId = PrefUtils.with(requireContext()).getInt("user_id", 0)
        val networkConnectionInterceptor = NetworkConnectionInterceptor(requireContext())
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(requireContext())
        val repository = PostRepository(api,db,requireContext())
        val factory = ProfilePaylasimlarFactory(repository, userId)

        viewModel =
            ViewModelProviders.of(this, factory).get(ProfilePaylasimlarViewModel::class.java)

        //viewModel.getPosts()
        viewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            recycler_profile_paylasimlar.also {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.setHasFixedSize(true)
                it.adapter = ProfilePaylasimlarAdapter(posts)
            }
        })
    }

}
