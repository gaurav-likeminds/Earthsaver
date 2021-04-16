package com.globalwarming.earthsaver.account

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.globalwarming.earthsaver.Image
import com.globalwarming.earthsaver.ImageActivity
import com.globalwarming.earthsaver.Util
import com.globalwarming.earthsaver.databinding.ItemImageBinding

class ImageAdapter: RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    private val items = ArrayList<Image>()

    fun setList(list: List<Image>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(private val binding: ItemImageBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Image) {
            binding.root.setOnClickListener {
                val intent = Intent(it.context, ImageActivity::class.java)
                intent.putExtra(ImageActivity.IMAGE, item)
                it.context.startActivity(intent)
            }

            Glide.with(binding.imageView)
                .load(item.url)
                .transform(CenterCrop(), RoundedCorners(Util.dpToPx(16)))
                .into(binding.imageView)
        }

    }

}