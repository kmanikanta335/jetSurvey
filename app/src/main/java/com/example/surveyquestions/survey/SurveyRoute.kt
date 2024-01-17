package com.example.surveyquestions.survey

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.surveyquestions.R
import com.example.surveyquestions.survey.question.NumberQuestion
import com.example.surveyquestions.survey.question.ObjectiveQuestion
import kotlin.reflect.KFunction2

private const val CONTENT_ANIMATION_DURATION = 300

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SurveyRoute(
    onSurveyComplete: () -> Unit,
    onNavUp: () -> Unit,
) {
    SurveyViewModel(photoUriManager = PhotoUriManager(LocalContext.current) ,context = LocalContext.current)
    val viewModel: SurveyViewModel = viewModel(
        factory = SurveyViewModelFactory(PhotoUriManager(LocalContext.current), LocalContext.current)
    )
    val surveyScreenData = viewModel.surveyScreenData ?: return

    BackHandler {
        if (!viewModel.onBackPressed()) {
            onNavUp()
        }
    }

    SurveyQuestionsScreen(
        surveyScreenData = surveyScreenData,
        isNextEnabled = viewModel.isNextEnabled,
        onClosePressed = {
            onNavUp()
        },
        onPreviousPressed = { viewModel.onPreviousPressed() },
        onNextPressed = { viewModel.onNextPressed() },
        onDonePressed = { viewModel.onDonePressed(onSurveyComplete) }
    ) { paddingValues ->

        val modifier = Modifier.padding(paddingValues)
        val translatedQuestions by viewModel.translatedQuestions.collectAsState()
        val translatedOptions by viewModel.translatedOptions.collectAsState()
        val translatedSubQuestion by viewModel.translatedSubQuestions.collectAsState()
        val translatedSubQuestionOptions by viewModel.translatedSubOptions.collectAsState()

        AnimatedContent(
            targetState = surveyScreenData,
            transitionSpec = {
                val animationSpec: TweenSpec<IntOffset> = tween(CONTENT_ANIMATION_DURATION)

                val direction = getTransitionDirection(
                    initialIndex = initialState.questionIndex,
                    targetIndex = targetState.questionIndex,
                )

                slideIntoContainer(
                    towards = direction,
                    animationSpec = animationSpec,
                ) togetherWith slideOutOfContainer(
                    towards = direction,
                    animationSpec = animationSpec
                )
            },
            label = "surveyScreenDataAnimation"
        ) { targetState ->

            when (targetState.Question.type) {
//                SurveyQuestion.FREE_TIME -> {
//                    FreeTimeQuestion(
//                        selectedAnswers = viewModel.freeTimeResponse,
//                        onOptionSelected = viewModel::onFreeTimeResponse,
//                        modifier = modifier,
//                    )
//                }
//
//                SurveyQuestion.SUPERHERO -> SuperheroQuestion(
//                    selectedAnswer = viewModel.superheroResponse,
//                    onOptionSelected = viewModel::onSuperheroResponse,
//                    modifier = modifier,
//                )
//
//                SurveyQuestion.LAST_TAKEAWAY -> {
//                    val supportFragmentManager =
//                        LocalContext.current.findActivity().supportFragmentManager
//                    TakeawayQuestion(
//                        dateInMillis = viewModel.takeawayResponse,
//                        onClick = {
//                            showTakeawayDatePicker(
//                                date = viewModel.takeawayResponse,
//                                supportFragmentManager = supportFragmentManager,
//                                onDateSelected = viewModel::onTakeawayResponse
//                            )
//                        },
//                        modifier = modifier,
//                    )
//                }
//
//                SurveyQuestion.FEELING_ABOUT_SELFIES ->
//                    FeelingAboutSelfiesQuestion(
//                        value = viewModel.feelingAboutSelfiesResponse,
//                        onValueChange = viewModel::onFeelingAboutSelfiesResponse,
//                        modifier = modifier,
//                    )
//
//                SurveyQuestion.TAKE_SELFIE -> TakeSelfieQuestion(
//                    imageUri = viewModel.selfieUri,
//                    getNewImageUri = viewModel::getNewSelfieUri,
//                    onPhotoTaken = viewModel::onSelfieResponse,
//                    modifier = modifier,
//                )
                "obj"->
                    targetState.Question.options?.let { it1 ->
                        ObjectiveQuestion(
                            titleResource = targetState.Question.ques,
                            directionsResource = "",
                            possibleAnswers = it1,
                            selectedAnswer = viewModel.getResponse(targetState.Question.id),
                            onOptionSelected = viewModel::handleQuestionResponse as KFunction2<Any, String, Unit>,
                            modifier = modifier,
                            question = targetState.Question
                        )
                    }


                "bool" ->
                    targetState.Question.options?.let {
                        SuperheroQuestion(
                            selectedAnswer =viewModel.getResponse(targetState.Question.id),
                            onOptionSelected = viewModel::handleQuestionResponse,
                            question = targetState.Question,
                            possibleAnswers = it,
                            modifier = modifier
                        )
                    }

                "num" ->
                    targetState.Question.ques?.let {
                        NumberQuestion(
                            titleResource = it,
                            directionsResource = "",
                            initialValue = "0",
                            onValueChange = viewModel::handleQuestionResponse as KFunction2<Any, String, Unit>,
                            validation = { input: Int -> input >= 0 },
                            modifier = modifier,
                            question = targetState.Question,
                            errorMessageResourceId = R.string.app_name
                        )
                    }
            }
        }
    }
}

private fun getTransitionDirection(
    initialIndex: Int,
    targetIndex: Int
): AnimatedContentTransitionScope.SlideDirection {
    return if (targetIndex > initialIndex) {
        // Going forwards in the survey: Set the initial offset to start
        // at the size of the content so it slides in from right to left, and
        // slides out from the left of the screen to -fullWidth
        AnimatedContentTransitionScope.SlideDirection.Left
    } else {
        // Going back to the previous question in the set, we do the same
        // transition as above, but with different offsets - the inverse of
        // above, negative fullWidth to enter, and fullWidth to exit.
        AnimatedContentTransitionScope.SlideDirection.Right
    }
}