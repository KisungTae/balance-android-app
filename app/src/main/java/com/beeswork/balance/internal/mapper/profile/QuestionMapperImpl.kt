package com.beeswork.balance.internal.mapper.profile

import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.ui.profilefragment.balancegame.QuestionDomain

class QuestionMapperImpl: QuestionMapper {

    override fun toQuestionDomain(questionDTO: QuestionDTO): QuestionDomain {
        return QuestionDomain(
            questionDTO.id,
            questionDTO.description,
            questionDTO.topOption,
            questionDTO.bottomOption,
            questionDTO.answer
        )
    }
}