package com.example.lonelyshop.data

data class User(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    var imagePath: String? = ""
)
