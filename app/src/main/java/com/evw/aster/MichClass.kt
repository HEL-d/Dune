package com.evw.aster


class MichClass{
    private var username: String? = null
    private var lastmessage: String? = null
    private var roomid: String? = null

     constructor(){}

    constructor(username:String?,lastmessage:String?,roomid:String?){
        this.username = username
        this.lastmessage = lastmessage
        this.roomid = roomid
    }
}

