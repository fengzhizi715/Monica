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
import androidx.compose.ui.unit.dp
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

val aspectRatios = listOf("9:16", "2:3", "Original", "1:1", "16:9", "1.91:1", "3:2", "3:4", "3:5")
var aspectRatiosIndex = mutableStateOf(2)

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
        showCroppedImageSettingDialog(
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

                cropTypesIndex.value = 0
                contentScalesIndex.value = 1
                aspectRatiosIndex.value = 2

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
private fun showCroppedImageSettingDialog(cropProperties:CropProperties,
                                          onConfirm: (cropProperties:CropProperties) -> Unit,
                                          onDismiss: () -> Unit) {

    var tempProperties:CropProperties = cropProperties

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                cropTypeSelect(tempProperties) {
                    tempProperties = it
                }

                contentScaleSelect(tempProperties) {
                    tempProperties = it
                }

                aspectRatioSelect(tempProperties) {
                    tempProperties = it
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