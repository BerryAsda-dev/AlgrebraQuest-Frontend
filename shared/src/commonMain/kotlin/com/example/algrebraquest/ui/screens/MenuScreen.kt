package com.example.algrebraquest.ui.screens

import algrebraquest.shared.generated.resources.Res
import algrebraquest.shared.generated.resources.personaje2
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algrebraquest.domain.Route
import com.example.algrebraquest.domain.WorldsCatalog
import com.example.algrebraquest.domain.emptyPlayer
import com.example.algrebraquest.ui.AppViewModel
import com.example.algrebraquest.ui.ScreenFrame
import com.example.algrebraquest.ui.UiState
import com.example.algrebraquest.ui.theme.Aq
import com.example.algrebraquest.ui.theme.AdaptiveGrid
import com.example.algrebraquest.ui.theme.AqButton
import com.example.algrebraquest.ui.theme.AqButtonStyle
import com.example.algrebraquest.ui.theme.AqCard
import com.example.algrebraquest.ui.theme.BackPill
import com.example.algrebraquest.ui.theme.MessageBanner
import com.example.algrebraquest.ui.theme.ProgressBar
import com.example.algrebraquest.ui.theme.ScreenHeader
import org.jetbrains.compose.resources.painterResource

/**
 * R-05 · Menú principal (MenuScreen.kt).
 * Incluye TopHudBar (vidas, XP, monedas), panel de misión recomendada,
 * botones a Mundos, Tutoriales, Tienda, Logros, Ranking, Reto relámpago, Ajustes y acompañante personaje2.jpg.
 */
@Composable
fun MenuScreen(state: UiState, vm: AppViewModel) {
    val player = state.player ?: emptyPlayer(
        state.session?.username ?: "alumno",
        state.session?.displayName ?: "Estudiante",
    )

    // El servidor (Railway) es la fuente de verdad: se descarga el perfil al entrar y cada 10 s.
    LaunchedEffect(Unit) {
        while (true) {
            vm.refreshPlayer()
            kotlinx.coroutines.delay(10_000)
        }
    }

    val nextMission = WorldsCatalog.nextMission(player, state.worlds)
    val availableChallenges = state.worlds
        .filter { WorldsCatalog.worldUnlocked(player, it, state.worlds) }
        .flatMap { w ->
            w.niveles
                .filter { WorldsCatalog.levelUnlocked(player, w, it) }
                .map { w to it }
        }

    ScreenFrame(state = state, vm = vm, showHud = true) {
        BackPill(onClick = { vm.popTo(Route.Home) }, label = "← Panel de Alumno")

        // ---------- Cabecera y acompañante de aventura (menu.js) ----------
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                ScreenHeader(
                    kicker = "CENTRO DE MISIONES",
                    title = "¡Hola, ${player.displayName}!",
                    subtitle = "Tu próxima respuesta te acerca a la meta.",
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Image(
                    painterResource(Res.drawable.personaje2),
                    contentDescription = "Tu compañero de aventura",
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.5.dp, Aq.Gold, RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop,
                )
                Text("Una pregunta a la vez.", color = Aq.Muted, fontSize = 10.sp)
            }
        }

        MessageBanner(state.error, state.notice)

        // ---------- Panel de Misión Recomendada (mission-panel de menu.js) ----------
        if (nextMission != null) {
            val (mundoMision, nivelMision) = nextMission
            val completadosMundo = WorldsCatalog.completedCount(player, mundoMision)
            val totalMundo = mundoMision.niveles.size.coerceAtLeast(1)
            val progresoMundo = completadosMundo.toFloat() / totalMundo.toFloat()

            AqCard(Modifier.fillMaxWidth(), accent = Aq.Gold) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${mundoMision.icono} ${mundoMision.nombre}",
                        color = Aq.parse(mundoMision.color, Aq.Mint),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "$completadosMundo/${mundoMision.niveles.size} niveles",
                        color = Aq.Muted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(
                    "SIGUIENTE RETO",
                    color = Aq.Mint,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Text(
                    "${nivelMision.icono} Nivel ${nivelMision.id}: ${nivelMision.titulo}",
                    color = Aq.Gold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                ProgressBar(fraction = progresoMundo, color = Aq.Gold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AqButton(
                        text = "▶ Continuar misión",
                        onClick = { vm.navigate(Route.LevelIntro(mundoMision.id, nivelMision.id)) },
                        modifier = Modifier.weight(1f),
                        style = AqButtonStyle.Gold,
                    )
                    AqButton(
                        text = "Ver tutorial",
                        onClick = { vm.navigate(Route.Tutorial(mundoMision.id, nivelMision.id)) },
                        modifier = Modifier.weight(1f),
                        style = AqButtonStyle.Ghost,
                    )
                }
            }
        }

        // ---------- Cuadrícula de accesos principales (menu-shortcuts de menu.js) ----------
        val shortcuts = listOf(
            MenuShortcut("🗺️", "Mundos", "Los 4 mundos de Álgebra", Aq.Green) { vm.navigate(Route.Worlds) },
            MenuShortcut("📚", "Tutoriales", "Temas y videos de apoyo", Aq.Mint) { vm.navigate(Route.TutorialsList) },
            MenuShortcut("🛒", "Tienda", "Trofeos y vidas extra", Aq.Gold) { vm.navigate(Route.Store) },
            MenuShortcut("🏆", "Logros", "Tu vitrina y medallas", Aq.Glow) { vm.navigate(Route.Achievements) },
            MenuShortcut("📊", "Ranking", "Tabla general por XP", Aq.GoldSoft) { vm.navigate(Route.Ranking) },
            MenuShortcut("⚡", "Reto relámpago", "Nivel disponible al azar", Aq.Mint) {
                val flash = availableChallenges.randomOrNull() ?: nextMission
                if (flash != null) vm.navigate(Route.LevelIntro(flash.first.id, flash.second.id))
                else vm.navigate(Route.Worlds)
            },
            MenuShortcut("⚙️", "Ajustes", "Nombre, biometría y datos", Aq.Border) { vm.navigate(Route.Settings) },
            MenuShortcut("⏻", "Cerrar sesión", "Salir al inicio de sesión", Aq.Red) { vm.logout() },
        )
        AdaptiveGrid(
            items = shortcuts,
            wideColumns = 3,
            tabletColumns = 2,
            phoneColumns = 1,
            spacing = 10.dp,
        ) { shortcut, itemModifier ->
            MenuShortcutCard(
                icon = shortcut.icon,
                title = shortcut.title,
                subtitle = shortcut.subtitle,
                accent = shortcut.accent,
                onClick = shortcut.onClick,
                modifier = itemModifier,
            )
        }
    }
}

private data class MenuShortcut(
    val icon: String,
    val title: String,
    val subtitle: String,
    val accent: androidx.compose.ui.graphics.Color,
    val onClick: () -> Unit,
)

@Composable
private fun MenuShortcutCard(
    icon: String,
    title: String,
    subtitle: String,
    accent: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Aq.Panel)
            .border(1.5.dp, accent.copy(alpha = 0.65f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(icon, fontSize = 26.sp)
        Text(title, color = Aq.Text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(subtitle, color = Aq.Muted, fontSize = 12.sp)
    }
}
