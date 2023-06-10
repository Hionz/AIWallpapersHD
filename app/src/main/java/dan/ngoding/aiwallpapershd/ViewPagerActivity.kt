package dan.ngoding.aiwallpapershd

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.WallpaperManager
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dan.ngoding.aiwallpapershd.Adapter.BomAdapter
import dan.ngoding.aiwallpapershd.Adapter.CatImagesAdapter
import dan.ngoding.aiwallpapershd.Adapter.ColorToneAdapter
import dan.ngoding.aiwallpapershd.Adapter.FavoriteAdapter
import dan.ngoding.aiwallpapershd.Adapter.ViewAdapter1
import dan.ngoding.aiwallpapershd.Adapter.ViewAdapter2
import dan.ngoding.aiwallpapershd.Adapter.ViewAdapter3
import dan.ngoding.aiwallpapershd.Adapter.ViewAdapter4
import dan.ngoding.aiwallpapershd.Adapter.ViewAdapter5
import dan.ngoding.aiwallpapershd.Adapter.WffImagesAdapter
import dan.ngoding.aiwallpapershd.Model.BomModel
import dan.ngoding.aiwallpapershd.Model.ColorToneModel
import dan.ngoding.aiwallpapershd.Model.WffModel
import dan.ngoding.aiwallpapershd.data.AppDatabase
import dan.ngoding.aiwallpapershd.data.dao.Fav
import dan.ngoding.aiwallpapershd.databinding.ActivityViewPagerBinding
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import java.util.Objects
import kotlin.math.abs
import kotlin.random.Random

class ViewPagerActivity : AppCompatActivity() {

    lateinit var binding: ActivityViewPagerBinding
    private lateinit var db: FirebaseFirestore

    private var prevBackgroundDrawable: Drawable? = null // define a variable to hold the previous background drawable

    lateinit var viewAdapter1: ViewAdapter1
    lateinit var viewAdapter2: ViewAdapter2
    lateinit var viewAdapter3: ViewAdapter3
    lateinit var viewAdapter4: ViewAdapter4
    lateinit var viewAdapter5: ViewAdapter5

    lateinit var bomAdapter: BomAdapter
    lateinit var catImagesAdapter: CatImagesAdapter
    lateinit var colorToneAdapter: ColorToneAdapter
    lateinit var favoriteAdapter: FavoriteAdapter
    lateinit var wffImagesAdapter: WffImagesAdapter

    lateinit var background: ImageView
    private lateinit var handler: Handler
    private lateinit var database: AppDatabase

    var progresDialogKustom : Dialog? = null
    var isFavourite = false
    var favoriteWallpaperId = ""

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPagerBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(binding.root)

        background = findViewById(R.id.background)

//        background.setOnLongClickListener {
//            if (binding.viewPager.visibility == View.VISIBLE) {
//                binding.viewPager.startAnimation(fadeOut)
//                binding.viewPager.visibility = View.GONE
//                binding.btnDownload.startAnimation(fadeOut)
//                binding.btnDownload.visibility = View.GONE
//                binding.btnFav.startAnimation(fadeOut)
//                binding.btnFav.visibility = View.GONE
//                binding.btnSet.startAnimation(fadeOut)
//                binding.btnSet.visibility = View.GONE
//            }
//            true
//        }
//
//        background.setOnTouchListener { _, event ->
//            if (event.action == MotionEvent.ACTION_UP && binding.viewPager.visibility == View.GONE) {
//                binding.viewPager.visibility = View.VISIBLE
//                binding.viewPager.startAnimation(fadeIn)
//                binding.btnDownload.visibility = View.VISIBLE
//                binding.btnDownload.startAnimation(fadeIn)
//                binding.btnFav.visibility = View.VISIBLE
//                binding.btnFav.startAnimation(fadeIn)
//                binding.btnSet.visibility = View.VISIBLE
//                binding.btnSet.startAnimation(fadeIn)
//                true
//            } else {
//                false
//            }
//        }


        setUpTransformer()

        // Initialize adapters with the context of the activity
        bomAdapter = BomAdapter(this, arrayListOf())
        catImagesAdapter = CatImagesAdapter(this, arrayListOf(), String())
        colorToneAdapter = ColorToneAdapter(this, arrayListOf())
        favoriteAdapter = FavoriteAdapter(this, arrayListOf())
        wffImagesAdapter = WffImagesAdapter(this, arrayListOf())

        viewAdapter1 = ViewAdapter1(this, arrayListOf())
        viewAdapter2 = ViewAdapter2(this, arrayListOf())
        viewAdapter3 = ViewAdapter3(this, arrayListOf())
        viewAdapter4 = ViewAdapter4(this, arrayListOf())
        viewAdapter5 = ViewAdapter5(this, arrayListOf())

        val clickedAdapter = intent.getStringExtra("clicked_adapter")
        val clickedPosition = intent.getIntExtra("clicked_position", 0)

        val uid = intent.getStringExtra("uid") //Key Abstract

        when (clickedAdapter) {
            "bom" -> {
                // Set viewAdapter1 as the adapter for the ViewPager
                binding.viewPager.adapter = viewAdapter1

                // Populate viewAdapter1 with data from Firebase Firestore
                db = FirebaseFirestore.getInstance()
                db.collection("bestofthemonth").addSnapshotListener { value, error ->
                    val listBestOfTheMonth = arrayListOf<BomModel>()
                    val data = value?.toObjects(BomModel::class.java)
                    listBestOfTheMonth.addAll(data!!)

                    // Set data to the adapter
                    bomAdapter.listBestOfTheMonth = listBestOfTheMonth

                    // Notify adapter of data change
                    bomAdapter.notifyDataSetChanged()

                    // Set data to viewAdapter1
                    viewAdapter1.setData(listBestOfTheMonth)

                    // Set current item
                    binding.viewPager.setCurrentItem(clickedPosition, true)

                    init()
                }
            }
            "cat" -> {
                // Set viewAdapter2 as the adapter for the ViewPager
                binding.viewPager.adapter = viewAdapter2
                val db = FirebaseFirestore.getInstance()

                // Populate viewAdapter2 with data from Firebase Firestore
                db.collection("categories").document(uid!!).collection("wallpapers")
                    .orderBy("id", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                        val listOfCatWallpaper = arrayListOf<BomModel>()
                        val data = value?.toObjects(BomModel::class.java)
                        listOfCatWallpaper.addAll(data!!)

                        // Set data to the adapter
                        catImagesAdapter.listOfCatWallpaper = listOfCatWallpaper

                        // Notify adapter of data change
                        catImagesAdapter.notifyDataSetChanged()

                        // Set data to viewAdapter2
                        viewAdapter2.setData(listOfCatWallpaper)

                        // Set current item
                        binding.viewPager.setCurrentItem(clickedPosition, true)

                        init()
                    }
            }
            "color_tone" -> {
                // Set viewAdapter2 as the adapter for the ViewPager
                binding.viewPager.adapter = viewAdapter3

                // Populate viewAdapter2 with data from Firebase Firestore
                db = FirebaseFirestore.getInstance()
                db.collection("colortoner").addSnapshotListener { value, error ->
                    val listColorTone = arrayListOf<ColorToneModel>()
                    val data = value?.toObjects(ColorToneModel::class.java)
                    listColorTone.addAll(data!!)

                    // Set data to the adapter
                    colorToneAdapter.listColorTone = listColorTone

                    // Notify adapter of data change
                    colorToneAdapter.notifyDataSetChanged()

                    // Set data to viewAdapter2
                    viewAdapter3.setData(listColorTone)

                    // Set current item
                    binding.viewPager.setCurrentItem(clickedPosition, true)

                    init()
                }
            }
            "fav" -> {
                // Set viewAdapter2 as the adapter for the ViewPager
                binding.viewPager.adapter = viewAdapter4

                database = AppDatabase.getInstance(this)

                val listOfFavWallpaper = arrayListOf<Fav>()
                val favList = database.favDao().getAll()
                listOfFavWallpaper.addAll(favList)

                // Set data to the adapter
                favoriteAdapter.listOfFavWallpaper = listOfFavWallpaper

                // Notify adapter of data change
                favoriteAdapter.notifyDataSetChanged()

                // Set data to viewAdapter4
                viewAdapter4.setData(listOfFavWallpaper)

                // Set current item
                binding.viewPager.setCurrentItem(clickedPosition, true)

                fun onResume() {
                    super.onResume()
                    binding.viewPager.adapter = viewAdapter4
                }

                onResume()

                init()
            }
            "wff" -> {
                // Set viewAdapter1 as the adapter for the ViewPager
                binding.viewPager.adapter = viewAdapter5

                // Populate viewAdapter1 with data from Firebase Firestore
                db = FirebaseFirestore.getInstance()
                db.collection("wff").orderBy("id", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                    val listOfWallpapersFromFans = arrayListOf<WffModel>()
                    val data = value?.toObjects(WffModel::class.java)

                    listOfWallpapersFromFans.addAll(data!!)

                    // Set data to the adapter
                    wffImagesAdapter.listOfWallpapersFromFans = listOfWallpapersFromFans

                    // Notify adapter of data change
                    wffImagesAdapter.notifyDataSetChanged()

                    // Set data to viewAdapter5
                    viewAdapter5.setData(listOfWallpapersFromFans)

                    // Set current item
                    binding.viewPager.setCurrentItem(clickedPosition, true)

                    init()
                }
            }
        }

        // Get link of current wallpaper
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (val currentAdapter = binding.viewPager.adapter) {
                    is ViewAdapter1 -> {
                        val currentWallpaper = currentAdapter.getCurrentWallpaper(position)
                        val imageLink = URL(currentWallpaper.link)
                        val url = currentWallpaper.link
                        // Do something with currentWallpaper
                        // Load the new background drawable
                        Glide.with(this@ViewPagerActivity)
                            .load(imageLink)
                            .transform(BlurTransformation(25, 3))
                            .into(object : CustomTarget<Drawable>() {
                                override fun onResourceReady(
                                    resource: Drawable,
                                    transition: Transition<in Drawable>?
                                ) {
                                    // Create a TransitionDrawable with the previous and new background drawables
                                    val crossFade = TransitionDrawable(
                                        arrayOf(
                                            prevBackgroundDrawable ?: ColorDrawable(Color.TRANSPARENT),
                                            resource
                                        )
                                    )

                                    background.setImageDrawable(crossFade) // Set the TransitionDrawable as the background
                                    crossFade.startTransition(1000) // Start the crossfade animation

                                    prevBackgroundDrawable = resource // Save the new drawable as the previous one
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {}
                            })

                        checkIfFavourite(url)

                        binding.btnSet.setOnClickListener {
                            wallpaparsDialog(url)
                        }

                        binding.btnFav.setOnClickListener{
                            favUnfavWallpaper(url)
                        }

                        binding.btnDownload.setOnClickListener{
                            val result : Deferred<Bitmap?> = GlobalScope.async {
                                imageLink.toBitmap()
                            }

                            GlobalScope.launch(Dispatchers.Main){
                                saveImage(result.await())
                            }
                        }
                    }
                    is ViewAdapter2 -> {
                        val currentWallpaper = currentAdapter.getCurrentWallpaper(position)
                        val imageLink = URL(currentWallpaper.link)
                        val url = currentWallpaper.link
                        // Do something with currentWallpaper
                        // Load the new background drawable
                        Glide.with(this@ViewPagerActivity)
                            .load(imageLink)
                            .transform(BlurTransformation(25, 3))
                            .into(object : CustomTarget<Drawable>() {
                                override fun onResourceReady(
                                    resource: Drawable,
                                    transition: Transition<in Drawable>?
                                ) {
                                    // Create a TransitionDrawable with the previous and new background drawables
                                    val crossFade = TransitionDrawable(
                                        arrayOf(
                                            prevBackgroundDrawable ?: ColorDrawable(Color.TRANSPARENT),
                                            resource
                                        )
                                    )

                                    background.setImageDrawable(crossFade) // Set the TransitionDrawable as the background
                                    crossFade.startTransition(1000) // Start the crossfade animation

                                    prevBackgroundDrawable = resource // Save the new drawable as the previous one
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {}
                            })

                        checkIfFavourite(url)

                        binding.btnSet.setOnClickListener {
                            wallpaparsDialog(url)
                        }

                        binding.btnFav.setOnClickListener{
                            favUnfavWallpaper(url)
                        }

                        binding.btnDownload.setOnClickListener{
                            val result : Deferred<Bitmap?> = GlobalScope.async {
                                imageLink.toBitmap()
                            }

                            GlobalScope.launch(Dispatchers.Main){
                                saveImage(result.await())
                            }
                        }
                    }
                    is ViewAdapter3 -> {
                        val currentWallpaper = currentAdapter.getCurrentWallpaper(position)
                        val imageLink = URL(currentWallpaper.link)
                        val url = currentWallpaper.link
                        // Do something with currentWallpaper
                        // Load the new background drawable
                        Glide.with(this@ViewPagerActivity)
                            .load(imageLink)
                            .transform(BlurTransformation(25, 3))
                            .into(object : CustomTarget<Drawable>() {
                                override fun onResourceReady(
                                    resource: Drawable,
                                    transition: Transition<in Drawable>?
                                ) {
                                    // Create a TransitionDrawable with the previous and new background drawables
                                    val crossFade = TransitionDrawable(
                                        arrayOf(
                                            prevBackgroundDrawable ?: ColorDrawable(Color.TRANSPARENT),
                                            resource
                                        )
                                    )

                                    background.setImageDrawable(crossFade) // Set the TransitionDrawable as the background
                                    crossFade.startTransition(1000) // Start the crossfade animation

                                    prevBackgroundDrawable = resource // Save the new drawable as the previous one
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {}
                            })

                        checkIfFavourite(url)

                        binding.btnSet.setOnClickListener {
                            wallpaparsDialog(url)
                        }

                        binding.btnFav.setOnClickListener{
                            favUnfavWallpaper(url)
                        }

                        binding.btnDownload.setOnClickListener{
                            val result : Deferred<Bitmap?> = GlobalScope.async {
                                imageLink.toBitmap()
                            }

                            GlobalScope.launch(Dispatchers.Main){
                                saveImage(result.await())
                            }
                        }
                    }
                    is ViewAdapter4 -> {
                        val currentWallpaper = currentAdapter.getCurrentWallpaper(position)
                        val imageLink = URL(currentWallpaper.link)
                        val url = currentWallpaper.link
                        // Do something with currentWallpaper
                        // Load the new background drawable
                        Glide.with(this@ViewPagerActivity)
                            .load(imageLink)
                            .transform(BlurTransformation(25, 3))
                            .into(object : CustomTarget<Drawable>() {
                                override fun onResourceReady(
                                    resource: Drawable,
                                    transition: Transition<in Drawable>?
                                ) {
                                    // Create a TransitionDrawable with the previous and new background drawables
                                    val crossFade = TransitionDrawable(
                                        arrayOf(
                                            prevBackgroundDrawable ?: ColorDrawable(Color.TRANSPARENT),
                                            resource
                                        )
                                    )

                                    background.setImageDrawable(crossFade) // Set the TransitionDrawable as the background
                                    crossFade.startTransition(1000) // Start the crossfade animation

                                    prevBackgroundDrawable = resource // Save the new drawable as the previous one
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {}
                            })

                        if (url != null) {
                            checkIfFavourite(url)
                        }

                        binding.btnSet.setOnClickListener {
                            if (url != null) {
                                wallpaparsDialog(url)
                            }
                        }

                        binding.btnFav.setOnClickListener{
                            if (url != null) {
                                favUnfavWallpaper(url)
                            }
                        }

                        binding.btnDownload.setOnClickListener{
                            val result : Deferred<Bitmap?> = GlobalScope.async {
                                imageLink.toBitmap()
                            }

                            GlobalScope.launch(Dispatchers.Main){
                                saveImage(result.await())
                            }
                        }
                    }
                    is ViewAdapter5 -> {
                        val currentWallpaper = currentAdapter.getCurrentWallpaper(position)
                        val imageLink = URL(currentWallpaper.link)
                        val url = currentWallpaper.link
                        // Do something with currentWallpaper
                        // Load the new background drawable
                        Glide.with(this@ViewPagerActivity)
                            .load(imageLink)
                            .transform(BlurTransformation(25, 3))
                            .into(object : CustomTarget<Drawable>() {
                                override fun onResourceReady(
                                    resource: Drawable,
                                    transition: Transition<in Drawable>?
                                ) {
                                    // Create a TransitionDrawable with the previous and new background drawables
                                    val crossFade = TransitionDrawable(
                                        arrayOf(
                                            prevBackgroundDrawable ?: ColorDrawable(Color.TRANSPARENT),
                                            resource
                                        )
                                    )

                                    background.setImageDrawable(crossFade) // Set the TransitionDrawable as the background
                                    crossFade.startTransition(1000) // Start the crossfade animation

                                    prevBackgroundDrawable = resource // Save the new drawable as the previous one
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {}
                            })
                        checkIfFavourite(url)

                        binding.btnSet.setOnClickListener {
                            wallpaparsDialog(url)
                        }

                        binding.btnFav.setOnClickListener{
                            favUnfavWallpaper(url)
                        }

                        binding.btnDownload.setOnClickListener{
                            val result : Deferred<Bitmap?> = GlobalScope.async {
                                imageLink.toBitmap()
                            }

                            GlobalScope.launch(Dispatchers.Main){
                                saveImage(result.await())
                            }
                        }
                    }
                }
            }
        })
    }

    private fun setUpTransformer(){
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }

        binding.viewPager.setPageTransformer(transformer)
    }

    private fun init(){
        handler = Handler(Looper.myLooper()!!)

        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.clipToPadding = false
        binding.viewPager.clipChildren = false
        binding.viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
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

        val name = "AIWallapersHD_${random1 + random2}"
        val data : OutputStream
        try {
            Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show()
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
        progresDialogKustom = Dialog(this@ViewPagerActivity)
        progresDialogKustom?.setContentView(R.layout.progres_dialog_kustom)
        progresDialogKustom?.show()
    }

    private fun cancelProgresDialog(){
        if (progresDialogKustom != null){
            progresDialogKustom?.dismiss()
            progresDialogKustom = null
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun wallpaparsDialog(url: String){
        val wallpaperDialog = Dialog(this)
        wallpaperDialog.setContentView(R.layout.set_wallpapers)
        wallpaperDialog.show()
        val urlImage = URL(url)
        val result : Deferred<Bitmap?> = GlobalScope.async {
            urlImage.toBitmap()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            wallpaperDialog.findViewById<Button>(R.id.btnHome).setOnClickListener{
                showProgresDialog()

                GlobalScope.launch(Dispatchers.Main){
                    try {
                        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                        val bitmap = result.await()
                        if (bitmap != null) {
                            WallpaperManager.FLAG_SYSTEM
                            wallpaperManager.setBitmap(bitmap)

                            // Show a success message only if the wallpaper was set correctly
                            Toast.makeText(this@ViewPagerActivity, "Wallpaper set", Toast.LENGTH_SHORT).show()
                        } else {
                            // Show an error message if the bitmap is null
                            Toast.makeText(this@ViewPagerActivity, "Error setting wallpaper: Bitmap is null, Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: IOException) {
                        // Handle the IO exception here, for example show an error message
                        Toast.makeText(this@ViewPagerActivity, "Error setting wallpaper: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        // Hide any progress dialogs or other UI elements
                        cancelProgresDialog()
                    }
                }
            }
            wallpaperDialog.findViewById<Button>(R.id.btnLock).setOnClickListener{
                showProgresDialog()

                GlobalScope.launch(Dispatchers.Main){
                    try {
                        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                        val bitmap = result.await()
                        if (bitmap != null) {
                            WallpaperManager.FLAG_LOCK
                            wallpaperManager.setBitmap(bitmap)

                            // Show a success message only if the wallpaper was set correctly
                            Toast.makeText(this@ViewPagerActivity, "Wallpaper set", Toast.LENGTH_SHORT).show()
                        } else {
                            // Show an error message if the bitmap is null
                            Toast.makeText(this@ViewPagerActivity, "Error setting wallpaper: Bitmap is null, Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: IOException) {
                        // Handle the IO exception here, for example show an error message
                        Toast.makeText(this@ViewPagerActivity, "Error setting wallpaper: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        // Hide any progress dialogs or other UI elements
                        cancelProgresDialog()
                    }
                }
            }
            wallpaperDialog.findViewById<Button>(R.id.btnBoth).setOnClickListener{
                showProgresDialog()

                GlobalScope.launch(Dispatchers.Main){
                    try {
                        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                        val bitmap = result.await()
                        if (bitmap != null) {
                            wallpaperManager.setBitmap(bitmap)

                            // Show a success message only if the wallpaper was set correctly
                            Toast.makeText(this@ViewPagerActivity, "Wallpaper set", Toast.LENGTH_SHORT).show()
                        } else {
                            // Show an error message if the bitmap is null
                            Toast.makeText(this@ViewPagerActivity, "Error setting wallpaper: Bitmap is null, Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: IOException) {
                        // Handle the IO exception here, for example show an error message
                        Toast.makeText(this@ViewPagerActivity, "Error setting wallpaper: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        // Hide any progress dialogs or other UI elements
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
                try {
                    val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                    val bitmap = result.await()
                    if (bitmap != null) {
                        wallpaperManager.setBitmap(bitmap)

                        // Show a success message only if the wallpaper was set correctly
                        Toast.makeText(this@ViewPagerActivity, "Wallpaper set", Toast.LENGTH_SHORT).show()
                    } else {
                        // Show an error message if the bitmap is null
                        Toast.makeText(this@ViewPagerActivity, "Error setting wallpaper: Bitmap is null, Please Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    // Handle the IO exception here, for example show an error message
                    Toast.makeText(this@ViewPagerActivity, "Error setting wallpaper: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    // Hide any progress dialogs or other UI elements
                    cancelProgresDialog()
                }
            }
        }
    }

    private fun favUnfavWallpaper(url: String) {
        val database = AppDatabase.getInstance(baseContext)
        val fav = database.favDao().get(url.hashCode())

        if (fav == null) {
            database.favDao().insert(Fav(url))
            binding.btnFav.setImageResource(R.drawable.ic_favorite)
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
            isFavourite = true
        } else {
            database.favDao().delete(Fav(url))
            binding.btnFav.setImageResource(R.drawable.ic_favorite_border)
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
            isFavourite = false
        }
    }

    private fun checkIfFavourite(url: String) {
        val database = AppDatabase.getInstance(baseContext)
        val favList = database.favDao().getAll()
        isFavourite = checkForCurrentWallpaper(favList, url)
        if (isFavourite) {
            binding.btnFav.setImageResource(R.drawable.ic_favorite)
        } else {
            binding.btnFav.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    private fun checkForCurrentWallpaper(favList: List<Fav>, url: String): Boolean {
        for (fav in favList) {
            if (fav.link == url) {
                favoriteWallpaperId = fav.uid.toString()
                return true
            }
        }
        return false
    }
}