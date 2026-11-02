package com.cf.vidya.shared.presentation.login

import app.cash.turbine.test
import com.cf.vidya.shared.data.fake.FakeAuthRepository
import com.cf.vidya.shared.domain.usecase.SendOtpUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = LoginViewModel(SendOtpUseCase(FakeAuthRepository()))

    @Test
    fun keepsOnlyTenDigits() {
        val vm = viewModel()
        vm.onEvent(LoginEvent.MobileChanged("+91 98765-432109"))
        assertEquals("9198765432", vm.state.value.mobile)
    }

    @Test
    fun numberStartingWithOneIsInvalid() {
        val vm = viewModel()
        vm.onEvent(LoginEvent.MobileChanged("1234567890"))
        assertFalse(vm.state.value.isMobileValid)
        assertTrue(vm.state.value.showInvalidMobile)
    }

    @Test
    fun validNumberOpensOtp() = runTest {
        val vm = viewModel()
        vm.onEvent(LoginEvent.MobileChanged("9650548592"))

        vm.effects.test {
            vm.onEvent(LoginEvent.GetOtpClicked)
            assertEquals(LoginEffect.OpenOtp("9650548592"), awaitItem())
        }
        assertFalse(vm.state.value.isSending)
    }
}
