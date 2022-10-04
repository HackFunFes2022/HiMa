package `fun`.hackathon.hima.ui.component

import `fun`.hackathon.hima.R
import `fun`.hackathon.hima.data.services.rememberFirebaseAuthLauncher
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser


@Composable
fun GoogleSignInButton(modifier: Modifier, user: MutableState<FirebaseUser?>) {
    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { result ->
            user.value = result.user
        },
        onAuthError = {
            user.value = null
        }
    )
    val token = stringResource(R.string.default_web_client_id)
    val context = LocalContext.current
    OutlinedButton(
        modifier = modifier,
        border = BorderStroke(1.dp, Color.Black),
        colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.White),
        onClick = {
            if (user.value == null) {
                val gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(token)
                        .requestEmail()
                        .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                launcher.launch(googleSignInClient.signInIntent)
            }
        },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.ic_google_icon), "")
            Text("Sign in")
        }
    }
}