package com.beeswork.balance.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import kotlinx.android.synthetic.main.dialog_fetch_error.*

class FetchErrorDialog(
    private val exceptionMessage: String?,
    private val fetchErrorListener: FetchErrorListener
): DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_fetch_error, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnFetchErrorClose.setOnClickListener { dismiss() }
        btnRefetch.setOnClickListener {
            dismiss()
            fetchErrorListener.onRefetch()
        }
        tvFetchErrorMessage.text = exceptionMessage
    }

    interface FetchErrorListener {
        fun onRefetch()
    }
}