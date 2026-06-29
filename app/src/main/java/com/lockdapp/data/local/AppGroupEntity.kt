package com.lockdapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lockdapp.domain.model.AppGroup

@Entity(tableName = "app_groups")
data class AppGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val packages: List<String>,  // TypeConverter → JSON
)

fun AppGroupEntity.toDomain() = AppGroup(id, name, packages)
fun AppGroup.toEntity()       = AppGroupEntity(id, name, packages)
