package com.evw.aster

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class CollectionClass(var link:String? = null, @ServerTimestamp var timestamp: Date? = null) {

}