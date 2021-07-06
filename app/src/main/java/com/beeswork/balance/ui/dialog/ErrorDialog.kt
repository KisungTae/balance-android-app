package com.beeswork.balance.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogErrorBinding
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.common.BaseDialog
import java.util.*

class ErrorDialog(
    private val error: String?,
    private val errorTitle: String?,
    private val errorMessage: String?,
    private val requestCode: Int?,
    private val onRetryListener: OnRetryListener?,
    private val onDismissListener: OnDismissListener?,
    private val id: UUID? = null
) : BaseDialog() {

    constructor(
        error: String?,
        errorTitle: String?,
        errorMessage: String?
    ) : this(error, errorTitle, errorMessage, null, null, null)

    constructor(
        error: String?,
        errorTitle: String?,
        errorMessage: String?,
        onDismissListener: OnDismissListener?,
        id: UUID?
    ) : this(error, errorTitle, errorMessage, null, null, onDismissListener, id)

    constructor(
        error: String?,
        errorTitle: String?,
        errorMessage: String?,
        requestCode: Int?,
        onRetryListener: OnRetryListener?
    ) : this(error, errorTitle, errorMessage, requestCode, onRetryListener, null)


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
        setupErrorMessage(error, errorMessage, binding.tvErrorDialogMessage)
        binding.btnErrorDialogClose.setOnClickListener { dismiss() }
    }

    private fun setupErrorTitle() {
        binding.tvErrorDialogTitle.text = errorTitle ?: resources.getString(R.string.error_title_generic)
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

    fun isErrorEqualTo(error: String?): Boolean {
        return this.error == error
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismissErrorDialog(id)
    }

    companion object {
        const val TAG = "errorDialog"
    }

    interface OnRetryListener {
        fun onRetry(requestCode: Int?)
    }

    interface OnDismissListener {
        fun onDismissErrorDialog(id: UUID?)
    }

}