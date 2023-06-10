package dan.ngoding.aiwallpapershd.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import dan.ngoding.aiwallpapershd.databinding.FragmentUploadBinding
import java.io.File
import java.io.FileOutputStream

class UploadFragment : Fragment() {

    private lateinit var binding: FragmentUploadBinding
    private val config = hashMapOf(
        "cloud_name" to "dkk9r2txw",
        "api_key" to "248319874547291",
        "api_secret" to "3kws8SCNpm-td03Qnnds5ihp9-M"
    )
    private var imgPath: Uri? = null
    private val READ_REQUEST_CODE = 42

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUploadBinding.inflate(layoutInflater, container, false)

        binding.uploadButton.setOnClickListener {
            openFileChooser()
        }

        binding.saveButton.setOnClickListener {
            imgPath?.let { uploadToCloudinary(it) }
        }

        return binding.root
    }

    private fun resetData() {
        imgPath = null
        binding.imagev.setImageURI(null)
        binding.saveButton.visibility = View.GONE
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    private fun uploadToCloudinary(fileUri: Uri) {
        val inputStream = requireContext().contentResolver.openInputStream(fileUri)
        MediaManager.init(requireContext(), config)
        if (inputStream == null) {
            Toast.makeText(requireContext(), "File does not exist", Toast.LENGTH_SHORT).show()
            return
        }
        // Create a file from the input stream
        val outputFile = File.createTempFile("temp", "jpg")
        inputStream.use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
            }
        }
        // Upload the output file
        MediaManager.get().upload(fileUri) // Call init() with applicationContext
            .callback(object :
                UploadCallback {
                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    Toast.makeText(requireContext(), "Task successful", Toast.LENGTH_SHORT).show()
                    resetData()
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    error?.let { Toast.makeText(requireContext(), "Task Not successful: ${it.description}", Toast.LENGTH_SHORT).show() }
                }

                override fun onStart(requestId: String?) {
                    Toast.makeText(requireContext(), "Start", Toast.LENGTH_SHORT).show()
                }
            }).dispatch()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val fileUri: Uri? = data?.data
            if (fileUri == null) {
                Toast.makeText(requireContext(), "File URI is null", Toast.LENGTH_SHORT).show()
                return
            }
            binding.imagev.setImageURI(fileUri)
            val filePath: Uri = fileUri
            imgPath = filePath
            binding.saveButton.visibility = View.VISIBLE
            binding.tvThanks.visibility = View.VISIBLE
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            binding.saveButton.visibility = View.GONE
        }
    }
}