package com.example.surveyquestions

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.surveyquestions.Destinations.SURVEY_RESULTS_ROUTE
import com.example.surveyquestions.Destinations.SURVEY_ROUTE
import com.example.surveyquestions.Destinations.WELCOME_ROUTE
import com.example.surveyquestions.survey.SurveyRoute
import com.example.surveyquestions.welcome.WelcomeRoute

object Destinations {
    const val WELCOME_ROUTE = "welcome"
    const val SIGN_UP_ROUTE = "signup/{email}"
    const val SIGN_IN_ROUTE = "signin/{email}"
    const val SURVEY_ROUTE = "survey"
    const val SURVEY_RESULTS_ROUTE = "surveyresults"
}

@Composable
fun JetsurveyNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = WELCOME_ROUTE,
    ) {
        composable(WELCOME_ROUTE) {
            WelcomeRoute(
                onSignInAsGuest = {
                    navController.navigate(SURVEY_ROUTE)
                }
            )
        }

        composable(SURVEY_ROUTE) {
            SurveyRoute(
                onSurveyComplete = {
                    navController.navigate(SURVEY_RESULTS_ROUTE)
                },
                onNavUp = navController::navigateUp,
            )
        }
    }
}
