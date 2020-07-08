package com.example.yeniappwkotlin.ui.fragment.profile_paylasimlar

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.example.yeniappwkotlin.util.Coroutines
import com.example.yeniappwkotlin.util.PrefUtils
import com.example.yeniappwkotlin.util.hide
import com.example.yeniappwkotlin.util.show
import kotlinx.android.synthetic.main.profile_paylasimlar_fragment.*

class ProfilePaylasimlarFragment : Fragment() {
    val errorMessage = "PROFILE_PAYLASIMLAR_EXPCEPTION"

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

        Coroutines.main {
            try {
                paylasimlar_progress_bar.show()
                val userPosts = viewModel.getPost.await()
                if (userPosts.isNotEmpty()){
                    paylasimlar_progress_bar.hide()
                    tvEmptyUserPosts.visibility = View.INVISIBLE
                    recycler_profile_paylasimlar.also {
                        it.layoutManager = LinearLayoutManager(requireContext())
                        it.setHasFixedSize(true)
                        it.adapter = ProfilePaylasimlarAdapter(userPosts)
                    }
                }else{
                    paylasimlar_progress_bar.hide()
                    tvEmptyUserPosts.visibility = View.VISIBLE
                    tvEmptyUserPosts.text = getString(R.string.empty_user_posts)
                }
            }catch (e : Exception){
                Log.d(errorMessage,e.printStackTrace().toString())
            }
        }
    }

}
