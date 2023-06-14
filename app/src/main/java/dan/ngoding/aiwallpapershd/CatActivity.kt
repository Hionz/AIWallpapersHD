package dan.ngoding.aiwallpapershd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dan.ngoding.aiwallpapershd.Adapter.CatImagesAdapter
import dan.ngoding.aiwallpapershd.Model.BomModel
import dan.ngoding.aiwallpapershd.databinding.ActivityCatBinding

class CatActivity : AppCompatActivity() {

    lateinit var binding : ActivityCatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(binding.root)

        val db = FirebaseFirestore.getInstance()
        val uid = intent.getStringExtra("uid") //Key Abstract
        val name = intent.getStringExtra("name")

        db.collection("categories").document(uid!!).collection("wallpapers").orderBy("id", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            val listOfCatWallpaper = arrayListOf<BomModel>()
            val data = value?.toObjects(BomModel::class.java)
            listOfCatWallpaper.addAll(data!!)

            binding.catTitle.text = name.toString()
            binding.catCount.text = "${listOfCatWallpaper.size} Wallpapers Available"

            binding.catRcv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            binding.catRcv.adapter = CatImagesAdapter(this, listOfCatWallpaper, uid)
        }
    }
}