package com.example.algrebraquest.ui.screens

import algrebraquest.shared.generated.resources.Res
import algrebraquest.shared.generated.resources.personaje
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algrebraquest.data.Role
import com.example.algrebraquest.platform.BiometricMode
import com.example.algrebraquest.platform.BiometricStatus
import com.example.algrebraquest.ui.AppViewModel
import com.example.algrebraquest.ui.UiState
import com.example.algrebraquest.ui.theme.Aq
import com.example.algrebraquest.ui.theme.AqBackground
import com.example.algrebraquest.ui.theme.AqButton
import com.example.algrebraquest.ui.theme.AqButtonStyle
import com.example.algrebraquest.ui.theme.AqCard
import com.example.algrebraquest.ui.theme.AqTextField
import com.example.algrebraquest.ui.theme.MessageBanner
import org.jetbrains.compose.resources.painterResource

private enum class AuthTab(val title: String) { LOGIN("Iniciar sesión"), REGISTER("Crear cuenta") }

private val USERNAME_REGEX = Regex("^[A-Za-z0-9_.-]{3,20}$")
private const val MIN_PASSWORD = 8

/** Validaciones en tiempo real. Devuelven null si el campo es válido. */
private fun usernameError(v: String): String? = when {
    v.isBlank() -> "El usuario es obligatorio"
    !USERNAME_REGEX.matches(v.trim()) -> "3 a 20 caracteres: letras, números, punto, guion o guion bajo"
    else -> null
}

private fun displayNameError(v: String): String? =
    if (v.trim().length > 20) "Máximo 20 caracteres" else null

private fun passwordError(v: String): String? = when {
    v.isEmpty() -> "La contraseña es obligatoria"
    v.length < MIN_PASSWORD -> "Mínimo $MIN_PASSWORD caracteres (${v.length}/$MIN_PASSWORD)"
    else -> null
}

private fun confirmError(password: String, confirm: String): String? = when {
    confirm.isEmpty() -> "Confirma tu contraseña"
    confirm != password -> "Las contraseñas no coinciden"
    else -> null
}

/**
 * R-01 · Acceso: inicio de sesión (todos los roles, la redirección por rol la decide el servidor)
 * y registro de nuevos ALUMNOS. Responsivo: dos columnas en pantallas anchas, una columna en móviles.
 */
@Composable
fun LoginScreen(state: UiState, vm: AppViewModel) {
    var view by remember { mutableStateOf(LoginView.WELCOME) }
    var selectedRole by remember { mutableStateOf(Role.ALUMNO) }
    var tab by remember { mutableStateOf(AuthTab.LOGIN) }

    LaunchedEffect(Unit) {
        vm.refreshBiometric()
        vm.checkServerHealth()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(Color(0xFF4D081F), Color(0xFF14040A), Color(0xFF0A0205)))),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize().safeContentPadding()) {
            val wide = maxWidth >= 840.dp
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(if (wide) 32.dp else 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                when (view) {
                    LoginView.WELCOME -> WelcomeScreen(
                        onContinue = { view = LoginView.ROLES },
                        modifier = Modifier.widthIn(max = 520.dp),
                    )
                    LoginView.ROLES -> RoleSelectionScreen(
                        onBack = { view = LoginView.WELCOME },
                        onRoleSelected = { role ->
                            selectedRole = role
                            tab = AuthTab.LOGIN
                            vm.clearMessages()
                            view = LoginView.LOGIN
                        },
                        modifier = Modifier.widthIn(max = 520.dp),
                    )
                    LoginView.LOGIN -> WineLoginScreen(
                        state = state,
                        vm = vm,
                        role = selectedRole,
                        tab = tab,
                        onTab = { tab = it; vm.clearMessages() },
                        onBack = { view = LoginView.ROLES },
                        modifier = Modifier.widthIn(max = 520.dp),
                    )
                }
            }
        }
    }
}

private enum class LoginView { WELCOME, ROLES, LOGIN }

private val WineHeader = Brush.verticalGradient(listOf(Color(0xFF4D081F), Color(0xFF21030C)))
private val WineCard = Brush.verticalGradient(listOf(Color(0xFF8C1135), Color(0xFF470518)))

@Composable
private fun WelcomeScreen(onContinue: () -> Unit, modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0xFF24040E))
            .border(1.dp, Color(0xFFB58A52).copy(alpha = 0.42f), RoundedCornerShape(26.dp))
            .clickable(onClick = onContinue),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.fillMaxWidth().background(WineHeader).padding(vertical = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Bienvenidos a", color = Color(0xFFFFF8EE), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("AlgebraQuest", color = Color(0xFFF6CF7D), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, Color(0xFFF6CF7D).copy(alpha = 0.45f), RoundedCornerShape(24.dp)),
            ) {
                Image(
                    painterResource(Res.drawable.personaje),
                    contentDescription = "Personaje de AlgebraQuest",
                    modifier = Modifier.size(250.dp),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        Text(
            "UPT",
            color = Color(0xFFFFF4DE),
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF21030C))
                .padding(vertical = 12.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RoleSelectionScreen(onBack: () -> Unit, onRoleSelected: (Role) -> Unit, modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(26.dp))
            .background(WineCard)
            .border(1.dp, Color(0xFFB58A52).copy(alpha = 0.42f), RoundedCornerShape(26.dp)),
    ) {
        Box(
            Modifier.fillMaxWidth().background(WineHeader).padding(14.dp),
        ) {
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF24040E).copy(alpha = 0.55f))
                    .clickable(onClick = onBack)
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            ) { Text("← Inicio", color = Color(0xFFF6CF7D), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Bienvenidos a", color = Color(0xFFFFF8EE), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("AlgebraQuest", color = Color(0xFFF6CF7D), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        Column(
            Modifier.fillMaxWidth().padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Selecciona tu rol", color = Color(0xFFFFF7EB), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            listOf(Role.ALUMNO, Role.SUPERVISOR, Role.ADMINISTRADOR).forEach { role ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFFFF5E4).copy(alpha = 0.12f))
                        .border(1.dp, Color(0xFFF6CF7D).copy(alpha = 0.38f), RoundedCornerShape(18.dp))
                        .clickable { onRoleSelected(role) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        Modifier
                            .size(58.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFFFF7EA)),
                        contentAlignment = Alignment.Center,
                    ) { Text(role.badge, fontSize = 28.sp) }
                    Column(Modifier.weight(1f)) {
                        Text(role.title, color = Color(0xFFFFF7EB), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        Text(
                            when (role) {
                                Role.ALUMNO -> "Juega y aprende Álgebra"
                                Role.SUPERVISOR -> "Supervisa el progreso"
                                Role.ADMINISTRADOR -> "Administra la plataforma"
                            },
                            color = Color(0xFFF6CF7D),
                            fontSize = 11.sp,
                        )
                    }
                    Text("›", color = Color(0xFFF6CF7D), fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun WineLoginScreen(
    state: UiState,
    vm: AppViewModel,
    role: Role,
    tab: AuthTab,
    onTab: (AuthTab) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier,
) {
    var username by remember(role) {
        mutableStateOf(
            state.biometricCredentials.firstOrNull { it.role.equals(role.name, ignoreCase = true) }?.username
                ?: state.biometricUser
                ?: "",
        )
    }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0xFF24040E))
            .border(1.dp, Color(0xFFB58A52).copy(alpha = 0.42f), RoundedCornerShape(26.dp))
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.fillMaxWidth().background(WineHeader).padding(14.dp),
        ) {
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF24040E).copy(alpha = 0.55f))
                    .clickable(onClick = onBack)
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            ) { Text("← Roles", color = Color(0xFFF6CF7D), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Bienvenidos a", color = Color(0xFFFFF8EE), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("AlgebraQuest", color = Color(0xFFF6CF7D), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "${role.badge}  ${role.title}",
                color = Color(0xFFF6CF7D),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            HealthPill(state, vm)

            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0A0205).copy(alpha = 0.45f))
                    .padding(4.dp),
            ) {
                AuthTab.entries.forEach { item ->
                    val active = item == tab
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(11.dp))
                            .background(if (active) Color(0xFF8C1135) else Color.Transparent)
                            .clickable(enabled = !state.loading) { onTab(item) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(item.title, color = if (active) Color.White else Color(0xFFF6CF7D), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (tab == AuthTab.LOGIN) {
                AqTextField(
                    value = username,
                    onValueChange = { username = it; if (state.error != null) vm.clearMessages() },
                    label = "Usuario",
                    enabled = !state.loading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )
                AqTextField(
                    value = password,
                    onValueChange = { password = it; if (state.error != null) vm.clearMessages() },
                    label = "Contraseña",
                    enabled = !state.loading,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailing = { PasswordToggle(showPassword) { showPassword = !showPassword } },
                )

                MessageBanner(state.error, state.notice)

                AqButton(
                    text = if (state.loading) "Validando con el servidor…" else "Entrar como ${role.title}",
                    onClick = { vm.login(username.trim(), password) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = username.isNotBlank() && password.isNotEmpty() && !state.loading,
                    loading = state.loading,
                )

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AqButton(
                        text = "Huella",
                        onClick = { vm.biometricLogin(BiometricMode.HUELLA, targetRole = role) },
                        modifier = Modifier.weight(1f),
                        style = AqButtonStyle.Gold,
                        enabled = !state.loading && state.biometricStatus == BiometricStatus.AVAILABLE,
                    )
                    AqButton(
                        text = "Rostro",
                        onClick = { vm.biometricLogin(BiometricMode.ROSTRO, targetRole = role) },
                        modifier = Modifier.weight(1f),
                        style = AqButtonStyle.Ghost,
                        enabled = !state.loading && state.biometricStatus == BiometricStatus.AVAILABLE,
                    )
                }
            } else {
                RegisterForm(state, vm, onGoLogin = { onTab(AuthTab.LOGIN) })
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Cabecera / hero
// ---------------------------------------------------------------------------------------------

@Composable
private fun Hero(state: UiState, vm: AppViewModel, large: Boolean) {
    val imageSize: Dp = if (large) 180.dp else 88.dp
    Column(
        Modifier.widthIn(max = 520.dp).fillMaxWidth(),
        horizontalAlignment = if (large) Alignment.Start else Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HealthPill(state, vm)
        if (large) {
            Image(
                painterResource(Res.drawable.personaje),
                contentDescription = "Personaje de Álgebra Quest",
                modifier = Modifier
                    .size(imageSize)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, Aq.Gold, RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop,
            )
            Title(centered = false, big = true)
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Image(
                    painterResource(Res.drawable.personaje),
                    contentDescription = "Personaje de Álgebra Quest",
                    modifier = Modifier
                        .size(imageSize)
                        .clip(RoundedCornerShape(20.dp))
                        .border(2.dp, Aq.Gold, RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop,
                )
                Title(centered = false, big = false)
            }
        }
        Text(
            "2x + 3 = 11   →   x = 4",
            color = Aq.Mint,
            fontSize = if (large) 18.sp else 13.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            "Resuelve misiones de Álgebra Universitaria, gana XP, monedas y trofeos.",
            color = Aq.Muted,
            fontSize = if (large) 16.sp else 13.sp,
            textAlign = if (large) TextAlign.Start else TextAlign.Center,
        )
    }
}

@Composable
private fun Title(centered: Boolean, big: Boolean) {
    Column(horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start) {
        Text("ÁLGEBRA", color = Aq.Text, fontSize = if (big) 40.sp else 24.sp, fontWeight = FontWeight.Bold)
        Text(
            "QUEST",
            color = Aq.Gold,
            fontSize = if (big) 64.sp else 36.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = if (big) 64.sp else 36.sp,
        )
    }
}

@Composable
private fun HealthPill(state: UiState, vm: AppViewModel) {
    val color = when (state.serverHealthy) {
        true -> Aq.Green
        false -> Aq.Red
        null -> Aq.Gold
    }
    val text = when (state.serverHealthy) {
        true -> "● Servidor en línea"
        false -> "● Sin conexión · toca para reintentar"
        null -> "● Conectando…"
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(Aq.Bg.copy(alpha = 0.7f))
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(50))
            .clickable { vm.checkServerHealth() }
            .padding(horizontal = 12.dp, vertical = 5.dp),
    ) { Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
}

// ---------------------------------------------------------------------------------------------
// Panel de autenticación (pestañas)
// ---------------------------------------------------------------------------------------------

@Composable
private fun AuthPanel(state: UiState, vm: AppViewModel, tab: AuthTab, onTab: (AuthTab) -> Unit, modifier: Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AqCard(Modifier.fillMaxWidth(), accent = Aq.Purple) {
            // Pestañas
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Aq.Bg.copy(alpha = 0.6f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                AuthTab.entries.forEach { t ->
                    val active = t == tab
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(11.dp))
                            .background(if (active) Aq.Purple else Color.Transparent)
                            .clickable(enabled = !state.loading) { onTab(t) }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            t.title,
                            color = if (active) Color.White else Aq.Muted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            when (tab) {
                AuthTab.LOGIN -> LoginForm(state, vm, onGoRegister = { onTab(AuthTab.REGISTER) })
                AuthTab.REGISTER -> RegisterForm(state, vm, onGoLogin = { onTab(AuthTab.LOGIN) })
            }
        }

        if (tab == AuthTab.LOGIN) BiometricPanel(state, vm)

        // Servidor al que apunta la app (solo lectura; es el backend de Railway)
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Servidor", color = Aq.Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text(
                state.serverUrl,
                color = Aq.GoldSoft,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { vm.checkServerHealth() },
            )
        }

        Text(
            "Kevin Gabriel Ramirez Vargas · 18-Nov-2026",
            color = Aq.Muted.copy(alpha = 0.6f),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun PasswordToggle(visible: Boolean, onToggle: () -> Unit) {
    Text(
        if (visible) "Ocultar" else "Mostrar",
        modifier = Modifier
            .clickable(onClick = onToggle)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        color = Aq.GoldSoft,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun LoginForm(state: UiState, vm: AppViewModel, onGoRegister: () -> Unit) {
    var username by remember { mutableStateOf(state.biometricUser ?: "") }
    var password by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }
    var usernameTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    val userErr = if (usernameTouched) usernameError(username) else null
    val passErr = if (passwordTouched && password.isBlank()) "La contraseña es obligatoria" else null
    val valid = username.isNotBlank() && password.isNotEmpty() && usernameError(username) == null

    Text("Bienvenido de nuevo", color = Aq.Gold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Text("Ingresa con tu cuenta de alumno, supervisor o administrador.", color = Aq.Muted, fontSize = 12.sp)

    AqTextField(
        value = username,
        onValueChange = { username = it; usernameTouched = true; if (state.error != null) vm.clearMessages() },
        label = "Usuario",
        enabled = !state.loading,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        error = userErr,
    )
    AqTextField(
        value = password,
        onValueChange = { password = it; passwordTouched = true; if (state.error != null) vm.clearMessages() },
        label = "Contraseña",
        enabled = !state.loading,
        visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailing = { PasswordToggle(show) { show = !show } },
        error = passErr,
    )

    MessageBanner(state.error, state.notice)

    AqButton(
        text = if (state.loading) "Validando con el servidor…" else "Entrar →",
        onClick = { vm.login(username.trim(), password) },
        modifier = Modifier.fillMaxWidth(),
        enabled = valid && !state.loading,
        loading = state.loading,
    )

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Text("¿Eres alumno nuevo? ", color = Aq.Muted, fontSize = 13.sp)
        Text(
            "Crea tu cuenta",
            color = Aq.Gold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(enabled = !state.loading, onClick = onGoRegister).padding(4.dp),
        )
    }
}

@Composable
private fun RegisterForm(state: UiState, vm: AppViewModel, onGoLogin: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }
    var usernameTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var confirmTouched by remember { mutableStateOf(false) }

    val userErr = if (usernameTouched) usernameError(username) else null
    val nameErr = displayNameError(displayName)
    val passErr = if (passwordTouched) passwordError(password) else null
    val confirmErr = if (confirmTouched) confirmError(password, confirm) else null
    val valid = usernameError(username) == null && nameErr == null &&
        passwordError(password) == null && confirmError(password, confirm) == null

    Text("Crea tu cuenta de alumno", color = Aq.Gold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Text(
        "Las cuentas nuevas son siempre de alumno. Supervisores y administradores los crea la institución.",
        color = Aq.Muted,
        fontSize = 12.sp,
    )

    AqTextField(
        value = username,
        onValueChange = { username = it; usernameTouched = true; if (state.error != null) vm.clearMessages() },
        label = "Usuario",
        enabled = !state.loading,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        error = userErr,
    )
    AqTextField(
        value = displayName,
        onValueChange = { displayName = it },
        label = "Nombre visible (opcional)",
        enabled = !state.loading,
        error = nameErr,
    )
    AqTextField(
        value = password,
        onValueChange = { password = it; passwordTouched = true; if (state.error != null) vm.clearMessages() },
        label = "Contraseña",
        enabled = !state.loading,
        visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailing = { PasswordToggle(show) { show = !show } },
        error = passErr,
    )
    AqTextField(
        value = confirm,
        onValueChange = { confirm = it; confirmTouched = true; if (state.error != null) vm.clearMessages() },
        label = "Confirmar contraseña",
        enabled = !state.loading,
        visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        error = confirmErr,
    )

    MessageBanner(state.error, state.notice)

    AqButton(
        text = if (state.loading) "Creando cuenta…" else "Crear cuenta y entrar →",
        onClick = { vm.register(username.trim(), displayName.trim(), password) },
        modifier = Modifier.fillMaxWidth(),
        enabled = valid && !state.loading,
        loading = state.loading,
    )

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Text("¿Ya tienes cuenta? ", color = Aq.Muted, fontSize = 13.sp)
        Text(
            "Inicia sesión",
            color = Aq.Gold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(enabled = !state.loading, onClick = onGoLogin).padding(4.dp),
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Sensor biométrico (huella dactilar y reconocimiento facial) por cuenta vinculada
// ---------------------------------------------------------------------------------------------

@Composable
private fun BiometricPanel(state: UiState, vm: AppViewModel) {
    val sensorAvailable = state.biometricStatus == BiometricStatus.AVAILABLE
    var selected by remember(state.biometricCredentials) { mutableStateOf(state.biometricCredentials.firstOrNull()?.username) }
    val cred = state.biometricCredentials.firstOrNull { it.username == selected }

    AqCard(Modifier.fillMaxWidth(), accent = Aq.Mint) {
        Text("Acceso biométrico", color = Aq.Mint, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        if (state.biometricCredentials.isEmpty()) {
            Text(
                "Inicia sesión una vez con contraseña y activa tu huella o rostro para entrar más rápido.",
                color = Aq.Muted,
                fontSize = 12.sp,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                state.biometricCredentials.forEach { c ->
                    val active = c.username == selected
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (active) Aq.Panel2 else Aq.Bg.copy(alpha = 0.5f))
                            .border(1.dp, if (active) Aq.Mint else Aq.Border, RoundedCornerShape(10.dp))
                            .clickable { selected = c.username }
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "${Role.parse(c.role).badge} ${c.displayName} (${c.username})",
                            color = Aq.Text,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                        )
                        Text(Role.parse(c.role).title, color = Aq.Mint, fontSize = 11.sp)
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AqButton(
                    text = "Huella dactilar",
                    onClick = { vm.biometricLogin(BiometricMode.HUELLA, targetUsername = cred?.username) },
                    modifier = Modifier.weight(1f),
                    style = AqButtonStyle.Gold,
                    enabled = !state.loading && sensorAvailable && cred != null,
                )
                AqButton(
                    text = "Reconocimiento facial",
                    onClick = { vm.biometricLogin(BiometricMode.ROSTRO, targetUsername = cred?.username) },
                    modifier = Modifier.weight(1f),
                    style = AqButtonStyle.Ghost,
                    enabled = !state.loading && sensorAvailable && cred != null,
                )
            }
        }

        val note = when {
            state.biometricStatus == BiometricStatus.NOT_ENROLLED ->
                "Registra una huella o rostro en los Ajustes de Android para habilitar el lector."
            !sensorAvailable -> "El sensor biométrico no está disponible en este dispositivo."
            else -> null
        }
        if (note != null) Text(note, color = Aq.Muted, fontSize = 12.sp)
    }
}
