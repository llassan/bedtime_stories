package com.vikash.bedtimestories.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.vikash.bedtimestories.AppState
import com.vikash.bedtimestories.audio.Narrator
import com.vikash.bedtimestories.audio.NeuralNarrator
import com.vikash.bedtimestories.audio.HINDI_ENGINE
import com.vikash.bedtimestories.audio.SYSTEM_VOICE
import com.vikash.bedtimestories.audio.VoicePack
import com.vikash.bedtimestories.audio.STORY_VOICES
import com.vikash.bedtimestories.data.Lang
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** Lets the family choose a storyteller and hear each one say hello. */
@Composable
fun VoicePicker(app: AppState, neural: NeuralNarrator, narrator: Narrator, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    var previewing by remember { mutableIntStateOf(-1) }
    var job by remember { mutableStateOf<Job?>(null) }
    DisposableEffect(Unit) { onDispose { job?.cancel() } }
    val pack = neural.hindiPack
    val confirm = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {}
    val hasHindi = STORY_VOICES.any { it.lang == app.lang && it.engine == HINDI_ENGINE }
    // A Hindi storyteller is chosen but its voice is not on the phone yet.
    val waitingForPack = app.storyVoice.engine == HINDI_ENGINE && pack.state != VoicePack.State.READY

    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (hasHindi && pack.state != VoicePack.State.READY) {
            val size = if (pack.sizeMb > 0) "${pack.sizeMb} MB" else "90 MB"
            Text(
                when (pack.state) {
                    VoicePack.State.FAILED -> app.t(
                        "The Hindi storytellers could not be downloaded. Check the internet connection; the app must be installed from Google Play.",
                        "हिन्दी कहानी सुनाने वाले डाउनलोड नहीं हो सके। इंटरनेट देखें; ऐप Google Play से इंस्टॉल होना चाहिए।",
                    )
                    VoicePack.State.DOWNLOADING -> app.t(
                        "Downloading the Hindi storytellers… Until then, your phone’s voice reads.",
                        "हिन्दी कहानी सुनाने वाले डाउनलोड हो रहे हैं… तब तक फ़ोन की आवाज़ पढ़ेगी।",
                    )
                    else -> app.t(
                        "Meera and Raghav read Hindi stories offline. Download them once (about $size); until then, your phone’s voice reads.",
                        "मीरा और राघव बिना इंटरनेट हिन्दी कहानियाँ सुनाते हैं। इन्हें एक बार डाउनलोड करें (लगभग $size); तब तक फ़ोन की आवाज़ पढ़ेगी।",
                    )
                },
                color = if (pack.state == VoicePack.State.FAILED) Rose else Mist,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        if (waitingForPack) {
            // The note above already says the phone's voice reads for now.
        } else if (app.storyVoice.engine == SYSTEM_VOICE) {
            Text(app.t("This language is read by your phone’s own voice. If you hear nothing, install it in the phone’s Text-to-speech settings.", "यह भाषा आपके फ़ोन की अपनी आवाज़ में पढ़ी जाती है। आवाज़ न आए तो फ़ोन की टेक्स्ट-टू-स्पीच सेटिंग में इसे इंस्टॉल करें।"), color = Mist, style = MaterialTheme.typography.bodySmall)
        } else when (neural.state) {
            NeuralNarrator.State.LOADING, NeuralNarrator.State.IDLE -> {
                Text(app.t("Waking the storytellers…", "कहानी सुनाने वाले जाग रहे हैं…"), color = Mist, style = MaterialTheme.typography.bodySmall)
                LinearProgressIndicator(Modifier.fillMaxWidth().clip(CircleShape))
            }
            NeuralNarrator.State.FAILED ->
                Text("The storyteller voices could not start on this device, so the phone’s own voice will read.", color = Rose, style = MaterialTheme.typography.bodySmall)
            NeuralNarrator.State.READY -> {}
        }
        STORY_VOICES.forEachIndexed { i, v ->
            if (v.lang != app.lang) return@forEachIndexed
            val selected = app.voice == i
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (selected) Gold.copy(alpha = 0.14f) else CardBlue)
                    .border(1.5.dp, if (selected) Gold else CardBlue, RoundedCornerShape(18.dp))
                    .clickable {
                        app.chooseVoice(i)
                        if (v.engine == HINDI_ENGINE) pack.download()
                    }
                    .padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(22.dp), contentAlignment = Alignment.Center) {
                    if (selected) Icon(Icons.Filled.Check, contentDescription = "Selected", tint = Gold)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(v.name, style = MaterialTheme.typography.titleMedium)
                    Text(v.blurb, color = Mist, style = MaterialTheme.typography.bodySmall)
                }
                if (v.engine == HINDI_ENGINE && pack.state != VoicePack.State.READY) {
                    when (pack.state) {
                        VoicePack.State.DOWNLOADING -> Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                            if (pack.progress > 0f) CircularProgressIndicator(progress = { pack.progress }, modifier = Modifier.size(26.dp), strokeWidth = 3.dp)
                            else CircularProgressIndicator(Modifier.size(26.dp), strokeWidth = 3.dp)
                        }
                        VoicePack.State.NEEDS_CONFIRM -> TextButton(onClick = { pack.confirm(confirm) }) { Text(app.t("Allow", "अनुमति दें")) }
                        VoicePack.State.FAILED -> TextButton(onClick = { pack.download() }) { Text(app.t("Retry", "फिर से")) }
                        else -> TextButton(onClick = { app.chooseVoice(i); pack.download() }) { Text(app.t("Download", "डाउनलोड")) }
                    }
                    return@Row
                }
                IconButton(onClick = {
                    app.playing = false
                    job?.cancel()
                    if (previewing == i) previewing = -1
                    else {
                        previewing = i
                        job = scope.launch {
                            try {
                                // Only the storytellers that run on the phone greet the child by name: the phone's own
                                // voice may be an online one, and the name must never leave the device.
                                val hello = when (v.lang) {
                                    // The Hindi storytellers run on the phone too, so they may use the name.
                                    Lang.HI -> if (v.engine == HINDI_ENGINE) "नमस्ते ${app.childName.ifBlank { "दोस्त" }}। मैं ${v.name} हूँ। चलो, साथ में एक कहानी सुनते हैं।"
                                        else "नमस्ते। चलो, साथ में एक कहानी सुनते हैं।"
                                    Lang.MR -> "नमस्कार. चला, आपण एक गोष्ट ऐकूया."
                                    Lang.GU -> "નમસ્તે. ચાલો, આપણે એક વાર્તા સાંભળીએ."
                                    Lang.TA -> "வணக்கம். வா, நாம் ஒரு கதை கேட்போம்."
                                    Lang.TE -> "నమస్తే. రా, మనం ఒక కథ విందాం."
                                    Lang.BN -> "নমস্কার। চলো, আমরা একটা গল্প শুনি।"
                                    Lang.KN -> "ನಮಸ್ಕಾರ. ಬಾ, ನಾವು ಒಂದು ಕಥೆ ಕೇಳೋಣ."
                                    Lang.ML -> "നമസ്കാരം. വരൂ, നമുക്ക് ഒരു കഥ കേൾക്കാം."
                                    Lang.PA -> "ਨਮਸਤੇ। ਆਓ, ਆਪਾਂ ਇੱਕ ਕਹਾਣੀ ਸੁਣੀਏ।"
                                    Lang.EN -> "Hello ${app.childName.ifBlank { "there" }}. I am ${v.name}. Shall we read a story together?"
                                }
                                if (!neural.speak(hello, v, v.pace) { _, _ -> }) narrator.speak(hello, app.slowVoice, v.lang.locale) { _, _ -> }
                            } finally {
                                if (previewing == i) previewing = -1
                            }
                        }
                    }
                }) {
                    when {
                        previewing == i && neural.buffering -> CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.5.dp)
                        previewing == i -> Icon(Icons.Filled.Stop, contentDescription = "Stop ${v.name}")
                        else -> Icon(Icons.Filled.PlayArrow, contentDescription = "Hear ${v.name}")
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(app.t("Extra-slow reading", "और धीरे पढ़ें"), style = MaterialTheme.typography.titleMedium)
                Text(app.t("A gentler pace for very sleepy listeners.", "नींद से भरी आँखों के लिए और धीमी रफ़्तार।"), color = Mist, style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = app.slowVoice, onCheckedChange = { app.setSlow(it) })
        }
    }
}
