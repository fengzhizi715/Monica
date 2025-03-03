package cn.netdiscovery.monica.ui.controlpanel.generategif

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import com.madgag.gif.fmsware.AnimatedGifEncoder
import java.awt.FileDialog
import java.awt.Frame
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import javax.imageio.ImageIO

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.generategif.GenerateGifView
 * @author: Tony Shen
 * @date:  2025/2/23 16:16
 * @version: V1.0 <描述当前版本功能>
 */
private val map = mutableMapOf<Int, BufferedImage>()

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun generateGif(state: ApplicationState) {
    var selectedImages by remember { mutableStateOf<List<File>>(emptyList()) }
    var frameDelay by remember { mutableStateOf(500) }
    var loopEnabled by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Create GIF", style = MaterialTheme.typography.h4)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val imageFile = chooseImageFile()
            imageFile?.let {
                selectedImages = selectedImages + it
            }
        }) {
            Text("Add Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedImages.isNotEmpty()) {
            Text("Selected Images:")
            Box(modifier = Modifier.height(600.dp).fillMaxWidth()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(selectedImages) { index, imageFile ->
                        Card(modifier = Modifier.padding(10.dp), shape = RoundedCornerShape(8.dp), ) {
                            Column(modifier = Modifier.padding(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                val bitmap = remember(imageFile) { ImageIO.read(imageFile).toComposeImageBitmap() }
                                Image(painter = BitmapPainter(bitmap), contentDescription = imageFile.name, modifier = Modifier.size(100.dp))
                                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                    Button(onClick = {
                                        selectedImages = selectedImages.toMutableList().apply { removeAt(index) }
                                    }) {
                                        Text("Delete")
                                    }

                                    if (index > 0) {
                                        Button(onClick = {
                                            selectedImages = selectedImages.toMutableList().apply {
                                                add(index - 1, removeAt(index))
                                            }
                                        }) {
                                            Text("Up")
                                        }
                                    }

                                    if (index < selectedImages.size - 1) {
                                        Button(onClick = {
                                            selectedImages = selectedImages.toMutableList().apply {
                                                add(index + 1, removeAt(index))
                                            }
                                        }) {
                                            Text("Down")
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        } else {
            Text("No images added", modifier = Modifier.height(600.dp).fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Frame Delay (ms): $frameDelay")
        Slider(
            value = frameDelay.toFloat(),
            onValueChange = { frameDelay = it.toInt() },
            valueRange = 100f..2000f,
            steps = 19
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = loopEnabled, onCheckedChange = { loopEnabled = it })
            Text("Loop GIF")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            generateGif(selectedImages, frameDelay, loopEnabled)
        }) {
            Text("Generate GIF")
        }
    }
}

fun chooseImageFile(): File? {
    val fileDialog = FileDialog(Frame(), "Choose an image", FileDialog.LOAD)
    fileDialog.isVisible = true
    val fileName = fileDialog.file
    val directory = fileDialog.directory
    return if (fileName != null && directory != null) {
        File(directory, fileName)
    } else {
        null
    }
}

fun generateGif(images: List<File>, frameDelay: Int, loopEnabled: Boolean) {
    val gifEncoder = AnimatedGifEncoder()
    gifEncoder.setSize(900, 1000);
    gifEncoder.start(FileOutputStream("output.gif"))

    gifEncoder.setDelay(frameDelay)
    gifEncoder.setRepeat(if (loopEnabled) 0 else 1) // Set loop option

    images.forEach { imageFile ->
        val image = ImageIO.read(imageFile)
        gifEncoder.addFrame(image)
    }

    gifEncoder.finish()
}