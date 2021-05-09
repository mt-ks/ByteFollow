package com.fastfollow.bytefollow.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.fastfollow.bytefollow.R
import com.fastfollow.bytefollow.databinding.FragmentHomeBinding
import com.fastfollow.bytefollow.databinding.FragmentLoginBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.goLogin.setOnClickListener {
            val navController = Navigation.findNavController(view)
            navController.navigate(R.id.action_homeFragment_to_loginFragment)
        }
    }
}