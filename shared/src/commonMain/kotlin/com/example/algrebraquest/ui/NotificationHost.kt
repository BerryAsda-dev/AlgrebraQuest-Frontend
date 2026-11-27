package com.example.algrebraquest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algrebraquest.ui.theme.Aq
import kotlinx.coroutines.delay

/**
 * Avisos emergentes de AlgebraQuest (nivel, vidas, trofeos, sesión y conexión).
 * Se dibuja encima de cualquier pantalla; cada aviso se cierra solo a los 4.5 s o al tocarlo.
 */
@Composable
fun NotificationHost(items: List<AppNotification>, onDismiss: (Long) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.safeContentPadding().padding(12.dp), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier.widthIn(max = 460.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items.forEach { n ->
                LaunchedEffect(n.id) {
                    delay(4_500)
                    onDismiss(n.id)
                }
                NotificationCard(n) { onDismiss(n.id) }
            }
        }
    }
}

@Composable
private fun NotificationCard(n: AppNotification, onClick: () -> Unit) {
    val (accent, icon) = when (n.kind) {
        NotificationKind.SUCCESS -> Aq.Green to "✅"
        NotificationKind.INFO -> Aq.Mint to "ℹ️"
        NotificationKind.WARNING -> Aq.Gold to "⚠️"
        NotificationKind.ERROR -> Aq.Red to "⛔"
        NotificationKind.REWARD -> Aq.Gold to "🏆"
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xF2111D45))
            .border(1.5.dp, accent.copy(alpha = 0.75f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(icon, fontSize = 20.sp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(n.title, color = accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            if (n.message.isNotBlank()) Text(n.message, color = Aq.Text, fontSize = 12.sp)
        }
    }
}
