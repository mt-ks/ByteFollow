package com.fastfollow.bytefollow.dialogs

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.fastfollow.bytefollow.BuildConfig
import com.fastfollow.bytefollow.R
import com.fastfollow.bytefollow.databinding.DialogNeedUpgradeBinding
import com.fastfollow.bytefollow.helpers.NextUI
import com.fastfollow.bytefollow.helpers.NextUInterface
import java.io.File


class UpdateDialog(
    private val activity: Activity,
    private val route : String,
    private val downloadName : String
) : NextUInterface, DialogFragment() {
    private var _binding : DialogNeedUpgradeBinding? = null
    private val binding get() = _binding!!

    override fun getView(): View {
        _binding = DialogNeedUpgradeBinding.inflate(LayoutInflater.from(activity))
        isCancelable = false
        binding.upgrade.setOnClickListener {
            if (!route.contains(".apk")){
                updateManuelHandler()
            }else{
                checkDownloadPermissions()
            }
        }
        return _binding!!.root
    }


    private fun updateManuelHandler()
    {
        try{
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(route)))
        }catch (e : Exception){}
    }


    private fun startDownload(){
        binding.xDownloaded.text = String.format(getString(R.string.downloaded_s),"%0")
        binding.downloadProgressArea.visibility = View.VISIBLE
        binding.downloadingProgress.progress = 0
        binding.upgrade.visibility = View.GONE
        (NextUI(route,downloadName,activity,this).download())
    }

    private fun checkDownloadPermissions()
    {
        if( checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ||
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ){

            ActivityCompat.requestPermissions(activity, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE),1)

        }else{
            startDownload()
        }

    }

    private fun checkPermission(permission : String) : Boolean
    {
        return ContextCompat.checkSelfPermission(activity,permission) != PackageManager.PERMISSION_GRANTED;
    }


    override fun onDownloadFinished(downloadPath: String) {

        activity.runOnUiThread {
            binding.downloadProgressArea.visibility = View.GONE
            binding.upgrade.visibility = View.VISIBLE
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val intent = Intent(Intent.ACTION_VIEW)
            val file = File(downloadPath)
            val data: Uri = FileProvider.getUriForFile(
                activity,
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )
            intent.setDataAndType(data, "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            activity.startActivity(intent)
        } else {
            val myIntent = Intent(Intent.ACTION_VIEW)
            val file = File(downloadPath)
            val extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString())
            val mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            myIntent.setDataAndType(Uri.fromFile(file), mimetype)
            activity.startActivity(myIntent)
        }

    }

    override fun onProgress(progress: Int) {
        activity.runOnUiThread{
            binding.downloadingProgress.progress = progress
            binding.xDownloaded.text = String.format(getString(R.string.downloaded_s),"%$progress")
        }
    }

    override fun onDownloadError(message: String) {
        activity.runOnUiThread{
            binding.downloadProgressArea.visibility = View.GONE
            binding.upgrade.visibility = View.VISIBLE
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            startDownload()
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}