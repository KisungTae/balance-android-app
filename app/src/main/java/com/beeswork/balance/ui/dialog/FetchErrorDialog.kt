package com.beeswork.balance.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogFetchErrorBinding

class FetchErrorDialog(
    private val exceptionMessage: String?,
    private val fetchErrorListener: FetchErrorListener
): DialogFragment() {

    private lateinit var binding: DialogFetchErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
        binding = DialogFetchErrorBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_fetch_error, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFetchErrorClose.setOnClickListener { dismiss() }
        binding.btnRefetch.setOnClickListener {
            dismiss()
            fetchErrorListener.onRefetch()
        }
        binding.tvFetchErrorMessage.text = exceptionMessage
    }

    companion object {
        const val TAG = "fetchErrorDialog"
    }

    interface FetchErrorListener {
        fun onRefetch()
    }
}