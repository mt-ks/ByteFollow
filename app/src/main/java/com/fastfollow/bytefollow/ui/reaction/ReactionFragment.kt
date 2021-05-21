package com.fastfollow.bytefollow.ui.reaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fastfollow.bytefollow.databinding.FragmentReactionBinding
import io.reactivex.disposables.CompositeDisposable

class ReactionFragment : Fragment() {
    private var _binding : FragmentReactionBinding? = null
    private val binding get() = _binding!!

    private var compositeDisposable : CompositeDisposable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReactionBinding.inflate(inflater,container,false)
        compositeDisposable = CompositeDisposable()
        return _binding!!.root
    }



}