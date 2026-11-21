package com.vikash.bedtimestories.ui.art

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.vikash.bedtimestories.data.Scene

/**
 * Every character on one screen, for checking the artwork while developing.
 * Open it with: adb shell am start -n com.vikash.bedtimestories/.MainActivity --ez gallery true
 */
@Composable
fun Gallery() {
    // Newest drawings first, so they are on screen without scrolling.
    // The child first, once in each hairstyle (boy or girl as the style suggests).
    val names = List(HAIR_STYLES.size) { "hero" } + listOf("donkey", "goat", "hen", "egg", "bell", "sun") + ALL_ACTORS
    LazyVerticalGrid(GridCells.Fixed(5), Modifier.fillMaxSize().safeDrawingPadding()) {
        items(names.size) { i ->
            val name = names[i]
            val scene = when (name) {
                "monkey", "lion", "elephant", "jackal", "bluejackal", "snake", "well", "capseller" -> Scene.JUNGLE
                "crocodile", "crane", "crab", "goose", "ant", "dove", "goat" -> Scene.RIVER
                "sun" -> Scene.SKY
                "hen", "egg" -> Scene.MEADOW
                "fish", "lumi", "turtle" -> Scene.SEA
                "whale", "boat", "seal" -> Scene.OCEAN
                "cloud", "bird", "dragon" -> Scene.SKY
                "fox", "deer", "tree", "penguin" -> Scene.SNOW
                "bear", "rabbit", "mouse", "snail", "oak", "honey", "sheep" -> Scene.MEADOW
                "owl", "hedgehog", "squirrel", "frog" -> Scene.FOREST
                else -> Scene.NIGHT
            }
            Column {
                Illustration(scene, name, ChildLook(skin = i % 5, hair = i % 8, hairColor = i % 5, pajama = i % 5, girl = ChildLook.looksLikeGirl(i % 8)), Modifier.fillMaxWidth().aspectRatio(0.95f), animated = true)
                Text(name, fontSize = 11.sp)
            }
        }
    }
}
