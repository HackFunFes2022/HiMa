package `fun`.hackathon.hima.ui.component

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun FirebaseScaffold(content: @Composable () -> Unit = {}) {
    val user = remember {
        mutableStateOf(Firebase.auth.currentUser)
    }
    Scaffold {
        content()
        when (user.value) {
            null ->
                FirebaseSignInDialog(onDismissRequest = { /*TODO*/ }, user = user)
        }
    }
}