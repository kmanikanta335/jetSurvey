package com.example.surveyquestions.welcome

import androidx.compose.runtime.Composable

@Composable
fun WelcomeRoute(
    onSignInAsGuest: () -> Unit,
) {
    val welcomeViewModel = WelcomeViewModel()

    WelcomeScreen(
        onSignInAsGuest = {
            welcomeViewModel.signInAsGuest(onSignInAsGuest)
        }
    )

}
