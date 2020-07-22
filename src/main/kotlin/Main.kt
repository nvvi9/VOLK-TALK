import adapters.VolkListenerAdapter
import kotlinx.coroutines.FlowPreview
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import org.apache.log4j.BasicConfigurator
import token.TOKEN
import worker.BackgroundWorker


@FlowPreview
@ExperimentalStdlibApi
fun main() {
    BasicConfigurator.configure()
    BackgroundWorker.initialize()

    JDABuilder.createDefault(TOKEN)
        .addEventListeners(VolkListenerAdapter())
        .setActivity(Activity.listening("басы"))
        .build()
}
