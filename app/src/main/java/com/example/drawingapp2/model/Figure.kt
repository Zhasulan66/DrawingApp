package com.example.drawingapp2.model

import android.graphics.PointF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

sealed class Figure {
    abstract var color: Color
    abstract var isSelected: Boolean
    // other common properties or methods
}

data class Circle(
    val center: PointF,
    var radius: Float,
    override  var color: Color,
    override var isSelected: Boolean = false) : Figure()

data class Cylinder(
    val center: Offset,
    var ovalRadiusX: Float,
    var ovalRadiusY: Float,
    override var color: Color,
    override var isSelected: Boolean = false) : Figure()

data class HalfCircle(
    val centerX: Float,
    var centerY: Float,
    var radius: Float,
    override  var color: Color,
    var isTop: Boolean,
    override var isSelected: Boolean = false) : Figure()

data class Line(
    val start: PointF,
    val end: PointF,
    var type: LineType = LineType.REGULAR,
    override  var color: Color,
    override var isSelected: Boolean = false) : Figure()

data class Octagon(
    val center: Offset,
    var radius: Float,
    override  var color: Color,
    override var isSelected: Boolean = false) : Figure()

data class Parallelogram(
    var points: MutableList<Offset>,
    override  var color: Color,
    override var isSelected: Boolean = false) : Figure()

data class Rectangle(
    val leftTop: PointF,
    var width: Float,
    var height: Float,
    override  var color: Color,
    override var isSelected: Boolean = false) : Figure()

data class RoundRectangle(
    val leftTop: PointF,
    var width: Float,
    var height: Float,
    override  var color: Color,
    override var isSelected: Boolean = false) : Figure()

data class Star(
    val center: Offset,
    var radius: Float,
    val numPoints: Int,
    override  var color: Color,
    override var isSelected: Boolean = false) : Figure()

data class Trapezoid(
    val points: MutableList<Offset>,
    override  var color: Color,
    override var isSelected: Boolean = false) : Figure()

data class Triangle(
    var point1: Offset,
    var point2: Offset,
    var point3: Offset,
    override  var color: Color,
    override var isSelected: Boolean = false) : Figure()