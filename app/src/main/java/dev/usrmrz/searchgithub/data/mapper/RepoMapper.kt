package dev.usrmrz.searchgithub.data.mapper

import com.google.gson.Gson
import dev.usrmrz.searchgithub.data.entities.OwnerEntity
import dev.usrmrz.searchgithub.data.entities.RepoEntity
import dev.usrmrz.searchgithub.domain.model.OwnerModel
import dev.usrmrz.searchgithub.domain.model.RepoModel
import javax.inject.Inject

//@Suppress("unused")
class RepoMapper @Inject constructor(private val gson: Gson) {
    fun mapToModel(entity: RepoEntity): RepoModel {
        val formattedDate = gson.toJson(entity.updatedAt)
        return RepoModel(
            id = entity.id,
            name = entity.name,
            fullName = entity.fullName,
            owner = entity.owner.toDomain(),
            description = entity.description,
            updatedAt = formattedDate,
            stars = entity.stars,
        )
    }

    private fun OwnerEntity.toDomain(): OwnerModel {
        return OwnerModel(
            login = this.login,
            url = this.url
        )
    }

    fun mapToEntity(model: RepoModel): RepoEntity {
        val formattedDate = gson.toJson(model.updatedAt)
        return RepoEntity(
            id = model.id,
            name = model.name,
            fullName = model.fullName,
            owner = model.owner.toEntity(),
            description = model.description.toString(),
            updatedAt = formattedDate,
            stars = model.stars,
        )
    }

    private fun OwnerModel.toEntity(): OwnerEntity {
        return OwnerEntity(
            login = this.login,
            url = this.url
        )
    }
}
