package com.example.surveyquestions.survey.question

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.surveyquestions.R
import com.example.surveyquestions.survey.QuestionWrapper

@Composable
fun SingleChoiceQuestion(
    titleResource: String,
    directionsResource: String,
    selectedAnswer: String?,
    possibleAnswers: List<String>,
    onOptionSelected: (String) -> Unit,
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
            RadioButtonWithImageRow(
                modifier = Modifier.padding(vertical = 8.dp),
                text = it,
                imageResourceId = R.drawable.green,
                imageId = R.drawable.worng,
                selected = selected,
                onOptionSelected = { currentSelectedAnswer = it
                    onOptionSelected(it) }
            )
        }
    }
}

@Composable
fun RadioButtonWithImageRow(
    text: String,
    @DrawableRes imageResourceId: Int,
    @DrawableRes imageId: Int,
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
            if(text=="YES"){
                Image(
                    painter = painterResource(id = imageResourceId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .padding(start = 0.dp, end = 8.dp)
                )
            }else{
                Image(
                    painter = painterResource(id = imageId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .padding(start = 0.dp, end = 8.dp)
                )
            }
            Spacer(Modifier.width(8.dp))

            Text(text, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Box(Modifier.padding(8.dp)) {
                RadioButton(selected, onClick = null)
            }
        }
    }
}
