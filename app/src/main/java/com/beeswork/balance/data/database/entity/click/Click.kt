package com.beeswork.balance.data.database.entity.click

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beeswork.balance.data.network.response.click.ClickDTO
import org.threeten.bp.OffsetDateTime
import java.util.*

@Entity(tableName = "click")
data class Click(

    @PrimaryKey
    val id: Long,

    val swiperId: UUID,
    val swipedId: UUID,
    val clicked: Boolean,
    var profilePhotoKey: String
) {
    fun isEqualTo(clickDTO: ClickDTO): Boolean {
        if (this.id != clickDTO.id
            || this.swiperId != clickDTO.swiperId
            || this.swipedId != clickDTO.swipedId
            || this.clicked != clickDTO.clicked
            || this.profilePhotoKey != clickDTO.profilePhotoKey) {
            return false
        }
        return true
    }
}