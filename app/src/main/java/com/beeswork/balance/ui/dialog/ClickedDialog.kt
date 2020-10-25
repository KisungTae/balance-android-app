package com.beeswork.balance.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import kotlinx.android.synthetic.main.dialog_clicked.*
import kotlinx.android.synthetic.main.dialog_match.*

class ClickedDialog(
    private val clickerPhotoKey: String?,
    private val message: String?
): DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
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
        btnClickedDialogClose.setOnClickListener { dismiss() }
        btnClickedDialogGoToClicked.setOnClickListener {
            dismiss()
        }
    }

}