package `fun`.hackathon.hima.ui.pages

sealed class NavItem(
    val name: String
) {
    object MainScreen : NavItem("main")
    object InputScreen : NavItem("input")
    object DetailScreen : NavItem("detail")
}