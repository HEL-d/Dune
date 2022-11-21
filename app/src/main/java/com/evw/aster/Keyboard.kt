package com.evw.aster

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import kotlin.jvm.JvmOverloads
import android.widget.LinearLayout
import android.util.SparseArray
import android.view.inputmethod.InputConnection
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.evw.aster.R

class Keyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {
    private var button1: Button? = null
    private var button2: Button? = null
    private var button3: Button? = null
    private var button4: Button? = null
    private var button5: Button? = null
    private var buttonDelete: Button? = null
    private var buttonEnter: Button? = null
    private val keyValues = SparseArray<String>()
    private var inputConnection: InputConnection? = null
    private fun init(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true)
        button1 = findViewById<View>(R.id.button_1) as Button
        button1!!.setOnClickListener(this)
        button2 = findViewById<View>(R.id.button_2) as Button
        button2!!.setOnClickListener(this)
        button3 = findViewById<View>(R.id.button_3) as Button
        button3!!.setOnClickListener(this)
        button4 = findViewById<View>(R.id.button_4) as Button
        button4!!.setOnClickListener(this)
        button5 = findViewById<View>(R.id.button_5) as Button
        button5!!.setOnClickListener(this)
        buttonDelete = findViewById<View>(R.id.button_delete) as Button
        buttonDelete!!.setOnClickListener(this)
        buttonEnter = findViewById<View>(R.id.button_enter) as Button
        buttonEnter!!.setOnClickListener(this)
        keyValues.put(R.id.button_1, "1")
        keyValues.put(R.id.button_2, "2")
        keyValues.put(R.id.button_3, "3")
        keyValues.put(R.id.button_4, "4")
        keyValues.put(R.id.button_5, "5")
        keyValues.put(R.id.button_enter, "\n")
    }

    override fun onClick(view: View) {
        if (inputConnection == null) return
        if (view.id == R.id.button_delete) {
            val selectedText = inputConnection!!.getSelectedText(0)
            if (TextUtils.isEmpty(selectedText)) {
                inputConnection!!.deleteSurroundingText(1, 0)
            } else {
                inputConnection!!.commitText("", 1)
            }
        } else {
            val value = keyValues[view.id]
            inputConnection!!.commitText(value, 1)
        }
    }

    fun setInputConnection(ic: InputConnection?) {
        inputConnection = ic
    }

    init {
        init(context, attrs)
    }
}