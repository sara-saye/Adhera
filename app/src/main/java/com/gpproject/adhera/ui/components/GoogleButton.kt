package com.gpproject.adhera.ui.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gpproject.adhera.R
import com.gpproject.adhera.ui.theme.*

@Composable
fun GoogleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    OutlinedButton(
        onClick = onClick,

        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),

        shape = RoundedCornerShape(16.dp),

        border = BorderStroke(
            1.dp,
            DividerColor
        ),

        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CardBackground
        )
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.google_icon),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Continue with Google",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}