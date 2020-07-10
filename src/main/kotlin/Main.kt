import adapters.VolkListenerAdapter
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import token.TOKEN

fun main() {
    JDABuilder.createDefault(TOKEN)
        .addEventListeners(VolkListenerAdapter())
        .setActivity(Activity.listening("басы"))
        .build()
}
