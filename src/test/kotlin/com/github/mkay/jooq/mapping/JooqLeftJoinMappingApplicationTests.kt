package com.github.mkay.jooq.mapping

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest
class JooqLeftJoinMappingApplicationTests {

    @Autowired
    private lateinit var objectService: ObjcetService

    companion object {
        @Container
        val mysql = MySQLContainer<Nothing>("mysql:8").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
            waitingFor(HostPortWaitStrategy())
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") { "r2dbc:mysql://${mysql.host}:${mysql.getMappedPort(MySQLContainer.MYSQL_PORT)}/${mysql.databaseName}" }
            registry.add("spring.r2dbc.username") { mysql.username }
            registry.add("spring.r2dbc.password") { mysql.password }
            registry.add("spring.flyway.url") { mysql.jdbcUrl }
            registry.add("spring.flyway.user") { mysql.username }
            registry.add("spring.flyway.password") { mysql.password }
        }
    }

    @Test
    fun `metadata is mapped on left-join`() {
        runBlocking {
            objectService.insertObjects(
                listOf(
                    ObjectDto("obj-1", "Object one",
                        MetadataDto("meta-1", "This is metadata for obj-1", "Some comment")
                    )
                )
            )
            objectService.insertObjects(
                listOf(
                    ObjectDto("obj-2", "Object two")
                )
            )

            val objects = objectService.readObjects(listOf("obj-1", "obj-2"))

            assertThat(objects).hasSize(2)
            assertThat(objects[0].objectId).isEqualTo("obj-1")
            assertThat(objects[0].metadata).isNotNull //should be mapped but is null
            assertThat(objects[1].objectId).isEqualTo("obj-2")
            assertThat(objects[1].metadata).isNotNull//should be null because there is no metadata
        }

    }

}
