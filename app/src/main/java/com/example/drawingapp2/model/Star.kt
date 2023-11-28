package com.example.drawingapp2.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class Star(
    val center: Offset,
    var radius: Float,
    val numPoints: Int,
    val color: Color
)
