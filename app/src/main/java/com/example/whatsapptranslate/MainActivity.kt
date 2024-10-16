package com.example.whatsapptranslate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var fabTranslate: FloatingActionButton
    private var isTranslationActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fabTranslate = findViewById(R.id.fab_translate)
        fabTranslate.setOnClickListener {
            toggleTranslation()
        }
    }

    private fun toggleTranslation() {
        isTranslationActive = !isTranslationActive
        if (isTranslationActive) {
            activateTranslation()
        } else {
            deactivateTranslation()
        }
    }

    private fun activateTranslation() {
        // Mostrar el cuadro de diálogo de traducción
        val dialogFragment = TranslationDialogFragment()
        dialogFragment.show(supportFragmentManager, "TranslationDialog")
    }

    private fun deactivateTranslation() {
        // Cerrar el cuadro de diálogo de traducción si está abierto
        val dialogFragment = supportFragmentManager.findFragmentByTag("TranslationDialog") as? TranslationDialogFragment
        dialogFragment?.dismiss()
    }
}
