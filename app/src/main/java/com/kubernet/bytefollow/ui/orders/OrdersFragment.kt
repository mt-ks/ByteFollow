package com.kubernet.bytefollow.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kubernet.bytefollow.adapters.MyOrdersAdapter
import com.kubernet.bytefollow.databinding.FragmentOrdersBinding
import com.kubernet.bytefollow.dialogs.LoadingDialog
import com.kubernet.bytefollow.model.MyOrderDetail
import com.kubernet.bytefollow.service.BFApi
import com.kubernet.bytefollow.service.BFClient
import com.kubernet.bytefollow.service.BFErrorHandler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class OrdersFragment : Fragment() {

    private val viewModel : OrdersViewModel by activityViewModels()
    private var _binding : FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private var compositeDisposable : CompositeDisposable? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingDialog : LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater,container,false)
        compositeDisposable = CompositeDisposable()
        loadingDialog = LoadingDialog(requireActivity())
        recyclerView = binding.orderRecycler
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.myOrders.value == null) checkOrders()
        viewModel.myOrders.observe(viewLifecycleOwner,{
            initRecycler(it)
        })
        binding.orderSwipeRefresh.setOnRefreshListener {
            checkOrders()
        }
    }

    private fun initRecycler(data : List<MyOrderDetail>)
    {

        binding.orderRecycler.visibility    = if(data.isNotEmpty()) View.VISIBLE else View.GONE
        binding.emptyOrderLayout.visibility = if(data.isNotEmpty()) View.GONE else View.VISIBLE

        val adapter = MyOrdersAdapter(requireContext(),data)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun checkOrders()
    {
        binding.orderSwipeRefresh.isRefreshing = true
        val api = (BFClient(requireActivity())).getClient().create(BFApi::class.java)
        compositeDisposable?.add(api.getOrders().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewModel.myOrders.value = it.ordered_list
                binding.orderSwipeRefresh.isRefreshing = false
            },{
                it.printStackTrace()
                val error = BFErrorHandler(requireActivity(),it)
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                binding.orderSwipeRefresh.isRefreshing = false
            }))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable?.clear()
    }
}