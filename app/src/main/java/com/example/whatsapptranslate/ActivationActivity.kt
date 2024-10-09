package com.example.whatsapptranslate

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ActivationActivity : AppCompatActivity() {

    // Declarar el código de solicitud de permiso de overlay
    private val REQUEST_CODE_OVERLAY_PERMISSION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activation)

        // Verificar el permiso de overlay (ventana flotante)
        if (!Settings.canDrawOverlays(this)) {
            requestOverlayPermission()
        }

        findViewById<Button>(R.id.btn_open_whatsapp).setOnClickListener {
            if (isAccessibilityServiceEnabled()) {
                if (Settings.canDrawOverlays(this)) {
                    openWhatsAppOrPlayStore()
                } else {
                    // Pedir permiso para mostrar ventanas flotantes si no está habilitado
                    requestOverlayPermission()
                }
            } else {
                // Pedir al usuario que habilite el servicio de accesibilidad
                promptEnableAccessibilityService()
            }
        }
    }

    private fun requestOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "El permiso de ventana flotante es necesario.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permiso de ventana flotante concedido.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        // Comprobar si el servicio de accesibilidad está activado
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices != null && enabledServices.contains(WhatsAppAccessibilityService::class.java.name)
    }

    private fun promptEnableAccessibilityService() {
        Toast.makeText(this, "Por favor, activa el servicio de accesibilidad para WhatsApp Translate.", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private fun openWhatsAppOrPlayStore() {
        val whatsappPackageName = "com.whatsapp"
        val intent = packageManager.getLaunchIntentForPackage(whatsappPackageName)

        if (intent != null) {
            // WhatsApp está instalado, lo abrimos
            startActivity(intent)
            // Enviar un broadcast para mostrar la burbuja flotante
            sendBroadcast(Intent("com.example.whatsapptranslate.SHOW_FLOATING_BUBBLE"))
            Toast.makeText(this, "Abriendo WhatsApp", Toast.LENGTH_SHORT).show()
        } else {
            // WhatsApp no está instalado, redirigimos a la Play Store
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$whatsappPackageName")))
            } catch (e: android.content.ActivityNotFoundException) {
                // Si la Play Store no está instalada, abrir en el navegador
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$whatsappPackageName")))
            }
            Toast.makeText(this, "WhatsApp no está instalado. Redirigiendo a la Play Store.", Toast.LENGTH_LONG).show()
        }
    }
}
