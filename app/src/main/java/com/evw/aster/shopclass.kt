package com.evw.aster

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class shopclass(@ServerTimestamp var timestamp: Date? = null,var link:String? = null) {

}