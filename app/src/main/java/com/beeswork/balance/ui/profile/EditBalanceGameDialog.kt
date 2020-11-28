package com.beeswork.balance.ui.profile

import android.graphics.Color
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
import com.beeswork.balance.internal.observeOnce
import kotlinx.android.synthetic.main.dialog_edit_balance_game.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class EditBalanceGameDialog : DialogFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: EditBalanceGameDialogViewModelFactory by instance()
    private lateinit var viewModel: EditBalanceGameDialogViewModel

    private lateinit var questions: MutableList<QuestionResponse>
    private var currentQuestionIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(EditBalanceGameDialogViewModel::class.java)
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
        setupSaveAnswersObserver()
        setupFetchRandomQuestionObserver()

        btnEditBalanceGameTopOption.setOnClickListener { answer(BalanceGameAnswer.TOP) }
        btnEditBalanceGameBottomOption.setOnClickListener { answer(BalanceGameAnswer.BOTTOM) }
        btnEditBalanceGameBack.setOnClickListener { previousQuestion() }
        btnEditBalanceGameClose.setOnClickListener { dismiss() }
        btnEditBalanceGameRandomQuestion.setOnClickListener { fetchRandomQuestion() }

        btnEditBalanceGameLoadQuestions.setOnClickListener { viewModel.fetchQuestions() }
        btnEditBalanceGameSaveAnswers.setOnClickListener { viewModel.saveAnswers(getAnswers()) }
        btnEditBalanceGameErrorClose.setOnClickListener {
            showLayout(loading = false, error = false)
            if (questions.size <= 0) dismiss()
        }

        viewModel.fetchQuestions()
    }

    private fun showLayout(loading: Boolean, error: Boolean) {

        llEditBalanceGameLoading.visibility = if (loading) View.VISIBLE else View.GONE
        llEditBalanceGameError.visibility = if (error) View.VISIBLE else View.GONE
    }


    private fun fetchRandomQuestion() {

        val questionIds = ArrayList<Int>(questions.size)
        for (question in questions)
            questionIds.add(question.id)
        showLayout(loading = true, error = false)
        viewModel.fetchRandomQuestion(questionIds)
    }

    private fun setupFetchRandomQuestionObserver() {

        viewModel.fetchRandomQuestion.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if (it.data != null) {
                        println("random question: " + it.data)
                        questions[currentQuestionIndex] = it.data
                        setupQuestion(it.data)
                        showLayout(loading = false, error = false)
                    }
                }
                Resource.Status.LOADING -> {
                    showLayout(loading = true, error = false)
                }
                Resource.Status.EXCEPTION -> showErrorLayout(
                    it.exceptionMessage,
                    showSaveQuestionsBtn = false,
                    showLoadQuestionsBtn = false
                )
            }
        })
    }


    private fun showErrorLayout(
        exceptionMessage: String?,
        showSaveQuestionsBtn: Boolean,
        showLoadQuestionsBtn: Boolean
    ) {
        tvEditBalanceGameErrorMessage.text = exceptionMessage
        btnEditBalanceGameSaveAnswers.visibility =
            if (showSaveQuestionsBtn) View.VISIBLE else View.GONE
        btnEditBalanceGameLoadQuestions.visibility =
            if (showLoadQuestionsBtn) View.VISIBLE else View.GONE
        showLayout(loading = false, error = true)
    }

    private fun setupSaveAnswersObserver() {
        viewModel.saveAnswers.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    println("setupSaveAnswersObserver susccess")
                    showLayout(loading = false, error = false)
                    dismiss()
                }
                Resource.Status.LOADING -> showLayout(loading = true, error = false)
                Resource.Status.EXCEPTION -> showErrorLayout(
                    it.exceptionMessage,
                    showSaveQuestionsBtn = true,
                    showLoadQuestionsBtn = false
                )
            }
        })
    }

    private fun setupQuestionsObserver() {
        viewModel.questions.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if (it.data != null) {
                        setupBalanceGame(it.data)
                        showLayout(loading = false, error = false)
                    }
                }
                Resource.Status.LOADING -> showLayout(loading = true, error = false)
                Resource.Status.EXCEPTION -> {
                    showErrorLayout(
                        it.exceptionMessage,
                        showSaveQuestionsBtn = false,
                        showLoadQuestionsBtn = true
                    )
                }
            }
        })
    }

    private fun answer(answer: Boolean) {

        if (currentQuestionIndex == questions.size)
            currentQuestionIndex--

        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size) {
            questions[currentQuestionIndex].answer = answer
            highlightAnswer(answer)
        }
        nextQuestion()
    }

    private fun setupBalanceGame(questionResponses: List<QuestionResponse>) {
        questions = questionResponses.toMutableList()
        nextQuestion()
        showLayout(loading = false, error = false)
    }

    private fun nextQuestion() {

        if (currentQuestionIndex < questions.size)
            currentQuestionIndex++

        btnEditBalanceGameBack.isEnabled = currentQuestionIndex != 0

        if (currentQuestionIndex == questions.size)
            viewModel.saveAnswers(getAnswers())
        else setupQuestion(questions[currentQuestionIndex])
    }

    private fun getAnswers(): Map<Int, Boolean> {
        val answers: MutableMap<Int, Boolean> = mutableMapOf()
        for (question in questions)
            answers[question.id] = question.answer!!
        return answers
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

        if (question.answer != null) highlightAnswer(question.answer)
        else resetAnswer()
    }

    private fun resetAnswer() {
        btnEditBalanceGameTopOption.setBackgroundColor(Color.GRAY)
        btnEditBalanceGameBottomOption.setBackgroundColor(Color.GRAY)
    }

    private fun highlightAnswer(answer: Boolean?) {
        resetAnswer()
        if (answer == BalanceGameAnswer.TOP)
            btnEditBalanceGameTopOption.setBackgroundColor(Color.GREEN)
        else btnEditBalanceGameBottomOption.setBackgroundColor(Color.GREEN)
    }

    companion object {
        const val TAG = "editBalanceGameDialog"
    }


}