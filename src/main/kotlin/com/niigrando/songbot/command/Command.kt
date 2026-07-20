package com.niigrando.songbot.command

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface Command {
    val name: String
    val description: String
    val usage: String

    fun execute(event: MessageReceivedEvent, args: List<String>)
}

