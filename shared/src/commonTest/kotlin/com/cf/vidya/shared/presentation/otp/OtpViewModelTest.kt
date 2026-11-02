package com.cf.vidya.shared.presentation.otp

import app.cash.turbine.test
import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.data.fake.FakeAuthRepository
import com.cf.vidya.shared.domain.model.AuthRules
import com.cf.vidya.shared.domain.usecase.SendOtpUseCase
import com.cf.vidya.shared.domain.usecase.VerifyOtpUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
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
class OtpViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(): OtpViewModel {
        val repository = FakeAuthRepository()
        return OtpViewModel("9650548592", SendOtpUseCase(repository), VerifyOtpUseCase(repository))
    }

    @Test
    fun wrongOtpShowsError() = runTest {
        val vm = viewModel()
        vm.onEvent(OtpEvent.OtpChanged("111111"))
        vm.onEvent(OtpEvent.LoginClicked)
        advanceUntilIdle()

        assertEquals(AppError.InvalidOtp, vm.state.value.error)
        assertFalse(vm.state.value.isVerifying)
    }

    @Test
    fun correctOtpOpensHome() = runTest {
        val vm = viewModel()
        vm.onEvent(OtpEvent.OtpChanged(FakeAuthRepository.VALID_OTP))

        vm.effects.test {
            vm.onEvent(OtpEvent.LoginClicked)
            assertEquals(OtpEffect.OpenHome, awaitItem())
        }
    }

    @Test
    fun resendUnlocksAfterCountdownAndRestartsIt() = runTest {
        val vm = viewModel()
        advanceTimeBy(10_001)
        assertEquals(AuthRules.RESEND_SECONDS - 10, vm.state.value.resendSecondsLeft)
        assertFalse(vm.state.value.canResend)

        advanceUntilIdle()
        assertTrue(vm.state.value.canResend)

        vm.onEvent(OtpEvent.ResendClicked)
        advanceTimeBy(1_000) // the fake send takes 0.8 s
        assertEquals(AuthRules.RESEND_SECONDS, vm.state.value.resendSecondsLeft)
    }

    @Test
    fun editNumberGoesBack() = runTest {
        val vm = viewModel()
        vm.effects.test {
            vm.onEvent(OtpEvent.EditNumberClicked)
            assertEquals(OtpEffect.GoBack, awaitItem())
        }
    }
}
