package com.beeswork.balance.data.database.repository.chat

interface ChatMessageInvalidationListener {
    fun onInvalidate(chatMessageInvalidation: ChatMessageInvalidation)
}