package `fun`.hackathon.hima.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ListSpacer(
    modifier: Modifier = Modifier,
    spacerColor: Color = if(isSystemInDarkTheme()) Color.LightGray else Color.DarkGray,
    isStraight: Boolean = true
) {
    if (isStraight) {
        Divider(
            color = spacerColor.copy(alpha = 0.5f),
            modifier = modifier,
            thickness = 1.dp
        )
    } else {
        Divider(
            color = spacerColor.copy(alpha = 0.5f),
            modifier = modifier,
            startIndent = 8.dp,
            thickness = 1.dp
        )
    }
}