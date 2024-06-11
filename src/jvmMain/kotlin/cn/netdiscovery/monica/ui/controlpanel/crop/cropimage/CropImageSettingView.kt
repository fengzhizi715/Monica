package cn.netdiscovery.monica.ui.controlpanel.crop.cropimage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.setting.CropProperties

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

                    onCropPropertiesChange.invoke(cropProperties.copy(cropType = cropTypes[cropTypesIndex.value]))

                    cropTypeExpanded = false
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

                    onCropPropertiesChange.invoke(cropProperties.copy(contentScale = scale))

                    contentScaleExpanded = false
                }){
                    Text(text = label)
                }
            }
        }
    }
}

@Composable
fun aspectRatioSelect(cropProperties: CropProperties,
                      onCropPropertiesChange: OnCropPropertiesChange) {
    title("Aspect Ratio")

    var aspectRatioExpanded by remember { mutableStateOf(false) }

    Column {
        Button(modifier = Modifier.width(180.dp),
            onClick = { aspectRatioExpanded = true },
            enabled = true){

            Text(text = aspectRatios[aspectRatiosIndex.value],
                fontSize = 11.5.sp,
                color = Color.LightGray)
        }

        DropdownMenu(expanded= aspectRatioExpanded, onDismissRequest = {aspectRatioExpanded =false}){
            aspectRatios.forEachIndexed{ index,label ->
                DropdownMenuItem(onClick = {
                    aspectRatiosIndex.value = index

                    onCropPropertiesChange.invoke(cropProperties.copy(aspectRatio = cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.aspectRatios[index].aspectRatio))

                    aspectRatioExpanded = false
                }){
                    Text(text = label)
                }
            }
        }
    }
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