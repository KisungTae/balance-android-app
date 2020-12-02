package com.beeswork.balance.ui.profile

import com.google.firebase.iid.GmsRpc

data class PhotoPicker(
    var key: String?,
    var status: Status
) {

    enum class Status {
        EMPTY,
        LOADING,
        ERROR,
        UPLOADED
    }
}