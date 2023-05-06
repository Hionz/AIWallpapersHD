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
import dan.ngoding.aiwallpapershd.data.dao.Fav
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class FavoriteAdapter(val requireContext: Context, val listOfFavWallpaper: ArrayList<Fav>) :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>(){

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val imageView = itemView.findViewById<RoundedImageView>(R.id.catImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(requireContext).inflate(R.layout.item_wallpaper, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(requireContext).load(listOfFavWallpaper[position].link)
            .placeholder(R.drawable.loading)
            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(50, 0)))
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.imageView)
        holder.itemView.setOnClickListener{
            val intent = Intent(requireContext, FinalWallpaper::class.java)
            intent.putExtra("uid", listOfFavWallpaper[position].link)
            intent.putExtra("link", listOfFavWallpaper[position].link)
            requireContext.startActivity(intent)
        }
    }

    override fun getItemCount() = listOfFavWallpaper.size
}