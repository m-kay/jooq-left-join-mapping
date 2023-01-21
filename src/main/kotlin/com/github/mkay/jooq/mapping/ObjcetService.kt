package com.github.mkay.jooq.mapping

import com.github.mkay.jooq.tables.references.METADATA
import com.github.mkay.jooq.tables.references.OBJECTS
import kotlinx.coroutines.reactive.awaitSingle
import org.jooq.DSLContext
import org.jooq.impl.DSL.row
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ObjcetService(private val jooqDsl: DSLContext) {

    suspend fun insertObjects(objects: List<ObjectDto>) {
        objects.forEach {
            jooqDsl.insertInto(OBJECTS, OBJECTS.OBJECT_ID, OBJECTS.NAME)
                .values(it.objectId, it.name)
                .awaitSingle()
            val metadata = it.metadata
            if(metadata != null){
                jooqDsl.insertInto(METADATA, METADATA.METADATA_ID, METADATA.OBJECT_ID, METADATA.DESCRIPTION, METADATA.COMMENT)
                    .values(metadata.metadataId, it.objectId, metadata.description, metadata.comment)
                    .awaitSingle()
            }
        }
    }

    suspend fun readObjects(objectIds: List<String>): List<ObjectDto> {
        return Flux.from(
            jooqDsl
                .select(
                    OBJECTS.OBJECT_ID,
                    OBJECTS.NAME,
                    row(
                        METADATA.METADATA_ID,
                        METADATA.DESCRIPTION,
                        METADATA.COMMENT,
                    ).mapping { id, desc, comment -> if(id != null) MetadataDto(id, desc!!, comment) else null}.`as`("METADATA")
                )
                .from(
                    OBJECTS.leftJoin(METADATA)
                        .on(OBJECTS.OBJECT_ID.eq(METADATA.OBJECT_ID))
                )
                .where(OBJECTS.OBJECT_ID.`in`(objectIds))
        )
            .map {
                it.into(ObjectDto::class.java)
            }
            .collectList()
            .awaitSingle()
    }
}

data class ObjectDto(
    val objectId: String,
    val name: String,
    val metadata: MetadataDto? = null
)

data class MetadataDto(
    val metadataId: String,
    val description: String,
    val comment: String? = null
)