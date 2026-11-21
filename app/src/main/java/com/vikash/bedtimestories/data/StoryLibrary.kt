package com.vikash.bedtimestories.data

import java.util.Calendar

/**
 * The built-in stories. All original. The child is always referred to by name
 * (`{name}`), never by pronoun, so every story reads naturally for any child.
 */
object StoryLibrary {

    private fun p(text: String, art: String) = Page(text, art)

    /** The stories, with the longer retelling wherever one has been written. */
    val stories: List<Story> by lazy {
        base.map { s -> LONG_ORIGINALS[s.id]?.let { long -> s.copy(pages = long.map { Page(it.en, it.art) }, long = true) } ?: s }
    }

    private val base: List<Story> = listOf(
        Story(
            "moon-button", "The Moon’s Lost Button", Mood.KINDNESS, Scene.NIGHT, "hero button",
            "A little kindness can make the whole night brighter.",
            listOf(
                p("One quiet night, {name} looked out of the window and saw that the Moon looked sad. A tiny silver button was missing from the Moon’s soft, glowing coat.", "hero"),
                p("“I dropped it somewhere down below,” sighed the Moon. “Without it, my coat will not stay closed, and the night feels chilly.” {name} pulled on warm slippers and said, “I will help you look.”", "hero sparkle"),
                p("{name} tiptoed through the garden. An old snail was sliding slowly along the path. “Have you seen a silver button?” asked {name}. “Something shiny rolled toward the pond,” said the snail.", "hero snail"),
                p("At the pond, a small frog was holding the button like a treasure. “It is the prettiest thing I have ever found,” said the frog. {name} smiled gently. “It belongs to the Moon, and the Moon is cold without it.”", "frog button hero"),
                p("The frog thought for a moment, then held it up. “Then the Moon should have it back.” {name} thanked the frog and promised to visit the pond again tomorrow.", "frog hero"),
                p("{name} held the button up high, and a moonbeam carried it home. The Moon buttoned the glowing coat and shone warm and bright. “Thank you, {name},” whispered the Moon. “Sleep well, kind friend.”", "hero star sparkle"),
            ),
        ),
        Story(
            "owl-dark", "The Owl Who Was Afraid of the Dark", Mood.COURAGE, Scene.FOREST, "owl star",
            "Brave means trying, even when you feel a little scared.",
            listOf(
                p("In a tall pine tree lived a little owl named Pip. Owls are meant to love the night, but Pip did not. When the sun went down, Pip hid under a wing and shivered.", "owl"),
                p("One evening, {name} was walking home through the forest and heard a tiny hoot. “Who is there?” asked {name}. “Only me,” said Pip. “I am afraid of the dark.”", "hero owl"),
                p("“I know that feeling,” said {name}. “Shall we look at the dark together? It is easier with a friend.” Pip peeked out with one round eye.", "hero owl"),
                p("First they saw the fireflies, blinking like tiny lanterns. Then they heard the crickets singing a slow, sleepy song. “The dark is not empty,” said Pip. “It is full of friends.”", "sparkle owl sparkle"),
                p("Pip stretched both wings and flew one small circle around the tree. Then a bigger one. The stars seemed to cheer. {name} clapped softly, so the forest would not wake.", "owl star hero"),
                p("“Being brave does not mean you are never scared,” said {name}. “It means you try anyway.” Pip hooted a happy goodnight, and {name} walked home under a sky that felt friendly.", "hero house"),
            ),
        ),
        Story(
            "sleepy-cloud", "The Sleepy Little Cloud", Mood.CALM, Scene.SKY, "cloud",
            "Slow breaths help busy bodies rest.",
            listOf(
                p("High above the hills floated a little cloud named Nimbo. All day Nimbo had raced the wind and chased the birds, and now Nimbo was very, very tired.", "cloud bird"),
                p("But Nimbo could not find a place to rest. The mountain was too pointy. The treetops were too tickly. The lake was much too wet.", "cloud oak"),
                p("{name} was lying in the grass, watching the sky turn pink. “You look sleepy, little cloud,” said {name}. “Try breathing slowly, like this. In... and out.”", "hero cloud"),
                p("Nimbo breathed in, and grew round and soft. Nimbo breathed out, and drifted a little lower. In... and out. In... and out. The wind grew quiet to listen.", "cloud"),
                p("Soon Nimbo settled gently on top of the hill, just like a pillow. The first star came out to keep watch. “That is much better,” yawned the little cloud.", "cloud~ star"),
                p("{name} yawned too, and walked home to a cozy bed. In... and out. In... and out. Outside the window, a small cloud was already dreaming.", "house cloud~ zzz"),
            ),
        ),
        Story(
            "lantern-fish", "The Lantern Fish", Mood.COURAGE, Scene.SEA, "lumi",
            "What makes you different can light the way for others.",
            listOf(
                p("Deep in the blue sea lived a small fish named Lumi, who had a tiny light on the tip of one fin. The other fish had bright stripes and long tails. Lumi only had a little glow.", "fish lumi fish"),
                p("{name} came sailing by in a small glass boat that could dive beneath the waves. “What a lovely light,” said {name}. Lumi blushed. “It is only small,” said the fish.", "hero lumi"),
                p("Just then, the water grew dark. A baby turtle was swimming in circles, crying. “I cannot find the way home to the reef!”", "turtle"),
                p("“A small light is exactly what we need,” said {name}. Lumi took a deep breath and swam to the front. The little glow shone gold in the dark water.", "hero lumi"),
                p("Past the waving seaweed they went, past the sleeping whale, all the way to the reef. The baby turtle’s family was waiting with open flippers.", "lumi turtle whale~"),
                p("“Your light is not small at all,” said {name}. “It is just the right size to help.” Lumi glowed all the way home, and {name} sailed back up to bed.", "turtle lumi hero"),
            ),
        ),
        Story(
            "bear-honey", "The Bear Who Shared the Honey", Mood.FRIENDSHIP, Scene.MEADOW, "bear honey",
            "Good things grow when we share them.",
            listOf(
                p("Bruno the bear had found the biggest pot of honey in the whole meadow. “Mine, all mine,” said Bruno, and carried it up to the top of a hill.", "bear honey"),
                p("{name} was picking flowers nearby with a rabbit and a little mouse. “That honey smells wonderful,” said the mouse. Bruno hugged the pot tight and turned away.", "hero rabbit mouse"),
                p("Bruno ate one pawful, then another. It was sweet, but the hilltop was very quiet. Down below, {name} and the others were laughing together.", "bear honey"),
                p("{name} climbed the hill and sat beside the bear. “We have warm bread,” said {name}, “but nothing to put on it. And you have honey, but no one to enjoy it with.”", "hero bear honey"),
                p("Bruno looked at the pot, then at the friendly faces. Slowly, the bear carried the honey down the hill. Everyone had honey on warm bread, and it tasted twice as sweet.", "rabbit honey bear mouse"),
                p("When the stars came out, Bruno walked {name} home. “Sharing made my honey smaller,” said the bear, “but it made my evening bigger.” {name} smiled all the way to bed.", "bear hero"),
            ),
        ),
        Story(
            "snail-race", "The Snail’s Slow Race", Mood.CALM, Scene.MEADOW, "snail oak",
            "There is no need to hurry. Slow can be wonderful.",
            listOf(
                p("Every evening the garden animals raced to the old oak tree. The rabbit zoomed. The squirrel zipped. And Shelly the snail came last, every single time.", "rabbit squirrel snail"),
                p("“I wish I were fast,” Shelly told {name}. {name} knelt down in the grass. “May I walk at your speed tonight? I would like to see what you see.”", "snail hero"),
                p("So they went slowly. They saw a spider finishing a silver web. They saw dewdrops holding tiny moons. They smelled the night flowers opening.", "snail sparkle hero"),
                p("The rabbit hopped back. “Why are you so slow?” Then the rabbit saw the web, and the dewdrops, and stopped. “Oh,” whispered the rabbit. “I never noticed.”", "rabbit snail"),
                p("One by one, the animals came back and walked slowly too. It was the longest race the garden had ever had, and the most beautiful one.", "snail hero rabbit squirrel"),
                p("They all reached the oak tree together, under a sky full of stars. “Slow is not last,” said {name}. “Slow is how you see everything.” And slowly, slowly, everyone went to bed.", "oak snail hero"),
            ),
        ),
        Story(
            "star-painter", "The Star Painter", Mood.WONDER, Scene.NIGHT, "painter star",
            "You do not have to be perfect to make something beautiful.",
            listOf(
                p("One night {name} found a ladder leaning against the sky. At the top sat an old woman with a long brush and a bucket of silver paint.", "ladder hero"),
                p("“I am the Star Painter,” she said. “Every night I paint the stars back on. But tonight my arms are tired, and half the sky is still dark.”", "painter star"),
                p("“I can help,” said {name}, “but I do not know how to paint a star.” The Star Painter laughed kindly. “Nobody does, the first time. Just begin.”", "hero painter"),
                p("{name}’s first star was wobbly. The second had too many points. But the third one twinkled, and the fourth one shone, and soon the brush was dancing.", "hero star sparkle"),
                p("“Look,” said the Star Painter. Far below, children in their beds were pointing up at the new stars. “They like the wobbly one best. It looks like it is waving.”", "painter hero star"),
                p("{name} climbed down the ladder, sleepy and proud. From the pillow, {name} could see it: one wobbly little star, waving goodnight.", "hero~ zzz star"),
            ),
        ),
        Story(
            "hedgehog-hug", "The Hedgehog’s Hug", Mood.FRIENDSHIP, Scene.FOREST, "hedgehog heart",
            "There are many ways to show someone you care.",
            listOf(
                p("Hazel the hedgehog wanted a hug more than anything. But whenever she came close, the other animals said, “Ouch!” and hopped away from her prickles.", "hedgehog squirrel"),
                p("{name} found Hazel sitting alone under a fern. “Nobody can hug me,” sniffed the hedgehog. “I am too prickly to be a friend.”", "hedgehog hero"),
                p("“There are many ways to hug,” said {name}. “A wave can be a hug. A shared blackberry can be a hug. So can sitting close and listening.”", "hero hedgehog"),
                p("So Hazel tried. She waved to the squirrel. She shared berries with the mole. She listened to the old toad’s very long story, all the way to the end.", "hedgehog squirrel frog"),
                p("Soon everyone wanted to sit beside Hazel. And when the night grew cold, {name} wrapped a soft scarf around her. Now Hazel could be hugged, prickles and all.", "hero hedgehog"),
                p("“That was my first real hug,” said Hazel. “It will not be your last,” said {name}. The forest friends said goodnight, and the fern leaves closed like a blanket.", "hedgehog heart hero"),
            ),
        ),
        Story(
            "dream-train", "The Little Train to Dreamland", Mood.CALM, Scene.NIGHT, "train",
            "Every night, a gentle journey is waiting for you.",
            listOf(
                p("At the edge of {name}’s bed, a little blue train was waiting. “All aboard for Dreamland,” whispered the conductor, a sleepy old cat in a tiny hat.", "train conductor"),
                p("{name} climbed in and pulled up a blanket. Chug... chug... chug... The train rolled slowly out of the bedroom and over the rooftops.", "train hero"),
                p("First stop: the Yawning Hills, where the sheep were already counting themselves. {name} gave a great big yawn. So did the cat.", "sheep sheep~ sheep~"),
                p("Second stop: the Pillow Fields, where everything was soft and white. The train went slower now. Chug... chug... The wheels were getting sleepy too.", "train cloud~"),
                p("Third stop: the Quiet Lake, smooth as glass, with the moon floating in the middle. {name}’s eyes were growing heavy. The blanket was so warm.", "conductor hero"),
                p("“Last stop, Dreamland,” purred the cat, very softly. But {name} did not hear. {name} was already there. Goodnight, little traveler.", "conductor zzz hero~"),
            ),
        ),
        Story(
            "thank-you-tree", "The Fox and the Thank-You Tree", Mood.KINDNESS, Scene.SNOW, "fox tree",
            "Every day holds something to be thankful for.",
            listOf(
                p("On a snowy evening, {name} met a little fox named Fern, who was hanging something on the branch of a bare winter tree.", "fox tree hero"),
                p("“This is the Thank-You Tree,” said Fern. “Each night I hang up one thing I am glad about. Tonight it is a feather, because the robin sang for me.”", "fox tree"),
                p("{name} thought about the day. “I am glad for warm soup,” said {name}, and tied a red ribbon to a branch. The tree seemed a little less bare.", "hero tree"),
                p("“And for my cozy bed. And for someone who says goodnight.” Each thank-you became a ribbon, a pinecone, or a paper star. The branches began to sparkle.", "tree star"),
                p("Other animals came out of the snow to add their own. A deer was glad for the moonlight. A mouse was glad for a crumb. Soon the whole tree was glowing.", "deer tree mouse"),
                p("“The tree was never empty,” said Fern. “We only had to notice.” {name} walked home through the soft snow, counting good things all the way to sleep.", "fox tree hero"),
            ),
        ),
        Story(
            "whisper-dragon", "The Dragon Who Whispered", Mood.FRIENDSHIP, Scene.SKY, "dragon",
            "A gentle voice can be the strongest one.",
            listOf(
                p("Ember was a small green dragon with a very big roar. When Ember said hello, birds fell out of trees and the village windows rattled.", "dragon bird"),
                p("So nobody came to play. Ember sat on a hill at sunset, puffing sad little smoke rings, when {name} walked up with both hands over both ears.", "dragon hero"),
                p("“You do not have to be loud to be heard,” said {name}. “Try a whisper. A whisper makes people lean in close.”", "hero dragon"),
                p("Ember tried. The first whisper blew {name}’s hat off. The second one only ruffled the grass. The third was as soft as a candle flame: “Hello.”", "dragon hero"),
                p("A rabbit crept closer. Then a lamb. Then the children from the village. Ember whispered a story about the clouds, and everyone leaned in to listen.", "rabbit sheep dragon"),
                p("When the story ended, Ember gave {name} a ride home, flying low and quiet over the sleeping roofs. “Goodnight,” whispered the dragon. And it was the softest sound in the world.", "dragon hero house"),
            ),
        ),
        Story(
            "blanket-boat", "The Blanket Boat", Mood.WONDER, Scene.OCEAN, "boat whale",
            "Your imagination can carry you gently to sleep.",
            listOf(
                p("When the lamp clicked off, {name}’s blanket began to ripple like water. The bed rocked gently. It had become a little boat on a wide, calm sea.", "boat hero"),
                p("A whale as big as a house swam up beside the boat. “Where are you sailing tonight?” asked the whale in a deep, slow voice. “To wherever dreams begin,” said {name}.", "whale boat"),
                p("“Then follow the moon path,” said the whale, and blew a fountain of sparkling mist to show the way. The boat drifted along the silver stripe on the water.", "whale sparkle boat"),
                p("They passed an island of sleeping seals, snoring softly. They passed a lighthouse that blinked slowly, like a tired eye. Blink... blink... blink.", "seal seal"),
                p("The waves rocked the boat. Up... and down. Up... and down. {name} lay back on the pillow and watched the stars sway overhead.", "boat hero"),
                p("“Here is where dreams begin,” the whale said quietly. The sea became a blanket again, warm and still. And {name} sailed on, fast asleep.", "whale zzz hero~"),
            ),
        ),
    )

    /** A different featured story each night. */
    fun tonight(): Story = stories[Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % stories.size]
}
