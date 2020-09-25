package com.test.youtubeplayer.bindingadaters

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.test.youtubeplayer.R

@BindingAdapter(value = ["imgSrc"])
fun loadImage(
    imageView: AppCompatImageView,
    url: String?
) {
    url?.let {
        Glide.with(imageView.context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .thumbnail(Glide.with(imageView.context).load(R.raw.youtube).centerCrop())
            .into(imageView)
    }
}