package com.example.lonelyshop.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.lonelyshop.data.Address
import com.example.lonelyshop.databinding.FragmentAddressBinding
import com.example.lonelyshop.util.Resource
import com.example.lonelyshop.viewmodels.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class AddressFragment:Fragment() {
    private lateinit var binding:FragmentAddressBinding
    val viewModels by viewModels<AddressViewModel>()
    val args by navArgs<AddressFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModels.addNewAddress.collectLatest {
                when(it){
                    is Resource.Loading->{ binding.progressbarAddress.visibility = View.VISIBLE}
                    is Resource.Success->{ binding.progressbarAddress.visibility = View.INVISIBLE
                    findNavController().navigateUp()
                    }
                    is Resource.Error->{
                       Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit

                }
            }
            lifecycleScope.launchWhenStarted {
               viewModels.error.collectLatest {
                   Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
               }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            val address = args.address
        if (address == null){
           binding.buttonDelelte.visibility = View.GONE

        }else{
            binding.apply {
                edAddressTitle.setText(address.addressTitle)
                edFullName.setText(address.fullName)
                edStreet.setText(address.street)
                edPhone.setText(address.phone)
                edCity.setText(address.city)
                edState.setText(address.state)
            }
        }
        binding.apply {
            binding.buttonSave.setOnClickListener {
                val addressTitle = binding.edAddressTitle.text.toString()
                val fullName = edFullName.text.toString()
                val street = edStreet.text.toString()
                val phone = edPhone.text.toString()
                val city = edCity.text.toString()
                val state = edState.text.toString()
               val address = Address(addressTitle,fullName,street,phone,city,state)
                viewModels.addAddress(address)

            }
        }

    }
}