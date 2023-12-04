package com.example.drawingapp2.menu

import android.net.Uri
import android.os.CountDownTimer
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.colorspace.ColorSpaces
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
import com.example.drawingapp2.ui.theme.*
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


    Row(modifier = modifier) {

        //left row
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.width(5.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 5.dp, bottomStart = 5.dp))
                    .background(Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(
                    onClick = {
                        /*TODO*/
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_menu),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Menu",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(
                    onClick = {
                        /*TODO*/
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_exit),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Exit",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(
                    onClick = {
                        /*TODO*/
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_qr),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "QR code",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp))
                    .background(Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(
                    onClick = {
                        /*TODO*/
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_list),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "List",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.width(20.dp))

        //center row
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            //select
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 5.dp, bottomStart = 5.dp))
                    .background(if (currentDrawMode == DrawMode.Selection) Color.LightGray else Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(onClick = {
                    currentDrawMode = DrawMode.Selection
                    onDrawModeChanged(currentDrawMode)

                }) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_select),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Select",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
            //pencil
            Box(
                modifier = Modifier
                    .background(if (currentDrawMode == DrawMode.Draw) Color.LightGray else Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(onClick = {
                    showPropertiesDialog = !showPropertiesDialog
                    currentDrawMode = DrawMode.Draw
                    onDrawModeChanged(currentDrawMode)

                }) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_pen),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Pencil",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
            //eraser
            Box(
                modifier = Modifier
                    .background(if (currentDrawMode == DrawMode.Erase) Color.LightGray else Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(
                    onClick = {
                        showEraserDialog = !showEraserDialog
                        currentDrawMode = DrawMode.Erase
                        onDrawModeChanged(currentDrawMode)
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_eraser),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Eraser",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
            //clear all
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(
                    onClick = {
                        onClearAll()
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_delete),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Clear",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
            //figure
            Box(
                modifier = Modifier
                    .background(
                        if (
                            currentDrawMode != DrawMode.Draw &&
                            currentDrawMode != DrawMode.Touch &&
                            currentDrawMode != DrawMode.Erase &&
                            currentDrawMode != DrawMode.Selection &&
                            currentDrawMode != DrawMode.MoveSelection
                        ) Color.LightGray else Color.White
                    )
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(onClick = { //drawmode and dialog
                    showFigureDialog = !showFigureDialog
                }) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_figure),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Figure",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
            //choose
            Box(
                modifier = Modifier
                    .background(if (showFeatureDialog) Color.LightGray else Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(onClick = { showFeatureDialog = !showFeatureDialog }) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_choose),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Choose",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
            //move
            Box(
                modifier = Modifier
                    .background(if (currentDrawMode == DrawMode.Touch) Color.LightGray else Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(
                    onClick = {
                        currentDrawMode = DrawMode.Touch
                        onDrawModeChanged(currentDrawMode)
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_move),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Move",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
            //theme
            Box(
                modifier = Modifier
                    .background(if (showBackgroundDialog) Color.LightGray else Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(onClick = { showBackgroundDialog = !showBackgroundDialog }) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_theme),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Theme",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
            //undo
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(onClick = {
                    onUndo()
                }) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_undo),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Undo",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
            //redo
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp))
                    .background(Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(onClick = {
                    onRedo()
                }) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_redo),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Redo",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }

        }

        Spacer(Modifier.width(20.dp))

        //right row
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 5.dp, bottomStart = 5.dp))
                    .background(Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(
                    onClick = {
                        /*TODO*/
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_add),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Add",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(
                    onClick = {
                        /*TODO*/
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_back),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Back",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(
                    onClick = {
                        /*TODO*/
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "1/2",
                            fontSize = 12.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                        Text(
                            text = "Page",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp))
                    .background(Color.White)
                    .padding(horizontal = 10.dp)
            ) {
                IconButton(
                    onClick = {
                        /*TODO*/
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon_forward),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Forward",
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        if (showEraserDialog) {
            EraserSelectionDialog(
                properties,
                onDismiss = { showEraserDialog = !showEraserDialog },
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
                    showFeatureDialog = !showFeatureDialog
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

}

@Composable
fun PropertiesMenuDialog(
    pathOption: PathProperties,
    onDismiss: () -> Unit,
    onOpenColorPicker: () -> Unit
) {
    var strokeWidth by remember { mutableStateOf(pathOption.strokeWidth) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() } // Dismiss dialog on background click
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-80).dp, x = (-90).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .width(520.dp)
                    .height(280.dp)
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.height(100.dp))
                Slider(
                    value = strokeWidth,
                    onValueChange = {
                        strokeWidth = it
                        pathOption.strokeWidth = strokeWidth
                    },
                    valueRange = 1f..100f,
                    onValueChangeFinished = {},
                    colors = SliderDefaults.colors(
                        thumbColor = Sea, // Change thumb color
                        activeTrackColor = Sea, // Change active track color
                        inactiveTrackColor = Color.Gray // Change inactive track color
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(alignment = Alignment.CenterHorizontally)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 65.dp, height = 40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PencilColor1)
                            .border(
                                BorderStroke(
                                    2.dp, color = Color(
                                        red = PencilColor1.red * 0.8f,
                                        green = PencilColor1.green * 0.8f,
                                        blue = PencilColor1.blue * 0.8f,
                                        alpha = PencilColor1.alpha
                                    )
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                pathOption.color = PencilColor1
                                onDismiss()
                            }
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 65.dp, height = 40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PencilColor2)
                            .border(
                                BorderStroke(
                                    2.dp, color = Color(
                                        red = PencilColor2.red * 0.8f,
                                        green = PencilColor2.green * 0.8f,
                                        blue = PencilColor2.blue * 0.8f,
                                        alpha = PencilColor2.alpha
                                    )
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                pathOption.color = PencilColor2
                                onDismiss()
                            }
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 65.dp, height = 40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PencilColor3)
                            .border(
                                BorderStroke(
                                    2.dp, color = Color(
                                        red = PencilColor3.red * 0.8f,
                                        green = PencilColor3.green * 0.8f,
                                        blue = PencilColor3.blue * 0.8f,
                                        alpha = PencilColor3.alpha
                                    )
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                pathOption.color = PencilColor3
                                onDismiss()
                            }
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 65.dp, height = 40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PencilColor4)
                            .border(
                                BorderStroke(
                                    2.dp, color = Color(
                                        red = PencilColor4.red * 0.8f,
                                        green = PencilColor4.green * 0.8f,
                                        blue = PencilColor4.blue * 0.8f,
                                        alpha = PencilColor4.alpha
                                    )
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                pathOption.color = PencilColor4
                                onDismiss()
                            }
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 65.dp, height = 40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PencilColor5)
                            .border(
                                BorderStroke(
                                    2.dp, color = Color(
                                        red = PencilColor5.red * 0.8f,
                                        green = PencilColor5.green * 0.8f,
                                        blue = PencilColor5.blue * 0.8f,
                                        alpha = PencilColor5.alpha
                                    )
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                pathOption.color = PencilColor5
                                onDismiss()
                            }
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 65.dp, height = 40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PencilColor6)
                            .border(
                                BorderStroke(
                                    2.dp, color = Color(
                                        red = PencilColor6.red * 0.8f,
                                        green = PencilColor6.green * 0.8f,
                                        blue = PencilColor6.blue * 0.8f,
                                        alpha = PencilColor6.alpha
                                    )
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                pathOption.color = PencilColor6
                                onDismiss()
                            }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 65.dp, height = 40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PencilColor7)
                            .border(
                                BorderStroke(
                                    2.dp, color = Color(
                                        red = PencilColor7.red * 0.8f,
                                        green = PencilColor7.green * 0.8f,
                                        blue = PencilColor7.blue * 0.8f,
                                        alpha = PencilColor7.alpha
                                    )
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                pathOption.color = PencilColor7
                                onDismiss()
                            }
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 65.dp, height = 40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PencilColor8)
                            .border(
                                BorderStroke(
                                    2.dp, color = Color(
                                        red = PencilColor8.red * 0.8f,
                                        green = PencilColor8.green * 0.8f,
                                        blue = PencilColor8.blue * 0.8f,
                                        alpha = PencilColor8.alpha
                                    )
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                pathOption.color = PencilColor8
                                onDismiss()
                            }
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 65.dp, height = 40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PencilColor9)
                            .border(
                                BorderStroke(
                                    2.dp, color = Color(
                                        red = PencilColor9.red * 0.8f,
                                        green = PencilColor9.green * 0.8f,
                                        blue = PencilColor9.blue * 0.8f,
                                        alpha = PencilColor9.alpha
                                    )
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                pathOption.color = PencilColor9
                                onDismiss()
                            }
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 65.dp, height = 40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PencilColor10)
                            .border(
                                BorderStroke(
                                    2.dp, color = Color(
                                        red = PencilColor10.red * 0.8f,
                                        green = PencilColor10.green * 0.8f,
                                        blue = PencilColor10.blue * 0.8f,
                                        alpha = PencilColor10.alpha
                                    )
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                pathOption.color = PencilColor10
                                onDismiss()
                            }
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 65.dp, height = 40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PencilColor11)
                            .border(
                                BorderStroke(
                                    2.dp, color = Color(
                                        red = PencilColor11.red * 0.8f,
                                        green = PencilColor11.green * 0.8f,
                                        blue = PencilColor11.blue * 0.8f,
                                        alpha = PencilColor11.alpha
                                    )
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                pathOption.color = PencilColor11
                                onDismiss()
                            }
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 65.dp, height = 40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(
                                BorderStroke(2.dp, Color.Gray),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                onOpenColorPicker()
                            }
                    )
                }

            }
            Canvas(
                modifier = Modifier
                    .size(width = 40.dp, height = 15.dp)
                    .offset(x = (-140).dp)
            ) {
                val path = Path()

                path.moveTo(0f, 0f)
                path.lineTo(20f, 15f)
                path.lineTo(40f, 0f)

                drawPath(
                    path = path,
                    color = Color.White,
                )
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
fun EraserSelectionDialog(
    pathOption: PathProperties,
    onDismiss: () -> Unit,
) {

    var strokeWidth by remember { mutableStateOf(pathOption.strokeWidth) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() } // Dismiss dialog on background click
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-80).dp, x = (-160).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .width(200.dp)
                    .background(Color.White)
            ) {
                Slider(
                    value = strokeWidth,
                    onValueChange = {
                        strokeWidth = it
                        pathOption.strokeWidth = strokeWidth
                    },
                    valueRange = 1f..100f,
                    onValueChangeFinished = {},
                    colors = SliderDefaults.colors(
                        thumbColor = Orange_Main, // Change thumb color
                        activeTrackColor = Orange_Main, // Change active track color
                        inactiveTrackColor = Color.Gray // Change inactive track color
                    )
                )

            }
            Canvas(
                modifier = Modifier.size(width = 40.dp, height = 15.dp)
            ) {
                val path = Path()

                path.moveTo(0f, 0f)
                path.lineTo(20f, 15f)
                path.lineTo(40f, 0f)

                drawPath(
                    path = path,
                    color = Color.White,
                )
            }

        }
    }
}

@Composable
fun FigureDialog(
    onDismiss: () -> Unit,
    onDrawModeChanged: (DrawMode) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() } // Dismiss dialog on background click
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-80).dp, x = (-40).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .width(340.dp)
                    .height(120.dp)
                    .background(Color.White)
            ) {

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

                Row {

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
            Canvas(
                modifier = Modifier
                    .size(width = 40.dp, height = 15.dp)
                    .offset(x = (10).dp)
            ) {
                val path = Path()

                path.moveTo(0f, 0f)
                path.lineTo(20f, 15f)
                path.lineTo(40f, 0f)

                drawPath(
                    path = path,
                    color = Color.White,
                )
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() } // Dismiss dialog on background click
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-80).dp, x = (30).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .width(180.dp)
                    .height(60.dp)
                    .background(Color.White)
            ) {

                Spacer(Modifier.height(10.dp))

                Row {
                    //Table dialog
                    IconButton(onClick = {
                        openTableDialog()

                    }) {
                        Icon(
                            painter = painterResource(R.drawable.choose_icon_table),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }

                    //Web Dialog
                    IconButton(onClick = {
                        onDismiss()
                        openWebDialog()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.choose_icon_search),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }

                    //Timer Dialog
                    IconButton(onClick = {
                        onDismiss()
                        openTimerDialog()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.choose_icon_timer),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }

                    //Image Dialog
                    IconButton(onClick = {
                        onDismiss()
                        launcher.launch("image/*")
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.choose_icon_image),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                }
            }

            Canvas(
                modifier = Modifier
                    .size(width = 40.dp, height = 15.dp)
                    .offset(x = (10).dp)
            ) {
                val path = Path()

                path.moveTo(0f, 0f)
                path.lineTo(20f, 15f)
                path.lineTo(40f, 0f)

                drawPath(
                    path = path,
                    color = Color.White,
                )
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() } // Dismiss dialog on background click
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-170).dp, x = (30).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .width(340.dp)
                    .height(300.dp)
                    .background(Color.White)
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

                Spacer(Modifier.height(10.dp))
                Column {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Table, row = $rowIntText, column = $columnIntText",
                            modifier = Modifier.padding(start = 20.dp),
                            color = Color.Black,
                            fontSize = 16.sp,
                        )

                        TextButton(
                            modifier = Modifier,
                            onClick = {
                                onPositiveClick(rowIntText, columnIntText)
                            },

                            ) {
                            Text(text = "OK")
                        }
                    }


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

                Spacer(Modifier.height(10.dp))

            }

            Canvas(
                modifier = Modifier
                    .size(width = 40.dp, height = 15.dp)
                    .offset(x = (-60).dp)
            ) {
                val path = Path()

                path.moveTo(0f, 0f)
                path.lineTo(20f, 15f)
                path.lineTo(40f, 0f)

                drawPath(
                    path = path,
                    color = Color.White,
                )
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

    val expandModeModifier = Modifier
        .fillMaxSize()
        .background(Color.Black)

    val commonModeModifier = Modifier
        .width(300.dp)
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
        modifier = if (expandMode) expandModeModifier
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
                .fillMaxSize()
                .then(
                    if (expandMode) {
                        Modifier
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    expandMode = !expandMode
                                }
                            }
                    } else Modifier
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

                                countdownTimer =
                                    object : CountDownTimer(totalMilliseconds, 1000) {
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

                                    countUpTimer =
                                        object : CountDownTimer(Long.MAX_VALUE, 1000) {
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

@Composable
fun BackgroundSelectionDialog(
    currentBackgroundColor: Color,
    onDismiss: () -> Unit,
    onNegativeClick: () -> Unit,
    onPositiveClick: (Color, Int) -> Unit,
) {

    var myColor = currentBackgroundColor

    var bgType = 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() } // Dismiss dialog on background click
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-80).dp, x = (80).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .width(520.dp)
                    .height(400.dp)
                    .background(Color.White)
            ) {

                Text(
                    text = "Theme",
                    color = Blue400,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 12.dp, start = 25.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))
                //Colors
                Column {
                    //Row 1 (1-6)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor1,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor1.red * 0.8f,
                                            green = ThemeColor1.green * 0.8f,
                                            blue = ThemeColor1.blue * 0.8f,
                                            alpha = ThemeColor1.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor1
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor2,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor2.red * 0.8f,
                                            green = ThemeColor2.green * 0.8f,
                                            blue = ThemeColor2.blue * 0.8f,
                                            alpha = ThemeColor2.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor2
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor3,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor3.red * 0.8f,
                                            green = ThemeColor3.green * 0.8f,
                                            blue = ThemeColor3.blue * 0.8f,
                                            alpha = ThemeColor3.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor3
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor4,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor4.red * 0.8f,
                                            green = ThemeColor4.green * 0.8f,
                                            blue = ThemeColor4.blue * 0.8f,
                                            alpha = ThemeColor4.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor4
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor5,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor5.red * 0.8f,
                                            green = ThemeColor5.green * 0.8f,
                                            blue = ThemeColor5.blue * 0.8f,
                                            alpha = ThemeColor5.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor5
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor6,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor6.red * 0.8f,
                                            green = ThemeColor6.green * 0.8f,
                                            blue = ThemeColor6.blue * 0.8f,
                                            alpha = ThemeColor6.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor6
                                }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    //Row 2 (7-12)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor7,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor7.red * 0.8f,
                                            green = ThemeColor7.green * 0.8f,
                                            blue = ThemeColor7.blue * 0.8f,
                                            alpha = ThemeColor7.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor7
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor8,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor8.red * 0.8f,
                                            green = ThemeColor8.green * 0.8f,
                                            blue = ThemeColor8.blue * 0.8f,
                                            alpha = ThemeColor8.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor8
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor9,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor9.red * 0.8f,
                                            green = ThemeColor9.green * 0.8f,
                                            blue = ThemeColor9.blue * 0.8f,
                                            alpha = ThemeColor9.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor9
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor10,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor10.red * 0.8f,
                                            green = ThemeColor10.green * 0.8f,
                                            blue = ThemeColor10.blue * 0.8f,
                                            alpha = ThemeColor10.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor10
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor11,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor11.red * 0.8f,
                                            green = ThemeColor11.green * 0.8f,
                                            blue = ThemeColor11.blue * 0.8f,
                                            alpha = ThemeColor11.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor11
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor12,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor12.red * 0.8f,
                                            green = ThemeColor12.green * 0.8f,
                                            blue = ThemeColor12.blue * 0.8f,
                                            alpha = ThemeColor12.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor12
                                }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    //Row 3 (13-18)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor13,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor13.red * 0.8f,
                                            green = ThemeColor13.green * 0.8f,
                                            blue = ThemeColor13.blue * 0.8f,
                                            alpha = ThemeColor13.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor13
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor14,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor14.red * 0.8f,
                                            green = ThemeColor14.green * 0.8f,
                                            blue = ThemeColor14.blue * 0.8f,
                                            alpha = ThemeColor14.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor14
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor15,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor15.red * 0.8f,
                                            green = ThemeColor15.green * 0.8f,
                                            blue = ThemeColor15.blue * 0.8f,
                                            alpha = ThemeColor15.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor15
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor16,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor16.red * 0.8f,
                                            green = ThemeColor16.green * 0.8f,
                                            blue = ThemeColor16.blue * 0.8f,
                                            alpha = ThemeColor16.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor16
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor17,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor17.red * 0.8f,
                                            green = ThemeColor17.green * 0.8f,
                                            blue = ThemeColor17.blue * 0.8f,
                                            alpha = ThemeColor17.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor17
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor18,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor18.red * 0.8f,
                                            green = ThemeColor18.green * 0.8f,
                                            blue = ThemeColor18.blue * 0.8f,
                                            alpha = ThemeColor18.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor18
                                }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    //Row 4 (19-24)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor19,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor19.red * 0.8f,
                                            green = ThemeColor19.green * 0.8f,
                                            blue = ThemeColor19.blue * 0.8f,
                                            alpha = ThemeColor19.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor19
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor20,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor20.red * 0.8f,
                                            green = ThemeColor20.green * 0.8f,
                                            blue = ThemeColor20.blue * 0.8f,
                                            alpha = ThemeColor20.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor20
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor21,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor21.red * 0.8f,
                                            green = ThemeColor21.green * 0.8f,
                                            blue = ThemeColor21.blue * 0.8f,
                                            alpha = ThemeColor21.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor21
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor22,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor22.red * 0.8f,
                                            green = ThemeColor22.green * 0.8f,
                                            blue = ThemeColor22.blue * 0.8f,
                                            alpha = ThemeColor22.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor22
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor23,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor23.red * 0.8f,
                                            green = ThemeColor23.green * 0.8f,
                                            blue = ThemeColor23.blue * 0.8f,
                                            alpha = ThemeColor23.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor23
                                }
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(30.dp)
                                .background(
                                    ThemeColor24,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .border(
                                    BorderStroke(
                                        2.dp, color = Color(
                                            red = ThemeColor24.red * 0.8f,
                                            green = ThemeColor24.green * 0.8f,
                                            blue = ThemeColor24.blue * 0.8f,
                                            alpha = ThemeColor24.alpha
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    myColor = ThemeColor24
                                }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))


                // Choose Background Type
                var firstTint by remember { mutableStateOf(Color.LightGray) }
                var secondTint by remember { mutableStateOf(Color.LightGray) }
                var thirdTint by remember { mutableStateOf(Color.LightGray) }
                //BG Lines
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    IconButton(
                        modifier = Modifier.size(width = 100.dp, height = 60.dp),
                        onClick = {
                            bgType = 0
                            firstTint = Color.Black
                            secondTint = Color.LightGray
                            thirdTint = Color.LightGray
                        }
                    ) {

                        Icon(
                            painter = painterResource(id = R.drawable.bg_line0),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )

                    }

                    IconButton(
                        modifier = Modifier.size(width = 100.dp, height = 60.dp),
                        onClick = {
                            bgType = 1
                            firstTint = Color.LightGray
                            secondTint = Color.Black
                            thirdTint = Color.LightGray
                        }
                    ) {

                        Icon(
                            painter = painterResource(id = R.drawable.bg_line1),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )

                    }

                    IconButton(
                        modifier = Modifier.size(width = 100.dp, height = 60.dp),
                        onClick = {
                            bgType = 2
                            firstTint = Color.LightGray
                            secondTint = Color.LightGray
                            thirdTint = Color.Black
                        }
                    ) {

                        Icon(
                            painter = painterResource(id = R.drawable.bg_line2),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )

                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(Color.White),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly

                ) {

                    TextButton(
                        onClick = onNegativeClick,
                        modifier = Modifier
                            .width(200.dp)
                            .fillMaxHeight()
                            .border(
                                BorderStroke(
                                    2.dp, color = Color(
                                        red = Green800.red * 0.6f,
                                        green = Green800.green * 0.6f,
                                        blue = Green800.blue * 0.6f,
                                        alpha = Green800.alpha
                                    )
                                ),
                                shape = RoundedCornerShape(5.dp)
                            )
                    ) {
                        Text(text = "CANCEL", color = Green800)
                    }
                    TextButton(
                        modifier = Modifier
                            .width(200.dp)
                            .fillMaxHeight()
                            .border(
                                BorderStroke(
                                    2.dp, color = Color(
                                        red = Green800.red * 0.6f,
                                        green = Green800.green * 0.6f,
                                        blue = Green800.blue * 0.6f,
                                        alpha = Green800.alpha
                                    )
                                ),
                                shape = RoundedCornerShape(5.dp)
                            ),
                        onClick = {
                            onPositiveClick(myColor, bgType)
                        },
                    ) {
                        Text(text = "OK", color = Green800)
                    }
                }
            }

            Canvas(
                modifier = Modifier
                    .size(width = 40.dp, height = 15.dp)
                    .offset(x = (100).dp)
            ) {
                val path = Path()

                path.moveTo(0f, 0f)
                path.lineTo(20f, 15f)
                path.lineTo(40f, 0f)

                drawPath(
                    path = path,
                    color = Color.White,
                )
            }
        }
    }

}

