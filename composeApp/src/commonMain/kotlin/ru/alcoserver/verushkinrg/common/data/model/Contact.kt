package ru.alcoserver.verushkinrg.common.data.model

data class Contact(
    val names: List<String>,
    val email: String,
    val notInContacts: Boolean = true,
    val notInBase: Boolean = false,
    val selected: Boolean = true,
    val blocked: Boolean = false
) {
    override fun toString(): String {
        return email
    }
}