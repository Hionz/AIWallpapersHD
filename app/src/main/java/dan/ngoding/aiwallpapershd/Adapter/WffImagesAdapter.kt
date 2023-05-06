package dan.ngoding.aiwallpapershd.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.makeramen.roundedimageview.RoundedImageView
import dan.ngoding.aiwallpapershd.Model.WffModel
import dan.ngoding.aiwallpapershd.R
import dan.ngoding.aiwallpapershd.activity_final_wallpaper_from_fans
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


class WffImagesAdapter(val requireContext: Context, val listOfWallpapersFromFans: ArrayList<WffModel>) :
    RecyclerView.Adapter<WffImagesAdapter.bomViewHolder>(){

    inner class bomViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val imageView = itemView.findViewById<RoundedImageView>(R.id.wff_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): bomViewHolder {
        return bomViewHolder(
            LayoutInflater.from(requireContext).inflate(R.layout.item_wff, parent, false)
        )
    }

    override fun onBindViewHolder(holder: bomViewHolder, position: Int) {
        Glide.with(requireContext).load(listOfWallpapersFromFans[position].link)
            .placeholder(R.drawable.loading)
            .apply(bitmapTransform(RoundedCornersTransformation(50,0)))
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.imageView)
        holder.itemView.setOnClickListener{
            val intent = Intent(requireContext, activity_final_wallpaper_from_fans::class.java)
            intent.putExtra("id", listOfWallpapersFromFans[position].id)
            intent.putExtra("link", listOfWallpapersFromFans[position].link)
            intent.putExtra("author", listOfWallpapersFromFans[position].author)
            intent.putExtra("description", listOfWallpapersFromFans[position].description)
            requireContext.startActivity(intent)
        }

    }

    override fun getItemCount() = listOfWallpapersFromFans.size

}
