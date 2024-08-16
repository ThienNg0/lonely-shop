package com.example.lonelyshop.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lonelyshop.LonelyApplication
import com.example.lonelyshop.data.User
import com.example.lonelyshop.util.RegisterValidation
import com.example.lonelyshop.util.Resource
import com.example.lonelyshop.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val store: StorageReference,
    app: Application
) : AndroidViewModel(app) {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())

            try {
                val userDocument = firestore.collection("user").document(auth.uid!!).get().await()
                val user = userDocument.toObject(User::class.java)
                user?.let {
                    _user.emit(Resource.Success(it))
                } ?: run {
                    _user.emit(Resource.Error("User not found"))
                }
            } catch (e: Exception) {
                _user.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    fun updateUser(user: User, imageUri: Uri?) {
        val areInputsValid = validateEmail(user.email) is RegisterValidation.Sucsses
                && user.firstName.trim().isNotEmpty()
                && user.lastName.trim().isNotEmpty()

        if (!areInputsValid) {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Error("Check your inputs"))
            }
            return
        }

        viewModelScope.launch {
            _updateInfo.emit(Resource.Loading())
        }

        if (imageUri == null) {
            saveUserInformation(user, true)
        } else {
            saveUserInformationWithNewImage(user, imageUri)
        }
    }

    private fun saveUserInformationWithNewImage(user: User, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val imageBitmap = MediaStore.Images.Media.getBitmap(
                    getApplication<LonelyApplication>().contentResolver, imageUri)
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()
                val imageDirectory = store.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageByteArray).await()
                val imageUrl = result.storage.downloadUrl.await().toString()
                saveUserInformation(user.copy(imagePath = imageUrl), false)
            } catch (e: Exception) {
                viewModelScope.launch {
                    _updateInfo.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    private fun saveUserInformation(user: User, shouldRetrieveOldImage: Boolean) {
        viewModelScope.launch {
            try {
                firestore.runTransaction { transaction ->
                    val documentRef = firestore.collection("user").document(auth.uid!!)
                    if (shouldRetrieveOldImage) {
                        val currentUser = transaction.get(documentRef).toObject(User::class.java)
                        val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                        transaction.set(documentRef, newUser)
                    } else {
                        transaction.set(documentRef, user)
                    }
                }.await() // Use await to handle this in a coroutine
                _updateInfo.emit(Resource.Success(user))
            } catch (e: Exception) {
                _updateInfo.emit(Resource.Error(e.message.toString()))
            }
        }
    }
}
