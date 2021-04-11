package com.globalwarming.earthsaver.util

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.globalwarming.earthsaver.R

class Loader(mContext: Context, private val text: String = "Logging in ..."): Dialog(mContext) {

    private lateinit var lottieView: LottieAnimationView
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loader)

        textView = findViewById(R.id.text_view)
        lottieView = findViewById(R.id.lottie)

        window?.setBackgroundDrawableResource(R.color.light_transparent)
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        textView.text = text

    }

    fun setText(text: String) {
        textView.text = text
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
        lottieView.cancelAnimation()
    }

}