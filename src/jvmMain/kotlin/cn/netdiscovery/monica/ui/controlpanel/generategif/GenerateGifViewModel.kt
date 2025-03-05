package cn.netdiscovery.monica.ui.controlpanel.generategif

import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.currentTime
import cn.netdiscovery.monica.utils.extension.launchWithLoading
import com.madgag.gif.fmsware.AnimatedGifEncoder
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import javax.imageio.ImageIO

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.generategif.GenerateGifViewModel
 * @author: Tony Shen
 * @date: 2025/3/4 14:06
 * @version: V1.0 <描述当前版本功能>
 */
class GenerateGifViewModel {

    private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

    fun generateGif(state: ApplicationState,images: List<File>, width: Int, height: Int, frameDelay: Int, loopEnabled: Boolean) {
        logger.info("start to generate gif")

        state.scope.launchWithLoading {
            val gifEncoder = AnimatedGifEncoder()
            gifEncoder.setSize(width, height)
            gifEncoder.start(FileOutputStream("output_${currentTime()}.gif"))

            gifEncoder.setDelay(frameDelay)
            gifEncoder.setRepeat(if (loopEnabled) 0 else 1) // Set loop option

            images.forEach { imageFile ->
                val image = ImageIO.read(imageFile)
                gifEncoder.addFrame(image)
            }

            gifEncoder.finish()
            logger.info("gif generated successfully!")
        }
    }
}