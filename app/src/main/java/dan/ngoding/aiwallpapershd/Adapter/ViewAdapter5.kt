package dan.ngoding.aiwallpapershd.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dan.ngoding.aiwallpapershd.FullScreenImageActivity
import dan.ngoding.aiwallpapershd.Model.ColorToneModel
import dan.ngoding.aiwallpapershd.Model.WffModel
import dan.ngoding.aiwallpapershd.R
import java.util.Random

class ViewAdapter5(private val requireContext: Context, private var data: ArrayList<WffModel>) :
    RecyclerView.Adapter<ViewAdapter5.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

        init {
            imageView.setOnClickListener {
                val intent = Intent(imageView.context, FullScreenImageActivity::class.java)
                intent.putExtra("image_url", data[adapterPosition].link)

                // Use a fade transition
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    imageView.context as Activity
                ).toBundle()
                imageView.context.startActivity(intent, options)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(requireContext).inflate(R.layout.image_container, parent, false)
        return ViewHolder(view)
    }

//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val currentItem = data[position]
//
//        val random = Random()
//        val placeholderColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
//
//        // Define the placeholder drawable as a ColorDrawable with the random color
//        val placeholderDrawable = ColorDrawable(placeholderColor)
//
//        Glide.with(requireContext)
//            .load(currentItem.link)
//            .centerCrop()
//            .placeholder(placeholderDrawable) // Set the placeholder drawable
//            .into(holder.imageView)
//    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = data[position]

        // Define the placeholder drawable as a transparent ColorDrawable
        val placeholderDrawable = ColorDrawable(Color.TRANSPARENT)

        Glide.with(requireContext)
            .load(currentItem.link)
            .centerCrop()
            .placeholder(placeholderDrawable) // Set the placeholder drawable
            .into(holder.imageView)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(newData: ArrayList<WffModel>) {
        data = newData
        notifyDataSetChanged()
    }

    fun getCurrentWallpaper(position: Int): WffModel {
        return data[position]
    }
}