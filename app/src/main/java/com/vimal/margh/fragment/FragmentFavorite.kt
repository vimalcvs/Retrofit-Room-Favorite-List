package com.vimal.margh.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vimal.margh.activity.ActivityDetail
import com.vimal.margh.adapter.AdapterFavorite
import com.vimal.margh.databinding.FragmentFavoriteBinding
import com.vimal.margh.db.Repository
import com.vimal.margh.models.ModelWallpaper
import com.vimal.margh.util.Constant.EXTRA_KEY

class FragmentFavorite : Fragment(), AdapterFavorite.OnItemClickListener {
    private val repository: Repository by lazy { Repository.getInstance(requireActivity())!! }
    private var binding: FragmentFavoriteBinding? = null
    private var adapter: AdapterFavorite? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        initViews()
        return root
    }

    private fun initViews() {
        adapter = AdapterFavorite(requireActivity(), ArrayList())
        repository.allFavorite().observe(viewLifecycleOwner) { products ->
            if (products.isNotEmpty()) {
                adapter!!.updateData(products)
                binding!!.icError.llEmptyFavorite.visibility = View.GONE
            } else {
                adapter!!.updateData(ArrayList())
                binding!!.icError.llEmptyFavorite.visibility = View.VISIBLE
            }
        }
        binding!!.rvRecycler.layoutManager = LinearLayoutManager(requireActivity())
        binding!!.rvRecycler.adapter = adapter
        adapter!!.setOnItemClickListener(this)
    }

    override fun onItemClick(modelWallpaper: ModelWallpaper?) {
        val intent = Intent(requireActivity(), ActivityDetail::class.java)
        intent.putExtra(EXTRA_KEY, modelWallpaper)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}