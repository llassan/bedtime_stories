package com.vikash.bedtimestories.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vikash.bedtimestories.AppState
import com.vikash.bedtimestories.Screen
import com.vikash.bedtimestories.audio.Narrator
import com.vikash.bedtimestories.audio.NeuralNarrator
import com.vikash.bedtimestories.data.Lang
import com.vikash.bedtimestories.data.Scene
import com.vikash.bedtimestories.ui.art.BOY_STYLES
import com.vikash.bedtimestories.ui.art.ChildLook
import com.vikash.bedtimestories.ui.art.GIRL_STYLES
import com.vikash.bedtimestories.ui.art.HAIR_COLORS
import com.vikash.bedtimestories.ui.art.HAIR_STYLES
import com.vikash.bedtimestories.ui.art.Illustration
import com.vikash.bedtimestories.ui.art.PAJAMA_COLORS
import com.vikash.bedtimestories.ui.art.SKIN_TONES

/** First-launch welcome and, later, the place to change the child's name, look and storyteller. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(app: AppState, neural: NeuralNarrator, narrator: Narrator, first: Boolean) {
    var name by rememberSaveable { mutableStateOf(app.childName) }
    var skin by rememberSaveable { mutableIntStateOf(app.look.skin) }
    var hair by rememberSaveable { mutableIntStateOf(app.look.hair) }
    var hairColor by rememberSaveable { mutableIntStateOf(app.look.hairColor) }
    var pajama by rememberSaveable { mutableIntStateOf(app.look.pajama) }
    var girl by rememberSaveable { mutableStateOf(app.look.girl) }
    val look = ChildLook(skin, hair, hairColor, pajama, girl)
    val focus = LocalFocusManager.current
    val valid = name.trim().isNotEmpty()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        if (!first) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { app.go(Screen.Home) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(app.t("Who is listening?", "कौन सुन रहा है?"), style = MaterialTheme.typography.titleLarge)
            }
        }
        Illustration(
            Scene.NIGHT, "hero star", look,
            Modifier.fillMaxWidth().height(220.dp).padding(top = 8.dp).clip(RoundedCornerShape(30.dp)),
            animated = true,
        )
        if (first) {
            Text(
                app.t("Welcome to Bedtime Stories", "सोने की कहानियों में स्वागत है"),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 22.dp),
            )
            Text(
                app.t("Every story is told about your child, in a real storyteller’s voice. Let’s set it up.", "हर रात एक प्यारी कहानी, मीठी आवाज़ में। चलिए शुरू करते हैं।"),
                color = Mist,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            )
        }
        OutlinedTextField(
            value = name,
            onValueChange = { name = it.take(20) },
            label = { Text(app.t("Child’s name", "बच्चे का नाम")) },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
        )

        Heading(app.t("Story language", "कहानी की भाषा"))
        // Wraps onto as many rows as it needs, so every language is visible without swiping.
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Lang.entries.forEach { l ->
                FilterChip(selected = app.lang == l, onClick = { app.chooseLang(l) }, label = { Text(l.label) })
            }
        }
        if (app.lang != Lang.EN) {
            Text(
                app.t("Classic tales are available in this language for now.", "इस भाषा में अभी पंचतंत्र और नीति कथाएँ उपलब्ध हैं।"),
                color = Mist,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 6.dp),
            )
        }

        Heading(app.t("Character", "पात्र"))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(true to app.t("Girl", "लड़की"), false to app.t("Boy", "लड़का")).forEach { (g, label) ->
                FilterChip(
                    selected = girl == g,
                    onClick = {
                        girl = g
                        // Keep the hairstyle if it suits both; otherwise pick the first one on offer.
                        val styles = if (g) GIRL_STYLES else BOY_STYLES
                        if (hair !in styles) hair = styles.first()
                    },
                    label = { Text(label) },
                )
            }
        }

        Heading(app.t("Skin", "रंग"))
        Swatches(SKIN_TONES, skin) { skin = it }
        Heading(app.t("Hair", "बाल"))
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (if (girl) GIRL_STYLES else BOY_STYLES).forEach { i ->
                val hindi = listOf("छोटे", "लंबे", "दो चोटी", "घुँघराले", "जूड़ा", "खड़े बाल", "साइड माँग", "बॉब कट")[i]
                FilterChip(selected = hair == i, onClick = { hair = i }, label = { Text(app.t(HAIR_STYLES[i], hindi)) })
            }
        }
        Spacer(Modifier.height(10.dp))
        Swatches(HAIR_COLORS, hairColor) { hairColor = it }
        Heading(app.t("Pyjamas", "रात के कपड़े"))
        Swatches(PAJAMA_COLORS, pajama) { pajama = it }

        Heading(app.t("Storyteller", "कहानी सुनाने वाला"))
        VoicePicker(app, neural, narrator)

        Spacer(Modifier.height(26.dp))
        Button(
            onClick = {
                focus.clearFocus()
                app.saveProfile(name, look)
                app.go(Screen.Home)
            },
            enabled = valid,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) { Text(if (first) app.t("Begin story time", "कहानी शुरू करें") else app.t("Save", "सहेजें"), fontSize = 17.sp) }
        Text(
            app.t("No accounts. Ads never appear while a story is being read.", "कोई खाता नहीं। कहानी के दौरान कभी विज्ञापन नहीं आता।"),
            color = Mist,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 12.dp),
        )
        // GPL-3.0 asks that people who receive the app are told it is free software.
        Text(
            "Free and open-source software, licensed under GPL-3.0.\nSource: github.com/llassan/bedtime_stories",
            color = Mist.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )
    }
}

@Composable
private fun Heading(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
}

@Composable
private fun Swatches(colors: List<Color>, selected: Int, onPick: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        colors.forEachIndexed { i, c ->
            Box(
                Modifier
                    .size(46.dp)
                    .border(2.5.dp, if (i == selected) Gold else Color.Transparent, CircleShape)
                    .padding(5.dp)
                    .clip(CircleShape)
                    .background(c)
                    .clickable { onPick(i) }
            )
        }
    }
}
