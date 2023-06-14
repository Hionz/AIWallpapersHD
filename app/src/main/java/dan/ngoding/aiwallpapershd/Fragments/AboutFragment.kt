package dan.ngoding.aiwallpapershd.Fragments

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import dan.ngoding.aiwallpapershd.R
import dan.ngoding.aiwallpapershd.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAboutBinding.inflate(layoutInflater)

        binding.btnMe.setOnClickListener {
            openLink("https://doc-hosting.flycricket.io/ai-wallpapers-hd-about-me/2703c2f7-87ba-44f4-92f5-56113a827d4c/other")
        }

        binding.btnFeedback.setOnClickListener {
            sendEmail()
        }

        binding.btnTheme.setOnClickListener {
            val dialog = Dialog(requireContext())
            themeDialog(dialog)
            dialog.show()
        }

        binding.btnPrivacy.setOnClickListener {
            openLink("https://doc-hosting.flycricket.io/ai-wallpapers-hd-privacy-policy/2aa91726-5982-4c06-bf91-9192d6ef8bea/privacy")
        }

        binding.btnTerm.setOnClickListener {
            openLink("https://doc-hosting.flycricket.io/ai-wallpapers-hd-terms-of-use/48689c8c-3646-41d7-ab14-f0782ff115da/terms")
        }

        return binding.root
    }

    private fun setTheme(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        requireActivity().recreate()
    }

    private fun themeDialog(dialog: Dialog){
        dialog.setContentView(R.layout.set_theme)
        dialog.findViewById<Button>(R.id.btnLight).setOnClickListener {
            setTheme(AppCompatDelegate.MODE_NIGHT_NO)
        }

        dialog.findViewById<Button>(R.id.btnDark).setOnClickListener {
            setTheme(AppCompatDelegate.MODE_NIGHT_YES)
        }

        dialog.findViewById<Button>(R.id.btnSystem).setOnClickListener {
            setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        dialog.findViewById<Button>(R.id.btnCncl).setOnClickListener {
            dialog.dismiss()
        }
    }


    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:danda.ngoding@gmail.com")
        startActivity(Intent.createChooser(intent, "Send Feedback"))
    }
}