package com.android.PetPamper

import java.time.LocalDate
import java.util.Date

sealed class Pet(
    protected open var name: String = "unnamed",
    protected open var birthDate: LocalDate = LocalDate.of(0,0,1),
    protected open var description: String = "",
    protected open var pictures: List<String> = listOf(""),) {

}
/*
* var name: String = "unnamed",
               var birthDate: LocalDate = LocalDate.of(0,0,1),
               var description: String = "",
               var pictures: List<String> = listOf(""),
* */
class Dog(override var name: String,
          override var birthDate: LocalDate,
          override var description: String,
          override var pictures: List<String>) : Pet() {

}