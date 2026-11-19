package com.vikash.bedtimestories.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vikash.bedtimestories.AppState
import com.vikash.bedtimestories.Screen
import com.vikash.bedtimestories.data.StoryMaker
import com.vikash.bedtimestories.ui.art.Illustration

/** Three choices make a brand-new story starring the child. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MakerScreen(app: AppState) {
    var companion by rememberSaveable { mutableIntStateOf(0) }
    var place by rememberSaveable { mutableIntStateOf(0) }
    var lesson by rememberSaveable { mutableIntStateOf(0) }
    val c = StoryMaker.companions[companion]
    val pl = StoryMaker.places[place]

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { app.go(Screen.Home) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Make your own story", style = MaterialTheme.typography.titleLarge)
        }
        Illustration(
            pl.scene, "hero ${c.actor}", app.look,
            Modifier.fillMaxWidth().height(200.dp).padding(top = 8.dp).clip(RoundedCornerShape(28.dp)),
            animated = true,
        )
        Text(
            "${app.childName} and ${c.name} in the ${pl.name}",
            style = MaterialTheme.typography.titleLarge,
            color = Gold,
            modifier = Modifier.padding(top = 14.dp),
        )

        Section("Who comes along?")
        Tiles(StoryMaker.companions.size, companion, { companion = it }) { i ->
            val it = StoryMaker.companions[i]
            Illustration(pl.scene, it.actor, app.look, Modifier.fillMaxWidth().aspectRatio(1.15f))
            TileLabel(it.name, "the ${it.kind}")
        }
        Section("Where do they go?")
        Tiles(StoryMaker.places.size, place, { place = it }) { i ->
            val it = StoryMaker.places[i]
            Illustration(it.scene, "", app.look, Modifier.fillMaxWidth().aspectRatio(1.15f))
            TileLabel(it.name, null)
        }
        Section("What is the story about?")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StoryMaker.lessons.forEachIndexed { i, it ->
                FilterChip(selected = lesson == i, onClick = { lesson = i }, label = { Text(it.label) })
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                val id = StoryMaker.id(companion, place, lesson)
                app.addCustom(id)
                app.go(Screen.Reader(id))
            },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) { Text("Create story", fontSize = 17.sp) }
        Text(
            "Your story is saved under “My stories”.",
            color = Mist,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}

@Composable
private fun Section(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 20.dp, bottom = 6.dp))
}

/** A three-across grid of picture tiles, one of which is selected. */
@Composable
private fun Tiles(count: Int, selected: Int, onPick: (Int) -> Unit, tile: @Composable (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        (0 until count).chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { i ->
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(CardBlue)
                            .border(2.dp, if (i == selected) Gold else CardBlue, RoundedCornerShape(18.dp))
                            .clickable { onPick(i) }
                    ) { tile(i) }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun TileLabel(title: String, sub: String?) {
    Column(Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, minLines = if (sub == null) 2 else 1, maxLines = 2)
        if (sub != null) Text(sub, color = Mist, style = MaterialTheme.typography.bodySmall, maxLines = 1)
    }
}
