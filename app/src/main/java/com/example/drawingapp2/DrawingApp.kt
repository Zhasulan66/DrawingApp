package com.example.drawingapp2

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawingapp2.gesture.MotionEvent
import com.example.drawingapp2.gesture.dragMotionEvent
import com.example.drawingapp2.menu.DrawingPropertiesMenu
import com.example.drawingapp2.model.*
import com.example.drawingapp2.ui.theme.Blue400
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@SuppressLint("UnrememberedMutableState")
@Composable
fun DrawingApp() {

    val context = LocalContext.current

    /**
     * Paths that are added, this is required to have paths with different options and paths
     *  ith erase to keep over each other
     */
    var paths = remember { mutableStateListOf<Pair<Path, PathProperties>>() }

    /**
     * Paths that are undone via button. These paths are restored if user pushes
     * redo button if there is no new path drawn.
     *
     * If new path is drawn after this list is cleared to not break paths after undoing previous
     * ones.
     */
    var pathsUndone = remember { mutableStateListOf<Pair<Path, PathProperties>>() }

    /**
     * Canvas touch state. [MotionEvent.Idle] by default, [MotionEvent.Down] at first contact,
     * [MotionEvent.Move] while dragging and [MotionEvent.Up] when first pointer is up
     */
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }

    /**
     * Current position of the pointer that is pressed or being moved
     */
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

    /**
     * Previous motion event before next touch is saved into this current position.
     */
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    /**
     * Draw mode, erase mode or touch mode to
     */
    var drawMode by remember { mutableStateOf(DrawMode.Draw) }

    /**
     * Path that is being drawn between [MotionEvent.Down] and [MotionEvent.Up]. When
     * pointer is up this path is saved to **paths** and new instance is created
     */
    var currentPath by remember { mutableStateOf(Path()) }

    /**
     * Properties of path that is currently being drawn between
     * [MotionEvent.Down] and [MotionEvent.Up].
     */
    var currentPathProperty by remember { mutableStateOf(PathProperties()) }

    //temporaries for figures
    var temporaryLine by mutableStateOf<Line?>(null)
    var temporaryDashedLine by mutableStateOf<Line?>(null)
    var temporaryArrowLine by mutableStateOf<Line?>(null)
    var temporaryDashedArrowLine by mutableStateOf<Line?>(null)
    var temporaryTriangle by remember { mutableStateOf<Triangle?>(null) }
    var temporaryParallelogram by remember { mutableStateOf<Parallelogram?>(null) }
    var temporaryTrapezoid by remember { mutableStateOf<Trapezoid?>(null) }
    var temporaryRectangle by mutableStateOf<Rectangle?>(null)
    var temporaryRoundRectangle by mutableStateOf<RoundRectangle?>(null)
    var temporaryCircle by mutableStateOf<Circle?>(null)
    var temporaryHalfCircle by remember { mutableStateOf<HalfCircle?>(null) }
    var temporaryOctagon by remember { mutableStateOf<Octagon?>(null) }
    var temporaryCylinder by remember { mutableStateOf<Cylinder?>(null) }
    var temporaryStar by remember { mutableStateOf<Star?>(null) }

    //list for all figures
    var figures by remember { mutableStateOf(emptyList<Figure>()) }

    //selection rect
    var selectionRectangle by mutableStateOf<Rectangle?>(Rectangle(PointF(0f,0f), 0f, 0f, Color.Red))
    var showSelectionIcons by remember { mutableStateOf(false) }
    var showColorCircles by remember { mutableStateOf(false) }
    var isFigureSelected by remember { mutableStateOf(false) }

    var deleteSelectedBtnRect by remember { mutableStateOf(Rect(Offset(0f, 0f), Size(0f, 0f))) }
    var colorSelectedBtnRect by remember { mutableStateOf(Rect(Offset(0f, 0f), Size(0f, 0f))) }
    var moveSelectedBtnRect by remember { mutableStateOf(Rect(Offset(0f, 0f), Size(0f, 0f))) }
    //path for selection
    var selectionPath by remember { mutableStateOf(Path()) }
    val painterDelete: Painter = painterResource(id = R.drawable.baseline_cancel_24)
    val painterColor: Painter = painterResource(id = R.drawable.img_delete)
    val painterMove: Painter = painterResource(id = R.drawable.baseline_move_24)

    var colorCircles by remember { mutableStateOf(emptyList<Circle>()) }

    //field background color
    var currentBackgroundColor by remember { mutableStateOf(Color.White) }
    var bgType by remember { mutableStateOf(0) }
    val myTable by remember { mutableStateOf(Table()) }

    //for image
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }

    // Calculate offsets for centering the table
    var centerX by remember { mutableStateOf(0f) }
    var centerY by remember { mutableStateOf(0f) }

    val canvasText = remember { StringBuilder() }
    val paint = remember {
        Paint().apply {
            textSize = 40f
            color = Color.Black.toArgb()
        }
    }

    var scale by remember { mutableStateOf(1f) }
    var myRotation by remember { mutableStateOf(0f) }

    var initialTouchPoint by mutableStateOf(Offset.Zero)


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)

    ) {

        var bg_lines by remember { mutableStateOf(listOf<Pair<Float, Float>>()) }
        var tableLines by remember { mutableStateOf(listOf<Pair<Float, Float>>()) }

        val distanceBetweenLines = 30.dp

        //canvas for bg lines
        Canvas(
            modifier = Modifier
                .background(currentBackgroundColor)
                .fillMaxSize()
        ) {
            val screenSize = size
            val numberOfHorizontalLines =
                (screenSize.height / distanceBetweenLines.toPx()).toInt()
            val numberOfVerticalLines = (screenSize.width / distanceBetweenLines.toPx()).toInt()


            when (bgType) {
                1 -> {
                    bg_lines = emptyList()
                    // Draw horizontal lines
                    for (i in 0 until numberOfHorizontalLines) {
                        val y = i * distanceBetweenLines.toPx()
                        bg_lines = bg_lines + Pair(0f, y)
                    }
                }

                2 -> {
                    bg_lines = emptyList()
                    // Draw horizontal lines
                    for (i in 0 until numberOfHorizontalLines) {
                        val y = i * distanceBetweenLines.toPx()
                        bg_lines = bg_lines + Pair(0f, y)
                    }
                    // Draw vertical lines
                    for (i in 0 until numberOfVerticalLines) {
                        val x = i * distanceBetweenLines.toPx()
                        bg_lines = bg_lines + Pair(x, 0f)
                    }
                }

                else -> {
                    // Clear all lines
                    bg_lines = emptyList()
                }
            }

            bg_lines.forEach { (x, y) ->
                drawLine(
                    color = Color.Gray,
                    start = Offset(x, y),
                    end = if (x == 0f) Offset(screenSize.width, y) else Offset(
                        x,
                        screenSize.height
                    ),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Square
                )
            }
        }

        val drawModifier = Modifier

            .fillMaxSize()
            //.weight(1f)
            .background(Color.Transparent)
//            .background(getRandomColor())
            .dragMotionEvent(
                onDragStart = { pointerInputChange ->
                    motionEvent = MotionEvent.Down
                    currentPosition = pointerInputChange.position
                    pointerInputChange.consumeDownChange()

                },
                onDrag = { pointerInputChange ->
                    motionEvent = MotionEvent.Move
                    currentPosition = pointerInputChange.position

                    if (drawMode == DrawMode.Touch) {
                        val change = pointerInputChange.positionChange()
                        println("DRAG: $change")
                        paths.forEach { entry ->
                            val path: Path = entry.first
                            path.translate(change)
                        }


                        val currentLineCount = tableLines.size

                        // Remove lines if needed
                        if (currentLineCount > 0) {
                            tableLines = tableLines.drop(currentLineCount)

                            // Trigger recomposition
                            Modifier.onGloballyPositioned { coordinates ->
                                // Do nothing, just trigger recomposition
                            }
                        }
                        centerX += change.x
                        centerY += change.y

                        currentPath.translate(change)

                    }
                    pointerInputChange.consumePositionChange()

                },
                onDragEnd = { pointerInputChange ->
                    motionEvent = MotionEvent.Up
                    pointerInputChange.consumeDownChange()
                },

                )
            .transformable(
                state = rememberTransformableState { zoomChange, panChange, rotationChange ->
                    scale = 1f
                    if (drawMode == DrawMode.Touch) {
                        val scaleFactor = 1.0f + (zoomChange - 1.0f) * 0.2f
                        scale *= scaleFactor
                        myTable.tableZoom *= scaleFactor
                        //myRotation += rotationChange * scaleFactor

                        Log.d("zoom", "zoom: $scale")

                        paths.forEachIndexed { index, entry ->
                            val originalPath = entry.first
                            val pathProperties = entry.second

                            // Apply the zoom transformation to the new path
                            originalPath.zoom(scale)
                            //originalPath.rotate(myRotation)
                            if (scale > 1) {
                                pathProperties.strokeWidth += scale * 3
                            } else {
                                pathProperties.strokeWidth -= scale * 3
                            }
                        }


                        val currentLineCount = tableLines.size

                        // Remove lines if needed
                        if (currentLineCount > 0) {
                            tableLines = tableLines.drop(currentLineCount)

                            // Trigger recomposition
                            Modifier.onGloballyPositioned { coordinates ->
                                // Do nothing, just trigger recomposition
                            }
                        }

                        if (myTable.tableZoom > 1) {
                            myTable.tableScale = 100f + myTable.tableZoom * 10
                            myTable.strokeWidth = 4.dp + (myTable.tableZoom).toInt().dp
                            //tableLines[tableLines.size - 2]
                            //tableStrokeWidth += (scale).dp
                        } else {
                            myTable.tableScale = 100f - myTable.tableZoom * 10
                            //myTable.strokeWidth = 4.dp - (scale).toInt().dp
                        }
                    }
                }
            )


        Canvas(
            modifier = drawModifier
        ) {
            if (showColorCircles) {
                colorCircles = emptyList()
                for (i in 0 until 8) {
                    colorCircles += Circle(
                        PointF(
                            if (i < 4) {
                                selectionRectangle!!.leftTop.x + 50f * i + 25f
                            } else {
                                selectionRectangle!!.leftTop.x + 50f * (i - 4) + 25f
                            },
                            if (i < 4) {
                                selectionRectangle!!.leftTop.y + selectionRectangle!!.height + 75f
                            } else {
                                selectionRectangle!!.leftTop.y + selectionRectangle!!.height + 125f
                            }
                        ),
                        25f,
                        color = when (i) {
                            0 -> {
                                Color.Black
                            }

                            1 -> {
                                Color.White
                            }

                            2 -> {
                                Color.Red
                            }

                            3 -> {
                                Color.Blue
                            }

                            4 -> {
                                Color.Green
                            }

                            5 -> {
                                Color.Magenta
                            }

                            6 -> {
                                Color.Cyan
                            }

                            7 -> {
                                Color.Yellow
                            }

                            else -> {
                                Color.Black
                            }
                        }
                    )
                }
            }

            //motion events
            when (motionEvent) {

                MotionEvent.Down -> {
                    if (drawMode == DrawMode.Draw || drawMode == DrawMode.Erase) {
                        currentPath.moveTo(currentPosition.x, currentPosition.y)
                    }

                    if (drawMode == DrawMode.LineDraw) {
                        temporaryLine = Line(
                            PointF(currentPosition.x, currentPosition.y),
                            PointF(currentPosition.x, currentPosition.y),
                            color = currentPathProperty.color
                        )
                    }

                    if (drawMode == DrawMode.DashLineDraw) {
                        temporaryDashedLine = Line(
                            PointF(currentPosition.x, currentPosition.y),
                            PointF(currentPosition.x, currentPosition.y),
                            color = currentPathProperty.color,
                            type = LineType.DASHED
                        )
                    }

                    if (drawMode == DrawMode.ArrowLineDraw) {
                        temporaryArrowLine = Line(
                            PointF(currentPosition.x, currentPosition.y),
                            PointF(currentPosition.x, currentPosition.y),
                            color = currentPathProperty.color,
                            type = LineType.ARROW
                        )
                    }

                    if (drawMode == DrawMode.DashArrowLineDraw) {
                        temporaryDashedArrowLine = Line(
                            PointF(currentPosition.x, currentPosition.y),
                            PointF(currentPosition.x, currentPosition.y),
                            color = currentPathProperty.color,
                            type = LineType.DASHED_ARROW
                        )
                    }

                    if (drawMode == DrawMode.TriangleDraw) {
                        temporaryTriangle = Triangle(
                            currentPosition,
                            currentPosition,
                            currentPosition,
                            currentPathProperty.color
                        )
                    }

                    if (drawMode == DrawMode.ParallelogramDraw) {
                        temporaryParallelogram = Parallelogram(
                            points = mutableListOf(
                                currentPosition,
                                currentPosition,
                                currentPosition,
                                currentPosition
                            ),
                            color = currentPathProperty.color
                        )
                    }

                    if (drawMode == DrawMode.TrapezoidDraw) {
                        temporaryTrapezoid = Trapezoid(
                            points = mutableListOf(
                                currentPosition,
                                currentPosition,
                                currentPosition,
                                currentPosition
                            ),
                            color = currentPathProperty.color
                        )
                    }

                    if (drawMode == DrawMode.RectDraw) {
                        // Start of the rectangle
                        initialTouchPoint = currentPosition
                        temporaryRectangle = Rectangle(
                            PointF(currentPosition.x, currentPosition.y),
                            0f,
                            0f,
                            currentPathProperty.color
                        )
                    }

                    if (drawMode == DrawMode.RoundRectDraw) {
                        initialTouchPoint = currentPosition
                        temporaryRoundRectangle = RoundRectangle(
                            PointF(currentPosition.x, currentPosition.y),
                            0f,
                            0f,
                            currentPathProperty.color
                        )
                    }

                    if (drawMode == DrawMode.CircleDraw) {
                        // Start of the circle
                        val center = PointF(currentPosition.x, currentPosition.y)
                        temporaryCircle = Circle(center, 0f, currentPathProperty.color)
                    }

                    if (drawMode == DrawMode.HalfCircleDraw) {
                        initialTouchPoint = currentPosition
                        temporaryHalfCircle = HalfCircle(
                            currentPosition.x,
                            currentPosition.y,
                            0f,
                            currentPathProperty.color,
                            true
                        )
                    }

                    if (drawMode == DrawMode.OctagonDraw) {
                        temporaryOctagon = Octagon(currentPosition, 50f, currentPathProperty.color)
                    }

                    if (drawMode == DrawMode.CylinderDraw) {
                        temporaryCylinder = Cylinder(
                            center = Offset(currentPosition.x, currentPosition.y),
                            ovalRadiusX = 100f, // Initial oval radius X
                            ovalRadiusY = 50f, // Initial oval radius Y
                            color = currentPathProperty.color
                        )
                    }

                    if (drawMode == DrawMode.StarDraw) {
                        temporaryStar = Star(
                            center = Offset(currentPosition.x, currentPosition.y),
                            radius = 100f, // Initial radius
                            numPoints = 5, // Number of points in the star
                            color = currentPathProperty.color
                        )
                    }

                    if (drawMode == DrawMode.Selection) {

                        // selection rect
                        selectionPath.moveTo(currentPosition.x, currentPosition.y)
                        /*selectionRectangle = Rectangle(
                            PointF(currentPosition.x, currentPosition.y),
                            0f,
                            0f,
                            Color.Red
                        )*/
                        showSelectionIcons = false
                        isFigureSelected = false


                    }

                    previousPosition = currentPosition

                }

                MotionEvent.Move -> {

                    if (drawMode == DrawMode.Draw || drawMode == DrawMode.Erase) {
                        currentPath.quadraticBezierTo(
                            previousPosition.x,
                            previousPosition.y,
                            (previousPosition.x + currentPosition.x) / 2,
                            (previousPosition.y + currentPosition.y) / 2

                        )
                    }

                    if (drawMode == DrawMode.LineDraw) {
                        temporaryLine?.end?.set(currentPosition.x, currentPosition.y)
                    }

                    if (drawMode == DrawMode.DashLineDraw) {
                        temporaryDashedLine?.end?.set(currentPosition.x, currentPosition.y)
                    }

                    if (drawMode == DrawMode.ArrowLineDraw) {
                        temporaryArrowLine?.end?.set(currentPosition.x, currentPosition.y)
                    }

                    if (drawMode == DrawMode.DashArrowLineDraw) {
                        temporaryDashedArrowLine?.end?.set(currentPosition.x, currentPosition.y)
                    }

                    if (drawMode == DrawMode.TriangleDraw) {
                        temporaryTriangle?.let { triangle ->
                            triangle.point2 = currentPosition
                            triangle.point3 = Offset(currentPosition.x, triangle.point1.y)
                        }
                    }

                    if (drawMode == DrawMode.ParallelogramDraw) {
                        temporaryParallelogram?.let { parallelogram ->
                            parallelogram.points[1] = Offset(currentPosition.x, currentPosition.y)
                            parallelogram.points[2] = Offset(
                                parallelogram.points[0].x + (parallelogram.points[1].x - parallelogram.points[0].x) / 2,
                                parallelogram.points[1].y
                            )
                            parallelogram.points[3] =
                                Offset(
                                    parallelogram.points[2].x,
                                    parallelogram.points[0].y
                                )
                        }
                    }

                    if (drawMode == DrawMode.TrapezoidDraw) {
                        temporaryTrapezoid?.let { trapezoid ->
                            trapezoid.points[1] = Offset(currentPosition.x, trapezoid.points[0].y)
                            trapezoid.points[2] = Offset(
                                trapezoid.points[0].x + (currentPosition.x - trapezoid.points[0].x) * 0.6f,
                                currentPosition.y
                            )
                            trapezoid.points[3] = Offset(
                                trapezoid.points[0].x + (currentPosition.x - trapezoid.points[0].x) * 0.4f,
                                currentPosition.y
                            )
                        }
                    }

                    if (drawMode == DrawMode.RectDraw) {
                        // Update the size of the temporary rectangle as the user drags
                        val left = min(initialTouchPoint.x, currentPosition.x)
                        val top = min(initialTouchPoint.y, currentPosition.y)
                        val right = max(initialTouchPoint.x, currentPosition.x)
                        val bottom = max(initialTouchPoint.y, currentPosition.y)

                        temporaryRectangle?.let { rectangle ->
                            rectangle.leftTop.x = left
                            rectangle.leftTop.y = top
                            rectangle.width = right - left
                            rectangle.height = bottom - top
                        }
                    }

                    if (drawMode == DrawMode.RoundRectDraw) {
                        val left = min(initialTouchPoint.x, currentPosition.x)
                        val top = min(initialTouchPoint.y, currentPosition.y)
                        val right = max(initialTouchPoint.x, currentPosition.x)
                        val bottom = max(initialTouchPoint.y, currentPosition.y)

                        temporaryRoundRectangle?.let { rectangle ->
                            rectangle.leftTop.x = left
                            rectangle.leftTop.y = top
                            rectangle.width = right - left
                            rectangle.height = bottom - top
                        }
                    }

                    if (drawMode == DrawMode.CircleDraw) {
                        // Update the radius of the temporary circle as the user drags
                        temporaryCircle?.let { circle ->
                            val dx = currentPosition.x - circle.center.x
                            val dy = currentPosition.y - circle.center.y
                            circle.radius =
                                max(0f, sqrt(dx * dx + dy * dy)) // Prevent negative radius
                        }
                    }

                    if (drawMode == DrawMode.HalfCircleDraw) {
                        temporaryHalfCircle?.let { halfCircle ->
                            val deltaY = currentPosition.y - initialTouchPoint.y
                            val radius = abs(deltaY)
                            halfCircle.radius = radius

                            // Determine if the touch is going towards the top or bottom
                            halfCircle.isTop = currentPosition.y < initialTouchPoint.y

                        }
                    }

                    if (drawMode == DrawMode.OctagonDraw) {
                        temporaryOctagon?.let { octagon ->
                            val deltaY = currentPosition.y - octagon.center.y
                            octagon.radius = abs(deltaY)
                        }
                    }

                    if (drawMode == DrawMode.CylinderDraw) {
                        temporaryCylinder?.let { cylinder ->
                            val newOvalRadiusX = max(20f, currentPosition.x - cylinder.center.x)
                            val newOvalRadiusY = max(10f, currentPosition.y - cylinder.center.y)
                            temporaryCylinder = Cylinder(
                                center = cylinder.center,
                                ovalRadiusX = newOvalRadiusX,
                                ovalRadiusY = newOvalRadiusY,
                                color = cylinder.color
                            )
                        }
                    }

                    if (drawMode == DrawMode.StarDraw) {
                        temporaryStar?.let { star ->
                            val newRadius = max(10f, currentPosition.y - star.center.y)
                            temporaryStar = Star(
                                center = star.center,
                                radius = newRadius,
                                numPoints = star.numPoints,
                                color = star.color
                            )
                        }
                    }

                    if (drawMode == DrawMode.Selection) {
                        // Update the size of the temporary rectangle as the user drags
                        selectionPath.quadraticBezierTo(
                            previousPosition.x,
                            previousPosition.y,
                            (previousPosition.x + currentPosition.x) / 2,
                            (previousPosition.y + currentPosition.y) / 2
                        )

                    }

                    previousPosition = currentPosition
                }

                MotionEvent.Up -> {
                    if (drawMode == DrawMode.Draw || drawMode == DrawMode.Erase) {
                        currentPath.lineTo(currentPosition.x, currentPosition.y)

                        // Pointer is up save current path
//                        paths[currentPath] = currentPathProperty
                        paths.add(Pair(currentPath, currentPathProperty))

                        // Since paths are keys for map, use new one for each key
                        // and have separate path for each down-move-up gesture cycle
                        currentPath = Path()


                        // Create new instance of path properties to have new path and properties
                        // only for the one currently being drawn
                        currentPathProperty = PathProperties(
                            strokeWidth = currentPathProperty.strokeWidth,
                            color = currentPathProperty.color,
                            strokeCap = currentPathProperty.strokeCap,
                            strokeJoin = currentPathProperty.strokeJoin,
                            eraseMode = currentPathProperty.eraseMode
                        )
                    }

                    if (drawMode == DrawMode.LineDraw) {
                        // Touch released, add the line to the list
                        temporaryLine?.let { line ->
                            figures = figures + line
                        }

                    }

                    if (drawMode == DrawMode.DashLineDraw) {

                        // Drawing dashed lines
                        temporaryDashedLine?.let { dashedLine ->
                            figures = figures + dashedLine
                        }

                    }

                    if (drawMode == DrawMode.ArrowLineDraw) {

                        // Drawing arrow lines
                        temporaryArrowLine?.let { arrowLine ->
                            figures = figures + arrowLine
                        }

                    }

                    if (drawMode == DrawMode.DashArrowLineDraw) {

                        // Drawing arrow lines
                        temporaryDashedArrowLine?.let { dashedArrowLine ->
                            figures = figures + dashedArrowLine
                        }

                    }

                    if (drawMode == DrawMode.TriangleDraw) {
                        temporaryTriangle?.let { triangle ->
                            figures = figures + triangle
                        }
                    }

                    if (drawMode == DrawMode.ParallelogramDraw) {
                        temporaryParallelogram?.let { parallelogram ->
                            figures = figures + parallelogram
                        }
                    }

                    if (drawMode == DrawMode.TrapezoidDraw) {
                        temporaryTrapezoid?.let { trapezoid ->
                            figures = figures + trapezoid
                        }
                    }

                    if (drawMode == DrawMode.RectDraw) {
                        // Touch released, add the final version of the rectangle to the list
                        temporaryRectangle?.let { rectangle ->
                            figures = figures + rectangle
                        }
                    }

                    if (drawMode == DrawMode.RoundRectDraw) {
                        // Touch released, add the final version of the rectangle to the list
                        temporaryRoundRectangle?.let { rectangle ->
                            figures = figures + rectangle
                        }
                    }

                    if (drawMode == DrawMode.CircleDraw) {
                        // Touch released, add the final version of the circle to the list
                        temporaryCircle?.let { circle ->
                            figures = figures + circle
                        }
                    }

                    if (drawMode == DrawMode.HalfCircleDraw) {
                        temporaryHalfCircle?.let { halfCircle ->
                            figures = figures + halfCircle
                        }
                    }

                    if (drawMode == DrawMode.OctagonDraw) {
                        temporaryOctagon?.let { octagon ->
                            figures = figures + octagon
                        }
                    }

                    if (drawMode == DrawMode.CylinderDraw) {
                        temporaryCylinder?.let { cylinder ->
                            figures = figures + cylinder
                        }
                    }

                    if (drawMode == DrawMode.StarDraw) {
                        temporaryStar?.let { star ->
                            figures = figures + star
                        }
                    }


                    if (drawMode == DrawMode.Selection) {
                        selectionPath.lineTo(currentPosition.x, currentPosition.y)
                        val (left, top, right, bottom) = selectionPath.getBounds()
                        val myRect = Rect(left, top, right, bottom)
                        /*selectionRectangle = Rectangle(
                            PointF(currentPosition.x, currentPosition.y),
                            0f,
                            0f,
                            Color.Red
                        )*/
                        selectionRectangle = Rectangle(
                            PointF(left, top),
                            right - left,
                            bottom - top,
                            Color.Red
                        )
                        selectionRectangle?.let { rectangle ->
                            rectangle.leftTop.x = left
                            rectangle.leftTop.y = top
                            rectangle.width = right - left
                            rectangle.height = bottom - top
                        }
                        selectionPath = Path()

                        //figure selection
                        figures.forEach { figure ->
                            when (figure) {
                                is Line -> {
                                    when (figure.type) {
                                        LineType.DASHED -> {
                                            if (myRect.contains(
                                                    Offset(
                                                        figure.start.x,
                                                        figure.start.y
                                                    )
                                                )
                                            ) {
                                                figure.isSelected = true
                                                isFigureSelected = true
                                            }
                                        }

                                        LineType.ARROW -> {
                                            if (myRect.contains(
                                                    Offset(
                                                        figure.start.x,
                                                        figure.start.y
                                                    )
                                                )
                                            ) {
                                                figure.isSelected = true
                                                isFigureSelected = true
                                            }
                                        }

                                        LineType.DASHED_ARROW -> {
                                            if (myRect.contains(
                                                    Offset(
                                                        figure.start.x,
                                                        figure.start.y
                                                    )
                                                )
                                            ) {
                                                figure.isSelected = true
                                                isFigureSelected = true
                                            }
                                        }

                                        else -> { //LineType.REGULAR
                                            if (myRect.contains(
                                                    Offset(
                                                        figure.start.x,
                                                        figure.start.y
                                                    )
                                                )
                                            ) {
                                                figure.isSelected = true
                                                isFigureSelected = true
                                            }
                                        }
                                    }

                                }

                                is Triangle -> {
                                    if (myRect.contains(
                                            Offset(
                                                figure.point1.x,
                                                figure.point1.y
                                            )
                                        )
                                    ) {
                                        figure.isSelected = true
                                        isFigureSelected = true
                                    }
                                }

                                is Parallelogram -> {
                                    if (myRect.contains(
                                            Offset(
                                                figure.points[0].x,
                                                figure.points[0].y
                                            )
                                        )
                                    ) {
                                        figure.isSelected = true
                                        isFigureSelected = true
                                    }
                                }

                                is Trapezoid -> {
                                    if (myRect.contains(
                                            Offset(
                                                figure.points[0].x,
                                                figure.points[0].y
                                            )
                                        )
                                    ) {
                                        figure.isSelected = true
                                        isFigureSelected = true
                                    }
                                }

                                is Rectangle -> {
                                    if (myRect.contains(
                                            Offset(
                                                figure.leftTop.x,
                                                figure.leftTop.y
                                            )
                                        )
                                    ) {
                                        figure.isSelected = true
                                        isFigureSelected = true
                                    }

                                }

                                is RoundRectangle -> {
                                    if (myRect.contains(
                                            Offset(
                                                figure.leftTop.x,
                                                figure.leftTop.y
                                            )
                                        )
                                    ) {
                                        figure.isSelected = true
                                        isFigureSelected = true
                                    }

                                }

                                is Circle -> {
                                    if (myRect.contains(
                                            Offset(
                                                figure.center.x,
                                                figure.center.y
                                            )
                                        )
                                    ) {
                                        figure.isSelected = true
                                        isFigureSelected = true
                                    }
                                }

                                is HalfCircle -> {
                                    if (myRect.contains(
                                            Offset(
                                                figure.centerX,
                                                figure.centerY
                                            )
                                        )
                                    ) {
                                        figure.isSelected = true
                                        isFigureSelected = true
                                    }
                                }

                                is Octagon -> {
                                    if (myRect.contains(
                                            Offset(
                                                figure.center.x,
                                                figure.center.y
                                            )
                                        )
                                    ) {
                                        figure.isSelected = true
                                        isFigureSelected = true
                                    }
                                }

                                is Cylinder -> {
                                    if (myRect.contains(
                                            Offset(
                                                figure.center.x,
                                                figure.center.y
                                            )
                                        )
                                    ) {
                                        figure.isSelected = true
                                        isFigureSelected = true
                                    }
                                }

                                is Star -> {
                                    if (myRect.contains(
                                            Offset(
                                                figure.center.x,
                                                figure.center.y
                                            )
                                        )
                                    ) {
                                        figure.isSelected = true
                                        isFigureSelected = true
                                    }
                                }
                            }
                        }

                        if (!isFigureSelected) {
                            selectionRectangle = Rectangle(
                                PointF(0f, 0f),
                                0f,
                                0f,
                                Color.Red
                            )
                            showSelectionIcons = false
                            showColorCircles = false
                        }

                    }

                    showSelectionIcons = drawMode == DrawMode.Selection

                    temporaryLine = null
                    temporaryDashedLine = null
                    temporaryArrowLine = null
                    temporaryDashedArrowLine = null
                    temporaryTriangle = null
                    temporaryParallelogram = null
                    temporaryTrapezoid = null
                    temporaryRectangle = null
                    temporaryRoundRectangle = null
                    temporaryCircle = null
                    temporaryHalfCircle = null
                    temporaryOctagon = null
                    temporaryCylinder = null
                    temporaryStar = null

                    // Since new path is drawn no need to store paths to undone
                    pathsUndone.clear()

                    // If we leave this state at MotionEvent.Up it causes current path to draw
                    // line from (0,0) if this composable recomposes when draw mode is changed
                    currentPosition = Offset.Unspecified
                    previousPosition = currentPosition
                    motionEvent = MotionEvent.Idle
                }

                else -> Unit
            }

            //table filling
            for (i in 0 until myTable.rowAmount + 1) {
                val y = i * myTable.tableScale + centerY
                tableLines = tableLines + Pair(centerX, y)
            }
            for (i in 0 until myTable.columnAmount + 1) {
                val x = i * myTable.tableScale + centerX
                tableLines = tableLines + Pair(x - 0.1f, centerY)
            }


            with(drawContext.canvas.nativeCanvas) {

                //image drawing
                imageUri?.let {
                    if (Build.VERSION.SDK_INT < 28) {  //28
                        bitmap.value = MediaStore.Images
                            .Media.getBitmap(context.contentResolver, it)
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, it)
                        bitmap.value = ImageDecoder.decodeBitmap(source)
                    }

                    bitmap.value?.let { btm ->
                        drawImage(
                            image = btm.asImageBitmap(),
                            dstOffset = IntOffset(x = centerX.toInt(), y = centerY.toInt()),
                            dstSize = IntSize(btm.width, btm.height),
                        )
                    }
                }

                //table drawing
                if (myTable.rowAmount != 0 && myTable.columnAmount != 0) {
                    tableLines.forEach { (x, y) ->
                        drawLine(
                            color = myTable.color,
                            start = Offset(x, y),
                            end = if (x == centerX) Offset(
                                centerX + myTable.columnAmount * myTable.tableScale, y
                            )
                            else Offset(x, centerY + myTable.rowAmount * myTable.tableScale),
                            strokeWidth = myTable.strokeWidth.toPx(),
                            cap = StrokeCap.Square
                        )
                    }
                }

                val checkPoint = saveLayer(null, null)

                //draw figures
                figures.forEach { figure ->
                    when (figure) {
                        is Line -> {
                            when (figure.type) {
                                LineType.DASHED -> {
                                    drawLine(
                                        color = figure.color,
                                        start = Offset(figure.start.x, figure.start.y),
                                        end = Offset(figure.end.x, figure.end.y),
                                        strokeWidth = 10f,
                                        pathEffect = PathEffect.dashPathEffect(
                                            floatArrayOf(
                                                20f,
                                                20f
                                            ), 0f
                                        )
                                    )
                                }

                                LineType.ARROW -> {
                                    val path = Path()
                                    path.moveTo(figure.start.x, figure.start.y)
                                    path.lineTo(figure.end.x, figure.end.y)

                                    drawPath(
                                        path,
                                        color = figure.color,
                                        style = Stroke(width = 10f, cap = StrokeCap.Round)
                                    )

                                    // Calculate arrowhead points
                                    val angle = atan2(
                                        figure.end.y - figure.start.y,
                                        figure.end.x - figure.start.x
                                    )
                                    val arrowPoints =
                                        calculateArrowheadPoints(
                                            Offset(figure.end.x, figure.end.y),
                                            angle
                                        )

                                    // Draw the arrowhead
                                    drawPath(
                                        arrowPoints,
                                        color = figure.color,
                                        style = Stroke(width = 10f, cap = StrokeCap.Round)
                                    )
                                }

                                LineType.DASHED_ARROW -> {
                                    val path = Path()
                                    path.moveTo(figure.start.x, figure.start.y)
                                    path.lineTo(figure.end.x, figure.end.y)

                                    drawPath(
                                        path, color = figure.color,
                                        style = Stroke(
                                            width = 10f,
                                            pathEffect = PathEffect.dashPathEffect(
                                                floatArrayOf(
                                                    20f,
                                                    20f
                                                ), 0f
                                            ),
                                            cap = StrokeCap.Round
                                        )
                                    ) // Modify stroke width as needed

                                    // Calculate arrowhead points
                                    val angle = atan2(
                                        figure.end.y - figure.start.y,
                                        figure.end.x - figure.start.x
                                    )
                                    val arrowPoints = calculateArrowheadPoints(
                                        Offset(
                                            figure.end.x,
                                            figure.end.y
                                        ), angle
                                    )

                                    // Draw the arrowhead
                                    drawPath(
                                        arrowPoints,
                                        color = figure.color,
                                        style = Stroke(width = 10f, cap = StrokeCap.Round)
                                    )
                                }

                                else -> { //LineType.REGULAR
                                    drawLine(
                                        color = figure.color,
                                        start = Offset(figure.start.x, figure.start.y),
                                        end = Offset(figure.end.x, figure.end.y),
                                        strokeWidth = 10f
                                    )
                                }
                            }

                        }

                        is Triangle -> {
                            drawLine(
                                color = figure.color,
                                start = figure.point1,
                                end = figure.point2,
                                strokeWidth = 10f,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = figure.color,
                                start = figure.point2,
                                end = figure.point3,
                                strokeWidth = 10f,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = figure.color,
                                start = figure.point3,
                                end = figure.point1,
                                strokeWidth = 10f,
                                cap = StrokeCap.Round
                            )
                        }

                        is Parallelogram -> {
                            val points = figure.points
                            drawLine(
                                color = figure.color,
                                start = points[0],
                                end = points[2],
                                strokeWidth = 10f,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = figure.color,
                                start = points[2],
                                end = points[1],
                                strokeWidth = 10f,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = figure.color,
                                start = points[1],
                                end = points[3],
                                strokeWidth = 10f,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = figure.color,
                                start = points[3],
                                end = points[0],
                                strokeWidth = 10f,
                                cap = StrokeCap.Round
                            )
                        }

                        is Trapezoid -> {
                            val points = figure.points
                            drawLine(
                                color = figure.color,
                                start = points[0],
                                end = points[1],
                                strokeWidth = 10f,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = figure.color,
                                start = points[1],
                                end = points[2],
                                strokeWidth = 10f,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = figure.color,
                                start = points[2],
                                end = points[3],
                                strokeWidth = 10f,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = figure.color,
                                start = points[3],
                                end = points[0],
                                strokeWidth = 10f,
                                cap = StrokeCap.Round
                            )
                        }

                        is Rectangle -> {
                            drawRect(
                                color = figure.color,
                                topLeft = Offset(figure.leftTop.x, figure.leftTop.y),
                                size = Size(figure.width, figure.height),
                                style = Stroke(10f)
                            )

                        }

                        is RoundRectangle -> {
                            drawRoundRect(
                                color = figure.color,
                                topLeft = Offset(figure.leftTop.x, figure.leftTop.y),
                                size = Size(figure.width, figure.height),
                                style = Stroke(10f),
                                cornerRadius = CornerRadius(20f, 20f)
                            )

                        }

                        is Circle -> {
                            drawCircle(
                                color = figure.color,
                                center = Offset(figure.center.x, figure.center.y),
                                radius = figure.radius,
                                style = Stroke(10f)
                            )
                        }

                        is HalfCircle -> {
                            val startAngle = if (figure.isTop) 180f else 0f
                            val sweepAngle = 180f

                            drawArc(
                                color = figure.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 10f, cap = StrokeCap.Round),
                                topLeft = Offset(
                                    figure.centerX - figure.radius,
                                    figure.centerY - figure.radius
                                ),
                                size = Size(figure.radius * 2, figure.radius * 2)
                            )

                            // Draw the bottom line
                            val startX = figure.centerX - figure.radius
                            val startY = figure.centerY
                            val endX = figure.centerX + figure.radius
                            val endY = startY

                            drawLine(
                                color = figure.color,
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = 10f,
                                cap = StrokeCap.Round
                            )
                        }

                        is Octagon -> {
                            val points = mutableListOf<Offset>()

                            val angleOffset = Math.PI / 8 // Angle offset to keep the top side flat

                            for (i in 0 until 8) {
                                val angle = Math.PI / 4 * i + angleOffset
                                val x = figure.center.x + figure.radius * cos(angle).toFloat()
                                val y = figure.center.y + figure.radius * sin(angle).toFloat()
                                points.add(Offset(x, y))
                            }

                            for (i in 0 until points.size) {
                                drawLine(
                                    color = figure.color,
                                    start = points[i],
                                    end = points[(i + 1) % points.size],
                                    strokeWidth = 10f,
                                    cap = StrokeCap.Round
                                )
                            }
                        }

                        is Cylinder -> {
                            drawCylinder(
                                centerX = figure.center.x,
                                centerY = figure.center.y,
                                ovalRadiusX = figure.ovalRadiusX,
                                ovalRadiusY = figure.ovalRadiusY,
                                color = figure.color
                            )
                        }

                        is Star -> {
                            drawStar(
                                centerX = figure.center.x,
                                centerY = figure.center.y,
                                radius = figure.radius,
                                numPoints = figure.numPoints,
                                color = figure.color
                            )
                        }
                    }
                }

                //draw lines
                if (drawMode == DrawMode.LineDraw) {
                    temporaryLine?.let { line ->
                        drawLine(
                            color = Color.Gray,
                            start = Offset(line.start.x, line.start.y),
                            end = Offset(line.end.x, line.end.y),
                            strokeWidth = 10f
                        )
                    }
                }

                // Drawing dashed lines
                if (drawMode == DrawMode.DashLineDraw) {
                    temporaryDashedLine?.let { dashedLine ->
                        drawLine(
                            color = Color.Gray,
                            start = Offset(dashedLine.start.x, dashedLine.start.y),
                            end = Offset(dashedLine.end.x, dashedLine.end.y),
                            strokeWidth = 10f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                        )
                    }
                }

                // Drawing Arrow lines
                if (drawMode == DrawMode.ArrowLineDraw) {
                    temporaryArrowLine?.let { arrowLine ->
                        val path = Path()
                        path.moveTo(arrowLine.start.x, arrowLine.start.y)
                        path.lineTo(arrowLine.end.x, arrowLine.end.y)

                        drawPath(
                            path,
                            color = arrowLine.color,
                            style = Stroke(width = 10f, cap = StrokeCap.Round)
                        )

                        // Calculate arrowhead points
                        val angle = atan2(
                            arrowLine.end.y - arrowLine.start.y,
                            arrowLine.end.x - arrowLine.start.x
                        )
                        val arrowPoints = calculateArrowheadPoints(
                            Offset(arrowLine.end.x, arrowLine.end.y),
                            angle
                        )

                        // Draw the arrowhead
                        drawPath(
                            arrowPoints,
                            color = arrowLine.color,
                            style = Stroke(width = 10f, cap = StrokeCap.Round)
                        )
                    }
                }

                // Drawing Dashed Arrow lines
                if (drawMode == DrawMode.DashArrowLineDraw) {
                    temporaryDashedArrowLine?.let { dashedArrowLine ->
                        val path = Path()
                        path.moveTo(dashedArrowLine.start.x, dashedArrowLine.start.y)
                        path.lineTo(dashedArrowLine.end.x, dashedArrowLine.end.y)

                        drawPath(
                            path,
                            color = dashedArrowLine.color,
                            style = Stroke(
                                width = 10f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f),
                                cap = StrokeCap.Round
                            ),
                        )

                        // Calculate arrowhead points
                        val angle = atan2(
                            dashedArrowLine.end.y - dashedArrowLine.start.y,
                            dashedArrowLine.end.x - dashedArrowLine.start.x
                        )
                        val arrowPoints = calculateArrowheadPoints(
                            Offset(
                                dashedArrowLine.end.x,
                                dashedArrowLine.end.y
                            ), angle
                        )

                        // Draw the arrowhead
                        drawPath(
                            arrowPoints,
                            color = dashedArrowLine.color,
                            style = Stroke(width = 10f, cap = StrokeCap.Round)
                        )
                    }
                }

                // draw triangle
                if (drawMode == DrawMode.TriangleDraw) {
                    temporaryTriangle?.let { triangle ->
                        drawLine(
                            color = Color.Gray,
                            start = triangle.point1,
                            end = triangle.point2,
                            strokeWidth = 10f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color.Gray,
                            start = triangle.point2,
                            end = triangle.point3,
                            strokeWidth = 10f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color.Gray,
                            start = triangle.point3,
                            end = triangle.point1,
                            strokeWidth = 10f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                //draw parallelogram
                if (drawMode == DrawMode.ParallelogramDraw) {
                    temporaryParallelogram?.let { parallelogram ->
                        val points = parallelogram.points
                        drawLine(
                            color = parallelogram.color,
                            start = points[0],
                            end = points[2],
                            strokeWidth = 10f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = parallelogram.color,
                            start = points[2],
                            end = points[1],
                            strokeWidth = 10f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = parallelogram.color,
                            start = points[1],
                            end = points[3],
                            strokeWidth = 10f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = parallelogram.color,
                            start = points[3],
                            end = points[0],
                            strokeWidth = 10f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                //draw trapezoid
                if (drawMode == DrawMode.TrapezoidDraw) {
                    temporaryTrapezoid?.let { trapezoid ->
                        val points = trapezoid.points
                        drawLine(
                            color = Color.Gray,
                            start = points[0],
                            end = points[1],
                            strokeWidth = 10f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color.Gray,
                            start = points[1],
                            end = points[2],
                            strokeWidth = 10f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color.Gray,
                            start = points[2],
                            end = points[3],
                            strokeWidth = 10f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color.Gray,
                            start = points[3],
                            end = points[0],
                            strokeWidth = 10f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // draw rectangles
                if (drawMode == DrawMode.RectDraw) {
                    temporaryRectangle?.let { rectangle ->
                        drawRect(
                            color = Color.Gray, // Adjust color for temporary rectangle
                            topLeft = Offset(rectangle.leftTop.x, rectangle.leftTop.y),
                            size = Size(rectangle.width, rectangle.height),
                            style = Stroke(10f)
                        )
                    }
                }

                // draw Round rectangles
                if (drawMode == DrawMode.RoundRectDraw) {
                    temporaryRoundRectangle?.let { rectangle ->
                        drawRoundRect(
                            color = Color.Gray,
                            topLeft = Offset(rectangle.leftTop.x, rectangle.leftTop.y),
                            size = Size(rectangle.width, rectangle.height),
                            style = Stroke(10f),
                            cornerRadius = CornerRadius(16f, 16f)
                        )
                    }
                }

                // draw circles
                if (drawMode == DrawMode.CircleDraw) {
                    temporaryCircle?.let { circle ->
                        drawCircle(
                            color = Color.Gray, // Adjust color for temporary circle
                            center = Offset(circle.center.x, circle.center.y),
                            radius = circle.radius,
                            style = Stroke(10f)
                        )
                    }
                }

                // Draw Half circles
                if (drawMode == DrawMode.HalfCircleDraw) {
                    temporaryHalfCircle?.let { halfCircle ->
                        val startAngle = if (halfCircle.isTop) 180f else 0f
                        val sweepAngle = 180f

                        drawArc(
                            color = halfCircle.color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 10f, cap = StrokeCap.Round),
                            topLeft = Offset(
                                halfCircle.centerX - halfCircle.radius,
                                halfCircle.centerY - halfCircle.radius
                            ),
                            size = Size(halfCircle.radius * 2, halfCircle.radius * 2)
                        )

                        // Draw the bottom line for the temporary half circle
                        val startX = halfCircle.centerX - halfCircle.radius
                        val startY = halfCircle.centerY
                        val endX = halfCircle.centerX + halfCircle.radius
                        val endY = startY

                        drawLine(
                            color = halfCircle.color,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 10f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // Draw Octagon
                if (drawMode == DrawMode.OctagonDraw) {
                    temporaryOctagon?.let { octagon ->
                        val points = mutableListOf<Offset>()

                        val angleOffset = Math.PI / 8 // Angle offset to keep the top side flat

                        for (i in 0 until 8) {
                            val angle = Math.PI / 4 * i + angleOffset
                            val x = octagon.center.x + octagon.radius * cos(angle).toFloat()
                            val y = octagon.center.y + octagon.radius * sin(angle).toFloat()
                            points.add(Offset(x, y))
                        }

                        for (i in 0 until points.size) {
                            drawLine(
                                color = octagon.color,
                                start = points[i],
                                end = points[(i + 1) % points.size],
                                strokeWidth = 10f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }

                // Draw Cylinder
                if (drawMode == DrawMode.CylinderDraw) {
                    temporaryCylinder?.let { cylinder ->
                        drawCylinder(
                            centerX = cylinder.center.x,
                            centerY = cylinder.center.y,
                            ovalRadiusX = cylinder.ovalRadiusX,
                            ovalRadiusY = cylinder.ovalRadiusY,
                            color = Color.Gray
                        )
                    }
                }

                // Draw Star
                if (drawMode == DrawMode.StarDraw) {
                    temporaryStar?.let { star ->
                        drawStar(
                            centerX = star.center.x,
                            centerY = star.center.y,
                            radius = star.radius,
                            numPoints = star.numPoints,
                            color = Color.Gray
                        )
                    }
                }

                //selection rect drawing
                if (drawMode == DrawMode.Selection) {
                    /*selectionRectangle?.let { rectangle ->
                        drawRect(
                            color = Color.Red,
                            topLeft = Offset(rectangle.leftTop.x, rectangle.leftTop.y),
                            size = Size(rectangle.width, rectangle.height),
                            style = Stroke(
                                10f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                            )
                        )
                    }*/

                    if(isFigureSelected) {
                        selectionRectangle?.let { rectangle ->
                            drawRect(
                                color = Color.Red,
                                topLeft = Offset(rectangle.leftTop.x, rectangle.leftTop.y),
                                size = Size(rectangle.width, rectangle.height),
                                style = Stroke(
                                    10f,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                                )
                            )
                        }
                    }

                    drawPath(
                        color = Color.Red,
                        path = selectionPath,
                        style = Stroke(
                            10f,
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                        )
                    )

                    /*if (showColorCircles) {
                        colorCircles.forEach { circle ->
                            drawCircle(
                                color = circle.color,
                                center = Offset(circle.center.x, circle.center.y),
                                radius = circle.radius,
                                //style = Stroke(10f)
                            )
                        }
                    }*/

                }

                paths.forEach {

                    val path = it.first
                    val property = it.second

                    if (!property.eraseMode) {
                        drawPath(
                            color = property.color,
                            path = path,
                            style = Stroke(
                                width = property.strokeWidth,
                                cap = property.strokeCap,
                                join = property.strokeJoin
                            )
                        )
                    } else {

                        // Source
                        drawPath(
                            color = Color.Transparent,
                            path = path,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }



                if (motionEvent != MotionEvent.Idle) {

                    if (!currentPathProperty.eraseMode) {
                        drawPath(
                            color = currentPathProperty.color,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            )
                        )
                    } else {
                        drawPath(
                            color = Color.Transparent,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }
                restoreToCount(checkPoint)
            }
        }


        SelectionIcons(
            (showSelectionIcons && isFigureSelected)
        )



        Clock(
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(20.dp)
        )

        DrawingPropertiesMenu(
            modifier = Modifier
                .background(currentBackgroundColor)
                .align(alignment = Alignment.BottomCenter),
            pathProperties = currentPathProperty,
            drawMode = drawMode,
            currentBackgroundColor = currentBackgroundColor,
            onUndo = {
                if (paths.isNotEmpty()) {

                    val lastItem = paths.last()
                    val lastPath = lastItem.first
                    val lastPathProperty = lastItem.second
                    paths.remove(lastItem)

                    pathsUndone.add(Pair(lastPath, lastPathProperty))

                }
            },
            onRedo = {
                if (pathsUndone.isNotEmpty()) {

                    val lastPath = pathsUndone.last().first
                    val lastPathProperty = pathsUndone.last().second
                    pathsUndone.removeLast()
                    paths.add(Pair(lastPath, lastPathProperty))
                }
            },
            onClearAll = {
                paths.clear()
                pathsUndone.clear()
                // delete all tableLines
                tableLines = emptyList()
                myTable.tableZoom = 1f
                myTable.rowAmount = 0
                myTable.columnAmount = 0
                //clear figures
                figures = emptyList()

            },
            onPathPropertiesChange = {
                motionEvent = MotionEvent.Idle
            },
            onDrawModeChanged = {
                motionEvent = MotionEvent.Idle
                drawMode = it
                currentPathProperty.eraseMode = (drawMode == DrawMode.Erase)
                /*Toast.makeText(
                context, "pathProperty: ${currentPathProperty.hashCode()}, " +
                        "Erase Mode: ${currentPathProperty.eraseMode}", Toast.LENGTH_SHORT
            ).show()*/
            },
            onBgChanged = { color, type ->
                currentBackgroundColor = color
                bgType = type
            },
            onDrawTable = { rowInt, columnInt ->
                tableLines = emptyList()
                myTable.tableZoom = 1f
                myTable.rowAmount = rowInt
                myTable.columnAmount = columnInt
            },
            myLauncher = launcher

        )
    }


}

private fun Path.zoom(zoom: Float) {
    val bounds = RectF(
        this.getBounds().left,
        this.getBounds().top,
        this.getBounds().right,
        this.getBounds().bottom
    )
    //this.asAndroidPath().computeBounds(bounds, true)

    val pivotX = bounds.centerX()
    val pivotY = bounds.centerY()

    val matrix = Matrix()

    // Translate the path so that the pivot point is at the origin
    matrix.preTranslate(-pivotX, -pivotY)

    // Scale the path uniformly based on the larger dimension

    matrix.postScale(zoom, zoom)

    // Translate the path back to its original position
    matrix.postTranslate(pivotX, pivotY)

    this.asAndroidPath().transform(matrix)
}

private fun Path.rotate(rotationDegrees: Float) {
    val bounds = RectF(
        this.getBounds().left,
        this.getBounds().top,
        this.getBounds().right,
        this.getBounds().bottom
    )

    val pivotX = bounds.centerX()
    val pivotY = bounds.centerY()

    val matrix = Matrix()

    // Translate the path so that the pivot point is at the origin
    matrix.preTranslate(-pivotX, -pivotY)

    // Rotate the path
    matrix.postRotate(rotationDegrees)

    // Translate the path back to its original position
    matrix.postTranslate(pivotX, pivotY)

    this.asAndroidPath().transform(matrix)
}

private fun calculateArrowheadPoints(endPoint: Offset, angle: Float): Path {
    val arrowPath = Path()
    val arrowLength = 30f // Length of the arrowhead
    val arrowAngle = 150 // Angle of the arrowhead

    // Calculate the adjusted angle for the arrowhead


    val x1 = endPoint.x + arrowLength * cos(angle - Math.toRadians(arrowAngle.toDouble())).toFloat()
    val y1 = endPoint.y + arrowLength * sin(angle - Math.toRadians(arrowAngle.toDouble())).toFloat()

    val x2 = endPoint.x + arrowLength * cos(angle + Math.toRadians(arrowAngle.toDouble())).toFloat()
    val y2 = endPoint.y + arrowLength * sin(angle + Math.toRadians(arrowAngle.toDouble())).toFloat()

    arrowPath.moveTo(endPoint.x, endPoint.y)
    arrowPath.lineTo(x1, y1)
    arrowPath.moveTo(endPoint.x, endPoint.y)
    arrowPath.lineTo(x2, y2)

    return arrowPath
}

fun DrawScope.drawCylinder(
    centerX: Float,
    centerY: Float,
    ovalRadiusX: Float,
    ovalRadiusY: Float,
    color: Color
) {

    // Draw the oval top
    drawOval(
        color = color, topLeft = Offset(centerX - ovalRadiusX, centerY - ovalRadiusY),
        size = Size(ovalRadiusX * 2, ovalRadiusY * 2), style = Stroke(10f)
    )

    // Calculate points for lines at the sides of the oval
    val startPointLeft = Offset(centerX - ovalRadiusX, centerY)
    val endPointLeft = Offset(centerX - ovalRadiusX, centerY + ovalRadiusY * 3.5f)

    val startPointRight = Offset(centerX + ovalRadiusX, centerY)
    val endPointRight = Offset(centerX + ovalRadiusX, centerY + ovalRadiusY * 3.5f)

    // Draw lines down from the oval
    drawLine(color, startPointLeft, endPointLeft, strokeWidth = 10f, cap = StrokeCap.Round)
    drawLine(color, startPointRight, endPointRight, strokeWidth = 10f, cap = StrokeCap.Round)


    val arcRect = Rect(
        topLeft = Offset(centerX - ovalRadiusX, centerY + ovalRadiusY * 3),
        bottomRight = Offset(centerX + ovalRadiusX, centerY + ovalRadiusY * 3 + ovalRadiusY)
    )

    // Draw the arc connecting the lines
    val path = Path().apply {

        arcTo(rect = arcRect, startAngleDegrees = 0f, sweepAngleDegrees = 180f, forceMoveTo = false)
    }
    drawPath(path, color = color, style = Stroke(10f, cap = StrokeCap.Round))
}

fun DrawScope.drawStar(
    centerX: Float,
    centerY: Float,
    radius: Float,
    numPoints: Int,
    color: Color
) {
    val outerAngle = 2 * PI / numPoints // Angle between each outer point
    val halfAngle = PI / numPoints // Half the angle between the points of the star

    val points = mutableListOf<Offset>()

    var currentAngle = -PI / 2 // Start from the top

    repeat(numPoints * 2) { i ->
        val currentRadius =
            if (i % 2 == 0) radius else radius / 2 // Alternating between outer and inner points
        val x = centerX + (currentRadius * cos(currentAngle)).toFloat()
        val y = centerY + (currentRadius * sin(currentAngle)).toFloat()

        points.add(Offset(x, y))

        currentAngle += if (i % 2 == 0) halfAngle else outerAngle - halfAngle
    }

    // Draw lines between the points to form the star
    repeat(numPoints * 2 - 1) { i ->
        drawLine(
            color = color,
            start = points[i],
            end = points[i + 1],
            strokeWidth = 10f,
            cap = StrokeCap.Round
        )
    }

    drawLine(
        color = color,
        start = points.last(),
        end = points.first(),
        strokeWidth = 10f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawText(text: String, x: Float, y: Float, paint: Paint) {

    val lines = text.split("\n")
    // 🔥🔥 There is not a built-in function as of 1.0.0
    // for drawing text so we get the native canvas to draw text and use a Paint object
    val nativeCanvas = drawContext.canvas.nativeCanvas

    lines.indices.withIndex().forEach { (posY, i) ->
        nativeCanvas.drawText(lines[i], x, posY * 40 + y, paint)
    }
}

class ClockViewModel : ViewModel() {
    private val _currentTime = mutableStateOf(getCurrentTime())
    val currentTime: State<String> get() = _currentTime

    init {
        // Use a coroutine to update the time every second
        viewModelScope.launch {
            while (true) {
                _currentTime.value = getCurrentTime()
                delay(1000)
            }
        }
    }

    private fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(currentTime)
    }
}

@Composable
fun Clock(
    modifier: Modifier
) {
    val viewModel: ClockViewModel = ClockViewModel()
    val currentTime by viewModel.currentTime

    Box(
        modifier = modifier
    ) {
        Text(text = currentTime, fontSize = 16.sp)
    }
}

@Composable
fun SelectionIcons(
    isShown: Boolean = false
) {
    if (isShown) {
        Row {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_move_24),
                    contentDescription = null,
                    tint = Color.LightGray
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_cancel_24),
                    contentDescription = null,
                    tint = Color.LightGray
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_cancel_24),
                    contentDescription = null,
                    tint = Color.LightGray
                )
            }
        }
    }
}
