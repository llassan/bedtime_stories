package com.vikash.bedtimestories.ui.art

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import com.vikash.bedtimestories.data.Scene
import com.vikash.bedtimestories.ui.Fredoka
import com.vikash.bedtimestories.ui.Nunito

/**
 * The Play Store feature graphic, drawn by the app itself so it always matches
 * the artwork. It is exactly 1024 x 500 pixels, 200 pixels down from the top.
 * Open with: adb shell am start -n com.vikash.bedtimestories/.MainActivity --ez promo true
 */
@Composable
fun Promo() {
    val d = LocalDensity.current
    fun px(v: Int) = with(d) { v.toDp() }
    fun font(v: Int) = with(d) { v.toSp() }
    Column(Modifier.fillMaxSize().background(Color.Black)) {
        Spacer(Modifier.height(px(200)))
        Box(Modifier.size(px(1024), px(500))) {
            Illustration(Scene.NIGHT, "owl hero fox", ChildLook(skin = 2, hair = 1, hairColor = 1, pajama = 3), Modifier.fillMaxSize())
            Column(Modifier.align(Alignment.TopStart).padding(start = px(56), top = px(48))) {
                Text("Bedtime Stories", color = Color(0xFFFFF6E0), fontFamily = Fredoka, fontWeight = FontWeight(600), fontSize = font(84))
                Text("Calm tales, read aloud in 10 languages", color = Color(0xFFFFD98E), fontFamily = Nunito, fontWeight = FontWeight(700), fontSize = font(34))
            }
        }
    }
}
