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
import com.beeswork.balance.internal.constant.BalanceGameAnswer
import kotlinx.android.synthetic.main.dialog_edit_balance_game.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance



class EditBalanceGameDialog: DialogFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: EditBalanceGameDialogViewModelFactory by instance()
    private lateinit var viewModel: EditBalanceGameDialogViewModel

    private lateinit var questions: List<QuestionResponse>
    private var currentQuestionIndex = -1
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

        setupQuestionsObserver()
        btnEditBalanceGameTopOption.setOnClickListener { answer(BalanceGameAnswer.TOP) }
        btnEditBalanceGameBottomOption.setOnClickListener { answer(BalanceGameAnswer.BOTTOM) }
        btnEditBalanceGameBack.setOnClickListener { previousQuestion() }
        btnEditBalanceGameClose.setOnClickListener { dismiss() }
        viewModel.fetchQuestions()
    }

    private fun setupQuestionsObserver() {
        viewModel.questions.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> setupBalanceGame(it.data!!)
                Resource.Status.LOADING -> {

                }
                Resource.Status.EXCEPTION -> {

                }
            }
        })
    }

    private fun answer(answer: Boolean) {

        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size)
            answers[questions[currentQuestionIndex].id] = answer
        nextQuestion()
    }

    private fun setupBalanceGame(questionResponses: List<QuestionResponse>) {
        questions = questionResponses
        for (questionResponse in questionResponses) {
            if (questionResponse.answer != null)
                answers[questionResponse.id] = questionResponse.answer
        }
        nextQuestion()
    }

    private fun nextQuestion() {

        if (currentQuestionIndex < questions.size)
            currentQuestionIndex++

        btnEditBalanceGameBack.isEnabled = currentQuestionIndex != 0

        if (currentQuestionIndex == questions.size) {
            viewModel.saveAnswers()
            println("save answers ===================")
            println(answers)
            //show loading page
        } else {
            setupQuestion(questions[currentQuestionIndex])
        }
    }

    private fun previousQuestion() {
        if (currentQuestionIndex >= questions.size)
            currentQuestionIndex = (questions.size - 2)
        else if (currentQuestionIndex > 0)
            currentQuestionIndex--

        btnEditBalanceGameBack.isEnabled = currentQuestionIndex != 0
        setupQuestion(questions[currentQuestionIndex])
    }

    private fun setupQuestion(question: QuestionResponse) {
        tvEditBalanceGameDescription.text = question.description
        btnEditBalanceGameTopOption.text = question.topOption
        btnEditBalanceGameBottomOption.text = question.bottomOption
    }

    companion object {
        const val TAG = "editBalanceGameDialog"
    }




















}