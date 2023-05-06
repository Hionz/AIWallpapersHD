package dan.ngoding.aiwallpapershd

import android.app.Dialog
import android.app.WallpaperManager
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import dan.ngoding.aiwallpapershd.data.AppDatabase
import dan.ngoding.aiwallpapershd.data.dao.Fav
import dan.ngoding.aiwallpapershd.databinding.ActivityFinalWallpaperBinding
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import java.util.*
import kotlin.random.Random

class FinalWallpaper : AppCompatActivity() {

    lateinit var binding : ActivityFinalWallpaperBinding
    var progresDialogKustom : Dialog? = null
    var isFavourite = false
    var favouriteWallpaperId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinalWallpaperBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.root)

        val url = intent.getStringExtra("link")
        val urlImage = URL(url)

        checkIfFavourite()

        Glide.with(this).load(url).into(binding.finalWallpaper)

        binding.btnSet.setOnClickListener {
            wallpaparsDialog()
        }

        binding.btnFav.setOnClickListener{
            favUnfavWallpaper()
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
        progresDialogKustom = Dialog(this@FinalWallpaper)
        progresDialogKustom?.setContentView(R.layout.progres_dialog_kustom)
        progresDialogKustom?.show()
    }

    private fun cancelProgresDialog(){
        if (progresDialogKustom != null){
            progresDialogKustom?.dismiss()
            progresDialogKustom = null
        }
    }

    private fun wallpaparsDialog(){
        val wallpaperDialog = Dialog(this)
        wallpaperDialog.setContentView(R.layout.set_wallpapers)
        wallpaperDialog.show()
        val url = intent.getStringExtra("link")
        val urlImage = URL(url)
        val result : Deferred<Bitmap?> = GlobalScope.async {
            urlImage.toBitmap()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            wallpaperDialog.findViewById<Button>(R.id.btnHome).setOnClickListener{
                showProgresDialog()

                GlobalScope.launch(Dispatchers.Main){

                    val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                    WallpaperManager.FLAG_SYSTEM
                    wallpaperManager.setBitmap(result.await())

                    if (wallpaperManager != null){
                        Toast.makeText(this@FinalWallpaper, "Wallpaper set", Toast.LENGTH_SHORT).show()
                        cancelProgresDialog()
                    }
                }
            }
            wallpaperDialog.findViewById<Button>(R.id.btnLock).setOnClickListener{
                showProgresDialog()

                GlobalScope.launch(Dispatchers.Main){

                    val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                    WallpaperManager.FLAG_LOCK
                    wallpaperManager.setBitmap(result.await())

                    if (wallpaperManager != null){
                        Toast.makeText(this@FinalWallpaper, "Wallpaper set", Toast.LENGTH_SHORT).show()
                        cancelProgresDialog()
                    }
                }
            }
            wallpaperDialog.findViewById<Button>(R.id.btnBoth).setOnClickListener{
                showProgresDialog()

                GlobalScope.launch(Dispatchers.Main){

                    val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                    wallpaperManager.setBitmap(result.await())

                    if (wallpaperManager != null){
                        Toast.makeText(this@FinalWallpaper, "Wallpaper set", Toast.LENGTH_SHORT).show()
                        cancelProgresDialog()
                    }
                }
            }
            wallpaperDialog.findViewById<Button>(R.id.btnCancel).setOnClickListener{
                wallpaperDialog.dismiss()
            }
        }else{
            showProgresDialog()

            GlobalScope.launch(Dispatchers.Main){

                val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                wallpaperManager.setBitmap(result.await())

                if (wallpaperManager != null){
                    Toast.makeText(this@FinalWallpaper, "Wallpaper set", Toast.LENGTH_SHORT).show()
                    cancelProgresDialog()
                }
            }
        }

    }

    private fun favUnfavWallpaper() {
        val link = intent.getStringExtra("link")
        val database = AppDatabase.getInstance(baseContext)
        val fav = database.favDao().get(link.hashCode())

        if (fav == null) {
            database.favDao().insert(Fav(link))
            binding.btnFav.setImageResource(R.drawable.ic_favorite)
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
            isFavourite = true
        } else {
            database.favDao().delete(Fav(link))
            binding.btnFav.setImageResource(R.drawable.ic_favorite_border)
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
            isFavourite = false
        }
    }

    private fun checkIfFavourite() {
        val database = AppDatabase.getInstance(baseContext)
        val favList = database.favDao().getAll()
        isFavourite = checkForCurrentWallpaper(favList)
        if (isFavourite) {
            binding.btnFav.setImageResource(R.drawable.ic_favorite)
        } else {
            binding.btnFav.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    private fun checkForCurrentWallpaper(favList: List<Fav>): Boolean {
        val link = intent.getStringExtra("link")
        favList.forEach { fav ->
            if (fav.link == link) {
                favouriteWallpaperId = fav.uid.toString()
                return true
            }
        }
        return false
    }
//    private fun setupSetWallAlert() {
//        val setOnDialog = LayoutSetOnBinding.inflate(layoutInflater)
//        val dialog = alertDialog(setOnDialog)
//        binding.setWallpaperButton.setOnClickListener {
//            if (isOrientationLandscape()) {
//                Toast.makeText(this, "Set Wallpapers only in Portrait Mode", Toast.LENGTH_SHORT)
//                    .show()
//                return@setOnClickListener
//            }
//            val model = viewModel.list[viewModel.currentPosition]
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                setOnDialog.onHomeScreenBtn.setOnClickListener {
//                    viewModel.applyWallpaper(
//                        applicationContext,
//                        model,
//                        WallpaperManager.FLAG_SYSTEM
//                    )
//                    dialog.dismiss()
//                }
//                setOnDialog.onLockScreenBtn.setOnClickListener {
//                    viewModel.applyWallpaper(applicationContext, model, WallpaperManager.FLAG_LOCK)
//                    dialog.dismiss()
//                }
//                setOnDialog.onBothScreenBtn.setOnClickListener {
//                    viewModel.applyWallpaper(applicationContext, model)
//                    dialog.dismiss()
//                }
//                dialog.show()
//            } else {
//                viewModel.applyWallpaper(applicationContext, model)
//            }
//        }
//    }

}