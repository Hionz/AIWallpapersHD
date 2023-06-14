package dan.ngoding.aiwallpapershd.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.makeramen.roundedimageview.RoundedImageView
import dan.ngoding.aiwallpapershd.Model.BomModel
import dan.ngoding.aiwallpapershd.R
import dan.ngoding.aiwallpapershd.ViewPagerActivity
import jp.wasabeef.glide.transformations.*


class CatImagesAdapter(
    val requireContext: Context,
    var listOfCatWallpaper: ArrayList<BomModel>,
    var uid: String
) :
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

        val progressBar = holder.itemView.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        Glide.with(requireContext).load(listOfCatWallpaper[position].link)
            .placeholder(R.drawable.loadinghldr)
            .apply(bitmapTransform(RoundedCornersTransformation(50,0)))
            .transition(DrawableTransitionOptions.withCrossFade(250))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(holder.imageView)

        holder.itemView.setOnClickListener{
            val intent = Intent(requireContext, ViewPagerActivity::class.java).apply {
                putExtra("uid", uid)
                putExtra("clicked_adapter", "cat")
                putExtra("clicked_position", position)
                putExtra("link", listOfCatWallpaper[position].link)
            }
            requireContext.startActivity(intent)
        }
    }

    override fun getItemCount() = listOfCatWallpaper.size
}
