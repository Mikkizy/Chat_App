package com.lord_ukaka.chatapplication.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lord_ukaka.chatapplication.util.Constants.MINIMUM_USERNAME_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.await
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val client: ChatClient
) : ViewModel() {

    private val _loginEvent = MutableSharedFlow<LoginEvents>()
    val loginEvent = _loginEvent.asSharedFlow()

    private fun isValidUserName(username: String) =
        username.length >= MINIMUM_USERNAME_LENGTH

    fun connectUser(username: String) {
        val trimmedUserName = username.trim()
        viewModelScope.launch {
            if (isValidUserName(trimmedUserName)) {
                val result = client.connectGuestUser(
                    trimmedUserName,
                    trimmedUserName
                ).await()
                if (result.isError) {
                    _loginEvent.emit(LoginEvents.ErrorLogin(result.error().message ?: "Unknown Error Occured"))
                    return@launch
                }
                _loginEvent.emit(LoginEvents.Success)
            } else {
                _loginEvent.emit(LoginEvents.ErrorInputTooShort)
            }
        }
    }

    sealed class LoginEvents {
        object ErrorInputTooShort : LoginEvents()
        data class ErrorLogin(val error: String) : LoginEvents()
        object Success : LoginEvents()
    }
}