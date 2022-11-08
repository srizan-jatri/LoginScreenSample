package com.srizan.loginscreensample

import android.util.Patterns
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    val loginState = mutableStateOf<LoginScreenSate>(LoginScreenSate.Idle)

    private val _events = Channel<LoginEvent>()
    val events = _events.receiveAsFlow()

    private val _email = mutableStateOf("")
    val email: State<String> = _email
    var emailSupportText by mutableStateOf("")

    private val _password = mutableStateOf("")
    val password: State<String> = _password
    val passwordSupportText by derivedStateOf {
        if (_password.value.isEmpty()) "Password is Empty"
        else if (_password.value.length < 6) "Enter at least 6 characters"
        else ""
    }

    private val isEmailValid by derivedStateOf {
        Patterns.EMAIL_ADDRESS.matcher(email.value).matches()
    }
    private val isPasswordValid by derivedStateOf {
        _password.value.length > 5
    }
    //https://developer.android.com/jetpack/compose/side-effects#derivedstateof

    fun updateUserName(userName: String) {
        _email.value = userName
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun login() {
        viewModelScope.launch {
            if (isEmailValid && isPasswordValid) {
                loginState.value = LoginScreenSate.Loading
                delay(3000)
                loginState.value = listOf(LoginScreenSate.Success, LoginScreenSate.Error).random()
            } else {
                _events.send(LoginEvent.ShowToast("Use valid credential!"))
                emailSupportText = if (_email.value.isEmpty()) "Email is Empty"
                else if (!isEmailValid) "Invalid Email"
                else ""
                // passwordSupportText = if (_password.value.isEmpty()) "Password is Empty" else if (_password.value.length < 6) "Enter at least 6 characters" else ""
            }
        }
    }

    fun restoreIdleState() {
        loginState.value = LoginScreenSate.Idle
    }
}

sealed interface LoginScreenSate {
    object Idle : LoginScreenSate
    object Loading : LoginScreenSate
    object Error : LoginScreenSate
    object Success : LoginScreenSate
}

sealed interface LoginEvent{
    data class ShowToast(val message: String): LoginEvent
    data class ShowSnackBar(val message: String): LoginEvent
}