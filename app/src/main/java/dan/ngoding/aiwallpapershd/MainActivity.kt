package dan.ngoding.aiwallpapershd

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import dan.ngoding.aiwallpapershd.Fragments.AboutFragment
import dan.ngoding.aiwallpapershd.Fragments.DownloadFragment
import dan.ngoding.aiwallpapershd.Fragments.FavoriteFragment
import dan.ngoding.aiwallpapershd.Fragments.HomeFragment
import dan.ngoding.aiwallpapershd.Fragments.UploadFragment
import dan.ngoding.aiwallpapershd.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    private var currentButton: View? = null

    private companion object {
        //Permission request constant, assign any value
        private const val STORAGE_PERMISSION_CODE = 100
        private const val TAG = "PERMISSION_TAG"
    }

    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content view
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(binding.root)

        // Set the initial fragment to HomeFragment
        currentFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragmentReplace, currentFragment!!).commit()

        // Set the initial button to the Home button and change its background color
        currentButton = binding.icHome
        currentButton?.setBackgroundColor(Color.LTGRAY)

        // Set the click listeners for the bottom navigation buttons
        binding.icHome.setOnClickListener{
            replaceFragment(HomeFragment(), binding.icHome)
        }
        binding.icUpload.setOnClickListener{
            if (checkPermission()){
                replaceFragment(UploadFragment(), binding.icUpload)
            }else{
                requestPermission()
            }
        }
        binding.icFav.setOnClickListener{
            replaceFragment(FavoriteFragment(), binding.icFav)
        }

        binding.icMore.setOnClickListener {
            replaceFragment(AboutFragment(), binding.icMore)
        }
    }

    private fun replaceFragment(fragment : Fragment, button: View) {
        // Only replace the fragment if it's not the same as the current fragment.
        if (fragment.javaClass != currentFragment?.javaClass) {
            // Reset the background color of the previously selected button.
            currentButton?.setBackgroundColor(Color.TRANSPARENT)

            // Set the background color of the newly selected button.
            button.setBackgroundColor(Color.LTGRAY)

            currentFragment = fragment
            currentButton = button
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.fade_in_fragment, 0)
            transaction.replace(R.id.fragmentReplace, fragment)
            transaction.commit()
        }
    }

    private fun requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            try {
                Log.e(TAG, "RequestPermission: try")
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }catch (e : Exception){
                Log.e(TAG, "RequestPermission", e)
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE)
        }
    }

    private val storageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            Log.e(TAG, "storageActivityResultLauncher: ")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                if (Environment.isExternalStorageManager()){
                    Log.e(TAG, "storageActivityResultLauncher: ")
                }
                else{
                    Log.e(TAG,"storageActivityResultLauncher")
                    Toast.makeText(this, "Manage External Storage Permission is DENIED...", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            Environment.isExternalStorageManager()
        }else{
            val write = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            replaceFragment(UploadFragment(), binding.icUpload)
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}