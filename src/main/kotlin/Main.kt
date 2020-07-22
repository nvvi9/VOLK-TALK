import adapters.VolkListenerAdapter
import kotlinx.coroutines.FlowPreview
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import token.TOKEN
import worker.BackgroundWorker


@FlowPreview
@ExperimentalStdlibApi
fun main() {
    BackgroundWorker.initialize()
    JDABuilder.createDefault(TOKEN)
        .addEventListeners(VolkListenerAdapter())
        .setActivity(Activity.listening("басы"))
        .build()
}
