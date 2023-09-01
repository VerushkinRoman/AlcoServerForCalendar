import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ru.alcoserver.verushkinrg.common.core.PlatformSDK
import ru.alcoserver.verushkinrg.notificationService.compose.ServiceScreen

fun main() = application {
    PlatformSDK.init()

    Window(
        title = "AlcoServer",
        state = rememberWindowState(width = Dp.Unspecified, height = Dp.Unspecified),
        resizable = false,
        onCloseRequest = ::exitApplication,
    ) {
        ServiceScreen(
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .width(IntrinsicSize.Max)
        )
    }
}
