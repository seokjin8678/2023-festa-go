package com.festago

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FestaGoApplication

fun main(args: Array<String>) {
    runApplication<FestaGoApplication>(*args)
}
