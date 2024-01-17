package com.example.surveyquestions.welcome

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.surveyquestions.R
import com.example.surveyquestions.survey.PhotoUriManager
import com.example.surveyquestions.survey.Question
import com.example.surveyquestions.survey.SurveyViewModel
import com.example.surveyquestions.survey.SurveyViewModelFactory
import com.example.surveyquestions.ui.theme.JetsurveyTheme

@Composable
fun WelcomeScreen(
    onSignInAsGuest: () -> Unit,
) {

    SignInCreateAccount(
        onSignInAsGuest = onSignInAsGuest,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    )

}

@Composable
private fun SignInCreateAccount(
    onSignInAsGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    OrSignInAsGuest(
        onSignInAsGuest = onSignInAsGuest,
        modifier = Modifier.fillMaxWidth()
    )

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrSignInAsGuest(
    onSignInAsGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SurveyViewModel = viewModel(
        factory = SurveyViewModelFactory(
            PhotoUriManager(LocalContext.current),
            LocalContext.current
        )
    )
    var questions:List<Question> = viewModel.questions
    val context = LocalContext.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Text(
//            text = stringResource(id = R.string.or),
//            style = MaterialTheme.typography.titleSmall,
//            color = MaterialTheme.colorScheme.onSurface.copy(alpha = stronglyDeemphasizedAlpha),
//            modifier = Modifier.paddingFromBaseline(top = 25.dp)
//        )
        OutlinedButton(
            onClick = onSignInAsGuest,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 24.dp),
        ) {
            Text(text = "welcome")
        }
        var mExpanded by remember { mutableStateOf(false) }
        // Create a list of cities
        val mCities = listOf<Pair<String,String>>(Pair("ENGLISH","en"), Pair("KANNADA","kn"), Pair("HINDI","hi"), Pair("TAMIL","ta"),Pair("TELUGU","te"), Pair("BENGALI","bn"), Pair("GUJURATHI","gu"))

        // Create a string value to store the selected city
        var mSelectedText by remember { mutableStateOf("") }
        val languageCode by viewModel.languageCode.observeAsState("en")

        var mTextFieldSize by remember { mutableStateOf(Size.Zero) }

        // Up Icon when expanded and down icon when collapsed
        val icon = if (mExpanded)
            Icons.Filled.KeyboardArrowUp
        else
            Icons.Filled.KeyboardArrowDown

        Column(Modifier.padding(20.dp)) {

            // Create an Outlined Text Field
            // with icon and not expanded
            OutlinedTextField(
                value = mSelectedText,
                onValueChange = { mSelectedText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        mTextFieldSize = coordinates.size.toSize()
                    },
                label = { Text("Select Language") },
                trailingIcon = {
                    Icon(icon,"contentDescription",
                        Modifier.clickable { mExpanded = !mExpanded })
                }
            )

            // Create a drop-down menu with list of cities,
            // when clicked, set the Text Field text as the city selected
            DropdownMenu(
                expanded = mExpanded,
                onDismissRequest = { mExpanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current){mTextFieldSize.width.toDp()})
            ) {
                mCities.forEach { label ->
                    DropdownMenuItem(
                        text={
                            Text(label.first)
                        },
                        onClick = {
                            mSelectedText = label.first
                            viewModel.setLanguageCode(label.second)

                            for (question in questions) {
                                viewModel.translateQuestion(question, "en", label.second, context)
                            }


                            mExpanded = false
                        })
                }
            }
        }
    }
}


@Preview(name = "Welcome light theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Welcome dark theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun WelcomeScreenPreview() {
    JetsurveyTheme {
        WelcomeScreen(
            onSignInAsGuest = {}
        )
    }
}