package com.gpproject.adhera.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gpproject.adhera.ui.theme.DividerColor
import com.gpproject.adhera.ui.theme.TextSecondary

@Composable
fun DividerText() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = DividerColor
        )

        Text(
            text = " OR ",
            color = TextSecondary
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = DividerColor
        )
    }
}