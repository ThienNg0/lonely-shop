package com.example.lonelyshop.fragments.lognRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.lonelyshop.R
import com.example.lonelyshop.activities.ShoppingActivity
import com.example.lonelyshop.databinding.FragmentLoginBinding
import com.example.lonelyshop.dialog.setupBottomSheetDiaLog
import com.example.lonelyshop.util.Resource
import com.example.lonelyshop.viewmodels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

          binding.tvDonHaveAccount.setOnClickListener{
              findNavController().navigate(R.id.action_loginFragment2_to_registerFragment)
          }
        binding.apply {
            binding.buttonLogin.setOnClickListener{
               val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    viewModel.login(email, password)
                } else {

                    Snackbar.make(requireView(), "Email and password must not be empty", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        binding.tvForgotPasswordLogin.setOnClickListener{
                setupBottomSheetDiaLog {email ->
                        viewModel.resetPassword(email)

                }
        }
        lifecycleScope.launch {
            viewModel.resetPassword.collect{
                when (it) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        Toast.makeText(requireContext(), "Reset link was sent to your email", Toast.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }

            }
        }

        lifecycleScope.launchWhenStarted {
                viewModel.login.collect{
                    when(it)
                    {
                        is Resource.Loading->{
                                binding.buttonLogin.startAnimation()
                        }
                        is Resource.Success->
                        {
                            binding.buttonLogin.revertAnimation()
                            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP) // Giữ lại flag này
                                startActivity(intent)
                                }
                        }
                        is Resource.Error->
                        {
                            Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()
                            binding.buttonLogin.revertAnimation()
                        }
                        else -> Unit
                    }
                }
        }

    }
}