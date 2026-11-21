package com.vikash.bedtimestories.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vikash.bedtimestories.AppState
import com.vikash.bedtimestories.Screen
import com.vikash.bedtimestories.audio.VoiceBox
import com.vikash.bedtimestories.data.Lang
import com.vikash.bedtimestories.data.Mood
import com.vikash.bedtimestories.data.Story
import com.vikash.bedtimestories.data.StoryLibrary
import com.vikash.bedtimestories.ui.art.ChildPortrait
import com.vikash.bedtimestories.ui.art.Illustration
import java.util.Calendar

@Composable
fun HomeScreen(app: AppState, voice: VoiceBox) {
    var filter by rememberSaveable { mutableStateOf("all") }
    var deleting by remember { mutableStateOf<Story?>(null) }
    val all = app.allStories()
    val shown = when (filter) {
        "all" -> all
        "fav" -> all.filter { it.id in app.favorites }
        "mine" -> all.filter { it.custom }
        "classic" -> all.filter { it.classic }
        else -> all.filter { it.mood.name == filter }
    }
    val featured = remember(app.lang) { val pool = all.filter { !it.custom }; pool[Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % pool.size] }
    val greeting = remember {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Good morning" to "सुप्रभात"
            in 12..16 -> "Good afternoon" to "नमस्ते"
            else -> "Good evening" to "शुभ संध्या"
        }
    }
    val full: (androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope.() -> GridItemSpan) = { GridItemSpan(maxLineSpan) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = full) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("${app.t(greeting.first, greeting.second)}, ${app.childName}", style = MaterialTheme.typography.headlineSmall)
                    Text(app.t("What shall we read tonight?", "आज रात कौन सी कहानी सुनें?"), color = Mist)
                }
                ChildPortrait(
                    app.look,
                    Modifier.size(56.dp).border(2.dp, Gold.copy(alpha = 0.6f), CircleShape).clip(CircleShape).clickable { app.go(Screen.Profile) },
                )
            }
        }

        item(span = full) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .clickable { app.go(Screen.Reader(featured.id)) }
            ) {
                Illustration(featured.scene, featured.cover, app.look, Modifier.fillMaxSize(), animated = true)
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.verticalGradient(listOf(Color(0xCC0B1033), Color.Transparent, Color.Transparent))
                    )
                )
                Column(Modifier.padding(18.dp)) {
                    Text(app.t("TONIGHT’S STORY", "आज की कहानी"), color = Gold, style = MaterialTheme.typography.labelMedium)
                    Text(
                        app.title(featured),
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(0.8f),
                    )
                    Text("${featured.minutes(app.lang)} ${app.t("min", "मिनट")} · ${app.t(featured.mood.label, featured.mood.labelHi)}", color = Mist, style = MaterialTheme.typography.bodySmall)
                }
                Box(
                    Modifier.align(Alignment.BottomEnd).padding(14.dp).size(48.dp).clip(CircleShape).background(Gold),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Filled.PlayArrow, contentDescription = "Read tonight’s story", tint = Night) }
            }
        }

        if (app.lang == Lang.EN) item(span = full) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF4B3A9B), Color(0xFF8A4F9E))))
                    .clickable { app.go(Screen.Maker) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Gold, modifier = Modifier.size(30.dp))
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Make your own story", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Pick a friend, a place and a lesson for ${app.childName}.",
                        color = Cream.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        item(span = full) {
            val chips = buildList {
                add("all" to app.t("All", "सभी"))
                add("fav" to app.t("Favorites", "पसंदीदा"))
                add("classic" to app.t("Classic tales", "पंचतंत्र और नीति कथाएँ"))
                if (app.customIds.isNotEmpty() && app.lang == Lang.EN) add("mine" to "My stories")
                Mood.entries.filter { m -> all.any { it.mood == m } }.forEach { add(it.name to app.t(it.label, it.labelHi)) }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(chips) { (key, label) ->
                    FilterChip(selected = filter == key, onClick = { filter = key }, label = { Text(label) })
                }
            }
        }

        items(shown, key = { it.id }) { story ->
            StoryCard(app, story, voice.count(story.voiceKey(app.lang)) > 0) { deleting = story }
        }

        if (shown.isEmpty()) {
            item(span = full) {
                Text(
                    if (filter == "fav") app.t("Tap the heart on a story to keep it here.", "कहानी पर दिल दबाएँ, वह यहाँ दिखेगी।") else app.t("No stories here yet.", "यहाँ अभी कोई कहानी नहीं है।"),
                    color = Mist,
                    modifier = Modifier.padding(vertical = 24.dp),
                )
            }
        }
    }

    deleting?.let { story ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Remove this story?") },
            text = { Text("“${app.title(story)}” and any voice recordings for it will be deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    voice.deleteStory(story.id)
                    app.removeCustom(story.id)
                    deleting = null
                }) { Text("Remove") }
            },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Keep") } },
        )
    }
}

@Composable
private fun StoryCard(app: AppState, story: Story, hasVoice: Boolean, onDelete: () -> Unit) {
    Column(
        Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(CardBlue)
            .clickable { app.go(Screen.Reader(story.id)) }
    ) {
        Box {
            Illustration(story.scene, story.cover, app.look, Modifier.fillMaxWidth().aspectRatio(1.1f))
            if (story.id in app.favorites) {
                Icon(
                    Icons.Filled.Favorite, contentDescription = "Favorite", tint = Rose,
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).size(20.dp),
                )
            }
            if (story.custom) {
                IconButton(onClick = onDelete, modifier = Modifier.align(Alignment.TopStart)) {
                    Icon(Icons.Filled.Delete, contentDescription = "Remove story", tint = Cream.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
                }
            }
        }
        Column(Modifier.padding(12.dp)) {
            Text(
                app.title(story),
                style = MaterialTheme.typography.titleMedium,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "${story.minutes(app.lang)} ${app.t("min", "मिनट")} · ${app.t(story.mood.label, story.mood.labelHi)}" + if (hasVoice) app.t(" · your voice", " · आपकी आवाज़") else "",
                color = Mist,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
