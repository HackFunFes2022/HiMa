package `fun`.hackathon.hima.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.firebase.auth.FirebaseUser

@Composable
fun FirebaseSignInDialog(
    onDismissRequest: () -> Unit,
    user: MutableState<FirebaseUser?>,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            color = Color.White,
            border = BorderStroke(3.dp, Color.Black),
            shape = AbsoluteRoundedCornerShape(10)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "ログインする", color = Color.Black, modifier = Modifier.padding(10.dp))
                GoogleSignInButton(
                    modifier = Modifier.padding(
                        vertical = 30.dp,
                        horizontal = 30.dp
                    ), user = user
                )
            }
        }
    }
}