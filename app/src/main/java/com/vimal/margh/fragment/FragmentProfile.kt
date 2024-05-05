package com.vimal.margh.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.vimal.margh.databinding.FragmentProfileBinding
import com.vimal.margh.util.Utils.contactApp
import com.vimal.margh.util.Utils.shareApp

class FragmentProfile : Fragment() {
    private var binding: FragmentProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding!!.root


        binding!!.btnLogout.setOnClickListener {
            Toast.makeText(
                requireActivity(),
                "Logout",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding!!.mcProfileShare.setOnClickListener { shareApp(requireActivity()) }

        binding!!.mcProfileContact.setOnClickListener { contactApp(requireActivity()) }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}