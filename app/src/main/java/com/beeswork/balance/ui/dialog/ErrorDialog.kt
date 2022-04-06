package com.beeswork.balance.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogErrorBinding
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.common.BaseDialog
import java.util.*

class ErrorDialog(
    private val title: String?,
    private val message: String?,
    private val retryBtnTitle: String?,
    private val requestCode: Int?,
    private val retryListener: RetryListener?,
    private val dismissListener: DismissListener?,
    private val id: UUID? = null
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

    override fun getTheme(): Int {
        return R.style.RoundedCornersDialog
    }

    private fun bindUI() {
        setupRetryListener()
        binding.tvErrorDialogTitle.text = title ?: resources.getString(R.string.error_title_generic)
        binding.tvErrorDialogMessage.text = message ?: ""
        if (retryBtnTitle != null) {
            binding.btnErrorDialogRetry.text = retryBtnTitle
        }
        binding.btnErrorDialogClose.setOnClickListener { dismiss() }
    }

    private fun setupRetryListener() {
        retryListener?.let {
            binding.btnErrorDialogRetry.setOnClickListener {
                dismiss()
                retryListener.onRetry(requestCode)
            }
        } ?: kotlin.run {
            binding.btnErrorDialogRetry.visibility = View.GONE
        }
    }

    private fun getErrorMessage(error: String?, errorMessage: String?): String {
        return errorMessage ?: kotlin.run {
            safeLet(error, context) { e, c ->
                val resourceId = resources.getIdentifier(e, "string", c.packageName)
                if (resourceId > 0) return@safeLet getString(resourceId)
                else return@safeLet getString(R.string.error_message_generic)
            } ?: getString(R.string.error_message_generic)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDismissErrorDialog(id)
    }

    companion object {
        const val TAG = "errorDialog"

        fun show(title: String, message: String?, fragmentManager: FragmentManager) {
            ErrorDialog(title, message, null, null, null, null).show(fragmentManager, TAG)
        }

        fun show(title: String, message: String?, dismissListener: DismissListener, fragmentManager: FragmentManager) {
            ErrorDialog(title, message, null, null, null, dismissListener).show(fragmentManager, TAG)
        }

        fun show(title: String, message: String?, requestCode: Int, retryListener: RetryListener, fragmentManager: FragmentManager) {
            ErrorDialog(title, message, null, requestCode, retryListener, null).show(fragmentManager, TAG)
        }

        fun show(
            title: String,
            message: String?,
            retryBtnTitle: String,
            requestCode: Int,
            retryListener: RetryListener,
            fragmentManager: FragmentManager
        ) {
            ErrorDialog(title, message, retryBtnTitle, requestCode, retryListener, null).show(fragmentManager, TAG)
        }
    }

    interface RetryListener {
        fun onRetry(requestCode: Int?)
    }

    interface DismissListener {
        fun onDismissErrorDialog(id: UUID?)
    }

}