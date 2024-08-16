package com.example.lonelyshop.viewmodels

import androidx.lifecycle.ViewModel
import com.example.lonelyshop.data.User
import com.example.lonelyshop.util.Constants.USER_CONLLECTION
import com.example.lonelyshop.util.RegisterFieldState
import com.example.lonelyshop.util.RegisterValidation
import com.example.lonelyshop.util.Resource
import com.example.lonelyshop.util.validateEmail
import com.example.lonelyshop.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {


    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())

    val register: Flow<Resource<User>> = _register
    private  val _validation = Channel<RegisterFieldState>()
             val validation = _validation.receiveAsFlow()

    fun createAccountWithEmailAndPassword(user: User, password: String) {
        checkValidation(user, password)
        if(checkValidation(user,password)) {
            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        saveUserInfo(it.uid,user)

                    }



                }
                .addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())

                }
        }else
        {
            val registerFieldState = RegisterFieldState(
                validateEmail(user.email),
                validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldState)
            }
        }
    }

    private fun saveUserInfo(userUid: String,user:User) {
            db.collection(USER_CONLLECTION)
                .document(userUid)
                .set(user)
                .addOnSuccessListener {
                    _register.value = Resource.Success(user)
                }.addOnFailureListener{
                    _register.value = Resource.Error(it.message.toString())
                }

    }


    private fun checkValidation(user: User, password: String):Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister = emailValidation is RegisterValidation.Sucsses &&
                passwordValidation is RegisterValidation.Sucsses
        return shouldRegister
    }
}
