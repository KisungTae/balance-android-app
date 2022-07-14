package com.beeswork.balance.domain.uistate.swipe

import com.beeswork.balance.internal.util.ContentComparator
import com.beeswork.balance.ui.common.paging.Pageable
import java.util.*

sealed class SwipeUIState : Pageable {

//    override fun getKey(): Long {
//        return id
//    }

    data class Header(override val key: Long = Long.MIN_VALUE) : SwipeUIState() {
//        override fun equals(other: Any?): Boolean {
//            if (this === other) {
//                return true
//            }
//
//            return other != null && javaClass == other.javaClass
//        }
//
//        override fun hashCode(): Int {
//            return Objects.hashCode(id)
//        }
    }

    data class Item(
        override val key: Long,
        val swiperId: UUID,
        val clicked: Boolean,
        val swiperProfilePhotoURL: String?,
    ) : SwipeUIState() {

//        override fun equals(other: Any?): Boolean {
//            return ContentComparator.equals(this, other) {
//                other as Item
//                id != other.id || swiperId != other.swiperId || clicked != other.clicked || swiperProfilePhotoURL != other.swiperProfilePhotoURL
//            }


//            if (this === other) {
//                return true
//            }
//            if (other == null || javaClass != other.javaClass) {
//                return false
//            }
//            other as Item
//            if (id != other.id || swiperId != other.swiperId || clicked != other.clicked || swiperProfilePhotoURL != other.swiperProfilePhotoURL) {
//                return false
//            }
//            return true
//        }
//
//        override fun hashCode(): Int {
//            return Objects.hash(id, swiperId, clicked, swiperProfilePhotoURL)
//        }
    }

    data class Footer(
        override val key: Long = Long.MIN_VALUE
    ) : SwipeUIState() {

//        override fun equals(other: Any?): Boolean {
//            if (this === other) {
//                return true
//            }
//            return other != null && javaClass == other.javaClass
//        }
//
//        override fun hashCode(): Int {
//            return Objects.hashCode(id)
//        }
    }
}