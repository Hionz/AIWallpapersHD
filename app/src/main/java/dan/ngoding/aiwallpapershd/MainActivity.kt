package dan.ngoding.aiwallpapershd

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dan.ngoding.aiwallpapershd.Fragments.DownloadFragment
import dan.ngoding.aiwallpapershd.Fragments.FavoriteFragment
import dan.ngoding.aiwallpapershd.Fragments.HomeFragment
import dan.ngoding.aiwallpapershd.Fragments.UploadFragment
import dan.ngoding.aiwallpapershd.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding


    private companion object {
        //Permission request constant, assign any value
        private const val STORAGE_PERMISSION_CODE = 100
        private const val TAG = "PERMISSION_TAG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.root)

        replaceFragment(HomeFragment())

        binding.icHome.setOnClickListener{

            replaceFragment(HomeFragment())
        }

//        binding.icDownload.setOnClickListener{
//            if (checkPermission()){
//                Log.e(TAG, "onCreate: Permission already granted, you can download now")
//                replaceFragment(DownloadFragment())
//            }else{
//                Log.e(TAG, "onCreate: Permission Denied, Try again...")
//                requestPermission()
//            }
//      }

        binding.icUpload.setOnClickListener{
            if (checkPermission()){
                Log.e(TAG, "onCreate: Permission already granted, you can download now")
                replaceFragment(UploadFragment())
            }else{
                Log.e(TAG, "onCreate: Permission Denied, Try again...")
                requestPermission()
            }
        }

            binding.icFav.setOnClickListener{
                replaceFragment(FavoriteFragment())
            }

    }

    fun replaceFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentReplace, fragment)
        transaction.commit()
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
        }
        else{
            storageActivityResultLauncher.launch(intent)
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    private val storageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            Log.e(TAG, "storageActivityResultLauncher: ")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                if (Environment.isExternalStorageManager()){
                    Log.e(TAG, "storageActivityResultLauncher: ")
                    replaceFragment(DownloadFragment())
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty()){
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (write && read){
                    Log.e(TAG, "onRequestPermissionResult")
                    replaceFragment(UploadFragment())
                }else{
                    Log.e(TAG, "onRequestPermissionResult: External Storage Permission DENIED...")
                    Toast.makeText(this, "External Storage Permission DENIED...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}