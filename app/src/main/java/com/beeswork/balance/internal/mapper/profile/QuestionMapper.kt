package com.beeswork.balance.internal.mapper.profile

import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.ui.profile.QuestionDomain

interface QuestionMapper {
    fun toQuestionDomain(questionDTO: QuestionDTO): QuestionDomain
}