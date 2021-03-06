package com.beeswork.balance.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogExceptionBinding

class ExceptionDialog(
    private val exceptionMessage: String?,
    private val exceptionDialogListener: ExceptionDialogListener?
) : DialogFragment() {

    private lateinit var binding: DialogExceptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogExceptionBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_exception, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnExceptionDialogClose.setOnClickListener {
            dismiss()
            exceptionDialogListener?.onClickExceptionDialogCloseBtn()
        }
        binding.tvExceptionDialogMessage.text = exceptionMessage
    }

    companion object {
        const val TAG = "exceptionDialog"
    }
}