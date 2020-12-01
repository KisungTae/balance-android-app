package com.beeswork.balance.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.R
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.network.response.QuestionResponse
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.BalanceGameAnswer
import kotlinx.android.synthetic.main.dialog_edit_balance_game.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class EditBalanceGameDialog : DialogFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val balanceRepository: BalanceRepository by instance()

    private lateinit var questions: MutableList<QuestionResponse>
    private var currentQuestionIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
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

        btnEditBalanceGameTopOption.setOnClickListener { answer(BalanceGameAnswer.TOP) }
        btnEditBalanceGameBottomOption.setOnClickListener { answer(BalanceGameAnswer.BOTTOM) }
        btnEditBalanceGameBack.setOnClickListener { previousQuestion() }
        btnEditBalanceGameClose.setOnClickListener { dismiss() }
        btnEditBalanceGameRandomQuestion.setOnClickListener { fetchRandomQuestion() }

        btnEditBalanceGameErrorFetchQuestions.setOnClickListener { fetchQuestions() }
        btnEditBalanceGameErrorSaveAnswers.setOnClickListener { saveAnswers() }
        btnEditBalanceGameErrorClose.setOnClickListener {
            if (btnEditBalanceGameErrorFetchQuestions.visibility == View.VISIBLE)
                dismiss()
            else showLayout(loading = false, error = false)
        }
        fetchQuestions()
    }

    private fun fetchQuestions() {

        showLayout(loading = true, error = false)

        CoroutineScope(Dispatchers.IO).launch {
            val response = balanceRepository.fetchQuestions()
            withContext(Dispatchers.Main) {
                when (response.status) {
                    Resource.Status.SUCCESS -> {
                        if (response.data != null) {
                            setupBalanceGame(response.data)
                            showLayout(loading = false, error = false)
                        }
                    }
                    Resource.Status.LOADING -> {}
                    Resource.Status.EXCEPTION -> {
                        showErrorLayout(
                            response.exceptionMessage,
                            showSaveQuestionsBtn = false,
                            showFetchQuestionsBtn = true
                        )
                    }
                }
            }
        }
    }

    private fun fetchRandomQuestion() {

        showLayout(loading = true, error = false)

        CoroutineScope(Dispatchers.IO).launch {
            val response = balanceRepository.fetchRandomQuestion(questions.map { it.id })
            withContext(Dispatchers.Main) {
                when (response.status) {
                    Resource.Status.SUCCESS -> {
                        if (response.data != null) {
                            questions[currentQuestionIndex] = response.data
                            setupQuestion(response.data)
                            showLayout(loading = false, error = false)
                        }
                    }
                    Resource.Status.LOADING -> {}
                    Resource.Status.EXCEPTION -> showErrorLayout(
                        response.exceptionMessage,
                        showSaveQuestionsBtn = false,
                        showFetchQuestionsBtn = false
                    )
                }
            }
        }
    }

    private fun saveAnswers() {

        showLayout(loading = true, error = false)

        CoroutineScope(Dispatchers.IO).launch {
            val response = balanceRepository.saveAnswers(getAnswers())
            withContext(Dispatchers.Main) {
                when (response.status) {
                    Resource.Status.SUCCESS -> {
                        showLayout(loading = false, error = false)
                        dismiss()
                    }
                    Resource.Status.LOADING -> {}
                    Resource.Status.EXCEPTION -> showErrorLayout(
                        response.exceptionMessage,
                        showSaveQuestionsBtn = true,
                        showFetchQuestionsBtn = false
                    )
                }
            }
        }
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
            saveAnswers()
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

    private fun showErrorLayout(
        exceptionMessage: String?,
        showSaveQuestionsBtn: Boolean,
        showFetchQuestionsBtn: Boolean
    ) {
        tvEditBalanceGameErrorMessage.text = exceptionMessage
        btnEditBalanceGameErrorSaveAnswers.visibility = if (showSaveQuestionsBtn) View.VISIBLE else View.GONE
        btnEditBalanceGameErrorFetchQuestions.visibility = if (showFetchQuestionsBtn) View.VISIBLE else View.GONE
        showLayout(loading = false, error = true)
    }

    private fun showLayout(loading: Boolean, error: Boolean) {

        llEditBalanceGameLoading.visibility = if (loading) View.VISIBLE else View.GONE
        llEditBalanceGameError.visibility = if (error) View.VISIBLE else View.GONE
    }

    companion object {
        const val TAG = "editBalanceGameDialog"
    }


}