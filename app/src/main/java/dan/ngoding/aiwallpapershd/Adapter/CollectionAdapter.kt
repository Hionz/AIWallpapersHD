package dan.ngoding.aiwallpapershd.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.makeramen.roundedimageview.RoundedImageView
import dan.ngoding.aiwallpapershd.FinalWallpaper
import dan.ngoding.aiwallpapershd.R
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class CollectionAdapter(val requireContext: Context, val listOfDownloadWallpaper: ArrayList<String>) :
    RecyclerView.Adapter<CollectionAdapter.bomViewHolder>(){

    inner class bomViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val imageView = itemView.findViewById<RoundedImageView>(R.id.catImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): bomViewHolder {
        return bomViewHolder(
            LayoutInflater.from(requireContext).inflate(R.layout.item_wallpaper, parent, false)
        )
    }

    override fun onBindViewHolder(holder: bomViewHolder, position: Int) {
        Glide.with(requireContext).load(listOfDownloadWallpaper[position])
            .placeholder(R.drawable.loadinghldr)
            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(50, 0)))
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.imageView)
    }

    override fun getItemCount() = listOfDownloadWallpaper.size

}