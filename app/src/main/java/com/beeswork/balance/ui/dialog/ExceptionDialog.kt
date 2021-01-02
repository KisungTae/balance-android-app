package com.beeswork.balance.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import kotlinx.android.synthetic.main.dialog_exception.*

class ExceptionDialog(
    private val exceptionMessage: String?,
    private val exceptionDialogListener: ExceptionDialogListener?
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_exception, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnExceptionDialogClose.setOnClickListener {
            dismiss()
            exceptionDialogListener?.onClickExceptionDialogCloseBtn()
        }
        tvExceptionDialogMessage.text = exceptionMessage
    }

    companion object {
        const val TAG = "exceptionDialog"
    }
}