package com.jjnex.musicbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MusicBotApplication

//classe pra rodar aplicacao principal erertere
fun main(args: Array<String>) {
    runApplication<MusicBotApplication>(*args)
}

