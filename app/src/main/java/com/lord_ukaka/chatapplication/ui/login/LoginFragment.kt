package com.lord_ukaka.chatapplication.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.lord_ukaka.chatapplication.BindingFragment
import com.lord_ukaka.chatapplication.R
import com.lord_ukaka.chatapplication.databinding.FragmentLoginBinding
import com.lord_ukaka.chatapplication.util.Constants
import com.lord_ukaka.chatapplication.util.Constants.MINIMUM_USERNAME_LENGTH
import com.lord_ukaka.chatapplication.util.navigateSafely
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment: BindingFragment<FragmentLoginBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoginBinding::inflate

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConfirm.setOnClickListener {
            setupConnectingUiState()
            viewModel.connectUser(binding.etUsername.text.toString())
        }

        binding.etUsername.addTextChangedListener {
            binding.etUsername.error = null
        }

        subscribeToEvents()
    }

    private fun subscribeToEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.loginEvent.collect { event ->
                when(event) {
                    is LoginViewModel.LoginEvents.ErrorInputTooShort -> {
                        setupIdleUiState()
                        binding.etUsername.error = getString(
                            R.string.error_username_too_short,
                            MINIMUM_USERNAME_LENGTH
                        )
                    }
                    is LoginViewModel.LoginEvents.ErrorLogin -> {
                        setupIdleUiState()
                        Toast.makeText(requireContext(), event.error, Toast.LENGTH_LONG).show()
                    }
                    is LoginViewModel.LoginEvents.Success -> {
                        setupIdleUiState()
                        Toast.makeText(
                            requireContext(),
                            "You have logged in successfully",
                            Toast.LENGTH_LONG
                        ).show()
                       findNavController().navigateSafely(R.id.navigateToChannelFragment)
                    }
                }
            }
        }
    }

    private fun setupConnectingUiState() {
        binding.progressBar.isVisible = true
        binding.btnConfirm.isEnabled = false
    }

    private fun setupIdleUiState() {
        binding.progressBar.isVisible = false
        binding.btnConfirm.isEnabled = true
    }
}