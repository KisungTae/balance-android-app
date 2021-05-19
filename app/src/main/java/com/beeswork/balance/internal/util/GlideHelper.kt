package com.beeswork.balance.internal.util

import com.beeswork.balance.GlideOptions
import com.beeswork.balance.R
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class GlideHelper {
    companion object {

        fun profilePhotoGlideOptions(): RequestOptions {
            return RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .priority(Priority.HIGH)
                .placeholder(R.drawable.ic_baseline_account_circle)
                .error(R.drawable.ic_baseline_account_circle)
        }

        fun cardPhotoGlideOptions(): RequestOptions {
            return RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .priority(Priority.HIGH)
                .placeholder(R.drawable.ic_baseline_person_1024)
                .error(R.drawable.ic_baseline_person_1024)
        }
    }
}