package ru.alcoserver.verushkinrg.common.data.model

data class FirebaseUser(
    val email: String = "",
    val nickname: String = "",
    val locale: String = "En",
    val token: String? = null
)

fun FirebaseUser.toUser(): User? {
    return token?.let {
        User(
            email = email,
            nickName = nickname,
            id = it
        )
    }
}