package com.vaveylax.yeniappwkotlin.ui.fragment.profile_paylasimlar

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager

import com.vaveylax.yeniappwkotlin.R
import com.vaveylax.yeniappwkotlin.data.db.database.AppDatabase
import com.vaveylax.yeniappwkotlin.data.network.MyApi
import com.vaveylax.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.vaveylax.yeniappwkotlin.data.network.repositories.PostRepository
import com.vaveylax.yeniappwkotlin.util.*
import kotlinx.android.synthetic.main.profile_paylasimlar_fragment.*
import java.lang.Exception

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

        val userId = PrefUtils.with(requireContext()).getInt("different_user", 0)
        val currentUserId = PrefUtils.with(requireContext()).getInt("user_id", 0)
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
                    val tvPaylasimCount = requireActivity().findViewById<TextView>(R.id.tvPaylasimCount)
                    tvPaylasimCount.text = userPosts.size.toString()
                    paylasimlar_progress_bar.hide()
                    tvEmptyUserPosts.visibility = View.INVISIBLE
                    recycler_profile_paylasimlar.also {
                        it.layoutManager = LinearLayoutManager(requireContext())
                        it.setHasFixedSize(true)
                        it.adapter = ProfilePaylasimlarAdapter(userPosts)
                    }
                }else{
                    if (currentUserId != userId){
                        stopProgressShowMessage( getString(R.string.empty_different_user_posts))
                    }else{
                        stopProgressShowMessage(getString(R.string.empty_user_posts))
                    }
                }
            }catch (e: ApiException) {
                Log.d("PAYLASIM_1",e.message!!)
                stopProgressShowMessage(getString(R.string.have_a_error))
            } catch (e: NoInternetException) {
                Log.d("PAYLASIM_2",e.message!!)
                stopProgressShowMessage(e.message)
            } catch (e : Exception){
                Log.d("PAYLASIM_3",e.message!!)
                //stopProgressShowMessage(getString(R.string.have_a_error))
            }
        }
    }

    private fun stopProgressShowMessage(e : String){
        try {
            paylasimlar_progress_bar.hide()
            tvEmptyUserPosts.visibility = View.VISIBLE
            tvEmptyUserPosts.text = e
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

}
