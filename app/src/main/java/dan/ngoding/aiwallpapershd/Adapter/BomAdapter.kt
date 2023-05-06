package dan.ngoding.aiwallpapershd.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dan.ngoding.aiwallpapershd.FinalWallpaper
import dan.ngoding.aiwallpapershd.Model.BomModel
import dan.ngoding.aiwallpapershd.R

class BomAdapter(val requireContext: Context, val listBestOfTheMonth: ArrayList<BomModel>) :
    RecyclerView.Adapter<BomAdapter.bomViewHolder>(){

    inner class bomViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val imageView = itemView.findViewById<ImageView>(R.id.bom_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): bomViewHolder {
        return bomViewHolder(
            LayoutInflater.from(requireContext).inflate(R.layout.item_bom, parent, false)
        )
    }

    override fun onBindViewHolder(holder: bomViewHolder, position: Int) {
        Glide.with(requireContext).load(listBestOfTheMonth[position].link).into(holder.imageView)
        holder.itemView.setOnClickListener{
            val intent = Intent(requireContext, FinalWallpaper::class.java)
            intent.putExtra("link", listBestOfTheMonth[position].link)
            requireContext.startActivity(intent)
        }

    }

    override fun getItemCount() = listBestOfTheMonth.size

}