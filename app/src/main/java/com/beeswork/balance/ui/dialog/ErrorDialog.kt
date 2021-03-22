package com.beeswork.balance.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogErrorBinding
import com.beeswork.balance.databinding.DialogExceptionBinding
import com.beeswork.balance.internal.constant.ExceptionCode

class ErrorDialog(
    private val error: String?,
    private val errorMessage: String?,
    private val onRetryListener: FetchErrorDialog.OnRetryListener?
): DialogFragment() {

    private lateinit var binding: DialogErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogErrorBinding.inflate(layoutInflater)
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
        onRetryListener?.let {
            binding.btnErrorDialogRetry.setOnClickListener {
                dismiss()
                onRetryListener.onRetry()
            }
        } ?: kotlin.run {
            binding.btnErrorDialogRetry.visibility = View.GONE
        }

        error?.let {

        }

        binding.tvExceptionDialogMessage.text = exceptionMessage
    }

    private fun getLocalizedErrorMessage(): String {
        error?.let {
            context?.getString(error)
        }
    }

    companion object {
        const val TAG = "errorDialog"
    }

}