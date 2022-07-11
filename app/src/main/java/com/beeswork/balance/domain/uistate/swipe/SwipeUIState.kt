package com.beeswork.balance.domain.uistate.swipe

import com.beeswork.balance.ui.common.paging.Pageable
import java.util.*

sealed class SwipeUIState(
    val id: Long
) : Pageable {

    override fun getKey(): Long {
        return id
    }

    class Header : SwipeUIState(Long.MIN_VALUE) {
        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }

            if (other == null || javaClass != other.javaClass) {
                return false
            }

            other as Header
            return id == other.id
        }

        override fun hashCode(): Int {
            return Objects.hashCode(id)
        }
    }

    class Item(
        id: Long,
        val swiperId: UUID,
        val clicked: Boolean,
        val swiperProfilePhotoURL: String?,
    ) : SwipeUIState(id) {

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }

            if (other == null || javaClass != other.javaClass) {
                return false
            }

            other as Item
            if (id != other.id || swiperId != other.swiperId || clicked != other.clicked || swiperProfilePhotoURL != other.swiperProfilePhotoURL) {
                return false
            }
            return true
        }

        override fun hashCode(): Int {
            return Objects.hash(id, swiperId, clicked, swiperProfilePhotoURL)
        }
    }

    class Footer : SwipeUIState((Long.MIN_VALUE + 1)) {

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other == null || javaClass != other.javaClass) {
                return false
            }
            other as Footer
            return id == other.id
        }

        override fun hashCode(): Int {
            return Objects.hashCode(id)
        }
    }
}