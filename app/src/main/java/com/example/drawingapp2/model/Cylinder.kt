package com.example.drawingapp2.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class Cylinder(
    val center: Offset,
    var ovalRadiusX: Float,
    var ovalRadiusY: Float,
    val color: Color
)
