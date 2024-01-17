package com.example.surveyquestions.survey

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.surveyquestions.R
import com.example.surveyquestions.survey.question.NumberQuestion
import com.example.surveyquestions.survey.question.ObjectiveQuestion
import com.example.surveyquestions.survey.question.SingleChoiceQuestion
import kotlin.reflect.KFunction2

@Composable
fun SuperheroQuestion(
    selectedAnswer: String?,
    onOptionSelected: KFunction2<Question, String, Unit>,
    question: Question,
    possibleAnswers : List<String>,
    modifier: Modifier = Modifier
) {
    val viewModel: SurveyViewModel = viewModel(
        factory = SurveyViewModelFactory(
            PhotoUriManager(LocalContext.current),
            LocalContext.current
        )
    )
    var currentSelectedAnswer by remember { mutableStateOf(selectedAnswer) }
    val translatedQuestions by viewModel.translatedQuestions.collectAsState()
    val translatedOptions by viewModel.translatedOptions.collectAsState()
    val translatedSubQuestion by viewModel.translatedSubQuestions.collectAsState()
    val translatedSubQuestionOptions by viewModel.translatedSubOptions.collectAsState()
    // val translatedQuestion = translatedQuestions[question.id] ?: ""
    val options = translatedOptions[question.id]
    val subQuestion = translatedSubQuestion[question.sub1?.id]
    val subOptions = translatedSubQuestionOptions[question.sub1?.id]

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp)
    ) {
        // SingleChoiceQuestion in a Row

        SingleChoiceQuestion(
            titleResource = question.ques,
            directionsResource = "",
            selectedAnswer = selectedAnswer,
            possibleAnswers=possibleAnswers,
            onOptionSelected = {
                onOptionSelected(question,it)
                currentSelectedAnswer = it
            },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)  // Adjust spacing between SingleChoiceQuestion and subquestions
        )
        if (currentSelectedAnswer == "YES") {
            when (question.sub1?.type) {
                "obj" -> question.sub1.options?.let {
                    question.sub1.ques?.let { it1 ->
                        ObjectiveQuestion(
                            titleResource = it1,
                            directionsResource = "",
                            possibleAnswers = it,
                            selectedAnswer = viewModel.getResponse(question.sub1.id),
                            onOptionSelected = viewModel::handleQuestionResponse as KFunction2<Any, String, Unit>,
                            question = question,
                            modifier = modifier
                                .weight(1f)
                                .padding(end = 8.dp)  // Adjust spacing between subquestions
                        )
                    }
                }

//                "bool" ->
//                    question.sub1.options?.let {
//                        SuperheroQuestion(
//                            selectedAnswer = viewModel.getResponse(question.sub1.id),
//                            onOptionSelected = viewModel::handleQuestionResponse,
//                            question = question,
//                            possibleAnswers= it,
//                            modifier = Modifier.weight(1f)
//                        )
//                    }

                "num" ->
                    question.sub1.ques?.let {
                        NumberQuestion(
                            titleResource = it,
                            directionsResource = "",
                            initialValue = "0",
                            onValueChange = viewModel::handleQuestionResponse as KFunction2<Any, String, Unit>,
                            validation = { input: Int -> input >= 0 },
                            question = question,
                            errorMessageResourceId = R.string.app_name,
                            modifier = modifier
                                .weight(1f)
                                .padding(end = 8.dp)  // Adjust spacing between subquestions
                        )
                    }
            }
        }
    }
}
