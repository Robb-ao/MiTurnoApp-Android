package com.example.miturnoapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.miturnoapp.databinding.ActivitySessionBinding

class SessionActivity : AppCompatActivity() {

    // ViewBinding para controlar la pantalla
    private lateinit var binding: ActivitySessionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflamos y activamos la vista
        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val savedName = sharedPreferences.getString("user_name", null)

        // Saltarse login si ya está registrado
        if (savedName != null) {
            navigateToMain(savedName)
            return
        }

        // Detectamos el clic
        binding.btnGetStarted.setOnClickListener {
            val enteredName = binding.etName.text.toString().trim()

            if (enteredName.isEmpty()) {
                // Pequeña validación
                Toast.makeText(this, "Please enter your name to continue", Toast.LENGTH_SHORT).show()
            } else {
                // Guardamos el nombre localmente
                val editor = sharedPreferences.edit()
                editor.putString("user_name", enteredName)
                editor.apply()

                // Saltamos a calendario
                navigateToMain(enteredName)
            }
        }
    }

    private fun navigateToMain(userName: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("USER_NAME_EXTRA", userName)
        }
        startActivity(intent)
        finish()
    }
}
