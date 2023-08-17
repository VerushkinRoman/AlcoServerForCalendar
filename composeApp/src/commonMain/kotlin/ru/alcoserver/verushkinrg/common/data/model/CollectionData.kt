package ru.alcoserver.verushkinrg.common.data.model

import com.google.cloud.firestore.CollectionReference
import ru.alcoserver.verushkinrg.common.data.model.Day

data class CollectionData(
    val id: String,
    val collectionReference: CollectionReference,
    val days: List<Day>
)
