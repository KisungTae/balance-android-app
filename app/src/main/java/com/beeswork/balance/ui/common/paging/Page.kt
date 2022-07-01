package com.beeswork.balance.ui.common.paging

class Page<Key: Any, Value: Any>(
    val items: List<Value>?,
    val firstKey: Key?,
    val lastKey: Key?,
    val reachedTop: Boolean,
    val reachedBottom: Boolean
) {
    companion object {

    }
}