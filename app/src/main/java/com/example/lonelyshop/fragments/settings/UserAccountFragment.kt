package com.example.lonelyshop.fragments.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.lonelyshop.data.User
import com.example.lonelyshop.databinding.FragmentUserAccountBinding
import com.example.lonelyshop.util.Resource
import com.example.lonelyshop.viewmodels.UserAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UserAccountFragment : Fragment() {
    private lateinit var binding: FragmentUserAccountBinding
    private val viewModel: UserAccountViewModel by viewModels()
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let {
                imageUri = it
                Glide.with(this).load(imageUri).into(binding.imageUser)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> showUserLoading()
                    is Resource.Success -> {
                        hideUserLoading()
                        showUserInformation(it.data!!)
                    }
                    is Resource.Error -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.updateInfo.collectLatest {
                when (it) {
                    is Resource.Loading -> binding.buttonSave.startAnimation()
                    is Resource.Success -> {
                        binding.buttonSave.revertAnimation()
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    else -> Unit
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            binding.apply {
                val firstName = edFirstName.text.toString().trim()
                val lastName = edLastName.text.toString().trim()
                val email = edEmail.text.toString().trim()
                val user = User(firstName, lastName, email)
                viewModel.updateUser(user, imageUri)
            }
        }

        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            imageActivityResultLauncher.launch(intent)
        }
    }

    private fun showUserInformation(data: User) {
        binding.apply {
            if (data.imagePath != null) {
                Glide.with(this@UserAccountFragment)
                    .load(data.imagePath)
                    .placeholder(ColorDrawable(Color.BLACK)) // Placeholder for better UX
                    .into(imageUser)
            }
            edFirstName.setText(data.firstName)
            edLastName.setText(data.lastName)
            edEmail.setText(data.email)
        }
    }


    private fun showUserLoading() {
        setVisibility(View.VISIBLE, View.INVISIBLE)
    }

    private fun hideUserLoading() {
        setVisibility(View.GONE, View.VISIBLE)
    }

    private fun setVisibility(loadingVisibility: Int, contentVisibility: Int) {
        binding.apply {
            progressbarAccount.visibility = loadingVisibility
            imageUser.visibility = contentVisibility
            imageEdit.visibility = contentVisibility
            edFirstName.visibility = contentVisibility
            edLastName.visibility = contentVisibility
            edEmail.visibility = contentVisibility
            tvUpdatePassword.visibility = contentVisibility
            buttonSave.visibility = contentVisibility
        }
    }
}
