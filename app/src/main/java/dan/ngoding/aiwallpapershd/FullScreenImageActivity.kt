package dan.ngoding.aiwallpapershd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.transition.Fade
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import dan.ngoding.aiwallpapershd.databinding.ActivityFullScreenImageBinding
import dan.ngoding.aiwallpapershd.databinding.ActivityViewPagerBinding

class FullScreenImageActivity : AppCompatActivity() {

    lateinit var binding: ActivityFullScreenImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(binding.root)

        val imageUrl = intent.getStringExtra("image_url")

        Glide.with(this)
            .load(imageUrl)
            .centerCrop()
            .into(binding.imageView)

        // Add a fade transition
        window.enterTransition = Fade(Fade.IN).apply {
            duration = 300
            addTarget(binding.imageView)
        }

        binding.imageView.setOnClickListener {
            window.returnTransition = Fade(Fade.OUT).apply {
                duration = 300
                addTarget(binding.imageView)
            }
            finishAfterTransition() // Finish the activity with a fade transition
        }
    }

    override fun onBackPressed() {
        // Use a fade transition to finish the activity
        window.returnTransition = Fade(Fade.OUT).apply {
            duration = 300
            addTarget(binding.imageView)
        }
        finishAfterTransition()
    }
}