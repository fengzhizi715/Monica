package cn.netdiscovery.monica.ui.controlpanel.crop.cropimage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropFrameFactory
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropOutlineProperty
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropProperties
import kotlinx.coroutines.launch

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.CropImageSettingView
 * @author: Tony Shen
 * @date:  2024/6/10 21:32
 * @version: V1.0 <描述当前版本功能>
 */

@Composable
fun cropTypeSelect(cropProperties: CropProperties,
                   onCropPropertiesChange: OnCropPropertiesChange) {
    subTitle("Crop Type")

    var expanded by remember { mutableStateOf(false) }

    Column {
        Button(modifier = Modifier.width(180.dp),
            onClick = { expanded = true },
            enabled = true){

            Text(text = cropTypes[cropTypesIndex.value].name,
                fontSize = 22.sp,
                color = Color.LightGray)
        }

        DropdownMenu(expanded= expanded, onDismissRequest = {expanded =false}){
            cropTypes.forEachIndexed{ index,label ->
                DropdownMenuItem(onClick = {
                    cropTypesIndex.value = index

                    onCropPropertiesChange.invoke(cropProperties.copy(cropType = cropTypes[cropTypesIndex.value]))

                    expanded = false
                }){
                    Text(text = label.name)
                }
            }
        }
    }
}

@Composable
fun contentScaleSelect(cropProperties: CropProperties,
                       onCropPropertiesChange: OnCropPropertiesChange) {
    subTitle("Content Scale")

    var expanded by remember { mutableStateOf(false) }

    Column {
        Button(modifier = Modifier.width(180.dp),
            onClick = { expanded = true },
            enabled = true){

            Text(text = contentScales[contentScalesIndex.value],
                fontSize = 22.sp,
                color = Color.LightGray)
        }

        DropdownMenu(expanded= expanded, onDismissRequest = {expanded =false}){
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

                    onCropPropertiesChange.invoke(cropProperties.copy(contentScale = scale))

                    expanded = false
                }){
                    Text(text = label)
                }
            }
        }
    }
}


@Composable
fun aspectRatioScrollableRow(cropProperties: CropProperties,
                             onCropPropertiesChange: OnCropPropertiesChange) {

    var selectRadio  = remember { mutableStateOf("Original") }

    subTitle("Aspect Ratio (${selectRadio.value})")

    desktopLazyRow {
        Card(
            elevation = 16.dp,
            modifier = Modifier.padding(start = 5.dp, top = 16.dp,end = 16.dp,bottom = 16.dp).clickable{
                selectRadio.value = "Original"
                onCropPropertiesChange.invoke(cropProperties.copy(aspectRatio = cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.aspectRatios[0].aspectRatio))
            }
        ) {
            Text(
                text = "Original",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }

        Card(
            elevation = 16.dp,
            modifier = Modifier.padding(16.dp).clickable {
                selectRadio.value = "9:16"
                onCropPropertiesChange.invoke(cropProperties.copy(aspectRatio = cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.aspectRatios[1].aspectRatio))
            }
        ) {
            Text(
                text = "9:16",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }

        Card(
            elevation = 16.dp,
            modifier = Modifier.padding(16.dp).clickable {
                selectRadio.value = "2:3"
                onCropPropertiesChange.invoke(cropProperties.copy(aspectRatio = cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.aspectRatios[2].aspectRatio))
            }
        ) {
            Text(
                text = "2:3",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }

        Card(
            elevation = 16.dp,
            modifier = Modifier.padding(16.dp).clickable {
                selectRadio.value = "1:1"
                onCropPropertiesChange.invoke(cropProperties.copy(aspectRatio = cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.aspectRatios[3].aspectRatio))
            }
        ) {
            Text(
                text = "1:1",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }

        Card(
            elevation = 16.dp,
            modifier = Modifier.padding(16.dp).clickable {
                selectRadio.value = "16:9"
                onCropPropertiesChange.invoke(cropProperties.copy(aspectRatio = cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.aspectRatios[4].aspectRatio))
            }
        ) {
            Text(
                text = "16:9",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }

        Card(
            elevation = 16.dp,
            modifier = Modifier.padding(16.dp).clickable {
                selectRadio.value = "1.91:1"
                onCropPropertiesChange.invoke(cropProperties.copy(aspectRatio = cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.aspectRatios[5].aspectRatio))
            }
        ) {
            Text(
                text = "1.91:1",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }

        Card(
            elevation = 16.dp,
            modifier = Modifier.padding(16.dp).clickable {
                selectRadio.value = "3:2"
                onCropPropertiesChange.invoke(cropProperties.copy(aspectRatio = cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.aspectRatios[6].aspectRatio))
            }
        ) {
            Text(
                text = "3:2",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }

        Card(
            elevation = 16.dp,
            modifier = Modifier.padding(16.dp).clickable {
                selectRadio.value = "3:4"
                onCropPropertiesChange.invoke(cropProperties.copy(aspectRatio = cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.aspectRatios[7].aspectRatio))
            }
        ) {
            Text(
                text = "3:4",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }

        Card(
            elevation = 16.dp,
            modifier = Modifier.padding(16.dp).clickable {
                selectRadio.value = "3:5"
                onCropPropertiesChange.invoke(cropProperties.copy(aspectRatio = cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.aspectRatios[8].aspectRatio))
            }
        ) {
            Text(
                text = "3:5",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun cropFrameScrollableRow(cropProperties: CropProperties, cropFrameFactory: CropFrameFactory,
                           onCropPropertiesChange: OnCropPropertiesChange) {

    var selectCropFrame  = remember { mutableStateOf("Rect") }

    val cropFrames = cropFrameFactory.getCropFrames()

    subTitle("Crop Frame (${selectCropFrame.value})")

    desktopLazyRow {
        Card(
            elevation = 16.dp,
            modifier = Modifier.padding(start = 5.dp, top = 16.dp, end = 16.dp, bottom = 16.dp).clickable {
                selectCropFrame.value = "Rect"
                val cropFrame = cropFrames[0]
                val cropOutlineProperty =
                    CropOutlineProperty(cropFrame.outlineType, cropFrame.cropOutlineContainer.selectedItem)
                onCropPropertiesChange.invoke(cropProperties.copy(cropOutlineProperty = cropOutlineProperty))
            }
        ) {
            Text(
                text = "Rect",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }

        Card(
            elevation = 16.dp,
            modifier = Modifier.padding(16.dp).clickable {
                selectCropFrame.value = "RoundedRect"
                val cropFrame = cropFrames[1]
                val cropOutlineProperty =
                    CropOutlineProperty(cropFrame.outlineType, cropFrame.cropOutlineContainer.selectedItem)
                onCropPropertiesChange.invoke(cropProperties.copy(cropOutlineProperty = cropOutlineProperty))
            }
        ) {
            Text(
                text = "RoundedRect",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }

        Card(
            elevation = 16.dp,
            modifier = Modifier.padding(16.dp).clickable {
                selectCropFrame.value = "CutCorner"
                val cropFrame = cropFrames[2]
                val cropOutlineProperty =
                    CropOutlineProperty(cropFrame.outlineType, cropFrame.cropOutlineContainer.selectedItem)
                onCropPropertiesChange.invoke(cropProperties.copy(cropOutlineProperty = cropOutlineProperty))
            }
        ) {
            Text(
                text = "CutCorner",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }

        Card(
            elevation = 16.dp,
            modifier = Modifier.padding(16.dp).clickable {
                selectCropFrame.value = "Oval"
                val cropFrame = cropFrames[3]
                val cropOutlineProperty =
                    CropOutlineProperty(cropFrame.outlineType, cropFrame.cropOutlineContainer.selectedItem)
                onCropPropertiesChange.invoke(cropProperties.copy(cropOutlineProperty = cropOutlineProperty))
            }
        ) {
            Text(
                text = "Oval",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }

        Card(
            elevation = 16.dp,
            modifier = Modifier.padding(16.dp).clickable {
                selectCropFrame.value = "Polygon"
                val cropFrame = cropFrames[4]
                val cropOutlineProperty =
                    CropOutlineProperty(cropFrame.outlineType, cropFrame.cropOutlineContainer.selectedItem)
                onCropPropertiesChange.invoke(cropProperties.copy(cropOutlineProperty = cropOutlineProperty))
            }
        ) {
            Text(
                text = "Polygon",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }

        Card(
            elevation = 16.dp,
            modifier = Modifier.padding(16.dp).clickable {
                selectCropFrame.value = "Custom"
                val cropFrame = cropFrames[5]
                val cropOutlineProperty =
                    CropOutlineProperty(cropFrame.outlineType, cropFrame.cropOutlineContainer.selectedItem)
                onCropPropertiesChange.invoke(cropProperties.copy(cropOutlineProperty = cropOutlineProperty))
            }
        ) {
            Text(
                text = "Custom",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }
    }
}


@Composable
internal fun subTitle(
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
internal fun desktopLazyRow(content: @Composable () -> Unit) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyRow(
        state = scrollState,
        modifier = Modifier
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    coroutineScope.launch {
                        scrollState.scrollBy(-delta)
                    }
                },
            )
    ) {
        item {
            content.invoke()
        }
    }
}