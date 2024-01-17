package com.example.surveyquestions.survey.question

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.surveyquestions.survey.Question
import com.example.surveyquestions.survey.QuestionWrapper
import kotlin.reflect.KFunction2

@Composable
fun ObjectiveQuestion(
    titleResource: String,
    directionsResource: String,
    possibleAnswers: List<String>,
    selectedAnswer: String?,
    onOptionSelected: KFunction2<Any, String, Unit>,
    question: Question,
    modifier: Modifier = Modifier,
) {
    QuestionWrapper(
        titleResource = titleResource,
        directionsResource = directionsResource,
        modifier = modifier.selectableGroup(),
    ) {
        var currentSelectedAnswer by remember { mutableStateOf(selectedAnswer) }

        possibleAnswers.forEach {
            val selected = it == currentSelectedAnswer
            RadioButtonRow(
                modifier = Modifier.padding(vertical = 8.dp),
                text = it,
                selected = selected,
                onOptionSelected = {
                    currentSelectedAnswer = it
                    onOptionSelected(question, it)
                }
            )
        }
    }
}

@Composable
fun RadioButtonRow(
    text: String,
    selected: Boolean,
    onOptionSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        ),
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .selectable(
                selected,
                onClick = onOptionSelected,
                role = Role.RadioButton
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Box(Modifier.padding(8.dp)) {
                RadioButton(selected, onClick = null)
            }
        }
    }
}
