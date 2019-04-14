package pl.dybuk.health.core.service.chat.dto

import android.text.LoginFilter

data class UserDto(
    val id:Int,
    val name:String,
    val username:String,
    val address: AddressDto,
    val phone : String,
    val website : String,
    val company: CompanyDto
)
