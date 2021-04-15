package com.beeswork.balance.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogErrorBinding
import com.beeswork.balance.internal.util.safeLet

class ErrorDialog(
    private val error: String?,
    private val errorTitle: String?,
    private val errorMessage: String?,
    private val requestCode: Int?,
    private val onRetryListener: OnRetryListener?,
    private val onDismissListener: OnDismissListener?
) : DialogFragment() {

    private lateinit var binding: DialogErrorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogErrorBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        setupRetryListener()
        setupErrorTitle()
        setupErrorMessage()
        binding.btnErrorDialogClose.setOnClickListener { dismiss() }
    }

    private fun setupErrorTitle() {
        binding.tvErrorDialogTitle.text = errorTitle ?: resources.getString(R.string.error_title_generic)
    }

    private fun setupErrorMessage() {
        errorMessage?.let { message ->
            binding.tvErrorDialogMessage.text = message
        } ?: kotlin.run {
            safeLet(error, context) { e, c ->
                val resourceId = resources.getIdentifier(e, "string", c.packageName)
                binding.tvErrorDialogMessage.text = if (resourceId > 0) getString(resourceId)
                else getString(R.string.error_message_generic)
            } ?: kotlin.run {
                binding.tvErrorDialogMessage.text = resources.getString(R.string.error_message_generic)
            }
        }
    }

    private fun setupRetryListener() {
        onRetryListener?.let {
            binding.btnErrorDialogRetry.setOnClickListener {
                dismiss()
                onRetryListener.onRetry(requestCode)
            }
        } ?: kotlin.run {
            binding.btnErrorDialogRetry.visibility = View.GONE
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss()
    }

    companion object {
        const val TAG = "errorDialog"
    }

    interface OnRetryListener {
        fun onRetry(requestCode: Int?)
    }

    interface OnDismissListener {
        fun onDismiss()
    }

}