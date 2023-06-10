package dan.ngoding.aiwallpapershd.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dan.ngoding.aiwallpapershd.CatActivity
import dan.ngoding.aiwallpapershd.Model.CatModel
import dan.ngoding.aiwallpapershd.R

class CatAdapter(val requireContext: Context, val listOfTheCategoties: ArrayList<CatModel>) :
    RecyclerView.Adapter<CatAdapter.bomViewHolder>(){

    inner class bomViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val imageView = itemView.findViewById<ImageView>(R.id.cat_image)
        val name = itemView.findViewById<TextView>(R.id.cat_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): bomViewHolder {
        return bomViewHolder(
            LayoutInflater.from(requireContext).inflate(R.layout.item_cat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: bomViewHolder, position: Int) {
        holder.name.text = listOfTheCategoties[position].name
        Glide.with(requireContext).load(listOfTheCategoties[position].link).into(holder.imageView)
        holder.itemView.setOnClickListener{
            val intent = Intent(requireContext, CatActivity::class.java).apply {
                putExtra("uid", listOfTheCategoties[position].id)
                putExtra("name", listOfTheCategoties[position].name)
            }
            requireContext.startActivity(intent)
        }
    }

    override fun getItemCount() = listOfTheCategoties.size

}