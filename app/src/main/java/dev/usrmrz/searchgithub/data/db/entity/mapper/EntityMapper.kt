package dev.usrmrz.searchgithub.data.db.entity.mapper

interface EntityMapper<Domain, Entity> {

    fun toEntity(domain: Domain): Entity

    fun toDomain(entity: Entity): Domain
}