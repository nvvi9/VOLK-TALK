package data

import utils.QuoteSizeObserver

object Quotes {

    const val helpText: String = "*базарь -> базарю\n*уйди -> ухожу\n*подсоби -> подсоблю"

    const val MAX_QUOTES_SIZE = 1000

    private val finalQuotes: Set<String> = setOf(
        "на случай если я буду нужен, то я там же, где и был, когда был не нужен",
        "если волк молчит - лучше его не перебивать"
    )

    val leaveVoiceChannelMessage: String
        get() = finalQuotes.random()

    private val answerSet: Set<String> = setOf(
        "ну ты внатуре малолетний дебил",
        "у нас на севере за такое по лицу бьют",
        "ты как из палаты выбрался?"
    )

    val wrongCommandAnswer: String
        get() = answerSet.random()

    private val songsUri: Set<String> = setOf(
        "https://www.youtube.com/watch?v=Bz4J7zFlyi8",
        "https://www.youtube.com/watch?v=QRfn4w9iKmk"
    )

    val songUri: String
        get() = songsUri.random()

    private val rejectDisconnectQuotes: Set<String> = setOf(
        "я может и не может но хотя бы не",
        "волк тот кто волк, а не тот кто"
    )

    val rejectDisconnectionMessage: String
        get() = rejectDisconnectQuotes.random()

    private val quotes = mutableSetOf<String>()

    @ExperimentalStdlibApi
    val quote: String?
        get() =
            quotes.randomOrNull()?.also {
                quotes.remove(it)
                quotesChangeObserver?.invoke(quotes.size)
            }


    private var quotesChangeObserver: QuoteSizeObserver? = null

    fun addQuote(quote: String) {
        synchronized(quotes) {
            if (quotes.size < MAX_QUOTES_SIZE) {
                quotes.add(quote)
            }
        }
    }

    fun setQuoteObserver(observer: QuoteSizeObserver) {
        quotesChangeObserver = observer
        quotesChangeObserver?.invoke(quotes.size)
    }
}