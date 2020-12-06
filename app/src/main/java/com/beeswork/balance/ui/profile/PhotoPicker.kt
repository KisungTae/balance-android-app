package com.beeswork.balance.ui.profile

import android.net.Uri
import com.google.firebase.iid.GmsRpc

data class PhotoPicker(
    var key: String?,
    var status: Status,
    var uri: Uri?
) {

    enum class Status {
        EMPTY,
        LOADING,
        ERROR,
        UPLOADED
    }
}