package com.example.drawingapp2.menu

import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.drawingapp2.DrawMode
import com.example.drawingapp2.R
import com.example.drawingapp2.model.PathProperties
import com.example.drawingapp2.ui.theme.Blue400
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import java.io.ByteArrayInputStream

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
) {

    val properties by rememberUpdatedState(newValue = pathProperties)
    var bgColor by remember { mutableStateOf(currentBackgroundColor) }

    var showPropertiesDialog by remember { mutableStateOf(false) }
    var showBackgroundDialog by remember { mutableStateOf(false) }
    var showEraserDialog by remember { mutableStateOf(false) }
    var showWebDialog by remember { mutableStateOf(false) }
    var showTableDialog by remember { mutableStateOf(false) }
    var currentDrawMode = drawMode

    Row(
        modifier = modifier
//            .background(getRandomColor())
        ,
        //verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.background(Color.Gray).padding(horizontal = 10.dp)
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
            modifier = Modifier.background(Color.Gray).padding(horizontal = 10.dp)
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
            modifier = Modifier.background(Color.Gray).padding(horizontal = 10.dp)
        ) {
            IconButton(onClick = {
                showPropertiesDialog = !showPropertiesDialog
                currentDrawMode = DrawMode.Draw
                onDrawModeChanged(currentDrawMode)

            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_brush_black_24),
                    contentDescription = null, tint = Color.LightGray
                )
            }
        }

        Box(
            modifier = Modifier.background(Color.Gray).padding(horizontal = 10.dp)
        ) {
            IconButton(onClick = { showBackgroundDialog = !showBackgroundDialog }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_color_bg_black_24),
                    contentDescription = null, tint = Color.LightGray
                )
            }
        }

        Box(
            modifier = Modifier.background(Color.Gray).padding(horizontal = 10.dp)
        ) {
            IconButton(onClick = { //drawmode and dialog
                 }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_shape_line_24),
                    contentDescription = null, tint = Color.LightGray
                )
            }
        }

        Box(
            modifier = Modifier.background(Color.Gray).padding(horizontal = 10.dp)
        ) {
            IconButton(onClick = { showTableDialog = !showTableDialog }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_view_module_24),
                    contentDescription = null, tint = Color.LightGray
                )
            }
        }

        Box(
            modifier = Modifier.background(Color.Gray).padding(horizontal = 10.dp)
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
            modifier = Modifier.background(Color.Gray).padding(horizontal = 10.dp)
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

        Box(
            modifier = Modifier.background(Color.Gray).padding(horizontal = 10.dp)
        ) {
            IconButton(onClick = {
                showWebDialog = !showWebDialog
            }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_web_24),
                    contentDescription = null,
                    tint = Color.LightGray
                )
            }
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

    if (showPropertiesDialog) {
        PropertiesMenuDialog(properties) {
            showPropertiesDialog = !showPropertiesDialog
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

    if (showWebDialog) {
        WebDialog()
    }

    if (showTableDialog) {
        TableDialog(
            { showTableDialog = !showTableDialog }, { showTableDialog = !showTableDialog },
            onPositiveClick = {rowInt, columnInt ->
                showTableDialog = !showTableDialog
                onDrawTable(rowInt, columnInt)
            }
        )
    }
}

@Composable
fun PropertiesMenuDialog(pathOption: PathProperties, onDismiss: () -> Unit) {

    var strokeWidth by remember { mutableStateOf(pathOption.strokeWidth) }

    Dialog(onDismissRequest = { onDismiss() }) {

        Card(
            //elevation = 2.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {

                Text(
                    text = "Properties",
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
                    path.lineTo(size.width, size.height / 2)

                    drawPath(
                        color = pathOption.color,
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

                ClassicColorPicker(
                    modifier = Modifier.size(300.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    onColorChanged = { color: HsvColor ->
                        pathOption.color = color.toColor()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }
}

@Composable
fun WebDialog() {
    var myOffset by remember { mutableStateOf(Offset.Zero) }
    Box(
        Modifier.fillMaxWidth(0.3f).fillMaxHeight(0.7f)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableDialog(
    onDismiss: () -> Unit,
    onNegativeClick: () -> Unit,
    onPositiveClick: (rowInt: Int, columnInt: Int) -> Unit
) {
    Dialog(onDismissRequest = onDismiss ) {
        Card(
            //elevation = 2.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            var rowIntText by remember { mutableStateOf(0) }
            var columnIntText by remember { mutableStateOf(0) }
            val boxColors = remember { mutableStateListOf<Color>().apply {
                // Initialize the list with the default color for each box
                repeat(8 * 12) {
                    add(Color.LightGray)
                }
            }}

            Spacer(Modifier.height(40.dp))

            Column(Modifier.padding(start = 20.dp)
                .pointerInput(Unit){
                    detectDragGestures { change, dragAmount ->
                        // Reset all box colors to light gray initially
                        for (i in 0 until 8) {
                            for (j in 0 until 12) {
                                val index = i * 12 + j
                                boxColors[index] = Color.LightGray
                            }
                        }

                        // Change the color of all boxes with lower i and j
                        for (row in 0..(change.position.y /50 ).toInt()) {
                            for (column in 0..(change.position.x /50 ).toInt()) {
                                val lowerIndex = row * 12 + column
                                boxColors[lowerIndex] = Color.Gray
                            }
                        }
                        Log.d("Table", "x = ${dragAmount.x}, y = ${dragAmount.y}")

                        rowIntText = (change.position.y /50 ).toInt() + 1
                        columnIntText = (change.position.x /50 ).toInt() + 1
                    }
                }
            ) {
                for(i in 0 until 8) {
                    Row (modifier = Modifier.padding(bottom = 5.dp)) {
                        for (j in 0 until 12) {

                            val index = i * 12 + j
                            Box(modifier = Modifier.padding(end = 5.dp).size(20.dp)
                                .background(boxColors[index])
                            )
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
                    { onPositiveClick(rowIntText, columnIntText) }
                    ,
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
fun EraserSelectionDialog(
    pathOption: PathProperties,
    onDismiss: () -> Unit,
    onClearAll: () -> Unit,
    changeToEraseMode: () -> Unit
) {

    var strokeWidth by remember { mutableStateOf(pathOption.strokeWidth) }

    Dialog(onDismissRequest = { onDismiss() }) {

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

    var bgType = 0;

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
                            painter = painterResource(R.drawable.baseline_square_24),
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
