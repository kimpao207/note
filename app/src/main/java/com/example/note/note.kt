package com.example.note

class note {

    var id:Int = 0
    var note_id: Int = 0
    var title:String? = null
    var need_notice: Int = 0
    var notice_date: String? = null
    var notice_time: String? = null
    var content: String? = null

    constructor()

    constructor(id:Int,note_id:Int,title:String,need_notice:Int,notice_date:String,notice_time:String,content:String){
        this.id = id
        this.note_id = note_id
        this.title = title
        this.need_notice = need_notice
        this.notice_date = notice_date
        this.notice_time = notice_time
        this.content = content
    }
}