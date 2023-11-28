package com.example.drawingapp2.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class Octagon(
    val center: Offset,
    var radius: Float,
    val color: Color
)
