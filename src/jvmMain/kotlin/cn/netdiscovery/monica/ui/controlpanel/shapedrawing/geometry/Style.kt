package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.shapedrawing.geometry.Style
 * @author: Tony Shen
 * @date: 2024/11/20 11:45
 * @version: V1.0 <描述当前版本功能>
 */
import androidx.compose.ui.graphics.Color

enum class Border(val effect: FloatArray?) {
    No(null),
    Dot(floatArrayOf(5f, 5f)),
    Dash(floatArrayOf(25f, 25f)),
    DashDot(floatArrayOf(25f, 10f, 5f, 10f)),
    Line(null);
}

enum class EqualityGroup {
    Equal1,
    Equal2,
    Equal3,
    EqualV,
    EqualO,
}

fun spans(text: String) = buildList {
    var last = 0
    while (true) {
        var next = text.indexOf('_', last)
        if (next == text.length - 1 || next == -1) next = text.length
        add(text.substring(last, next))
        if (next == text.length) break
        if (text[next + 1] == '{') {
            last = text.indexOf('}', next + 1)
            if (last == 0) error("Expected '}'")
            add(text.substring(next + 2, last))
            last++
        } else {
            add(text[next + 1].toString())
            last = next + 2
        }
    }
}

data class Style(var name: List<String>?, var color: Color, var border: Border, var equalityGroup: EqualityGroup?, var fill: Boolean, var scale: Float, var alpha: Float, var bounded: Boolean)