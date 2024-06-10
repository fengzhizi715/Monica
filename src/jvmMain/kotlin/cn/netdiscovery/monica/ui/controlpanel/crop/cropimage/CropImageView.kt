package cn.netdiscovery.monica.ui.controlpanel.crop.cropimage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.config.KEY_CROP_FIRST
import cn.netdiscovery.monica.rxcache.rxCache
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.OutlineType
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.RectCropShape
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropDefaults
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropOutlineProperty
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropProperties
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropType
import cn.netdiscovery.monica.ui.widget.toolTipButton

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.CropImageView
 * @author: Tony Shen
 * @date: 2024/5/27 14:00
 * @version: V1.0 <描述当前版本功能>
 */
val cropTypes = mutableListOf(CropType.Dynamic, CropType.Static)
var cropTypesIndex = mutableStateOf(0)

val contentScales = listOf("None", "Fit", "Crop", "FillBounds", "FillWidth", "FillHeight", "Inside")
var contentScalesIndex = mutableStateOf(1)

@Composable
fun cropImage(state: ApplicationState) {
    val handleSize: Float = LocalDensity.current.run { 20.dp.toPx() }
    var croppedImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var crop by remember { mutableStateOf(false) }

    var showSettingDialog by remember { mutableStateOf(false) }
    var showCropDialog by remember { mutableStateOf(false) }
    var isCropping by remember { mutableStateOf(false) }

    var cropProperties by remember {
        mutableStateOf(
            CropDefaults.properties(
                cropOutlineProperty = CropOutlineProperty(
                    OutlineType.Rect,
                    RectCropShape(0, "Rect")
                ),
                handleSize = handleSize
            )
        )
    }
    var cropStyle by remember { mutableStateOf(CropDefaults.style()) }

//    val cropFrameFactory = remember {
//        CropFrameFactory(
//            listOf(
//                state.currentImage!!.toComposeImageBitmap()
//            )
//        )
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            ImageCropper(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                imageBitmap = state.currentImage!!.toComposeImageBitmap(),
                contentDescription = "Image Cropper",
                cropStyle = cropStyle,
                cropProperties = cropProperties,
                crop = crop,
                onCropStart = {
                    isCropping = true
                },
                onCropSuccess = {
                    croppedImage = it
                    isCropping = false
                    crop = false
                    showCropDialog = true
                }
            )
        }

        Row(modifier = Modifier.align(Alignment.CenterEnd)
            .padding(start =10.dp, end = 10.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(15))) {

            Column(
                Modifier.padding(start =10.dp, end = 10.dp, top = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {

                toolTipButton(text = "settings",
                    painter = painterResource("images/cropimage/settings.png"),
                    onClick = {
                        showSettingDialog = true
                    })

                toolTipButton(text = "crop",
                    painter = painterResource("images/cropimage/crop.png"),
                    onClick = {
                        crop = true
                    })
            }
        }
    }

    if (showSettingDialog) {
        showSettingDialog(
            cropProperties,
            onConfirm = {
                cropProperties = it
                showSettingDialog = false
            },
            onDismiss = {
                showSettingDialog = false
            }
        )
    }

    if (showCropDialog) {
        croppedImage?.let {
            showCroppedImageDialog(imageBitmap = it, onConfirm = {
                showCropDialog = !showCropDialog
                croppedImage = null

                cropFlag.set(false)
                rxCache.remove(KEY_CROP_FIRST)
                state.addQueue(state.currentImage!!)
                state.currentImage = it.toAwtImage()
                state.isCropSize = false
                state.togglePreviewWindow(false)
            }, onDismiss = {
                showCropDialog = !showCropDialog
                croppedImage = null
            })
        }
    }
}

@Composable
private fun showSettingDialog(cropProperties:CropProperties,
                              onConfirm: (cropProperties:CropProperties) -> Unit,
                              onDismiss: () -> Unit) {

    var tempProperties:CropProperties = cropProperties

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                title("Crop Type")

                var cropTypeExpanded by remember { mutableStateOf(false) }

                Column {
                    Button(modifier = Modifier.width(180.dp),
                        onClick = { cropTypeExpanded = true },
                        enabled = true){

                        Text(text = cropTypes[cropTypesIndex.value].name,
                            fontSize = 11.5.sp,
                            color = Color.LightGray)
                    }

                    DropdownMenu(expanded= cropTypeExpanded, onDismissRequest = {cropTypeExpanded =false}){
                        cropTypes.forEachIndexed{ index,label ->
                            DropdownMenuItem(onClick = {
                                cropTypesIndex.value = index

                                tempProperties = tempProperties.copy(cropType = cropTypes[cropTypesIndex.value])

                                cropTypeExpanded = false
                            }){
                                Text(text = label.name)
                            }
                        }
                    }
                }

                title("Content Scale")

                var contentScaleExpanded by remember { mutableStateOf(false) }

                Column {
                    Button(modifier = Modifier.width(180.dp),
                        onClick = { contentScaleExpanded = true },
                        enabled = true){

                        Text(text = contentScales[contentScalesIndex.value],
                            fontSize = 11.5.sp,
                            color = Color.LightGray)
                    }

                    DropdownMenu(expanded= contentScaleExpanded, onDismissRequest = {contentScaleExpanded =false}){
                        contentScales.forEachIndexed{ index,label ->
                            DropdownMenuItem(onClick = {
                                contentScalesIndex.value = index

                                val scale = when (index) {
                                    0 -> ContentScale.None
                                    1 -> ContentScale.Fit
                                    2 -> ContentScale.Crop
                                    3 -> ContentScale.FillBounds
                                    4 -> ContentScale.FillWidth
                                    5 -> ContentScale.FillHeight
                                    else -> ContentScale.Inside
                                }

                                tempProperties = tempProperties.copy(contentScale = scale)

                                contentScaleExpanded = false
                            }){
                                Text(text = label)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(tempProperties)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
internal fun title(
    text: String,
    fontSize: TextUnit = 20.sp
) {
    Text(
        modifier = Modifier.padding(vertical = 1.dp),
        text = text,
        color = MaterialTheme.colors.primary,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun showCroppedImageDialog(imageBitmap: ImageBitmap,
                                   onConfirm: () -> Unit,
                                   onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit,
                bitmap = imageBitmap,
                contentDescription = "result"
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}