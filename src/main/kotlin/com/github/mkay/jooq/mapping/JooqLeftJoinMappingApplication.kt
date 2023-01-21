package com.github.mkay.jooq.mapping

import io.r2dbc.spi.ConnectionFactory
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class JooqLeftJoinMappingApplication {

    @Bean
    fun jooqConfiguration(connectionFactory: ConnectionFactory): DSLContext {
        return DSL.using(connectionFactory)
    }
}

fun main(args: Array<String>) {
    runApplication<JooqLeftJoinMappingApplication>(*args)
}
