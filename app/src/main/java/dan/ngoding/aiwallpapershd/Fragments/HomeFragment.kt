package dan.ngoding.aiwallpapershd.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dan.ngoding.aiwallpapershd.Adapter.BomAdapter
import dan.ngoding.aiwallpapershd.Adapter.CatAdapter
import dan.ngoding.aiwallpapershd.Adapter.ColorToneAdapter
import dan.ngoding.aiwallpapershd.Adapter.WffImagesAdapter
import dan.ngoding.aiwallpapershd.Model.BomModel
import dan.ngoding.aiwallpapershd.Model.CatModel
import dan.ngoding.aiwallpapershd.Model.ColorToneModel
import dan.ngoding.aiwallpapershd.Model.WffModel
import dan.ngoding.aiwallpapershd.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    lateinit var binding : FragmentHomeBinding
    lateinit var db : FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        db = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db.collection("bestofthemonth").addSnapshotListener { value, error ->
            // Check if the view is attached before updating the UI
            if (isAdded) {
                val listBestOfTheMonth = arrayListOf<BomModel>()
                val data = value?.toObjects(BomModel::class.java)
                listBestOfTheMonth.addAll(data!!)

                binding.rcvBom.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
                binding.rcvBom.adapter = BomAdapter(requireContext(), listBestOfTheMonth)
            }
        }

        db.collection("colortoner").addSnapshotListener { value, error ->
            // Check if the view is attached before updating the UI
            if (isAdded) {
                val listTheColorTone = arrayListOf<ColorToneModel>()
                val data = value?.toObjects(ColorToneModel::class.java)
                listTheColorTone.addAll(data!!)

                binding.rcvTct.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
                binding.rcvTct.adapter = ColorToneAdapter(requireContext(), listTheColorTone)
            }
        }

        db.collection("categories").addSnapshotListener { value, error ->
            // Check if the view is attached before updating the UI
            if (isAdded) {
                val listOfTheCategories = arrayListOf<CatModel>()
                val data = value?.toObjects(CatModel::class.java)
                listOfTheCategories.addAll(data!!)

                binding.rcvCat.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.rcvCat.adapter = CatAdapter(requireContext(), listOfTheCategories)
                binding.rcvCat.isNestedScrollingEnabled = false
            }
        }

        db.collection("wff").orderBy("id", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            // Check if the view is attached before updating the UI
            if (isAdded) {
                val listOfWallpapersFromFans = arrayListOf<WffModel>()
                val data = value?.toObjects(WffModel::class.java)

                listOfWallpapersFromFans.addAll(data!!)

                binding.rcvWff.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.rcvWff.adapter = WffImagesAdapter(requireContext(), listOfWallpapersFromFans)
                binding.rcvWff.isNestedScrollingEnabled = false
            }
        }
    }
}