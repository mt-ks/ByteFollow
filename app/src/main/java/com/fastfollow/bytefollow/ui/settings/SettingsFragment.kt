package com.fastfollow.bytefollow.ui.settings

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fastfollow.bytefollow.AppActivity
import com.fastfollow.bytefollow.MainActivity
import com.fastfollow.bytefollow.R
import com.fastfollow.bytefollow.databinding.FragmentSettingsBinding
import com.fastfollow.bytefollow.storage.UserStorage
import io.reactivex.disposables.CompositeDisposable


class SettingsFragment : Fragment() {

    private var _binding : FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var compositeDisposable : CompositeDisposable? = null
    private lateinit var userStorage: UserStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)
        compositeDisposable = CompositeDisposable()
        userStorage = UserStorage(requireContext())
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.goToTelegram.setOnClickListener { goTelegram() }
        binding.logout.setOnClickListener { logout() }
        binding.setLanguage.setOnClickListener { languageDialog() }
    }


    private fun goTelegram()
    {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/bytefollow")))
    }

    fun logout()
    {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.are_you_sure_logout))
            .setPositiveButton(R.string.yes) { _, _ -> logoutHandler() }
            .setNegativeButton(R.string.no) { p0, _ -> p0.cancel() }
            .show()
    }

    private fun logoutHandler()
    {
        userStorage.clearDB()
        startActivity(Intent(activity, MainActivity::class.java))
        activity?.finish()
    }

    private fun languageDialog()
    {
        val alertBuilder = AlertDialog.Builder(requireContext())
        alertBuilder.setTitle("Language")
        val items = arrayOf("Turkish - TR","English - EN")
        alertBuilder.setSingleChoiceItems(items, -1) { p0, p1 ->
            when (p1) {
                0 -> changeLanguage("tr",p0)
                1 -> changeLanguage("en",p0)
            }

        }
        val alertDialog = alertBuilder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun changeLanguage(language : String, dialogInterface: DialogInterface)
    {
        userStorage.customLanguage = language
        dialogInterface.cancel()
        startActivity(Intent(activity, AppActivity::class.java))
        activity?.finish()
    }
}