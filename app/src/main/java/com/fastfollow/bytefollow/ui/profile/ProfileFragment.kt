package com.fastfollow.bytefollow.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fastfollow.bytefollow.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private val viewModel : ProfileViewModel by activityViewModels()
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        return _binding!!.root
    }
}