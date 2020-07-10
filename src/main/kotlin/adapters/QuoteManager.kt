package adapters

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import network.Service
import utils.quote

data class QuoteManager @JvmOverloads constructor(
    private val maxQuotes: Int = 1000,
    private val finalQuotes: Set<String> = setOf(
        "на случай если я буду нужен, то я там же, где и был, когда был не нужен",
        "если волк молчит - лучше его не перебивать"
    ),
    private val answerSet: Set<String> = setOf(
        "ну ты внатуре малолетний дебил",
        "у нас на севере за такое по лицу бьют",
        "ты как из палаты выбрался?"
    ),
    private val songsUri: Set<String> = setOf(
        "https://www.youtube.com/watch?v=Bz4J7zFlyi8",
        "https://www.youtube.com/watch?v=QRfn4w9iKmk"
    ),
    private val rejectDisconnectQuotes: Set<String> = setOf(
        "я может и не может но хотя бы не",
        "волк тот кто волк, а не тот кто"
    ),
    private val helpText: String = "*базарь -> базарю\n*уйди -> ухожу\n*подсоби -> подсоблю"
) {

    private val quotesList = mutableListOf<String?>()
    val quote: String?
        get() {
            val quoteElement = quotesList.filterNotNull().firstOrNull()
            quotesList.remove(quoteElement)
            updateQuotesList()
            return quoteElement
        }

    val finalQuote: String
        get() = finalQuotes.random()

    val answer: String
        get() = answerSet.random()

    val songUri: String
        get() = songsUri.random()

    val rejectDisconnectQuote: String
        get() = rejectDisconnectQuotes.random()

    val embedHelp: MessageEmbed
        get() = EmbedBuilder().setDescription(helpText).build()

    init {
        updateQuotesList()
    }

    private fun updateQuotesList() {
        CoroutineScope(Dispatchers.IO).launch {
            val startTime = System.currentTimeMillis()
            (1..(maxQuotes - quotesList.size)).map {
                Service.retrofitService.getQuoteAsync()
            }.forEach {
                try {
                    val quote = it.await().quote
                    quotesList.add(quote)
                    println("${quotesList.size}) -> ${(System.currentTimeMillis() - startTime) / 1e3} sec")
                } catch (t: Throwable) {
                    println(t.message)
                }
            }
            println("Total quotes: ${(System.currentTimeMillis() - startTime) / 1e3} sec")
        }
    }
}