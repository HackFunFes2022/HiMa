package `fun`.hackathon.hima


import `fun`.hackathon.hima.ui.component.FirebaseScaffold
import `fun`.hackathon.hima.ui.pages.DetailScreen
import `fun`.hackathon.hima.ui.pages.InputScreen
import `fun`.hackathon.hima.ui.pages.MainScreen
import `fun`.hackathon.hima.ui.pages.NavItem
import `fun`.hackathon.hima.ui.theme.HiMaTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No Current NavController")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            HiMaTheme {
                val navController = rememberNavController()
                FirebaseScaffold {
                    CompositionLocalProvider(
                        LocalNavController provides navController
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = NavItem.MainScreen.name
                        ) {
                            composable(NavItem.MainScreen.name) {
                                MainScreen()
                            }
                            composable(NavItem.InputScreen.name) {
                                InputScreen()
                            }
                            composable(NavItem.DetailScreen.name + "/{id}") {
                                val id = it.arguments?.getString("id")
                                println(id)
                                if (id == null) {
                                    DetailScreen(id = "null")
                                } else {
                                    DetailScreen(id = id)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HiMaTheme {
        Greeting("Android")
    }
}