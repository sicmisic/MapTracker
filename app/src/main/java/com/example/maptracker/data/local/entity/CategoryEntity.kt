package com.example.maptracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.maptracker.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorHex: String,
)

fun CategoryEntity.toDomain() = Category(id = id, name = name, colorHex = colorHex)
fun Category.toEntity() = CategoryEntity(id = id, name = name, colorHex = colorHex)
