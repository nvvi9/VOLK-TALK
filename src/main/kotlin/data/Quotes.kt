package data

private typealias QuoteSizeObserver = ((currentSize: Int) -> Unit)

object Quotes {

    const val helpText: String = "*мысль -> мыслю\n*уйди -> ухожу\n*подсоби -> подсоблю\n\nВопросы?"

    const val MAX_QUOTES_SIZE = 1000

    private val finalQuotes = setOf(
        "на случай, если я буду нужен, то я там же, где и был, когда был не нужен",
        "если волк молчит - лучше его не перебивать"
    )

    val leaveVoiceChannelMessage: String
        get() = finalQuotes.random()

    private val answerSet = setOf(
        "ты кто такой, чтобы это сделать?",
        "ты как из палаты выбрался?"
    )

    val wrongCommandAnswer: String
        get() = answerSet.random()

    private val songsUri = setOf(
        "https://www.youtube.com/watch?v=Bz4J7zFlyi8",
        "https://www.youtube.com/watch?v=QRfn4w9iKmk"
    )

    val songUri: String
        get() = songsUri.random()

    private val rejectDisconnectQuotes = setOf(
        "я может и не может, но хотя бы не",
        "волк тот кто волк, а не тот кто"
    )

    val rejectDisconnectionMessage: String
        get() = rejectDisconnectQuotes.random()

    private val imagesUri = setOf(
        "https://leonardo.osnova.io/3da573a0-99f6-d33d-53d2-e8ab238d9a91/-/resize/800/-/progressive/yes/",
        "https://pbs.twimg.com/media/EV5evr8WkAYilFg.jpg"
    )

    val imageUri: String
        get() = imagesUri.random()

    private val quotes = mutableSetOf<String>()

    @ExperimentalStdlibApi
    val quote: String?
        get() = quotes.randomOrNull()?.also {
            quotes.remove(it)
            quotesChangeObserver?.invoke(quotes.size)
        }

    private var quotesChangeObserver: QuoteSizeObserver? = null

    fun addQuote(quote: String) {
        if (quotes.size < MAX_QUOTES_SIZE) {
            quotes.add(quote)
        }
    }

    fun setQuoteObserver(observer: QuoteSizeObserver) {
        quotesChangeObserver = observer
        quotesChangeObserver?.invoke(quotes.size)
    }
}
