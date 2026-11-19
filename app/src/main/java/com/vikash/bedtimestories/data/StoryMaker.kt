package com.vikash.bedtimestories.data

/**
 * Builds a new story from three choices: a companion, a place and a lesson.
 * Works fully offline. A made story is identified by its choices
 * (`custom:<companion>:<place>:<lesson>`), so it can be rebuilt at any time.
 */
object StoryMaker {

    data class Companion(val name: String, val kind: String, val actor: String) {
        val full get() = "$name the $kind"
    }

    data class Place(val name: String, val scene: Scene, val journey: String)

    data class Lesson(
        val mood: Mood,
        val label: String,
        val scene3: String,
        val moral: String,
        val problem: String,
        val action: String,
        val ending: String,
    )

    val companions = listOf(
        Companion("Pip", "owl", "owl"),
        Companion("Momo", "kitten", "cat"),
        Companion("Tuck", "turtle", "tortoise"),
        Companion("Bibi", "bunny", "rabbit"),
        Companion("Sunny", "puppy", "puppy"),
        Companion("Nori", "penguin", "penguin"),
    )

    val places = listOf(
        Place("Starry Forest", Scene.FOREST, "They walked beneath tall, whispering trees. Fireflies lit the path like tiny lanterns, and the moss was as soft as a pillow."),
        Place("Sleepy Sea", Scene.OCEAN, "They sailed in a little shell boat over slow, rolling waves. Silver fish swam alongside, and the water hummed a quiet tune."),
        Place("Cloud Kingdom", Scene.SKY, "They climbed a staircase made of mist, higher and higher. Up there, the clouds were as bouncy as beds and as pink as peaches."),
        Place("Snowy Mountain", Scene.SNOW, "They crunched through fresh, sparkling snow. Every snowflake caught the moonlight, and their breath made tiny clouds in the air."),
        Place("Moonlit Meadow", Scene.MEADOW, "They wandered through tall grass that swished like a lullaby. Night flowers opened one by one, glowing softly in the moonlight."),
    )

    val lessons = listOf(
        Lesson(
            Mood.KINDNESS, "Kindness", "hedgehog", "A small kindness can warm a whole night.",
            "Beside the path sat a tiny hedgehog, shivering. “I lost my scarf in the wind,” it sniffed.",
            "{name} did not think twice. {name} unwound a warm scarf and tucked it around the hedgehog. {c} added a soft leaf for a hat.",
            "The hedgehog’s smile was as bright as a lantern. “I will remember this forever,” it said. {name} felt warm from head to toe, even without a scarf.",
        ),
        Lesson(
            Mood.COURAGE, "Courage", "{c}", "Being brave is easier with a friend beside you.",
            "Ahead, the path crossed a wobbly little bridge. {c} stopped. “I am too scared to cross,” said {c}, trembling.",
            "{name} felt a flutter of worry too, but held out a hand. “We will go one step at a time, together.” One step. Then another. Then another.",
            "On the other side, the whole {place} sparkled before them. “We did it!” cheered {c}. “Being brave is easier with a friend,” said {name}.",
        ),
        Lesson(
            Mood.FRIENDSHIP, "Sharing", "mouse star", "Good things taste sweeter when everyone has some.",
            "They found a tree hung with exactly three glowing moon-pears. Just then, a hungry little mouse peeked out from the roots.",
            "“One for you, one for me, and one for our new friend,” said {name}. {c} nodded happily, and the mouse squeaked with joy.",
            "The three of them sat together, munching and giggling. The pears tasted sweeter than anything {name} had ever eaten, because everyone had some.",
        ),
        Lesson(
            Mood.CALM, "Patience", "sparkle {c}", "Good things take time, and waiting can be peaceful.",
            "In the middle of the {place} was a small flower bud, shut tight. “It only opens for those who wait,” said {c}.",
            "So they waited. {name} counted slow breaths. In... and out. In... and out. The night grew still and quiet around them.",
            "Then, petal by petal, the flower opened, and a soft golden light poured out. “Good things take time,” whispered {name}.",
        ),
        Lesson(
            Mood.WONDER, "Gratitude", "{c} star", "There is always something good to notice.",
            "{c} stopped beside a still, round pool. “This is the Thank-You Pool,” said {c}. “Whisper something you are glad for, and watch.”",
            "“I am glad for my cozy bed,” whispered {name}. A star dropped into the pool and glowed. “And for you, {c}.” Another star. And another.",
            "Soon the pool shone like the night sky itself. “So many good things,” said {name}, “and I only had to notice them.”",
        ),
    )

    fun id(companion: Int, place: Int, lesson: Int) = "custom:$companion:$place:$lesson"

    fun fromId(id: String): Story? {
        val parts = id.split(':')
        if (parts.size != 4 || parts[0] != "custom") return null
        val c = companions.getOrNull(parts[1].toIntOrNull() ?: -1) ?: return null
        val pl = places.getOrNull(parts[2].toIntOrNull() ?: -1) ?: return null
        val l = lessons.getOrNull(parts[3].toIntOrNull() ?: -1) ?: return null
        fun t(s: String) = s.replace("{c}", c.name).replace("{place}", pl.name)
        return Story(
            id = id,
            title = "{name} and ${c.name} in the ${pl.name}",
            mood = l.mood,
            scene = pl.scene,
            cover = "hero ${c.actor}",
            moral = l.moral,
            custom = true,
            pages = listOf(
                Page("One evening, just as the sky turned the color of plums, {name} heard a soft tap at the window. It was ${c.full}. “Come with me to the ${pl.name},” said ${c.name}. “Something wonderful is waiting.”", "${c.actor} hero"),
                Page(pl.journey, "hero ${c.actor}"),
                Page(t(l.problem), l.scene3.replace("{c}", c.actor)),
                Page(t(l.action), "hero heart ${c.actor}"),
                Page(t(l.ending), "${c.actor} sparkle hero"),
                Page("Then ${c.name} walked {name} all the way home. The ${pl.name} grew quiet behind them. “Goodnight, {name},” said ${c.name}. “We will go again in your dreams.” And {name} fell fast asleep.", "hero~ zzz ${c.actor}"),
            ),
        )
    }
}
