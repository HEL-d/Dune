package com.evw.aster

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class Updateavatardilog {
    private val MAX_IMAGE_SIZE = 800f
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var mContext: Context? = null
    fun takedilaog(context: Context?, url:String, profilepic:String,phonenumber:String,username:String) {
        mContext = context
        val dialog = mContext?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.welcombox)
        val dialogbutton: MaterialButton = dialog?.findViewById(R.id.last_okay) as MaterialButton
        val progessbar: MaterialNeonProgressBar = dialog.findViewById(R.id.opbar) as MaterialNeonProgressBar
        val updates = hashMapOf<String, Any>("avatarurl" to url, "profilepic" to profilepic)
        dialogbutton.setOnClickListener {
            dialogbutton.background = ContextCompat.getDrawable(context!!,R.drawable.nothingbutton)
            progessbar.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
               val url1 = URL(profilepic)
                val image : Bitmap = BitmapFactory.decodeStream(
                    withContext(Dispatchers.IO) {
                        withContext(Dispatchers.IO) {
                            url1.openConnection()
                        }.getInputStream()
                    })
                val ratio: Float = Math.min(MAX_IMAGE_SIZE / image.getWidth(), MAX_IMAGE_SIZE / image.getHeight())
                val width = Math.round(ratio as Float * image.getWidth()).toInt()
                val height = Math.round(ratio as Float * image.getHeight()).toInt()
              //  val newBitmap = Bitmap.createScaledBitmap(image, width, height, true)
                val newBitmap = Bitmap.createBitmap(image)
                val bytes = ByteArrayOutputStream()
                newBitmap.compress(Bitmap.CompressFormat.PNG, 80, bytes);
                val fileInBytes: ByteArray = bytes.toByteArray()
                val timestamp = "" + System.currentTimeMillis()
                sendData(fileInBytes,timestamp,dialog,url,phonenumber,username)

            }
            }
        dialog.show()
    }

    private suspend fun sendData(
        fileInBytes: ByteArray,
        timestamp: String,
        dialog: Dialog,
        url: String,
        phonenumber: String,
        username: String
    ):String {
        return suspendCoroutine { continuation ->
            val storageref = FirebaseStorage.getInstance().reference.child("profilepic").child(timestamp)
            val uploadtask: UploadTask = storageref.putBytes(fileInBytes)
            uploadtask.addOnFailureListener {
                continuation.resumeWithException(it)
            }.addOnSuccessListener {
               it.task.continueWithTask(com.google.android.gms.tasks.Continuation<UploadTask.TaskSnapshot,Task<Uri>>{ task ->
                   if (!task.isSuccessful){
                       task.exception?.let {
                           continuation.resumeWithException(it)
                       }
                   }
                   return@Continuation storageref.downloadUrl
               }).addOnCompleteListener { uriTask ->
                   if (uriTask.isSuccessful){
                       val downloaduri = uriTask.result
                       CoroutineScope(Dispatchers.IO).launch {
                           pastedatatofirestore(downloaduri.toString(),dialog,url,phonenumber,username)
                       }
                       continuation.resume(downloaduri.toString())

                   }

               }


            }



        }

    }

    private suspend fun pastedatatofirestore(
        downloaduri: String,
        dialog: Dialog,
        url: String,
        phonenumber: String,
        username: String
    ): Boolean {
        val updates = hashMapOf<String, Any>("avatarurl" to url, "profilepic" to downloaduri)
        return try {
            val locationuser = FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).update(updates).await()
             val usernameloc = FirebaseFirestore.getInstance().collection("Usersname").document(username).update(updates).await()
             val phonenumberlocation = FirebaseFirestore.getInstance().collection("Contacts").document(phonenumber).update(updates).await()
            withContext(Dispatchers.Main){
                dialog.dismiss()
                val intent:Intent = Intent(mContext,MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("fc","token")
                mContext?.startActivity(intent)
                (mContext as Activity).finish()
            }
            true
        } catch (e:Exception){
            false
        }

    }


}
