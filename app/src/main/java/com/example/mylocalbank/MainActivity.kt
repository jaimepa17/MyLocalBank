package com.example.mylocalbank

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.mylocalbank.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val homeFragment = HomeFragment()
    private val gastosFragment = GastosFragment()
    private val ingresosFragment = IngresosFragment()
    private val configFragment = ConfigFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SECURITY CHECK
        if (com.example.mylocalbank.utils.SecurityManager.isBiometricEnabled(this)) {
            if (com.example.mylocalbank.utils.SecurityManager.canAuthenticate(this)) {
                // Hide content initially
                binding.root.visibility = android.view.View.INVISIBLE

                com.example.mylocalbank.utils.SecurityManager.authenticate(
                        this,
                        onSuccess = {
                            binding.root.visibility = android.view.View.VISIBLE
                            safeCheckPaydays()
                        },
                        onError = {
                            // Fallback: Don't close app, just show content
                            binding.root.visibility = android.view.View.VISIBLE
                            android.widget.Toast.makeText(
                                            this,
                                            "Aviso: Autenticación no completada",
                                            android.widget.Toast.LENGTH_SHORT
                                    )
                                    .show()
                            safeCheckPaydays()
                        }
                )
            } else {
                // Fallback: Biometric enabled but not working/available
                binding.root.visibility = android.view.View.VISIBLE // Ensure visible
                android.widget.Toast.makeText(
                                this,
                                "⚠️ Biometría no disponible",
                                android.widget.Toast.LENGTH_LONG
                        )
                        .show()
                safeCheckPaydays()
            }
        } else {
            safeCheckPaydays()
        }

        // Show home by default
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, homeFragment)
                    .commit()
        }

        setupBottomNav()
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragment =
                    when (item.itemId) {
                        R.id.nav_home -> homeFragment
                        R.id.nav_gastos -> gastosFragment
                        R.id.nav_ingresos -> ingresosFragment
                        R.id.nav_config -> configFragment
                        else -> homeFragment
                    }

            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit()

            true
        }

        binding.bottomNav.selectedItemId = R.id.nav_home
    }

    private fun safeCheckPaydays() {
        try {
            com.example.mylocalbank.utils.PaydayManager.checkPaydays(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
