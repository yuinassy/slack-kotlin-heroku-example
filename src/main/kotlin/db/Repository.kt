package db

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object PostsTable : UUIDTable("posts") {
    val point = integer("point")
}

class PostDAO(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PostDAO>(PostsTable)

    val point by PostsTable.point
}

class Repository {
}
