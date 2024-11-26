package cn.netdiscovery.monica.ui.widget.properties

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.widget.properties.ExposedSelectionMenu
 * @author: Tony Shen
 * @date: 2024/11/26 13:08
 * @version: V1.0 <描述当前版本功能>
 */

/**
 * Expandable selection menu
 * @param title of the displayed item on top
 * @param index index of selected item
 * @param options list of [String] options
 * @param onSelected lambda to be invoked when an item is selected that returns
 * its index.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExposedSelectionMenu(
    title: String,
    index: Int,
    options: List<String>,
    onSelected: (Int) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[index]) }
    var selectedIndex = remember { index }

    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { },
            label = { Text(title) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )
        )
        ExposedDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = {
                expanded = false

            }
        ) {
            options.forEachIndexed { index: Int, selectionOption: String ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                        selectedIndex = index
                        onSelected(selectedIndex)
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}