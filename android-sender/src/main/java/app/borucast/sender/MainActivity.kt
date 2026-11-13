package app.borucast.sender

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF090D18)
private val Panel = Color(0xFF151D2D)
private val Cyan = Color(0xFF59D9E8)
private val Muted = Color(0xFF9AA8BD)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(9, 13, 24)
        window.navigationBarColor = android.graphics.Color.rgb(9, 13, 24)
        setContent { BoruTheme { BoruHome() } }
    }
}

@Composable
private fun BoruTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(primary = Cyan, background = Ink, surface = Panel,
            onBackground = Color.White, onSurface = Color.White),
        content = content
    )
}

@Composable
private fun BoruHome() {
    var selected by remember { mutableStateOf("Cihazlar") }
    var pickedName by remember { mutableStateOf<String?>(null) }
    var showInfo by remember { mutableStateOf(false) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { pickedName = it.lastPathSegment ?: "Dosya seçildi" }
    }
    Scaffold(
        containerColor = Ink,
        bottomBar = {
            NavigationBar(containerColor = Panel, contentColor = Color.White) {
                listOf("Cihazlar", "Medya", "Ayarlar").forEach { item ->
                    NavigationBarItem(
                        selected = selected == item, onClick = { selected = item },
                        icon = { Icon(when (item) {
                            "Medya" -> Icons.Rounded.PermMedia
                            "Ayarlar" -> Icons.Rounded.Settings
                            else -> Icons.Rounded.Tv
                        }, contentDescription = item) },
                        label = { Text(item) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Cyan,
                            selectedTextColor = Cyan, indicatorColor = Color(0xFF233447),
                            unselectedIconColor = Muted, unselectedTextColor = Muted)
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(46.dp).background(
                        Brush.linearGradient(listOf(Cyan, Color(0xFF3477B9))),
                        RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Cast, null, tint = Ink, modifier = Modifier.size(27.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("BÖRÜ CAST", fontSize = 22.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
                        Text("Ekranını özgür bırak.", color = Muted, fontSize = 12.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { showInfo = true }) { Icon(Icons.Rounded.Info, "Bilgi", tint = Muted) }
                }
            }
            item {
                Column(Modifier.fillMaxWidth().background(
                    Brush.linearGradient(listOf(Color(0xFF18384A), Color(0xFF17243A), Panel)),
                    RoundedCornerShape(24.dp)).padding(20.dp)) {
                    Text("BÜYÜK EKRANA\nBAĞLAN", fontSize = 26.sp, lineHeight = 31.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(8.dp))
                    Text("Aynı Wi-Fi ağına bağlı cihazları bul ve yayın deneyimini başlat.", color = Muted, fontSize = 13.sp)
                    Spacer(Modifier.height(18.dp))
                    Button(onClick = { selected = "Cihazlar"; showInfo = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan, contentColor = Ink),
                        shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().height(52.dp)) {
                        Icon(Icons.Rounded.Search, null); Spacer(Modifier.width(8.dp))
                        Text("TV CİHAZI ARA", fontWeight = FontWeight.Bold)
                    }
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("HIZLI ERİŞİM", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Spacer(Modifier.weight(1f))
                    Text("0.1.0 • İlk sürüm", color = Muted, fontSize = 11.sp)
                }
            }
            item { FeatureCard(Icons.Rounded.PhotoLibrary, "Fotoğraf ve video", "Büyük ekranda görüntüle") { picker.launch(arrayOf("image/*", "video/*")) } }
            item { FeatureCard(Icons.Rounded.MusicNote, "Müzik", "Ses dosyalarını seç") { picker.launch(arrayOf("audio/*")) } }
            item { FeatureCard(Icons.Rounded.FolderOpen, "Dosya gönder", "Telefonundan dosya seç") { picker.launch(arrayOf("*/*")) } }
            item { FeatureCard(Icons.Rounded.ScreenShare, "Ekran yansıtma", "Telefon ekranını paylaş") { showInfo = true } }
            item {
                Column(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(18.dp)).padding(16.dp)) {
                    Text("Seçilen içerik", color = Muted, fontSize = 12.sp)
                    Spacer(Modifier.height(5.dp))
                    Text(pickedName ?: "Henüz dosya seçilmedi", fontWeight = FontWeight.SemiBold)
                }
            }
            item { Text("Güvenli yerel bağlantı • İnternet üzerinden aktarım yok", color = Muted, fontSize = 11.sp) }
        }
    }
    if (showInfo) {
        AlertDialog(onDismissRequest = { showInfo = false },
            title = { Text("BÖRÜ CAST • 0.1.0") },
            text = { Text("Bu ilk geliştirme sürümünde arayüz ve Android dosya seçimi hazırlandı. TV keşfi, eşleştirme, gerçek aktarım ve ekran yansıtma sonraki adımlarda etkinleştirilecek.") },
            confirmButton = { TextButton(onClick = { showInfo = false }) { Text("Tamam") } },
            containerColor = Panel, titleContentColor = Color.White, textContentColor = Muted)
    }
}

@Composable
private fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String, subtitle: String, onClick: () -> Unit
) {
    Row(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(18.dp))
        .clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(48.dp).background(Color(0xFF203448), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Cyan, modifier = Modifier.size(25.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(3.dp))
            Text(subtitle, color = Muted, fontSize = 12.sp)
        }
        Icon(Icons.Rounded.ChevronRight, null, tint = Muted)
    }
}
