package com.evw.aster

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class FriendListClass(val username:String? = null, val uid:String? = null,
                           val roomid:String? = null,
                           val name:String? = null,@ServerTimestamp var timestamp: Date? = null)
