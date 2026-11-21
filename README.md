# Bedtime Stories

A calm bedtime story app for Android. Fifty-two illustrated stories, read aloud,
in ten languages: English, Hindi, Marathi, Gujarati, Tamil, Telugu, Bengali,
Kannada, Malayalam and Punjabi.

- **Stories about your child.** Twelve original English stories use the child's
  name and their own drawn character, and a story maker builds new ones.
- **Forty traditional tales** from the Panchatantra and other well-loved fables,
  retold gently, in every language.
- **Read to me.** English is read by offline neural storytellers (Kokoro v0.19,
  run by sherpa-onnx). Hindi has two more, Meera and Raghav (Kokoro v1.0), whose
  model is the on-demand Play asset pack `hindivoice`, downloaded the first time a
  family picks one. Other languages use the phone's text-to-speech voice.
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

`fetch-voice.sh` downloads the speech library, the English voice model and the
Hindi voice model (about 300 MB in all), which are too large for git.

A plain `assembleDebug` APK does not contain the Hindi asset pack. To try the
Hindi download on a device, build the bundle and install it in Play's local
testing mode:

```bash
./gradlew :app:bundleDebug
```

```bash
bundletool build-apks --bundle=app/build/outputs/bundle/debug/app-debug.aab --output=/tmp/bs.apks --local-testing --connected-device --ks=$HOME/.android/debug.keystore --ks-pass=pass:android --ks-key-alias=androiddebugkey --key-pass=pass:android
```

```bash
bundletool install-apks --apks=/tmp/bs.apks
```

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

Source: https://github.com/llassan/bedtime_stories
