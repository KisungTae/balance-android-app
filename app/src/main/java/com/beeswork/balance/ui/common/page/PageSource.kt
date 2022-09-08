package com.beeswork.balance.ui.common.page

abstract class PageSource<Key: Any, Value: Any> {

    abstract suspend fun load(pageLoadParam: PageLoadParam<Key>): PageLoadResult<Key, Value>

}