package com.vaveylax.yeniappwkotlin.data.db.entities

import androidx.room.Entity
import java.io.Serializable

@Entity
data class PostLikes(
    var begeni_durum : Int? = null
) : Serializable