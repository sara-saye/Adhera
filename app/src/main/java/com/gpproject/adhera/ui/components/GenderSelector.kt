package com.gpproject.adhera.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gpproject.adhera.data.model.Gender
import com.gpproject.adhera.ui.theme.TextPrimary

@Composable
fun GenderSelector(

    selectedGender: Gender?,

    onGenderSelected: (Gender) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            RadioButton(
                selected = selectedGender == Gender.MALE,
                onClick = {
                    onGenderSelected(Gender.MALE)
                }
            )

            Text(
                text = "Male",
                color = TextPrimary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            RadioButton(
                selected = selectedGender == Gender.FEMALE,
                onClick = {
                    onGenderSelected(Gender.FEMALE)
                }
            )

            Text(
                text = "Female",
                color = TextPrimary
            )
        }
    }
}