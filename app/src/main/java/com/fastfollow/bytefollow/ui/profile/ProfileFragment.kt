package com.fastfollow.bytefollow.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.fastfollow.bytefollow.databinding.FragmentProfileBinding
import com.fastfollow.bytefollow.helpers.UserRequireChecker
import com.fastfollow.bytefollow.service.BFApi
import com.fastfollow.bytefollow.service.BFClient
import com.fastfollow.bytefollow.service.TKApi
import com.fastfollow.bytefollow.service.TKClient
import com.fastfollow.bytefollow.storage.UserStorage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ProfileFragment : Fragment() {
    private val viewModel : ProfileViewModel by activityViewModels()
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var compositeDisposable : CompositeDisposable?= null
    private lateinit var userStorage : UserStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        compositeDisposable = CompositeDisposable()
        userStorage = UserStorage(requireContext())
        return _binding!!.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userDetail.observe(viewLifecycleOwner,{
            try{
                binding.usernameField.text = it.user.uniqueId
                binding.followersCount.text = it.stats.followerCount.toString()
                binding.followingCount.text = it.stats.followingCount.toString()
                binding.likesCount.text = it.stats.diggCount.toString()
                Glide.with(requireContext()).load(it.user.avatarLarger).into(binding.userAvatar)
            }catch (e : Exception){ }
        });

        binding.swipeRefreshProfile.setOnRefreshListener {
            getMeInfo()
            getProfileDetail()
        }
    }

    private fun getMeInfo()
    {
        val api = BFClient(requireActivity()).getClient().create(BFApi::class.java)
        compositeDisposable?.add(api.me().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                userStorage.meInfo = it
                userStorage.credit = it.credit
                viewModel.currentCredit.value = it.credit
                binding.swipeRefreshProfile.isRefreshing = false;
            },{
                binding.swipeRefreshProfile.isRefreshing = false;
                it.printStackTrace()
            }))
    }

    private fun getProfileDetail()
    {
        val api = TKClient(requireActivity()).getClient().create(TKApi::class.java)
        compositeDisposable?.add(api.getUserInfo(userStorage.username).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val check = UserRequireChecker(it)
                if(check.checkUser())
                {
                    viewModel.userDetail.value = check.userDetail
                    userStorage.userDetail = check.userDetail
                }

            },{
               it.printStackTrace()
            }))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable?.clear()
    }


}