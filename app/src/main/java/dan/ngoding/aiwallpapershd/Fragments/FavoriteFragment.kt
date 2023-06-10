package dan.ngoding.aiwallpapershd.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dan.ngoding.aiwallpapershd.Adapter.FavoriteAdapter
import dan.ngoding.aiwallpapershd.data.AppDatabase
import dan.ngoding.aiwallpapershd.data.dao.Fav
import dan.ngoding.aiwallpapershd.databinding.FragmentFavoriteBinding

class FavoriteFragment : Fragment() {

    lateinit var binding : FragmentFavoriteBinding
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        database = AppDatabase.getInstance(requireContext())

        val listOfFavWallpaper = arrayListOf<Fav>()
        val favList = database.favDao().getAll()
        listOfFavWallpaper.addAll(favList)

        binding.favCount.text = "${listOfFavWallpaper.size} Wallpapers Available"

        binding.rcvFavourite.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rcvFavourite.adapter = FavoriteAdapter(requireContext(), listOfFavWallpaper)
        binding.rcvFavourite.isNestedScrollingEnabled = false

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val favList = database.favDao().getAll()
        val listOfFavWallpaper = ArrayList(favList)
        binding.favCount.text = "${listOfFavWallpaper.size} Wallpapers Available"
        binding.rcvFavourite.adapter?.apply {
            // Update the adapter's data and notify the view that the data has changed.
            this as FavoriteAdapter
            this.listOfFavWallpaper.clear()
            this.listOfFavWallpaper.addAll(listOfFavWallpaper)
            notifyDataSetChanged()
        }
    }
}