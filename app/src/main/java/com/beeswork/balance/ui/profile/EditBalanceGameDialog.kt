package com.beeswork.balance.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.QuestionResponse
import com.beeswork.balance.internal.Resource
import kotlinx.android.synthetic.main.dialog_edit_balance_game.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance



class EditBalanceGameDialog: DialogFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: EditBalanceGameDialogViewModelFactory by instance()
    private lateinit var viewModel: EditBalanceGameDialogViewModel

    private lateinit var questions: List<QuestionResponse>
    private val currentQuestionIndex = -1
    private val answers: MutableMap<Int, Boolean> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
        viewModel = ViewModelProvider(this, viewModelFactory).get(EditBalanceGameDialogViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_balance_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        btnEditBalanceGameClose.setOnClickListener { dismiss() }
        setupBalanceGameQuestionsObserver()
    }

    private fun setupBalanceGameQuestionsObserver() {
        viewModel.questions.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> setupBalanceGame(it.data!!)
                Resource.Status.LOADING -> {

                }
                Resource.Status.EXCEPTION -> {

                }
            }
        })
        viewModel.fetchQuestions()
    }

    private fun setupBalanceGame(questionResponses: List<QuestionResponse>) {
        questions = questionResponses
        for (questionResponse in questionResponses) {
            if (questionResponse.answer != null)
                answers[questionResponse.id] = questionResponse.answer
        }
    }

    private fun nextQuestion() {

    }

    companion object {
        const val TAG = "editBalanceGameDialog"
    }




















}