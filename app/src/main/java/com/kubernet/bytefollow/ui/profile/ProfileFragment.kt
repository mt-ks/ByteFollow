package com.kubernet.bytefollow.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import com.bumptech.glide.Glide
import com.kubernet.bytefollow.R
import com.kubernet.bytefollow.databinding.FragmentProfileBinding
import com.kubernet.bytefollow.helpers.SessionChecker
import com.kubernet.bytefollow.helpers.SessionInterface
import com.kubernet.bytefollow.helpers.UserRequireChecker
import com.kubernet.bytefollow.service.*
import com.kubernet.bytefollow.storage.UserStorage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ProfileFragment : SessionInterface, Fragment() {
    private val viewModel : ProfileViewModel by activityViewModels()
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var compositeDisposable : CompositeDisposable?= null
    private lateinit var userStorage : UserStorage
    private lateinit var navController: NavController

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
        navController = Navigation.findNavController(view)

        viewModel.userDetail.observe(viewLifecycleOwner,{
            try{
                binding.usernameField.text = it.user.uniqueId
                binding.followersCount.text = it.stats.followerCount.toString()
                binding.followingCount.text = it.stats.followingCount.toString()
                binding.likesCount.text = it.stats.heartCount.toString()
                Glide.with(requireContext()).load(it.user.avatarLarger).into(binding.userAvatar)
            }catch (e : Exception){ }
        });

        binding.swipeRefreshProfile.setOnRefreshListener {
            getMeInfo()
            getProfileDetail()
            SessionChecker(requireActivity(),this)
        }

        binding.earnCoinButton.setOnClickListener { navController.navigateSafe(R.id.action_profileFragment_to_reactionFragment) }
        binding.newOrderArea.setOnClickListener { navController.navigateSafe(R.id.action_profileFragment_to_searchFragment) }
        binding.ordersArea.setOnClickListener { navController.navigateSafe(R.id.action_profileFragment_to_ordersFragment) }
        binding.promotionArea.setOnClickListener { navController.navigateSafe(R.id.action_profileFragment_to_promotionFragment) }
        binding.referenceArea.setOnClickListener { navController.navigateSafe(R.id.action_profileFragment_to_referenceFragment) }
    }

    fun NavController.navigateSafe(
        @IdRes resId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = null,
        navExtras: Navigator.Extras? = null
    ) {
        val action = currentDestination?.getAction(resId) ?: graph.getAction(resId)
        if (action != null && currentDestination?.id != action.destinationId) {
            navigate(resId, args, navOptions, navExtras)
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
                val error = (BFErrorHandler(requireActivity(),it))
                Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT).show()
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

    override fun onSessionExpired() {

    }


}