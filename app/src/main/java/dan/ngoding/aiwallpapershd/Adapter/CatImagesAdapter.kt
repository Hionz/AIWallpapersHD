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
import dan.ngoding.aiwallpapershd.FinalWallpaper
import dan.ngoding.aiwallpapershd.Model.BomModel
import dan.ngoding.aiwallpapershd.R
import jp.wasabeef.glide.transformations.*


class CatImagesAdapter(val requireContext: Context, val listOfCatWallpaper: ArrayList<BomModel>) :
    RecyclerView.Adapter<CatImagesAdapter.bomViewHolder>(){

    inner class bomViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val imageView = itemView.findViewById<RoundedImageView>(R.id.catImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): bomViewHolder {
        return bomViewHolder(
            LayoutInflater.from(requireContext).inflate(R.layout.item_wallpaper, parent, false)
        )
    }

    override fun onBindViewHolder(holder: bomViewHolder, position: Int) {

        Glide.with(requireContext).load(listOfCatWallpaper[position].link)
            .placeholder(R.drawable.loading)
            .apply(bitmapTransform(RoundedCornersTransformation(50,0)))
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.imageView)
        holder.itemView.setOnClickListener{
            val intent = Intent(requireContext, FinalWallpaper::class.java)
            intent.putExtra("id", listOfCatWallpaper[position].link)
            intent.putExtra("link", listOfCatWallpaper[position].link)
            requireContext.startActivity(intent)
        }

    }

//    private fun CircularProgressDrawable(): CircularProgressDrawable {
//        val circularProgressDrawable = CircularProgressDrawable()
//        circularProgressDrawable.strokeWidth = 5f
//        circularProgressDrawable.centerRadius = 30f
//        circularProgressDrawable.start()
//
//        return circularProgressDrawable
//    }


    override fun getItemCount() = listOfCatWallpaper.size

}
