package com.example.drawingapp2.menu

import android.net.Uri
import android.os.CountDownTimer
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.drawingapp2.DrawMode
import com.example.drawingapp2.R
import com.example.drawingapp2.model.PathProperties
import com.example.drawingapp2.model.TimeUnit
import com.example.drawingapp2.ui.theme.Blue400
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor

@Composable
fun DrawingPropertiesMenu(
    modifier: Modifier = Modifier,
    pathProperties: PathProperties,
    drawMode: DrawMode,
    currentBackgroundColor: Color,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onClearAll: () -> Unit,
    onPathPropertiesChange: (PathProperties) -> Unit,
    onDrawModeChanged: (DrawMode) -> Unit,
    onBgChanged: (Color, Int) -> Unit,
    onDrawTable: (Int, Int) -> Unit,
    myLauncher: ManagedActivityResultLauncher<String, Uri?>
) {

    val properties by rememberUpdatedState(newValue = pathProperties)
    var bgColor by remember { mutableStateOf(currentBackgroundColor) }

    var showEraserDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showBackgroundDialog by remember { mutableStateOf(false) }
    var showFigureDialog by remember { mutableStateOf(false) }
    var showFeatureDialog by remember { mutableStateOf(false) }
    var showTableDialog by remember { mutableStateOf(false) }
    var showWebDialog by remember { mutableStateOf(false) }
    var showTimerDialog by remember { mutableStateOf(false) }

    var currentDrawMode = drawMode


    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .background(Color.Gray)
                .padding(horizontal = 10.dp)
        ) {
            IconButton(
                onClick = {
                    /*currentDrawMode = if (currentDrawMode == DrawMode.Touch) {
                        DrawMode.Draw
                    } else {
                        DrawMode.Touch
                    }
                    onDrawModeChanged(currentDrawMode)*/
                    currentDrawMode = DrawMode.Touch
                    onDrawModeChanged(currentDrawMode)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_touch_app_black_24),
                    contentDescription = null,
                    tint = if (currentDrawMode == DrawMode.Touch) Color.Black else Color.LightGray
                )
            }
        }
        Box(
            modifier = Modifier
                .background(Color.Gray)
                .padding(horizontal = 10.dp)
        ) {
            IconButton(
                onClick = {
                    /*currentDrawMode = if (currentDrawMode == DrawMode.Erase) {
                        DrawMode.Draw
                    } else {
                        DrawMode.Erase
                    }*/
                    /* currentDrawMode = DrawMode.Erase
                     onDrawModeChanged(currentDrawMode)*/
                    showEraserDialog = !showEraserDialog
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_eraser_black_24dp),
                    contentDescription = null,
                    tint = if (currentDrawMode == DrawMode.Erase) Color.Black else Color.LightGray
                )
            }
        }

        Box(
            modifier = Modifier
                .background(if(currentDrawMode == DrawMode.Selection) Color.Black else Color.Gray)
                .padding(horizontal = 10.dp)
        ) {
            IconButton(onClick = {
                currentDrawMode = DrawMode.Selection
                onDrawModeChanged(currentDrawMode)

            }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_fit_screen_24),
                    contentDescription = null, tint = Color.LightGray
                )
            }
        }

        Box(
            modifier = Modifier
                .background(Color.Gray)
                .padding(horizontal = 10.dp)
        ) {
            IconButton(onClick = {
                showPropertiesDialog = !showPropertiesDialog
                currentDrawMode = DrawMode.Draw
                onDrawModeChanged(currentDrawMode)

            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_brush_black_24),//selection
                    contentDescription = null, tint = Color.LightGray
                )
            }
        }

        Box(
            modifier = Modifier
                .background(Color.Gray)
                .padding(horizontal = 10.dp)
        ) {
            IconButton(onClick = { showBackgroundDialog = !showBackgroundDialog }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_color_bg_black_24),
                    contentDescription = null, tint = Color.LightGray
                )
            }
        }

        Box(
            modifier = Modifier
                .background(Color.Gray)
                .padding(horizontal = 10.dp)
        ) {
            IconButton(onClick = { //drawmode and dialog
                showFigureDialog = !showFigureDialog
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_shape_line_24),
                    contentDescription = null, tint = Color.LightGray
                )
            }
        }

        Box(
            modifier = Modifier
                .background(Color.Gray)
                .padding(horizontal = 10.dp)
        ) {
            IconButton(onClick = { showFeatureDialog = !showFeatureDialog }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_hive_24),
                    contentDescription = null, tint = Color.LightGray
                )
            }
        }

        Box(
            modifier = Modifier
                .background(Color.Gray)
                .padding(horizontal = 10.dp)
        ) {
            IconButton(onClick = {
                onUndo()
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_undo_black_24),
                    contentDescription = null,
                    tint = Color.LightGray
                )
            }
        }

        Box(
            modifier = Modifier
                .background(Color.Gray)
                .padding(horizontal = 10.dp)
        ) {
            IconButton(onClick = {
                onRedo()
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_redo_black_24),
                    contentDescription = null,
                    tint = Color.LightGray
                )
            }
        }

    }

    if (showEraserDialog) {
        EraserSelectionDialog(
            properties,
            onDismiss = { showEraserDialog = !showEraserDialog },
            onClearAll = {
                showEraserDialog = !showEraserDialog
                onClearAll()
            },
            changeToEraseMode = {
                showEraserDialog = !showEraserDialog
                currentDrawMode = DrawMode.Erase
                onDrawModeChanged(currentDrawMode)
            }
        )
    }

    if (showPropertiesDialog) {
        PropertiesMenuDialog(
            pathOption = properties,
            onDismiss = {
                showPropertiesDialog = !showPropertiesDialog
            },
            onOpenColorPicker = {
                showColorPicker = !showColorPicker
            }
        )
    }

    if (showColorPicker) {
        ColorPickerDialog(properties) {
            showColorPicker = !showColorPicker
        }
    }

    if (showBackgroundDialog) {
        BackgroundSelectionDialog(
            bgColor,
            onDismiss = { showBackgroundDialog = !showBackgroundDialog },
            onNegativeClick = { showBackgroundDialog = !showBackgroundDialog },
            onPositiveClick = { color: Color, bgType: Int ->
                showBackgroundDialog = !showBackgroundDialog
                bgColor = color
                onBgChanged(bgColor, bgType)
            }
        )
    }

    if (showFigureDialog) {
        FigureDialog(
            onDismiss = {
                showFigureDialog = !showFigureDialog
            },
            onDrawModeChanged = {
                //dismiss included :)
                currentDrawMode = it
                onDrawModeChanged(currentDrawMode)
            }
        )
    }

    if (showFeatureDialog) {
        FeatureDialog(
            onDismiss = {
                showFeatureDialog = !showFeatureDialog
            },
            openTableDialog = {
                showTableDialog = !showTableDialog
            },
            openWebDialog = {
                showWebDialog = !showWebDialog
            },
            openTimerDialog = {
                showTimerDialog = !showTimerDialog
            },
            launcher = myLauncher
        )
    }

    if (showTableDialog) {
        TableDialog(
            { showTableDialog = !showTableDialog }, { showTableDialog = !showTableDialog },
            onPositiveClick = { rowInt, columnInt ->
                showTableDialog = !showTableDialog
                onDrawTable(rowInt, columnInt)
            }
        )
    }

    if (showWebDialog) {
        WebDialog()
    }

    if (showTimerDialog) {
        TimerDialog(
            onShowTimerDialog = {
                showTimerDialog = !showTimerDialog
            }
        )
    }

}

@Composable
fun EraserSelectionDialog(
    pathOption: PathProperties,
    onDismiss: () -> Unit,
    onClearAll: () -> Unit,
    changeToEraseMode: () -> Unit
) {

    var strokeWidth by remember { mutableStateOf(pathOption.strokeWidth) }

    //Dialog(onDismissRequest = { onDismiss() }) {

        Card(
            //elevation = 2.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {

                Text(
                    text = "Eraser",
                    color = Blue400,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp)
                )

                Canvas(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    val path = Path()
                    path.moveTo(0f, size.height / 2)
                    path.lineTo(size.width / 2, size.height / 2)

                    drawPath(
                        color = Color.Gray,
                        path = path,
                        style = Stroke(
                            width = strokeWidth
                        )
                    )
                }

                Text(
                    text = "Stroke Width ${strokeWidth.toInt()}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Slider(
                    value = strokeWidth,
                    onValueChange = {
                        strokeWidth = it
                        pathOption.strokeWidth = strokeWidth
                    },
                    valueRange = 1f..100f,
                    onValueChangeFinished = {}
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    IconButton(
                        onClick = {
                            changeToEraseMode()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_eraser_black_24dp),
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }

                    IconButton(onClick = {
                        onClearAll()
                        onDismiss()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_clear_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    //}
}

@Composable
fun PropertiesMenuDialog(
    pathOption: PathProperties,
    onDismiss: () -> Unit,
    onOpenColorPicker: () -> Unit
) {

    var strokeWidth by remember { mutableStateOf(pathOption.strokeWidth) }

    Dialog(onDismissRequest = { onDismiss() }) {

        Card(
            //elevation = 2.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {

            val bgColor1 = colorResource(R.color.dark_white)
            val bgColor2 = colorResource(R.color.light_brown)
            val bgColor3 = colorResource(R.color.light_green)
            val bgColor4 = colorResource(R.color.light_blue)
            val bgColor5 = colorResource(R.color.light_black)
            val bgColor6 = colorResource(R.color.dark_green)
            val bgColor7 = colorResource(R.color.dark_blue)
            val bgColor8 = colorResource(R.color.dark_purple)

            Column(modifier = Modifier.padding(8.dp)) {

                Text(
                    text = "Properties",
                    color = Blue400,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column {

                        //First couple
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .background(
                                        colorResource(R.color.dark_white),
                                        shape = RoundedCornerShape(50.dp)
                                    )
                                    .clickable {
                                        pathOption.color = bgColor1
                                    }
                            )
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .background(
                                        colorResource(R.color.light_brown),
                                        shape = RoundedCornerShape(50.dp)
                                    )
                                    .clickable {
                                        pathOption.color = bgColor2
                                    }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        //Second couple
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .background(
                                        colorResource(R.color.light_green),
                                        shape = RoundedCornerShape(50.dp)
                                    )
                                    .clickable {
                                        pathOption.color = bgColor3
                                    }
                            )
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .background(
                                        colorResource(R.color.light_blue),
                                        shape = RoundedCornerShape(50.dp)
                                    )
                                    .clickable {
                                        pathOption.color = bgColor4
                                    }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        //Third couple
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .background(
                                        colorResource(R.color.light_black),
                                        shape = RoundedCornerShape(50.dp)
                                    )
                                    .clickable {
                                        pathOption.color = bgColor5
                                    }
                            )
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .background(
                                        colorResource(R.color.dark_green),
                                        shape = RoundedCornerShape(50.dp)
                                    )
                                    .clickable {
                                        pathOption.color = bgColor6
                                    }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        //Fourth couple
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .background(
                                        colorResource(R.color.dark_blue),
                                        shape = RoundedCornerShape(50.dp)
                                    )
                                    .clickable {
                                        pathOption.color = bgColor7
                                    }
                            )
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .background(
                                        colorResource(R.color.dark_purple),
                                        shape = RoundedCornerShape(50.dp)
                                    )
                                    .clickable {
                                        pathOption.color = bgColor8
                                    }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        //Fifth couple
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .background(
                                        colorResource(R.color.dark_purple),
                                        shape = RoundedCornerShape(50.dp)
                                    )
                                    .clickable {
                                        pathOption.color = bgColor8
                                    }
                            )
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .background(
                                        colorResource(R.color.dark_blue),
                                        shape = RoundedCornerShape(50.dp)
                                    )
                                    .clickable {
                                        //open color picker dialog
                                        onOpenColorPicker()

                                    }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(20.dp)
                                .background(
                                    colorResource(R.color.black),
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .clickable {
                                    pathOption.strokeWidth = 5f
                                }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(25.dp)
                                .height(25.dp)
                                .background(
                                    colorResource(R.color.black),
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .clickable {
                                    pathOption.strokeWidth = 10f
                                }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .background(
                                    colorResource(R.color.black),
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .clickable {
                                    pathOption.strokeWidth = 50f
                                }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(35.dp)
                                .height(35.dp)
                                .background(
                                    colorResource(R.color.black),
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .clickable {
                                    pathOption.strokeWidth = 80f
                                }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .background(
                                    colorResource(R.color.black),
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .clickable {
                                    pathOption.strokeWidth = 100f
                                }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(50.dp)
                                .background(
                                    colorResource(R.color.black),
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .clickable {
                                    pathOption.strokeWidth = 150f
                                }
                        )
                    }

                    // Rotating the slider by -90 degrees to make it vertical
                    Slider(
                        value = strokeWidth,
                        onValueChange = {
                            strokeWidth = it
                            pathOption.strokeWidth = strokeWidth
                        },
                        valueRange = 5f..100f,
                        onValueChangeFinished = {},
                        modifier = Modifier
                            .width(200.dp)//200
                            .graphicsLayer {
                                rotationZ = -90f
                                translationY = 100f

                            }
                            .width(10.dp),
                    )

                }

                Canvas(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    val path = Path()
                    path.moveTo(0f, size.height / 2)
                    path.lineTo(size.width, size.height / 2)

                    drawPath(
                        color = pathOption.color,
                        path = path,
                        style = Stroke(
                            width = pathOption.strokeWidth
                        )
                    )
                }

                /*Text(
                    text = "Stroke Width ${strokeWidth.toInt()}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Slider(
                    value = strokeWidth,
                    onValueChange = {
                        strokeWidth = it
                        pathOption.strokeWidth = strokeWidth
                    },
                    valueRange = 1f..100f,
                    onValueChangeFinished = {}
                )*/

                /*Spacer(modifier = Modifier.height(8.dp))

                ClassicColorPicker(
                    modifier = Modifier.size(300.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    onColorChanged = { color: HsvColor ->
                        pathOption.color = color.toColor()
                    }
                )*/

                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }
}

@Composable
fun ColorPickerDialog(
    pathOption: PathProperties,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Column {

            ClassicColorPicker(
                modifier = Modifier
                    .size(300.dp)
                    .align(alignment = Alignment.CenterHorizontally),
                onColorChanged = { color: HsvColor ->
                    pathOption.color = color.toColor()
                }
            )

        }

    }


}

@Composable
fun BackgroundSelectionDialog(
    currentBackgroundColor: Color,
    onDismiss: () -> Unit,
    onNegativeClick: () -> Unit,
    onPositiveClick: (Color, Int) -> Unit,
) {

    var myColor = currentBackgroundColor
    val bgColor1 = colorResource(R.color.dark_white)
    val bgColor2 = colorResource(R.color.light_brown)
    val bgColor3 = colorResource(R.color.light_green)
    val bgColor4 = colorResource(R.color.light_blue)
    val bgColor5 = colorResource(R.color.light_black)
    val bgColor6 = colorResource(R.color.dark_green)
    val bgColor7 = colorResource(R.color.dark_blue)
    val bgColor8 = colorResource(R.color.dark_purple)

    var bgType = 0

    Dialog(onDismissRequest = onDismiss) {

        BoxWithConstraints(
            Modifier
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .background(Color.White)
        ) {

            val widthInDp = LocalDensity.current.run { maxWidth }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = "BG Color",
                    color = Blue400,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .background(
                                    colorResource(R.color.dark_white),
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .clickable {
                                    myColor = bgColor1
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .background(
                                    colorResource(R.color.light_brown),
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .clickable {
                                    myColor = bgColor2
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .background(
                                    colorResource(R.color.light_green),
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .clickable {
                                    myColor = bgColor3
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .background(
                                    colorResource(R.color.light_blue),
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .clickable {
                                    myColor = bgColor4
                                }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
//                                .padding(horizontal = 4.dp)
                                .background(
                                    colorResource(R.color.light_black),
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .clickable {
                                    myColor = bgColor5
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .background(
                                    colorResource(R.color.dark_green),
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .clickable {
                                    myColor = bgColor6
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .background(
                                    colorResource(R.color.dark_blue),
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .clickable {
                                    myColor = bgColor7
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .background(
                                    colorResource(R.color.dark_purple),
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .clickable {
                                    myColor = bgColor8
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))


                // Choose Background Type
                var firstTint by remember { mutableStateOf(Color.LightGray) }
                var secondTint by remember { mutableStateOf(Color.LightGray) }
                var thirdTint by remember { mutableStateOf(Color.LightGray) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    IconButton(
                        onClick = {
                            bgType = 0
                            firstTint = Color.Black
                            secondTint = Color.LightGray
                            thirdTint = Color.LightGray
                        },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.figure_square_24),
                            contentDescription = null,
                            tint = firstTint
                        )
                    }

                    IconButton(
                        onClick = {
                            bgType = 1
                            firstTint = Color.LightGray
                            secondTint = Color.Black
                            thirdTint = Color.LightGray
                        },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_table_rows_24),
                            contentDescription = null,
                            tint = secondTint
                        )
                    }

                    IconButton(
                        onClick = {
                            bgType = 2
                            firstTint = Color.LightGray
                            secondTint = Color.LightGray
                            thirdTint = Color.Black
                        },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_view_module_24),
                            contentDescription = null,
                            tint = thirdTint
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color(0xffF3E5F5)),
                    verticalAlignment = Alignment.CenterVertically

                ) {

                    TextButton(
                        onClick = onNegativeClick,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(text = "CANCEL")
                    }
                    TextButton(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = {
                            onPositiveClick(myColor, bgType)
                        },
                    ) {
                        Text(text = "OK")
                    }
                }
            }
        }
    }
}

@Composable
fun FigureDialog(
    onDismiss: () -> Unit,
    onDrawModeChanged: (DrawMode) -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {

        Card(
            //elevation = 2.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {

                Text(
                    text = "Figures",
                    color = Blue400,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp)
                )

                Spacer(Modifier.height(10.dp))

                Row {
                    IconButton(onClick = {
                        onDismiss()
                        onDrawModeChanged(DrawMode.LineDraw)

                    }) {
                        Icon(
                            painter = painterResource(R.drawable.figure_line_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    IconButton(onClick = {
                        onDismiss()
                        onDrawModeChanged(DrawMode.DashLineDraw)

                    }) {
                        Icon(
                            painter = painterResource(R.drawable.figure_dash_line_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    IconButton(onClick = {
                        onDismiss()
                        onDrawModeChanged(DrawMode.ArrowLineDraw)

                    }) {
                        Icon(
                            painter = painterResource(R.drawable.figure_arrow_line_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    IconButton(onClick = {
                        onDismiss()
                        onDrawModeChanged(DrawMode.DashArrowLineDraw)

                    }) {
                        Icon(
                            painter = painterResource(R.drawable.figure_dash_arrow_line_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    IconButton(onClick = {
                        onDismiss()
                        onDrawModeChanged(DrawMode.TriangleDraw)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.figure_triangle_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    IconButton(onClick = {
                        onDismiss()
                        onDrawModeChanged(DrawMode.ParallelogramDraw)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.figure_parallelogram_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    IconButton(onClick = {
                        onDismiss()
                        onDrawModeChanged(DrawMode.TrapezoidDraw)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.figure_trapezoid_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }
                }

                Row{

                    IconButton(onClick = {
                        onDismiss()
                        onDrawModeChanged(DrawMode.RectDraw)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.figure_square_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    IconButton(onClick = {
                        onDismiss()
                        onDrawModeChanged(DrawMode.RoundRectDraw)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.figure_round_rect_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    IconButton(onClick = {
                        onDismiss()
                        onDrawModeChanged(DrawMode.CircleDraw)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.figure_circle_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    IconButton(onClick = {
                        onDismiss()
                        onDrawModeChanged(DrawMode.HalfCircleDraw)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.figure_half_circle_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    IconButton(onClick = {
                        onDismiss()
                        onDrawModeChanged(DrawMode.OctagonDraw)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.figure_hexagon_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    IconButton(onClick = {
                        onDismiss()
                        onDrawModeChanged(DrawMode.CylinderDraw)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.figure_cylinder_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    IconButton(onClick = {
                        onDismiss()
                        onDrawModeChanged(DrawMode.StarDraw)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.figure_star_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun FeatureDialog(
    onDismiss: () -> Unit,
    openTableDialog: () -> Unit,
    openWebDialog: () -> Unit,
    openTimerDialog: () -> Unit,
    launcher: ManagedActivityResultLauncher<String, Uri?>
) {
    Dialog(onDismissRequest = { onDismiss() }) {

        Card(
            //elevation = 2.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {

                Text(
                    text = "Features",
                    color = Blue400,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp)
                )

                Spacer(Modifier.height(10.dp))

                Row {
                    //Table dialog
                    IconButton(onClick = {
                        onDismiss()
                        openTableDialog()

                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_view_module_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    //Web Dialog
                    IconButton(onClick = {
                        onDismiss()
                        openWebDialog()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_web_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    //Timer Dialog
                    IconButton(onClick = {
                        onDismiss()
                        openTimerDialog()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_time_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    //Image Dialog
                    IconButton(onClick = {
                        onDismiss()
                        launcher.launch("image/*")
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_image_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TableDialog(
    onDismiss: () -> Unit,
    onNegativeClick: () -> Unit,
    onPositiveClick: (rowInt: Int, columnInt: Int) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            //elevation = 2.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            var rowIntText by remember { mutableStateOf(0) }
            var columnIntText by remember { mutableStateOf(0) }
            val boxColors = remember {
                mutableStateListOf<Color>().apply {
                    // Initialize the list with the default color for each box
                    repeat(8 * 12) {
                        add(Color.LightGray)
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
            Column {

                Text(
                    text = "Table, row = $rowIntText, column = $columnIntText",
                    modifier = Modifier.padding(start = 20.dp),
                    color = Color.Black,
                    fontSize = 16.sp,
                )


                Column(
                    Modifier
                        .padding(start = 20.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                // Reset all box colors to light gray initially
                                boxColors.replaceAll { Color.LightGray }

                                // Change the color of all boxes with lower i and j
                                for (row in 0..(change.position.y / 26.dp.toPx()).toInt()) { //25 for tablet
                                    for (column in 0..(change.position.x / 26.dp.toPx()).toInt()) {
                                        if (row < 8 && column < 12) {
                                            val lowerIndex = row * 12 + column
                                            boxColors[lowerIndex] = Color.Gray
                                        }
                                    }
                                }

                                rowIntText = (change.position.y / 26.dp.toPx()).toInt() + 1
                                columnIntText = (change.position.x / 26.dp.toPx()).toInt() + 1

                                if (rowIntText > 8)
                                    rowIntText = 8
                                if (columnIntText > 12)
                                    columnIntText = 12
                                if (rowIntText < 0)
                                    rowIntText = 0
                                if (columnIntText < 0)
                                    columnIntText = 0


                            }
                        }
                ) {
                    for (i in 0 until 8) {
                        Row(modifier = Modifier.padding(bottom = 5.dp)) {
                            for (j in 0 until 12) {

                                val index = i * 12 + j
                                Box(modifier = Modifier
                                    .padding(end = 5.dp)
                                    .size(20.dp)
                                    .background(boxColors[index])
                                    .clickable {
                                        // Change the color of the clicked box
                                        boxColors.replaceAll { Color.LightGray }
                                        boxColors[index] = Color.Gray

                                        // Change the color of all boxes with lower i and j (including the clicked one)
                                        for (row in 0..i) {
                                            for (column in 0..j) {
                                                val lowerIndex = row * 12 + column
                                                boxColors[lowerIndex] = Color.Gray
                                                rowIntText = row + 1
                                                columnIntText = column + 1
                                            }
                                        }

                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // Buttons
            Row(
                modifier = Modifier
                    .width(350.dp)
                    .height(60.dp)
                    .background(Color(0xffF3E5F5)),
                verticalAlignment = Alignment.CenterVertically

            ) {

                TextButton(
                    onClick = onNegativeClick,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Text(text = "CANCEL")
                }
                TextButton(
                    onClick =
                    { onPositiveClick(rowIntText, columnIntText) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),

                    ) {
                    Text(text = "OK")
                }
            }

        }
    }

}

@Composable
fun WebDialog() {
    var myOffset by remember { mutableStateOf(Offset.Zero) }
    Box(
        Modifier
            .fillMaxWidth(0.3f)
            .fillMaxHeight(0.7f)
            .offset(x = (myOffset.x / 3).dp, y = (myOffset.y / 3).dp)
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress { change, dragAmount ->
                    myOffset += dragAmount
                }
            },
        contentAlignment = Alignment.CenterEnd
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )


                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.databaseEnabled = true
                    settings.allowFileAccessFromFileURLs = true
                    settings.setSupportZoom(true)  // Enable zoom controls
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                    settings.userAgentString = System.getProperty("http.agent")

                    //settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                    //settings.saveFormData = true
                    //settings.allowFileAccess = true
                    //settings.allowContentAccess = true
                    //settings.defaultZoom = WebSettings.ZoomDensity.CLOSE

                    webViewClient = WebViewClient()
                }
            },
            update = { view ->
                // Load the URL
                view.loadUrl("https://www.google.kz/?hl=ru")


            }
        )
    }
}

@Composable
fun TimerDialog(
    onShowTimerDialog: () -> Unit
) {
    var myOffset by remember { mutableStateOf(Offset.Zero) }
    var countdownText by remember { mutableStateOf("00:00:00") }
    var counting by remember { mutableStateOf(false) }
    var countdownTimer: CountDownTimer? by remember { mutableStateOf(null) }
    var countUpTimer: CountDownTimer? by remember { mutableStateOf(null) }
    var remainingTime: Long by remember { mutableStateOf(0) }
    var timerPaused by remember { mutableStateOf(false) }
    var isCountUp by remember { mutableStateOf(false) }

    var expandMode by remember { mutableStateOf(false) }

    val expandModeModifier = Modifier.fillMaxSize()
        .background(Color.Black)

    val commonModeModifier = Modifier.width(300.dp)
        .height(150.dp)
        .offset(
            x = (myOffset.x / 3).dp,
            y = (myOffset.y / 3).dp
        ) // /3 for speed decreasing
        .background(Color.Gray)
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                myOffset += dragAmount
            }
        }

    Box(
        modifier = if(expandMode) expandModeModifier
        else commonModeModifier

        ) {
        var hours by remember { mutableStateOf(0) }
        var minutes by remember { mutableStateOf(0) }
        var seconds by remember { mutableStateOf(0) }

        val scrollStateHours = rememberLazyListState()
        val scrollStateMinutes = rememberLazyListState()
        val scrollStateSeconds = rememberLazyListState()


        Column(
            modifier = Modifier
                .fillMaxSize().then(
                    if (expandMode){
                    Modifier
                        .pointerInput(Unit) {
                            detectTapGestures{
                                expandMode =!expandMode
                            }
                        }
                     }else Modifier
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!expandMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            onShowTimerDialog()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_cancel_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .width(300.dp)
                    .height(50.dp)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!counting) {
                    ScrollableTimer(
                        scrollStateHours,
                        value = hours,
                        timeUnit = TimeUnit.HOURS,
                        onValueChanged = { hours = it }
                    )
                    Text(":", color = Color.White, fontSize = 30.sp)
                    ScrollableTimer(
                        scrollStateMinutes,
                        value = minutes,
                        timeUnit = TimeUnit.MINUTES,
                        onValueChanged = { minutes = it }
                    )
                    Text(":", color = Color.White, fontSize = 30.sp)
                    ScrollableTimer(
                        scrollStateSeconds,
                        value = seconds,
                        timeUnit = TimeUnit.SECONDS,
                        onValueChanged = { seconds = it }
                    )
                } else {
                    val (hoursText, minutesText, secondsText) = countdownText.split(":")
                    TimerText(hoursText)
                    Text(":", color = Color.White, fontSize = 30.sp)
                    TimerText(minutesText)
                    Text(":", color = Color.White, fontSize = 30.sp)
                    TimerText(secondsText)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (!expandMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    //ok button
                    IconButton(onClick = {
                        // Reset timer values
                        hours = 0
                        minutes = 0
                        seconds = 0
                        counting = false
                        countdownText = "00:00:00"
                        timerPaused = false
                        countdownTimer?.cancel()
                        countUpTimer?.cancel()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_restart_alt_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    //play btn
                    IconButton(onClick = {
                        if (!counting) {
                            // Start countdown timer
                            countdownTimer?.cancel()
                            countUpTimer?.cancel()
                            counting = true

                            isCountUp = hours == 0 && minutes == 0 && seconds == 0

                            if (isCountUp) {
                                var elapsedSeconds = 0

                                countUpTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
                                    override fun onTick(millisUntilFinished: Long) {
                                        hours = elapsedSeconds / 3600
                                        minutes = (elapsedSeconds % 3600) / 60
                                        seconds = elapsedSeconds % 60

                                        val formattedTime = String.format(
                                            "%02d:%02d:%02d",
                                            hours,
                                            minutes,
                                            seconds
                                        )

                                        countdownText = formattedTime
                                        elapsedSeconds++
                                    }

                                    override fun onFinish() {
                                        // Not used in CountUp timer
                                        //countdownText = "00:00:00"
                                        counting = false
                                    }
                                }.start()
                            } else {
                                val totalMilliseconds =
                                    (hours * 3600 + minutes * 60 + seconds) * 1000L

                                countdownTimer = object : CountDownTimer(totalMilliseconds, 1000) {
                                    override fun onTick(millisUntilFinished: Long) {
                                        hours = (millisUntilFinished / 3600000).toInt()
                                        minutes =
                                            ((millisUntilFinished % 3600000) / 60000).toInt()
                                        seconds =
                                            ((millisUntilFinished % 60000) / 1000).toInt()

                                        val formattedTime = String.format(
                                            "%02d:%02d:%02d",
                                            hours,
                                            minutes,
                                            seconds
                                        )

                                        countdownText = formattedTime
                                    }

                                    override fun onFinish() {
                                        countdownText = "00:00:00"
                                        counting = false
                                    }
                                }.start()
                            }
                        } else {
                            // Pause or resume countdown timer
                            if (!timerPaused) {
                                remainingTime = (hours * 3600 + minutes * 60 + seconds) * 1000L
                                countdownTimer?.cancel()
                                countUpTimer?.cancel()
                                timerPaused = true
                            } else {
                                val totalMilliseconds =
                                    (hours * 3600 + minutes * 60 + seconds) * 1000L

                                val time =
                                    if (remainingTime > 0) remainingTime else totalMilliseconds


                                if (isCountUp) {
                                    var elapsedSeconds = time / 1000

                                    countUpTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
                                        override fun onTick(millisUntilFinished: Long) {
                                            hours = (elapsedSeconds / 3600).toInt()
                                            minutes = ((elapsedSeconds % 3600) / 60).toInt()
                                            seconds = (elapsedSeconds % 60).toInt()

                                            val formattedTime = String.format(
                                                "%02d:%02d:%02d",
                                                hours,
                                                minutes,
                                                seconds
                                            )

                                            countdownText = formattedTime
                                            elapsedSeconds++
                                        }

                                        override fun onFinish() {
                                            // Not used in CountUp timer
                                            //countdownText = "00:00:00"
                                            counting = false
                                        }
                                    }.start()
                                } else {

                                    countdownTimer = object : CountDownTimer(time, 1000) {
                                        override fun onTick(millisUntilFinished: Long) {
                                            hours = (millisUntilFinished / 3600000).toInt()
                                            minutes =
                                                ((millisUntilFinished % 3600000) / 60000).toInt()
                                            seconds =
                                                ((millisUntilFinished % 60000) / 1000).toInt()

                                            val formattedTime = String.format(
                                                "%02d:%02d:%02d",
                                                hours,
                                                minutes,
                                                seconds
                                            )

                                            countdownText = formattedTime
                                        }

                                        override fun onFinish() {
                                            countdownText = "00:00:00"
                                            counting = false
                                        }
                                    }.start()
                                }

                                timerPaused = false
                            }

                        }
                    }) {
                        Icon(
                            painter = if (counting && !timerPaused)
                                painterResource(R.drawable.baseline_pause_circle_24)
                            else painterResource(R.drawable.baseline_play_circle_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    //expand btn
                    IconButton(onClick = {
                        expandMode = !expandMode
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_aspect_ratio_24),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun TimerText(
    text: String
) {
    Box(
        modifier = Modifier
            .width(90.dp)
            .height(80.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(4.dp)
                .background(
                    color = Color.Transparent
                )
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ScrollableTimer(
    scrollState: LazyListState,
    value: Int,
    timeUnit: TimeUnit,
    onValueChanged: (Int) -> Unit
) {
    val items = when (timeUnit) {
        TimeUnit.HOURS -> (0..23).toList()
        TimeUnit.MINUTES, TimeUnit.SECONDS -> (0..59).toList()
    }

    Box(
        modifier = Modifier
            .width(90.dp)
            .height(80.dp)
            .padding(4.dp)
    ) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(items) { index, item ->
                val formattedItem = "%02d".format(item) // Ensure 2-digit format
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(4.dp)
                        .background(
                            color = Color.Transparent
                        )
                        .clickable {
                            onValueChanged(index)
                        }
                ) {
                    Text(
                        text = formattedItem,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

