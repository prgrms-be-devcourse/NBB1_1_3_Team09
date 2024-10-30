package com.grepp.nbe1_3_team9

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class Nbe13Team9Application

fun main(args: Array<String>) {
	runApplication<Nbe13Team9Application>(*args)
}
