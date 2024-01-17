package com.example.surveyquestions.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WelcomeViewModel : ViewModel() {


    fun signInAsGuest(
        onSignInComplete: () -> Unit,
    ) {
        onSignInComplete()
    }
}

