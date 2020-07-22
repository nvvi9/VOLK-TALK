package utils

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

typealias QuoteSizeObserver = ((currentSize: Int) -> Unit)
typealias MessageReceivedEvent = suspend (GuildMessageReceivedEvent) -> Unit