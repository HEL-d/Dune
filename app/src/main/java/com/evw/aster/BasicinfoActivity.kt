package com.evw.aster
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.smb.glowbutton.GlowButton
import java.util.Calendar

class BasicinfoActivity : AppCompatActivity() {
    lateinit var materialeditText:EditText
    lateinit var materialEditText2: EditText
    lateinit var button: GlowButton
    lateinit var internetConnectivity: InternetConnectivity
    lateinit var viewDialog: ViewDialog
    lateinit var networkbox: Networkbox
    lateinit var progressdialog: progressdialog
    lateinit var dialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basicinfo)
        materialeditText = findViewById(R.id.name_now)
        materialEditText2 = findViewById(R.id.birthday_now)
        internetConnectivity = InternetConnectivity()
        viewDialog = ViewDialog()
        networkbox = Networkbox()
        button = findViewById(R.id.button_save)
        progressdialog = progressdialog()
        dialog = Dialog(this)
        showdialog()
        materialEditText2.setOnClickListener {
          val calender:Calendar = Calendar.getInstance()
            val year = calender.get(Calendar.YEAR)
            val month = calender.get(Calendar.MONTH)
            val day = calender.get(Calendar.DAY_OF_MONTH)
            val listener :DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
                val dat = (dayOfMonth.toString() + "-" + (month + 1) + "-" + year)
                materialEditText2.setText(dat)
            }
            val datePickerDialog:DatePickerDialog = DatePickerDialog(this,R.style.DatePickerTheme,listener,year,month,day)
            datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.show()
        }

        button.setOnClickListener {
            if (materialeditText.text?.isEmpty() == true || materialEditText2.text?.isEmpty() == true){
                viewDialog.showDialog(this,"Both fields are required to proceed","FYI: fields are excluded from your profile")
            } else{
                internetConnectivity.checkInternetConnection(object:InternetConnectivity.ConnectivityCallback{
                    override fun onDetected(isConnected: Boolean) {
                        if (isConnected){
                        //    button.visibility = View.GONE
                            dialog.show()
                            Handler(Looper.getMainLooper()).postDelayed({
                                dialog.dismiss()
                                val intent:Intent = Intent(this@BasicinfoActivity,UniqueNameActivity::class.java)
                                   intent.putExtra("name",materialeditText.text.toString())
                                   intent.putExtra("birthday",materialEditText2.text.toString())
                                   startActivity(intent)
                               },2000)
                        } else {
                            networkbox.shownetworkdialog(this@BasicinfoActivity)
                        }
                    }

                },this)

            }



        }


    }



    private fun showdialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.progressdialog)
    }


}