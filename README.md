# Bedtime Stories

A calm bedtime story app for Android. Fifty-two illustrated stories, read aloud,
in ten languages: English, Hindi, Marathi, Gujarati, Tamil, Telugu, Bengali,
Kannada, Malayalam and Punjabi.

- **Stories about your child.** Twelve original English stories use the child's
  name and their own drawn character, and a story maker builds new ones.
- **Forty traditional tales** from the Panchatantra and other well-loved fables,
  retold gently, in every language.
- **Read to me.** English is read by four offline neural storytellers (Kokoro,
  run by sherpa-onnx). Other languages use the phone's text-to-speech voice.
- **My voice.** A parent can record each page and have it played back.
- **Made for bedtime.** A soft lullaby, a sleep timer and a goodnight screen.
- **Ads.** One banner on the browsing screens only, marked child-directed. No
  ads while a story is being read, and no fullscreen ads.

## Building

```bash
scripts/fetch-voice.sh
```

```bash
./gradlew :app:assembleDebug
```

`fetch-voice.sh` downloads the speech library and the English voice model
(about 150 MB), which are too large for git.

Debug builds show Google's test ads. For a release build, put your own AdMob
ids in `ads.properties` at the project root:

```
admobAppId=ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY
admobBannerUnitId=ca-app-pub-XXXXXXXXXXXXXXXX/ZZZZZZZZZZ
```

## Layout

- `app/src/main/java/.../data` — the stories and their translations
- `app/src/main/java/.../ui/art` — the illustrations, drawn in code
- `app/src/main/java/.../audio` — narration, recording and the lullaby
- `app/src/main/java/.../ads` — the banner

To review the artwork, launch the gallery on a debug build:

```bash
adb shell am start -n com.vikash.bedtimestories/.MainActivity --ez gallery true
```

## Licence

GPL-3.0. See `LICENSE` and `NOTICE` for the third-party components.
