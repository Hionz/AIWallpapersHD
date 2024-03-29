package dan.ngoding.aiwallpapershd.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import dan.ngoding.aiwallpapershd.FinalWallpaper
import dan.ngoding.aiwallpapershd.Model.ColorToneModel
import dan.ngoding.aiwallpapershd.R
import dan.ngoding.aiwallpapershd.ViewPagerActivity

class ColorToneAdapter(val requireContext: Context, var listColorTone: ArrayList<ColorToneModel>) :
    RecyclerView.Adapter<ColorToneAdapter.bomViewHolder>(){

    inner class bomViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val cardBack = itemView.findViewById<CardView>(R.id.item_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): bomViewHolder {
        return bomViewHolder(
            LayoutInflater.from(requireContext).inflate(R.layout.item_color_tone, parent, false)
        )
    }

    override fun onBindViewHolder(holder: bomViewHolder, position: Int) {
        val color = listColorTone[position].color
        holder.cardBack.setBackgroundColor(Color.parseColor(color!!))

        holder.itemView.setOnClickListener {
            val intent = Intent(requireContext, ViewPagerActivity::class.java).apply {
                putExtra("clicked_adapter", "color_tone")
                putExtra("link", listColorTone[position].link)
                putExtra("clicked_position", position)
            }
            requireContext.startActivity(intent)
        }
    }

    override fun getItemCount() = listColorTone.size

}