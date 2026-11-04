package com.cf.vidya.shared.presentation.settings

import app.cash.turbine.test
import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.data.fake.FakeAuthRepository
import com.cf.vidya.shared.domain.repository.AuthRepository
import com.cf.vidya.shared.domain.usecase.LogoutUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
class SettingsViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(repository: AuthRepository = FakeAuthRepository()) =
        SettingsViewModel(LogoutUseCase(repository))

    @Test
    fun logOutAsksForConfirmationFirst() = runTest {
        var calls = 0
        val counting = object : AuthRepository by FakeAuthRepository() {
            override suspend fun logout(): AppResult<Unit> {
                calls++
                return AppResult.Success(Unit)
            }
        }
        val vm = viewModel(counting)

        vm.onEvent(SettingsEvent.LogoutClicked)
        advanceUntilIdle()
        assertTrue(vm.state.value.isLogoutDialogVisible)
        assertEquals(0, calls)

        vm.onEvent(SettingsEvent.LogoutDismissed)
        assertFalse(vm.state.value.isLogoutDialogVisible)
    }

    @Test
    fun confirmingLogsOut() = runTest {
        val vm = viewModel()
        vm.effects.test {
            vm.onEvent(SettingsEvent.LogoutClicked)
            vm.onEvent(SettingsEvent.LogoutConfirmed)
            assertTrue(vm.state.value.isLoggingOut)
            assertEquals(SettingsEffect.LoggedOut, awaitItem())
        }
    }

    @Test
    fun failedLogoutShowsTheErrorAndClosesTheDialog() = runTest {
        val failing = object : AuthRepository by FakeAuthRepository() {
            override suspend fun logout() = AppResult.Failure(AppError.Network)
        }
        val vm = viewModel(failing)
        vm.effects.test {
            vm.onEvent(SettingsEvent.LogoutClicked)
            vm.onEvent(SettingsEvent.LogoutConfirmed)
            assertEquals(SettingsEffect.ShowError(AppError.Network), awaitItem())
            advanceUntilIdle()
            assertFalse(vm.state.value.isLogoutDialogVisible)
            assertFalse(vm.state.value.isLoggingOut)
        }
    }

    @Test
    fun menuRowsSendTheirEffects() = runTest {
        val vm = viewModel()
        vm.effects.test {
            vm.onEvent(SettingsEvent.ShareAppClicked)
            assertEquals(SettingsEffect.ShareApp, awaitItem())
            vm.onEvent(SettingsEvent.ContactUsClicked)
            assertEquals(SettingsEffect.ContactUs, awaitItem())
            vm.onEvent(SettingsEvent.TermsClicked)
            assertEquals(SettingsEffect.OpenTerms, awaitItem())
            vm.onEvent(SettingsEvent.PrivacyPolicyClicked)
            assertEquals(SettingsEffect.OpenPrivacyPolicy, awaitItem())
        }
    }
}
