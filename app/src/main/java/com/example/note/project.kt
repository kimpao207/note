package com.example.note

class project {

    var id:Int = 0
    var name:String? = null
    var need_password: Int = 0
    var password: String? = null

    constructor()

    constructor(id:Int,name:String,need_password:Int,password:String){
        this.id = id
        this.name = name
        this.need_password = need_password
        this.password = password
    }
}