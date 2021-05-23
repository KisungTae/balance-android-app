package com.beeswork.balance.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogClickedBinding

class ClickedDialog(
    private val clickPhotoKey: String?
): DialogFragment() {

    private lateinit var binding: DialogClickedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
        binding = DialogClickedBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_clicked, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnClickedDialogClose.setOnClickListener { dismiss() }
        binding.btnClickedDialogGoToClicked.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "clickedDialog"
    }

}