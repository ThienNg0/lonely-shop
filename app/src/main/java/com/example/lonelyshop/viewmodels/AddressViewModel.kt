package com.example.lonelyshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lonelyshop.data.Address
import com.example.lonelyshop.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth // Inject FirebaseAuth
) : ViewModel() {

    private val _addNewAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addNewAddress = _addNewAddress.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun addAddress(address: Address) {
        val validateInputs = validateInputs(address)
        if (validateInputs) {
            auth.uid?.let { uid -> // Handle nullability
                firestore.collection("user").document(auth.uid!!).collection("address")
                    .document()
                    .set(address).addOnSuccessListener {
                        viewModelScope.launch {
                            _addNewAddress.emit(Resource.Success(address))
                        }
                    }.addOnFailureListener {
                        viewModelScope.launch {
                            _addNewAddress.emit(Resource.Error(it.message.toString()))
                        }
                    }
            } ?: run {
                viewModelScope.launch {
                    _error.emit("User not authenticated.")
                }
            }
        } else {
            viewModelScope.launch {
                _error.emit("All fields are required.")
            }
        }
    }

    private fun validateInputs(address: Address): Boolean {
        return address.addressTitle.isNotEmpty() &&
                address.city.isNotEmpty() &&
                address.state.isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.fullName.isNotEmpty() &&
                address.street.trim().isNotEmpty()
    }
}
