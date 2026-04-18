package com.gpproject.adhera.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier

fun Modifier.adheraScreenPadding() = this
    .statusBarsPadding()
    .navigationBarsPadding()