package ru.alcoserver.verushkinrg.common.data.model

import kotlin.random.Random

data class Friend(
    val name: String,
    val email: String,
    val position: Int = Random.nextInt(999, Int.MAX_VALUE),
    val selected: Boolean = false,
    val blocked: Boolean = false
) {
    override fun toString(): String {
        return email
    }
}