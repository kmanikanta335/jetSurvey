package com.example.surveyquestions.survey.question

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.surveyquestions.survey.Question
import com.example.surveyquestions.survey.QuestionWrapper
import kotlin.reflect.KFunction2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberQuestion(
    titleResource: String,
    directionsResource: String,
    initialValue: String,
    onValueChange: KFunction2<Any, String, Unit>,
    validation: (Int) -> Boolean,
    errorMessageResourceId: Int,
    question: Question,
    modifier: Modifier = Modifier,
) {
    QuestionWrapper(
        modifier = modifier,
        titleResource = titleResource,
        directionsResource = directionsResource,
    ) {
        var inputValue by remember { mutableStateOf(initialValue) }
        var isError by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = inputValue,
            onValueChange = {
                inputValue = it
                isError = !validation(it.toIntOrNull() ?: 0)
            },
            label = { Text(text = "") },
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (!isError) {
                        onValueChange(question,inputValue)
                    }
                }
            ),
        )

        if (isError) {
            Text(
                text = stringResource(id = errorMessageResourceId),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
