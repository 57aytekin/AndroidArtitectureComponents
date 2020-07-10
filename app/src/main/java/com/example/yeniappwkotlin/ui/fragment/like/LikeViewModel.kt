package com.example.yeniappwkotlin.ui.fragment.like

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yeniappwkotlin.data.db.entities.Likes
import com.example.yeniappwkotlin.data.network.repositories.LikesRepository
import com.example.yeniappwkotlin.util.ApiException
import com.example.yeniappwkotlin.util.Coroutines
import com.example.yeniappwkotlin.util.NoInternetException
import com.example.yeniappwkotlin.util.lazyDeffered
import kotlinx.coroutines.Job
import java.lang.Exception

class LikeViewModel(
    private val repository: LikesRepository,
    private val userId : Int
) : ViewModel() {
    private lateinit var job: Job

    /*private val _likes = MutableLiveData<List<Likes>>()
    val likes : LiveData<List<Likes>>
        get() = _likes

    fun getLikes() {
        job = Coroutines.ioThenMain(
            {repository.getLikes(userId)},
            {_likes.value = it}
        )
    }*/

    val getLikes by lazyDeffered {
        repository.getLikes(userId)
    }

    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }
}
