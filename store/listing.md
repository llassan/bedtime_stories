# Bedtime Stories — Play Store listing kit

Draft. Everything here describes the app as it is today; check each answer against
the Play Console's current wording before submitting.

## App title (max 30 chars)

```
Bedtime Stories: Moral Tales
```

## Short description (max 80 chars)

```
Calm bedtime stories read aloud in 10 languages. Panchatantra and moral tales.
```

## Full description (max 4000 chars)

```
A calm way to end the day. Bedtime Stories reads gentle, illustrated tales aloud in ten languages, with a soft lullaby underneath and a sleep timer that lets everything fade out by itself.

52 STORIES
• 40 traditional tales from the Panchatantra and other well-loved fables: The Monkey and the Crocodile, The Thirsty Crow, The Lion and the Mouse, The Blue Jackal, The Honest Woodcutter and many more
• 12 original bedtime stories in English that star your own child
• Every tale is retold gently for bedtime, with a simple lesson at the end: kindness, courage, friendship, patience, honesty

TEN LANGUAGES
Read and listen in English, Hindi, Marathi, Gujarati, Tamil, Telugu, Bengali, Kannada, Malayalam and Punjabi. All 40 traditional tales are available in every language, and the whole app switches language with one tap.

STORIES ABOUT YOUR CHILD
Type your child's name and choose how their character looks: skin, hair and pyjamas. The English bedtime stories are then told about them, and they appear in the pictures. A story maker lets you pick a friend, a place and a lesson to create new stories of your own.

READ TO ME
• English stories are read by four built-in storytellers that work without internet
• Other languages are read by your phone's own voice
• In English, the sentence being read lights up, so early readers can follow along
• Pages turn by themselves
• An extra-slow reading pace for very sleepy listeners

IN YOUR OWN VOICE
Record yourself reading each page, and your child can hear the story in your voice, even on nights when you are away.

MADE FOR BEDTIME
• A dark, restful design that is easy on tired eyes
• A soft lullaby under every story
• A sleep timer: 5, 15 or 30 minutes, or when the story ends
• A quiet goodnight screen at the end

RESPECTFUL BY DESIGN
• No accounts and no sign-up
• Your child's name, favourites and your recordings stay on your device
• One small banner advertisement on the browsing screens. No advertisements while a story is being read, and no full-screen advertisements, ever
• Advertisements are set to child-directed and general-audience only
• Free and open-source software

A NOTE ON VOICES
English narration is built in and works offline. For other languages the app uses the text-to-speech voice installed on your phone; if you hear nothing, install that language in your phone's Text-to-speech settings.

Sweet dreams.
```

## Category and tags

- Category: **Books & Reference** (alternative: Education)
- Tags to consider: Storytelling, Kids, Audiobooks

## Contact details

- Email: aiamvikku@gmail.com (the same address as VoiceScribe)
- Website: https://github.com/llassan/bedtime_stories
- Privacy policy URL: https://llassan.com/bedtime-stories/privacy/ (live; source is
  `store/privacy-policy.html`, deployed through the llassan-platform frontend)

## Target audience and content (your declarations)

These are legal declarations made in your name, so treat the answers below as a
starting point to confirm, not as settled.

- **Target age groups:** children are clearly part of the audience (the stories are
  written for ages about 3 to 8), so select the child age bands that apply and
  parents. Selecting any band under 13 brings the app under the Families Policy.
- **Appeals to children:** Yes.
- **Ads:** Yes, the app contains ads.
- **Families ads requirement:** the app uses only Google AdMob, which is on Google's
  list of Families self-certified ad SDKs. Requests are tagged child-directed and
  under the age of consent, capped at content rating G, and the advertising ID
  permission is removed.
- **Teacher Approved / Designed for Families programme:** optional; do not opt in
  until the translations have had a native read.

## Content rating questionnaire (expected answers)

- Violence, fear, sexual content, language, controlled substances, gambling: **No**
  to all. (The traditional tales are softened: nobody is hurt.)
- User-generated content or sharing between users: **No**. Recordings stay on the device.
- Shares location: **No**.
- Expected result: rated for everyone (IARC "3+" / "Everyone").

## Data safety form (draft)

The app itself collects nothing. The answers below come from the Google Mobile Ads
SDK; confirm them against Google's current "Prepare for Google Play's data disclosure
requirements" page for the SDK version in use (24.9.0).

- **Does the app collect or share user data?** Yes (through the ads SDK).
- **Data types collected**
  - Location → Approximate location (derived from IP address)
  - App activity → App interactions (ad shown / tapped)
  - App info and performance → Diagnostics
  - Device or other IDs → check Google's page: the advertising ID is NOT collected
    (permission removed), but the SDK may still use other device identifiers.
- **Purposes:** Advertising or marketing; Analytics; Fraud prevention, security and compliance.
- **Shared with third parties?** Yes, with Google as the ad provider.
- **Processed ephemerally / required or optional:** collection is required (cannot be
  switched off by the user).
- **Encrypted in transit:** Yes.
- **Data deletion request:** the developer holds no data; uninstalling removes
  everything on the device.
- **Not collected:** name, email, audio, files, contacts, photos. The child's name and
  the voice recordings never leave the device, and the app is excluded from cloud
  backup.
- **Committed to the Play Families Policy:** Yes.

## Permissions to explain if asked

- `RECORD_AUDIO` — optional; only when a parent records their own reading.
- `INTERNET`, `ACCESS_NETWORK_STATE` — banner advertisement only.

## Graphics still needed

- App icon, 512 × 512 (the launcher icon is a crescent moon and stars on indigo)
- Feature graphic, 1024 × 500
- Phone screenshots, at least 4, 9:16. Suggested set:
  1. Home screen with tonight's story
  2. A story page with the highlighted sentence
  3. The character and storyteller screen
  4. The same tale in Hindi (or another language)
  5. The story maker
  6. The goodnight screen
- Take them from a build without the test banner, or crop below it. Test ads must not
  appear in store screenshots.

## Release notes for the first release

```
The first release of Bedtime Stories: 52 illustrated bedtime stories read aloud in ten languages, with a lullaby, a sleep timer and the option to record the story in your own voice.
```

## Before production

- Closed test with at least 12 testers for 14 days (required for this developer account).
- Real AdMob app and banner unit ids in `ads.properties`; add the app to `app-ads.txt` on llassan.com.
- A native-speaker read of each non-English language.
