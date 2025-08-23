package com.ajay.seenu.expensetracker.data.mapper

import com.ajay.seenu.expensetracker.CategoryEntity
import com.ajay.seenu.expensetracker.domain.model.Category

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        type = type.toDomain(),
        label = label,
        color = color,
        iconRes = iconRes.toInt()
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        type = type.toEntity(),
        label = label,
        iconRes = iconRes.toLong(),
        color = color
    )
}