package com.evw.aster

import com.google.firebase.firestore.ServerTimestamp


import java.util.*

data class Notifyclass(val username:String? = null, val uid:String? = null,
                       val name:String? = null, val lptext:String? = null, val requestText:String? = null, var isread:String? = null,
                       @ServerTimestamp var timestamp: Date? = null,val profilepic:String? = null)
