package com.vikash.bedtimestories.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.vikash.bedtimestories.AppState
import com.vikash.bedtimestories.Screen
import com.vikash.bedtimestories.SleepMode
import com.vikash.bedtimestories.audio.Narrator
import com.vikash.bedtimestories.audio.NeuralNarrator
import com.vikash.bedtimestories.audio.ReadAloud
import com.vikash.bedtimestories.audio.SYSTEM_VOICE
import com.vikash.bedtimestories.audio.VoiceBox
import com.vikash.bedtimestories.data.Lang
import com.vikash.bedtimestories.data.Scene
import com.vikash.bedtimestories.data.Story
import com.vikash.bedtimestories.ui.art.Illustration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The storybook. Swipe to read yourself, press play to be read to by the
 * chosen storyteller, or switch to "My voice" to record each page and hear
 * your own reading played back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(app: AppState, story: Story, narrator: Narrator, neural: NeuralNarrator, voice: VoiceBox, player: ReadAloud) {
    val pages = story.pages
    val pager = rememberPagerState { pages.size + 1 } // the extra page is "The End"
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val view = LocalView.current

    var myVoice by rememberSaveable { mutableStateOf(voice.count(story.id) > 0) }
    var recording by remember { mutableStateOf(false) }
    var recTick by remember { mutableIntStateOf(0) } // bumps when recordings change on disk
    var notice by remember { mutableStateOf<String?>(null) }
    var showTimer by remember { mutableStateOf(false) }
    var showVoices by remember { mutableStateOf(false) }

    val page = pager.currentPage
    val onStoryPage = page < pages.size

    fun stopRecording() {
        if (recording) {
            if (!voice.stopRecording()) notice = "That recording was too short to keep."
            recording = false
            recTick++
        }
    }

    val startRecording = {
        app.playing = false
        notice = null
        if (voice.startRecording(story.id, pager.currentPage)) recording = true
        else notice = "The microphone could not be started."
    }
    val micPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startRecording() else notice = "Allow the microphone to record your own voice."
    }

    // Reading aloud runs in [ReadAloud], outside this screen, so it carries on with the phone
    // locked. Pressing play (or changing the voice while playing) starts it at this page.
    LaunchedEffect(app.playing, myVoice, app.voice, app.slowVoice) {
        if (app.playing) player.start(story, pager.settledPage, myVoice)
    }
    // Swiping to another page while it plays carries on reading from there.
    LaunchedEffect(pager.settledPage) {
        if (app.playing && player.storyId == story.id && pager.settledPage != player.page) {
            player.start(story, pager.settledPage, myVoice)
        }
    }
    // Follow the pages the player turns, including any turned while the screen was off.
    val firstTurn = remember { player.turn }
    LaunchedEffect(player.turn) {
        val target = player.page
        if (player.turn != firstTurn && player.storyId == story.id && target != pager.currentPage && target <= pages.size) {
            if (kotlin.math.abs(target - pager.currentPage) == 1) pager.animateScrollToPage(target) else pager.scrollToPage(target)
        }
    }
    LaunchedEffect(player.failed) {
        if (player.failed && player.storyId == story.id) {
            notice = app.t("No voice for this language was found. Install it in the phone’s Text-to-speech settings.", "इस भाषा की आवाज़ नहीं मिली। फ़ोन की टेक्स्ट-टू-स्पीच सेटिंग में इसे इंस्टॉल करें।")
        }
    }

    // Voice the rest of the story ahead of time so later pages start without a wait.
    LaunchedEffect(pager.settledPage, app.voice, app.slowVoice, myVoice, app.lang) {
        val from = pager.settledPage
        if (from < pages.size) {
            val ahead = pages.drop(from).filterIndexed { i, _ -> !(myVoice && voice.has(story.id, from + i)) }
            neural.prepare(ahead.map { app.text(it) }, app.storyVoice, app.speed)
        }
    }

    // Turning the page ends a recording in progress.
    LaunchedEffect(pager.currentPage) { stopRecording() }

    val busy = app.playing || recording
    DisposableEffect(busy) {
        view.keepScreenOn = busy
        onDispose { view.keepScreenOn = false }
    }
    DisposableEffect(Unit) {
        onDispose {
            if (voice.isRecording) voice.stopRecording()
        }
    }

    Column(Modifier.fillMaxSize()) {
        HorizontalPager(state = pager, modifier = Modifier.weight(1f), beyondViewportPageCount = 1) { i ->
            if (i < pages.size) {
                val text = app.text(pages[i])
                val h = player.highlight.takeIf { player.storyId == story.id && player.page == i }
                val lit = app.playing && h != null && i == pager.settledPage && h.first in 0 until h.second && h.second <= text.length
                val shown = buildAnnotatedString {
                    append(text)
                    if (lit && h != null) {
                        // The sentence being spoken stays bright; the rest of the page dims.
                        addStyle(SpanStyle(color = Cream.copy(alpha = 0.45f)), 0, text.length)
                        addStyle(SpanStyle(color = Gold), h.first, h.second)
                    }
                }
                Column(Modifier.fillMaxSize()) {
                    Illustration(
                        story.scene, pages[i].art, app.look,
                        Modifier.fillMaxWidth().weight(0.95f).clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)),
                        animated = i == pager.currentPage,
                        topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 64.dp,
                    )
                    Box(Modifier.fillMaxWidth().weight(1f).padding(start = 22.dp, end = 22.dp, top = 18.dp).verticalScroll(rememberScrollState())) {
                        Text(shown, style = StoryText)
                    }
                }
            } else {
                EndPage(app, story, onAgain = { scope.launch { pager.scrollToPage(0) } })
            }
        }

        if (onStoryPage) {
            // Page progress.
            Row(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                repeat(pages.size) { i ->
                    val fill by animateFloatAsState(if (i <= page) 1f else 0f, label = "progress")
                    Box(Modifier.weight(1f).height(4.dp).clip(CircleShape).background(Color(0x33FFFFFF))) {
                        Box(Modifier.fillMaxWidth(fill).height(4.dp).background(Gold))
                    }
                }
            }

            val status = when {
                recording -> app.t("Recording… read the page aloud, then press stop.", "रिकॉर्ड हो रहा है… पन्ना पढ़ें, फिर रोकें।")
                app.playing && !myVoice && app.storyVoice.engine != SYSTEM_VOICE && neural.state == NeuralNarrator.State.LOADING -> app.t("Waking the storyteller…", "कहानी सुनाने वाला जाग रहा है…")
                app.playing && neural.buffering -> app.t("${app.storyVoice.name} is taking a breath…", "${app.storyVoice.name} साँस ले रहे हैं…")
                else -> notice
            }
            if (status != null) {
                Text(
                    status,
                    color = if (status == notice) Rose else Mist,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )
            }

            if (myVoice) {
                val recorded = remember(recTick, page) { voice.has(story.id, page) }
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(48.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (recording) {
                        FilledTonalButton(onClick = { stopRecording() }) {
                            Icon(Icons.Filled.Stop, contentDescription = null, tint = Rose)
                            Spacer(Modifier.width(6.dp))
                            Text(app.t("Stop recording", "रिकॉर्डिंग रोकें"))
                        }
                    } else {
                        OutlinedButton(onClick = {
                            app.playing = false // narration must not run on behind the permission prompt
                            val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                                PackageManager.PERMISSION_GRANTED
                            if (granted) startRecording() else micPermission.launch(Manifest.permission.RECORD_AUDIO)
                        }) {
                            Icon(Icons.Filled.Mic, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text(if (recorded) app.t("Record this page again", "यह पन्ना फिर रिकॉर्ड करें") else app.t("Record this page", "यह पन्ना रिकॉर्ड करें"))
                        }
                        if (recorded) {
                            TextButton(onClick = { voice.delete(story.id, page); recTick++ }) { Text("Delete") }
                        }
                    }
                }
            }

            // Player bar.
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 14.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color(0x552B3288))
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Who is reading; tap to change.
                Row(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(22.dp))
                        .clickable { showVoices = true }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(if (myVoice) Icons.Filled.Mic else Icons.Filled.RecordVoiceOver, contentDescription = null, tint = Gold, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(app.t("Read by", "आवाज़"), color = Mist, style = MaterialTheme.typography.labelSmall)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (myVoice) app.t("My voice", "मेरी आवाज़") else app.storyVoice.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Change voice", modifier = Modifier.size(20.dp))
                        }
                    }
                }
                IconButton(onClick = { scope.launch { pager.animateScrollToPage(page - 1) } }, enabled = page > 0) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous page", modifier = Modifier.size(30.dp))
                }
                Box(contentAlignment = Alignment.Center) {
                    val waiting = app.playing && !myVoice && app.storyVoice.engine != SYSTEM_VOICE && (neural.buffering || neural.state == NeuralNarrator.State.LOADING)
                    if (waiting) CircularProgressIndicator(Modifier.size(72.dp), strokeWidth = 3.dp)
                    FilledIconButton(
                        onClick = {
                            stopRecording()
                            notice = null
                            app.playing = !app.playing
                        },
                        modifier = Modifier.size(62.dp),
                    ) {
                        Icon(
                            if (app.playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (app.playing) "Pause" else "Read to me",
                            modifier = Modifier.size(34.dp),
                        )
                    }
                }
                IconButton(onClick = { scope.launch { pager.animateScrollToPage(page + 1) } }) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "Next page", modifier = Modifier.size(30.dp))
                }
            }
        } else {
            Spacer(Modifier.height(14.dp))
        }
    }

    // Controls that float over the illustration.
    Row(Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Glass(Icons.AutoMirrored.Filled.ArrowBack, "Back to stories") { app.go(Screen.Home) }
        Text(
            if (onStoryPage) app.title(story) else "",
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
        )
        val fav = story.id in app.favorites
        Glass(if (fav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, if (fav) "Remove from favorites" else "Add to favorites", if (fav) Rose else Cream) { app.toggleFavorite(story.id) }
        Spacer(Modifier.width(8.dp))
        Glass(if (app.musicOn) Icons.Filled.MusicNote else Icons.Filled.MusicOff, if (app.musicOn) "Turn lullaby off" else "Turn lullaby on", if (app.musicOn) Gold else Mist) { app.toggleMusic() }
        Spacer(Modifier.width(8.dp))
        Glass(Icons.Filled.Bedtime, "Sleep timer", if (app.sleepMode != SleepMode.OFF) Gold else Cream) { showTimer = true }
    }

    if (showVoices) {
        ModalBottomSheet(onDismissRequest = { showVoices = false }, containerColor = NightSoft) {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).navigationBarsPadding()) {
                Text(app.t("Who reads tonight?", "आज कौन सुनाएगा?"), style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 12.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (myVoice) Gold.copy(alpha = 0.14f) else CardBlue)
                        .clickable { app.playing = false; myVoice = !myVoice }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = null, tint = Gold)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(app.t("My voice", "मेरी आवाज़"), style = MaterialTheme.typography.titleMedium)
                        Text(app.t("Record each page yourself. Pages you skip are read by the storyteller.", "हर पन्ना ख़ुद रिकॉर्ड करें। बाक़ी पन्ने कहानी सुनाने वाला पढ़ेगा।"), color = Mist, style = MaterialTheme.typography.bodySmall)
                    }
                    RadioButton(selected = myVoice, onClick = null)
                }
                Text(app.t("Storytellers", "कहानी सुनाने वाले"), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 18.dp, bottom = 8.dp))
                VoicePicker(app, neural, narrator)
                Spacer(Modifier.height(20.dp))
            }
        }
    }

    if (showTimer) {
        AlertDialog(
            onDismissRequest = { showTimer = false },
            title = { Text(app.t("Sleep timer", "सोने का टाइमर")) },
            text = {
                Column {
                    Text(app.t("Stories and music stop by themselves.", "कहानी और संगीत अपने आप रुक जाएँगे।"), color = Mist, modifier = Modifier.padding(bottom = 8.dp))
                    SleepMode.entries.forEach { mode ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(selected = app.sleepMode == mode, role = Role.RadioButton) {
                                    app.setSleep(mode)
                                    showTimer = false
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(selected = app.sleepMode == mode, onClick = null)
                            Spacer(Modifier.width(12.dp))
                            Text(app.t(mode.label, mode.labelHi))
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showTimer = false }) { Text(app.t("Close", "बंद करें")) } },
        )
    }
}

/** A round, see-through button that stays readable on top of any picture. */
@Composable
private fun Glass(icon: ImageVector, label: String, tint: Color = Cream, onClick: () -> Unit) {
    Box(
        Modifier.size(42.dp).clip(CircleShape).background(Color(0x660B1033)).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp)) }
}

@Composable
private fun EndPage(app: AppState, story: Story, onAgain: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Illustration(
            Scene.NIGHT, "hero~ zzz star", app.look,
            Modifier.fillMaxWidth().height(330.dp).clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)),
            animated = true,
        )
        Text(app.t("The End", "समाप्त"), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 24.dp))
        Text(
            story.moral(app.lang),
            style = StoryText,
            color = Gold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 12.dp),
        )
        Text(
            app.t("Sweet dreams, ${app.childName}.", "मीठे सपने, ${app.childName}।"),
            color = Mist,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
        )
        Button(onClick = { app.go(Screen.Home) }, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(52.dp)) {
            Text(app.t("Choose another story", "दूसरी कहानी चुनें"))
        }
        TextButton(onClick = onAgain, modifier = Modifier.padding(top = 4.dp)) { Text(app.t("Read it again", "फिर से सुनें")) }
    }
}
