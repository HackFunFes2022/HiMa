package `fun`.hackathon.hima.ui.pages

import `fun`.hackathon.hima.Greeting
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable

@Composable
fun DetailScreen(id: String) {
    Scaffold {
        Greeting(name = id)
    }
}