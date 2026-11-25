package com.example.algrebraquest.platform

import androidx.compose.runtime.Composable
import com.example.algrebraquest.data.RAILWAY_BASE_URL

/** El navegador de escritorio no expone huella ni rostro a la app. */
private class WebBiometricGate : BiometricGate {
    override fun status() = BiometricStatus.UNSUPPORTED
    override suspend fun authenticate(
        title: String,
        subtitle: String,
        mode: BiometricMode,
    ): BiometricResult = BiometricResult.Error("La biometría no está disponible en el navegador. Usa tu usuario y contraseña.")
}

/** Almacén en memoria (se pierde al recargar la página; nunca se guarda la contraseña). */
private class WebMemorySecureStore : SecureStore {
    private val credentials = mutableListOf<BiometricCredential>()
    override fun saveBiometricCredential(credential: BiometricCredential) {
        credentials.removeAll { it.username.equals(credential.username, ignoreCase = true) }
        credentials.add(0, credential)
    }
    override fun loadBiometricCredential() = credentials.firstOrNull()
    override fun loadAllBiometricCredentials(): List<BiometricCredential> = credentials.toList()
    override fun removeBiometricCredential(username: String) {
        credentials.removeAll { it.username.equals(username, ignoreCase = true) }
    }
    override fun clearBiometricCredential() { credentials.clear() }
    override var serverUrl: String? = null
}

actual fun platformServices(): PlatformServices =
    PlatformServices(WebBiometricGate(), WebMemorySecureStore(), RAILWAY_BASE_URL)

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // En escritorio no hay botón "atrás" del sistema.
}
