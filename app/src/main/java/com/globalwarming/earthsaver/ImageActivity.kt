package com.globalwarming.earthsaver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.globalwarming.earthsaver.databinding.ActivityImageBinding

class ImageActivity : AppCompatActivity() {

    private lateinit var image: Image
    private lateinit var binding: ActivityImageBinding

    companion object {
        const val IMAGE = "image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image)

        image = intent.getParcelableExtra(IMAGE)!!

        binding.toolbar.title = "Uploaded at ${Util.getTime(image.timestamp)}"

        binding.textView.text = image.title
        Glide.with(binding.imageView).load(image.url).into(binding.imageView)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

}