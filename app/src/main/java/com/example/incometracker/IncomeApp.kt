package com.example.incometracker

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.incometracker.data.SettingsStore
import com.example.incometracker.security.AppLockState
import com.example.incometracker.worker.RecurringIncomeWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class IncomeApp : Application() {

    private val appScope = CoroutineScope(Dispatchers.Default)
    private val lockTimeoutMillis = 3 * 60 * 1000L // 3 minutes

    override fun onCreate() {
        super.onCreate()
        scheduleRecurringIncomeWorker()
        setupLockObserver()
    }

    private fun scheduleRecurringIncomeWorker() {
        val request = PeriodicWorkRequestBuilder<RecurringIncomeWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "recurring_income_generator",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun setupLockObserver() {
        val settings = SettingsStore(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onStop(owner: LifecycleOwner) {
                appScope.launch { settings.setLastBackgroundAt(System.currentTimeMillis()) }
            }

            override fun onStart(owner: LifecycleOwner) {
                appScope.launch {
                    val lockEnabled = settings.appLockEnabled.first()
                    val pinSet = settings.pinSet.first()
                    if (!lockEnabled || !pinSet) {
                        AppLockState.unlock()
                        return@launch
                    }
                    val lastBg = settings.lastBackgroundAt.first()
                    val now = System.currentTimeMillis()
                    val shouldLock = lastBg > 0 && (now - lastBg) >= lockTimeoutMillis
                    if (shouldLock) AppLockState.lock()
                }
            }
        })
    }
}
