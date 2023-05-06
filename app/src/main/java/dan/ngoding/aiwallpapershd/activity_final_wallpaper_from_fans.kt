package dan.ngoding.aiwallpapershd

import android.app.Dialog
import android.app.WallpaperManager
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import dan.ngoding.aiwallpapershd.Adapter.FavoriteAdapter
import dan.ngoding.aiwallpapershd.Model.WffModel
import dan.ngoding.aiwallpapershd.data.AppDatabase
import dan.ngoding.aiwallpapershd.data.dao.Fav
import dan.ngoding.aiwallpapershd.databinding.ActivityFinalWallpaperFromFansBinding
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import java.util.*
import kotlin.random.Random

class activity_final_wallpaper_from_fans : AppCompatActivity() {

    private lateinit var adapter: FavoriteAdapter
    private var list = mutableListOf<Fav>()
    private lateinit var database: AppDatabase
    lateinit var binding : ActivityFinalWallpaperFromFansBinding
    var progresDialogKustom : Dialog? = null
    var isFavourite = false
    var favouriteWallpaperId = ""

    val btnFav = findViewById<ImageView>(R.id.btnFav)
//    var tvAuthor : TextView = findViewById(R.id.wff_author)
//    var tvDescription : TextView = findViewById(R.id.wff_description)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinalWallpaperFromFansBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.root)

        val db = FirebaseFirestore.getInstance()
        val id = intent.getStringExtra("id")
        val author = intent.getStringExtra("author")
        val description = intent.getStringExtra("description")

        db.collection("wallpapersfromfans").addSnapshotListener { value, error ->
            val listOfWallpapersFromFans = arrayListOf<WffModel>()
            val data = value?.toObjects(WffModel::class.java)
            listOfWallpapersFromFans.addAll(data!!)

            binding.id.text = id.toString()
            binding.wffAuthor.text = author.toString()
            binding.wffDescription.text = description.toString()
        }
        val url = intent.getStringExtra("link")
        val urlImage = URL(url)

        Glide.with(this).load(url).into(binding.finalWallpaperFromFans)

        binding.btnSet.setOnClickListener {

            val url = intent.getStringExtra("link")
            val urlImage = URL(url)
            val result : Deferred<Bitmap?> = GlobalScope.async {
                urlImage.toBitmap()
            }

            showProgresDialog()

            GlobalScope.launch(Dispatchers.Main){

                val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                wallpaperManager.setBitmap(result.await())

                if (wallpaperManager != null){
                    Toast.makeText(this@activity_final_wallpaper_from_fans, "Wallpaper set", Toast.LENGTH_SHORT).show()
                    cancelProgresDialog()
                }
            }
        }

        binding.btnFav.setOnClickListener{

        }
        binding.btnDownload.setOnClickListener{
            val result : Deferred<Bitmap?> = GlobalScope.async {
                urlImage.toBitmap()
            }

            GlobalScope.launch(Dispatchers.Main){
                saveImage(result.await())
            }
        }
    }

    fun URL.toBitmap() : Bitmap?{
        return try {
            BitmapFactory.decodeStream(openStream())
        }catch (e : IOException){
            null
        }
    }

    private fun saveImage(image : Bitmap?) {

        val random1 = Random.nextInt(528985)
        val random2 = Random.nextInt(528985)

        val name = "AIWallapersHd_${random1 + random2}"
        val data : OutputStream
        try {
            val resolver = contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$name.jpg")
            contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + File.separator + "AI Wallpapers HD"
            )
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            data = resolver.openOutputStream(Objects.requireNonNull(imageUri)!!)!!
            image?.compress(Bitmap.CompressFormat.JPEG, 100, data)
            Objects.requireNonNull<OutputStream>(data)
            Toast.makeText(this, "Image Save | /Pictures/AI Wallapers HD/$name.jpg", Toast.LENGTH_SHORT).show()
        }catch (e : Exception){
            Toast.makeText(this, "Image Not Save", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showProgresDialog(){
        progresDialogKustom = Dialog(this)
        progresDialogKustom?.setContentView(R.layout.progres_dialog_kustom)
        progresDialogKustom?.show()
    }

    private fun cancelProgresDialog(){
        if (progresDialogKustom != null){
            progresDialogKustom?.dismiss()
            progresDialogKustom = null
        }
    }
}

//    private fun favUnfavWallpaper() {
//        val link = intent.getStringExtra("link")
//        val favList = AppDatabase.getInstance(baseContext).favDao().getAll()
//        favList.forEach {
//            if (it.link == link) {
//                AppDatabase.getInstance(baseContext).favDao().delete(it)
//                binding.btnFav.setImageResource(R.drawable.ic_favorite_border)
//                Toast.makeText(this, "removed from favourite", Toast.LENGTH_SHORT).show()
//            } else {
//                AppDatabase.getInstance(baseContext).favDao().insert(Fav(link = link))
//                binding.btnFav.setImageResource(R.drawable.ic_favorite)
//                Toast.makeText(this, "added to favourite", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    fun checkIfFavourite() {
//        val database = AppDatabase.getInstance(baseContext)
//        val list = database.favDao().getAll()
//        isFavourite = checkForCurrentWallpaper(list)
//        if (isFavourite) {
//            binding.btnFav.setImageResource(R.drawable.ic_favorite)
//        } else {
//            binding.btnFav.setImageResource(R.drawable.ic_favorite_border)
//        }
//    }
//
//    private fun checkForCurrentWallpaper(list: List<Fav>): Boolean {
//        var isFound = false;
//        if (intent != null){
//            val currentLink = intent.getStringExtra("link")
//            list.forEach {
//                if (it.link == currentLink) {
//                    isFound = true
//                    favouriteWallpaperId = it.uid.toString()
//                }
//            }
//        }
//        return isFound
//    }
