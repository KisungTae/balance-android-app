package com.beeswork.balance.data.database.entity.swipe

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import java.util.*

@Entity(tableName = "swipe")
data class Swipe(

    @PrimaryKey
    val id: Long,

    val swiperId: UUID,
    val swipedId: UUID,
    val clicked: Boolean,
    var profilePhotoKey: String
) {
    fun isEqualTo(swipeDTO: SwipeDTO): Boolean {
        if (this.id != swipeDTO.id
            || this.swiperId != swipeDTO.swiperId
            || this.swipedId != swipeDTO.swipedId
            || this.clicked != swipeDTO.clicked
            || this.profilePhotoKey != swipeDTO.profilePhotoKey) {
            return false
        }
        return true
    }
}