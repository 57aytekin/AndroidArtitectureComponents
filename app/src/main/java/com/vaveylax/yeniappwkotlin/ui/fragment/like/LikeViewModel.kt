package com.vaveylax.yeniappwkotlin.ui.fragment.like

import androidx.lifecycle.ViewModel
import com.vaveylax.yeniappwkotlin.data.network.repositories.LikesRepository
import com.vaveylax.yeniappwkotlin.util.lazyDeffered
import kotlinx.coroutines.Job

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

    suspend fun updateLikesBudgetCount(userId: Int) = repository.updateLikesBudgetCount(userId)

    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }
}
