package com.example.surveyquestions.survey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.surveyquestions.ui.theme.slightlyDeemphasizedAlpha
import com.example.surveyquestions.ui.theme.stronglyDeemphasizedAlpha

@Composable
fun QuestionWrapper(
    titleResource: String,
    modifier: Modifier = Modifier,
    directionsResource: String? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(32.dp))
        QuestionTitle(titleResource)
        directionsResource?.let {
            Spacer(Modifier.height(18.dp))
            QuestionDirections(it)
        }
        Spacer(Modifier.height(18.dp))

        content()
    }
}

@Composable
private fun QuestionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha),
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.inverseOnSurface,
                shape = MaterialTheme.shapes.small
            )
            .padding(vertical = 24.dp, horizontal = 16.dp)
    )
}

@Composable
private fun QuestionDirections(
    directionsResource: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = directionsResource,
        color = MaterialTheme.colorScheme.onSurface
            .copy(alpha = stronglyDeemphasizedAlpha),
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )
}
