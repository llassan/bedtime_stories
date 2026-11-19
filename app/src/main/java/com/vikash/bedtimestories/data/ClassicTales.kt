package com.vikash.bedtimestories.data

/**
 * Traditional tales from the Panchatantra and other well-loved fables, retold
 * gently for bedtime in English and Hindi. The old stories are in the public
 * domain; these retellings are written for this app, and the harsher endings
 * are softened so nobody is hurt.
 */
object ClassicTales {

    private fun p(art: String, en: String, hi: String) = Page(en, art, hi)

    private fun tale(
        id: String, scene: Scene, mood: Mood, cover: String,
        title: String, titleHi: String, moral: String, moralHi: String,
        vararg pages: Page,
    ) = Story(id, title, mood, scene, cover, moral, pages.toList(), titleHi = titleHi, moralHi = moralHi, classic = true)

    /** The tales with every extra language attached. */
    val stories: List<Story> by lazy {
        (base + ClassicTales2.base).map { story ->
            val id = story.id
            val extra = mapOf(
                Lang.MR to (TALES_MR[id] ?: TALES2_MR[id]), Lang.GU to (TALES_GU[id] ?: TALES2_GU[id]),
                Lang.TA to (TALES_TA[id] ?: TALES2_TA[id]), Lang.TE to (TALES_TE[id] ?: TALES2_TE[id]),
                Lang.BN to (TALES_BN[id] ?: TALES2_BN[id]), Lang.KN to (TALES_KN[id] ?: TALES2_KN[id]),
                Lang.ML to (TALES_ML[id] ?: TALES2_ML[id]), Lang.PA to (TALES_PA[id] ?: TALES2_PA[id]),
            ).filterValues { it != null && it.pages.size == story.pages.size }.mapValues { it.value!! }
            // The first set carries its Hindi inline; the second set keeps it in a map like the others.
            val hindi = TALES2_HI[id]?.takeIf { it.pages.size == story.pages.size }
            story.copy(
                titleHi = story.titleHi ?: hindi?.title,
                moralHi = story.moralHi ?: hindi?.moral,
                more = extra,
                pages = story.pages.mapIndexed { i, page ->
                    page.copy(hi = page.hi ?: hindi?.pages?.get(i), more = extra.mapValues { it.value.pages[i] })
                },
            )
        }
    }

    private val base: List<Story> = listOf(
        tale(
            "monkey-crocodile", Scene.RIVER, Mood.WISDOM, "monkey crocodile",
            "The Monkey and the Crocodile", "बंदर और मगरमच्छ",
            "A calm, quick mind can get you out of trouble.", "शांत और तेज़ दिमाग़ हर मुश्किल से निकाल लेता है।",
            p("monkey", "A clever monkey lived in a rose-apple tree beside a river. The fruit was sweet, and the monkey was happy.", "नदी किनारे जामुन के पेड़ पर एक चतुर बंदर रहता था। जामुन मीठे थे और बंदर बहुत ख़ुश था।"),
            p("monkey crocodile", "One day a crocodile came to rest under the tree. The monkey threw down some fruit, and the two became friends.", "एक दिन एक मगरमच्छ पेड़ के नीचे आराम करने आया। बंदर ने उसे जामुन दिए और दोनों दोस्त बन गए।"),
            p("crocodile monkey", "But the crocodile's wife wanted to eat the monkey's heart. So the crocodile said, “Come to my home for dinner. Ride on my back.”", "पर मगरमच्छ की पत्नी बंदर का दिल खाना चाहती थी। मगरमच्छ बोला, “मेरे घर खाने पर चलो। मेरी पीठ पर बैठ जाओ।”"),
            p("monkey crocodile", "In the middle of the river, the crocodile told the truth. The monkey stayed calm. “Oh dear,” said the monkey, “I left my heart in the tree. Let us go back for it.”", "नदी के बीच में मगरमच्छ ने सच बता दिया। बंदर घबराया नहीं। वह बोला, “अरे, मैं तो अपना दिल पेड़ पर ही भूल आया। चलो, वापस चलकर ले आते हैं।”"),
            p("monkey", "The crocodile swam back. The monkey leapt into the tree and was safe. “A true friend never tricks a friend,” said the monkey, and went happily to sleep.", "मगरमच्छ वापस किनारे आया। बंदर झट से पेड़ पर चढ़ गया और बच गया। वह बोला, “सच्चा दोस्त कभी धोखा नहीं देता।” फिर वह चैन से सो गया।"),
        ),
        tale(
            "lion-rabbit", Scene.JUNGLE, Mood.WISDOM, "lion rabbit",
            "The Lion and the Clever Rabbit", "शेर और चतुर खरगोश",
            "Wisdom is stronger than strength.", "बुद्धि बल से बड़ी होती है।",
            p("lion", "A proud lion frightened every animal in the jungle. Each day he roared, and each day the animals hid.", "एक घमंडी शेर जंगल के सब जानवरों को डराता था। वह रोज़ दहाड़ता और सब जानवर छिप जाते।"),
            p("rabbit", "One day it was the little rabbit's turn to visit the lion. The rabbit walked slowly, thinking and thinking.", "एक दिन शेर के पास जाने की बारी नन्हे खरगोश की थी। खरगोश धीरे-धीरे चलता रहा और सोचता रहा।"),
            p("lion rabbit", "“You are late!” roared the lion. “I am sorry,” said the rabbit. “Another lion stopped me. He says this jungle is his.”", "शेर गरजा, “तुम देर से आए!” खरगोश बोला, “माफ़ कीजिए। रास्ते में एक और शेर ने मुझे रोक लिया। वह कहता है कि यह जंगल उसका है।”"),
            p("lion well rabbit", "The rabbit led the lion to a deep well. The lion looked in and saw a lion looking back. He roared. The roar came back, even louder.", "खरगोश शेर को एक गहरे कुएँ के पास ले गया। शेर ने अंदर झाँका तो उसे एक शेर दिखा। वह दहाड़ा। कुएँ से और ज़ोर की दहाड़ लौटकर आई।"),
            p("rabbit", "The lion was so startled that he ran far, far away and never troubled anyone again. That night the whole jungle slept in peace.", "शेर इतना घबरा गया कि बहुत दूर भाग गया और फिर कभी किसी को नहीं सताया। उस रात पूरा जंगल चैन से सोया।"),
        ),
        tale(
            "thirsty-crow", Scene.MEADOW, Mood.WISDOM, "crow pitcher",
            "The Thirsty Crow", "प्यासा कौआ",
            "Where there is a will, there is a way.", "जहाँ चाह, वहाँ राह।",
            p("crow", "It was a very hot day. A crow flew over the fields, looking everywhere for water.", "बहुत गर्मी का दिन था। एक कौआ खेतों के ऊपर उड़ता हुआ हर जगह पानी ढूँढ रहा था।"),
            p("crow pitcher", "At last the crow saw a pitcher in a garden. But the water was low, too low for a beak to reach.", "आख़िर उसे एक बगीचे में एक घड़ा दिखा। पर पानी बहुत नीचे था। चोंच वहाँ तक पहुँचती ही नहीं थी।"),
            p("crow", "The crow did not give up. The crow sat quietly and thought. Then the crow saw some small pebbles on the ground.", "कौए ने हार नहीं मानी। वह चुपचाप बैठकर सोचने लगा। तभी उसे ज़मीन पर कुछ छोटे कंकड़ दिखे।"),
            p("pitcher crow", "One by one, the crow dropped the pebbles into the pitcher. Plop. Plop. Plop. Slowly, slowly, the water rose.", "कौए ने एक-एक करके कंकड़ घड़े में डाले। टप। टप। टप। धीरे-धीरे पानी ऊपर आने लगा।"),
            p("crow pitcher", "At last the water reached the top. The crow drank the cool water, said a happy caw, and flew home to rest.", "आख़िर पानी ऊपर तक आ गया। कौए ने ठंडा पानी पिया, ख़ुशी से काँव-काँव किया और आराम करने घर उड़ गया।"),
        ),
        tale(
            "tortoise-geese", Scene.RIVER, Mood.CALM, "goose tortoise goose",
            "The Tortoise and the Geese", "कछुआ और हंस",
            "Sometimes the wisest thing is to stay quiet.", "कभी-कभी चुप रहना ही सबसे बड़ी समझदारी है।",
            p("tortoise goose", "A tortoise lived in a pond with two kind geese. The tortoise loved to talk, all day long.", "एक तालाब में एक कछुआ दो भले हंसों के साथ रहता था। कछुए को दिन भर बातें करना बहुत अच्छा लगता था।"),
            p("goose tortoise goose", "One summer the pond began to dry. “We will carry you to a new lake,” said the geese. “Hold this stick in your mouth. But you must not speak.”", "एक गर्मी में तालाब सूखने लगा। हंस बोले, “हम तुम्हें नई झील तक ले चलेंगे। यह लकड़ी मुँह से पकड़ लो। पर याद रखना, बोलना बिलकुल नहीं।”"),
            p("goose tortoise goose", "Up they flew, over the fields and the village. Children pointed up and laughed. “Look, a flying tortoise!”", "वे खेतों और गाँव के ऊपर उड़ चले। बच्चे ऊपर देखकर हँसने लगे, “देखो, उड़ता हुआ कछुआ!”"),
            p("tortoise", "The tortoise wanted to answer. The tortoise opened its mouth, and down it tumbled, plop, into a soft heap of hay.", "कछुए का जवाब देने का मन हुआ। उसने मुँह खोला और धम्म से नीचे घास के नरम ढेर पर आ गिरा।"),
            p("goose tortoise", "The geese flew down and carried their friend the rest of the way. “Next time,” said the tortoise softly, “I will listen first.” And the lake was quiet all night.", "हंस नीचे आए और अपने दोस्त को झील तक ले गए। कछुआ धीरे से बोला, “अगली बार मैं पहले बात सुनूँगा।” और झील रात भर शांत रही।"),
        ),
        tale(
            "blue-jackal", Scene.JUNGLE, Mood.WISDOM, "bluejackal",
            "The Blue Jackal", "नीला सियार",
            "Be yourself. You cannot hide who you are for long.", "जो हो, वही बने रहो। असलियत ज़्यादा दिन नहीं छिपती।",
            p("jackal", "A hungry jackal wandered into a village one night. Dogs barked, and the jackal ran and fell into a tub of blue dye.", "एक रात एक भूखा सियार गाँव में घुस आया। कुत्ते भौंके तो वह भागा और नीले रंग के एक टब में जा गिरा।"),
            p("bluejackal", "The jackal climbed out, blue from nose to tail. Back in the jungle, nobody knew who this strange animal was.", "सियार बाहर निकला तो नाक से पूँछ तक नीला था। जंगल में कोई नहीं पहचान पाया कि यह अजीब जानवर कौन है।"),
            p("bluejackal lion", "“I am your new king,” said the blue jackal. Even the lion bowed. Every day the animals brought the king good food.", "नीला सियार बोला, “मैं तुम्हारा नया राजा हूँ।” शेर ने भी सिर झुका लिया। जानवर रोज़ राजा के लिए अच्छा खाना लाने लगे।"),
            p("bluejackal jackal", "One moonlit night, the other jackals began to howl. The blue jackal forgot everything and howled along with them. “Owooo!”", "एक चाँदनी रात में दूसरे सियार हुआँ-हुआँ करने लगे। नीला सियार सब भूल गया और वह भी उनके साथ बोल पड़ा, “हुआँ-हुआँ!”"),
            p("jackal lion", "Now everyone knew the truth. The jackal said sorry, washed off the blue, and went back to being a jackal. It felt much better.", "अब सबको सच पता चल गया। सियार ने माफ़ी माँगी, नीला रंग धो डाला और फिर से सियार बन गया। उसे बहुत हल्का लगा।"),
        ),
        tale(
            "lion-mouse", Scene.JUNGLE, Mood.KINDNESS, "lion mouse",
            "The Lion and the Mouse", "शेर और चूहा",
            "No one is too small to help.", "कोई भी इतना छोटा नहीं कि मदद न कर सके।",
            p("lion~", "A big lion was sleeping under a tree. A little mouse ran over his paw by mistake, and the lion woke up.", "एक बड़ा शेर पेड़ के नीचे सो रहा था। एक नन्हा चूहा ग़लती से उसके पंजे पर से दौड़ गया और शेर जाग गया।"),
            p("lion mouse", "The lion caught the mouse. “Please let me go,” said the mouse. “One day I will help you.” The lion laughed, but he let the mouse go.", "शेर ने चूहे को पकड़ लिया। चूहा बोला, “मुझे छोड़ दीजिए। किसी दिन मैं आपके काम आऊँगा।” शेर हँसा, पर उसने चूहे को जाने दिया।"),
            p("lion", "Some days later, the lion walked into a hunter's net. He pulled and pulled, but the ropes held him tight.", "कुछ दिन बाद शेर एक शिकारी के जाल में फँस गया। उसने बहुत ज़ोर लगाया, पर रस्सियाँ टस से मस न हुईं।"),
            p("mouse lion", "The little mouse heard the lion and came running. Nibble, nibble, nibble went the tiny teeth, until the ropes fell away.", "नन्हे चूहे ने शेर की आवाज़ सुनी और दौड़ा चला आया। उसके छोटे दाँत कुतर-कुतर करते रहे और रस्सियाँ कट गईं।"),
            p("lion mouse", "“Thank you, little friend,” said the lion. From that day the two were friends, and they often watched the stars together.", "शेर बोला, “धन्यवाद, नन्हे दोस्त।” उस दिन से दोनों दोस्त बन गए और अक्सर साथ बैठकर तारे देखा करते।"),
        ),
        tale(
            "doves-net", Scene.MEADOW, Mood.FRIENDSHIP, "dove dove dove",
            "The Doves and the Net", "कबूतर और जाल",
            "Together we are strong.", "एकता में बल है।",
            p("dove dove dove", "A flock of doves flew across the sky, looking for food. Below, they saw grains of rice on the ground.", "कबूतरों का एक झुंड दाने की तलाश में आसमान में उड़ रहा था। नीचे उन्हें ज़मीन पर चावल के दाने दिखे।"),
            p("dove dove", "The doves flew down to eat. Suddenly a net fell over them. Each dove pulled a different way, and nobody could get free.", "कबूतर दाना चुगने नीचे उतरे। अचानक उन पर एक जाल आ गिरा। हर कबूतर अलग दिशा में खींचने लगा और कोई छूट न सका।"),
            p("dove dove dove", "“Stop,” said the oldest dove. “Let us all fly up together, at the count of three. One, two, three!”", "सबसे बड़े कबूतर ने कहा, “रुको। हम सब एक साथ उड़ेंगे। तीन तक गिनो। एक, दो, तीन!”"),
            p("dove dove dove", "All the wings beat together. Up went the doves, and up went the net with them, over the trees and far away.", "सारे पंख एक साथ फड़फड़ाए। कबूतर ऊपर उठे और जाल भी उनके साथ उठ गया, पेड़ों के पार, बहुत दूर।"),
            p("mouse dove dove", "They flew to their friend the mouse, who nibbled the net open. The doves thanked the mouse and slept safely in their tree.", "वे अपने दोस्त चूहे के पास पहुँचे। चूहे ने जाल कुतर दिया। कबूतरों ने चूहे को धन्यवाद दिया और अपने पेड़ पर चैन से सो गए।"),
        ),
        tale(
            "crane-crab", Scene.RIVER, Mood.WISDOM, "crane crab",
            "The Crane and the Crab", "बगुला और केकड़ा",
            "Think carefully before you trust sweet words.", "मीठी बातों पर भरोसा करने से पहले सोच लो।",
            p("crane fish", "An old crane lived by a pond full of fish. The crane was too slow to catch them, so the crane made a plan.", "मछलियों से भरे एक तालाब के किनारे एक बूढ़ा बगुला रहता था। वह अब मछलियाँ पकड़ नहीं पाता था, तो उसने एक चाल सोची।"),
            p("crane fish", "“This pond will soon dry up,” said the crane sadly. “I can carry you to a big lake, one at a time.” The fish believed the crane.", "बगुला उदास होकर बोला, “यह तालाब जल्दी सूख जाएगा। मैं तुम्हें एक-एक करके बड़ी झील तक पहुँचा दूँगा।” मछलियों ने उसकी बात मान ली।"),
            p("crab crane", "But the clever crab was not so sure. “Take me first,” said the crab, “and let me see this lake.” The crab held on to the crane's neck.", "पर चतुर केकड़े को शक हुआ। वह बोला, “पहले मुझे ले चलो। मैं वह झील देखना चाहता हूँ।” केकड़े ने बगुले की गर्दन पकड़ ली।"),
            p("crab crane", "They flew far, and there was no lake at all. “You tricked us!” said the crab, and gave the crane's neck a gentle pinch. “Take me home.”", "वे दूर तक उड़े, पर कोई झील थी ही नहीं। केकड़ा बोला, “तुमने हमें धोखा दिया!” उसने बगुले की गर्दन पर हल्की चिकोटी काटी। “मुझे घर ले चलो।”"),
            p("crab fish", "The crane flew straight back and never tried a trick again. The crab told the fish everything, and the pond was safe and sleepy once more.", "बगुला सीधा वापस लौटा और फिर कभी चाल नहीं चली। केकड़े ने मछलियों को सब बता दिया और तालाब फिर से सुरक्षित और शांत हो गया।"),
        ),
        tale(
            "elephants-mice", Scene.JUNGLE, Mood.KINDNESS, "elephant mouse",
            "The Elephants and the Mice", "हाथी और चूहे",
            "Kindness always comes back to you.", "भलाई हमेशा लौटकर आती है।",
            p("mouse mouse", "A village of mice lived beside a lake. They were small and busy, and very happy.", "एक झील के किनारे चूहों का एक गाँव था। वे छोटे थे, मेहनती थे और बहुत ख़ुश थे।"),
            p("elephant mouse", "Every day a herd of elephants walked to the lake, right through the village. The mice had to run from the huge feet.", "हाथियों का एक झुंड रोज़ झील पर जाता था, ठीक गाँव के बीच से। चूहों को उनके बड़े पैरों से बचकर भागना पड़ता था।"),
            p("mouse elephant", "The mouse king went to the elephant king. “Please take another path. One day we will help you too.” The elephant smiled and agreed.", "चूहों का राजा हाथियों के राजा के पास गया। “कृपया दूसरा रास्ता ले लीजिए। किसी दिन हम भी आपके काम आएँगे।” हाथी मुस्कुराया और मान गया।"),
            p("elephant", "Later, hunters caught the elephants in strong rope nets. The elephant king remembered the mice and called for help.", "कुछ समय बाद शिकारियों ने हाथियों को मज़बूत रस्सियों के जाल में पकड़ लिया। हाथियों के राजा को चूहे याद आए और उसने मदद के लिए पुकारा।"),
            p("mouse elephant mouse", "Hundreds of mice came running. They nibbled every rope until the elephants were free. Big and small, they were friends for ever.", "सैकड़ों चूहे दौड़े चले आए। उन्होंने सारी रस्सियाँ कुतर दीं और हाथी आज़ाद हो गए। बड़े और छोटे, सब हमेशा के लिए दोस्त बन गए।"),
        ),
        tale(
            "hare-tortoise", Scene.MEADOW, Mood.CALM, "rabbit tortoise",
            "The Hare and the Tortoise", "खरगोश और कछुआ",
            "Slow and steady wins the race.", "धीरे-धीरे चलने वाला ही दौड़ जीतता है।",
            p("rabbit tortoise", "A hare liked to boast about how fast he was. “You are so slow,” he laughed at the tortoise.", "एक खरगोश को अपनी तेज़ चाल पर बड़ा घमंड था। वह कछुए पर हँसता, “तुम कितने धीमे हो!”"),
            p("tortoise rabbit", "“Let us have a race,” said the tortoise quietly. All the animals came to watch. Ready, steady, go!", "कछुआ धीरे से बोला, “चलो, दौड़ लगाते हैं।” सारे जानवर देखने आ गए। तैयार, एक, दो, तीन!"),
            p("rabbit~ oak", "The hare ran far ahead. “I have plenty of time,” he said. He lay down under a tree and fell fast asleep.", "खरगोश बहुत आगे निकल गया। वह बोला, “मेरे पास बहुत समय है।” वह एक पेड़ के नीचे लेट गया और गहरी नींद सो गया।"),
            p("tortoise rabbit~", "The tortoise kept walking. Step by step by step. Past the sleeping hare, up the hill, and over the line.", "कछुआ चलता रहा। एक-एक क़दम, एक-एक क़दम। सोते हुए खरगोश के पास से, पहाड़ी के ऊपर, और जीत की रेखा के पार।"),
            p("tortoise rabbit", "The hare woke up and ran, but it was too late. “Well done,” he said. The tortoise smiled, and everyone walked slowly home to bed.", "खरगोश जागा और दौड़ा, पर देर हो चुकी थी। वह बोला, “शाबाश!” कछुआ मुस्कुराया और सब धीरे-धीरे सोने के लिए घर चल दिए।"),
        ),
        tale(
            "ant-dove", Scene.RIVER, Mood.KINDNESS, "ant dove",
            "The Ant and the Dove", "चींटी और कबूतर",
            "One good turn deserves another.", "नेकी का बदला नेकी से मिलता है।",
            p("ant", "A little ant went to the river for a drink. A wave came, and the ant slipped into the water.", "एक नन्ही चींटी नदी पर पानी पीने गई। एक लहर आई और चींटी पानी में गिर गई।"),
            p("dove ant", "A dove in a tree saw the ant. Quickly the dove dropped a leaf into the river. The ant climbed on and floated to the bank.", "पेड़ पर बैठे एक कबूतर ने चींटी को देखा। उसने झट से एक पत्ता नदी में गिरा दिया। चींटी पत्ते पर चढ़ गई और किनारे आ लगी।"),
            p("ant dove", "“Thank you, kind dove,” said the ant. “I will not forget this.” The dove cooed softly and flew back to the nest.", "चींटी बोली, “धन्यवाद, दयालु कबूतर। मैं यह कभी नहीं भूलूँगी।” कबूतर गुटर-गूँ करके अपने घोंसले में लौट गया।"),
            p("ant", "Soon after, a hunter crept up with a net to catch the dove. The ant saw him and gave his foot a tiny nip.", "कुछ दिन बाद एक शिकारी जाल लेकर कबूतर को पकड़ने आया। चींटी ने उसे देख लिया और उसके पैर में हल्के से काट लिया।"),
            p("dove ant", "“Ouch!” said the hunter, and dropped the net. The dove flew away, safe. That evening the ant and the dove watched the sunset together.", "शिकारी चिल्लाया, “आह!” और जाल उसके हाथ से छूट गया। कबूतर उड़कर बच गया। उस शाम चींटी और कबूतर ने साथ बैठकर ढलता सूरज देखा।"),
        ),
        tale(
            "musical-donkey", Scene.NIGHT, Mood.WISDOM, "donkey jackal",
            "The Musical Donkey", "गाने वाला गधा",
            "There is a right time and place for everything.", "हर काम का एक सही समय और सही जगह होती है।",
            p("donkey jackal", "A donkey and a jackal were good friends. Each night they slipped into a field to eat sweet cucumbers.", "एक गधा और एक सियार पक्के दोस्त थे। वे हर रात एक खेत में जाकर मीठी ककड़ियाँ खाते थे।"),
            p("donkey", "One night the moon was full and bright. The donkey felt so happy. “I want to sing!” said the donkey.", "एक रात पूरा चाँद चमक रहा था। गधा बहुत ख़ुश था। वह बोला, “मेरा गाने का मन कर रहा है!”"),
            p("jackal donkey", "“Please do not,” whispered the jackal. “The farmer will wake up. Sing tomorrow, on the hill.” But the donkey did not listen.", "सियार फुसफुसाया, “अभी मत गाओ। किसान जाग जाएगा। कल पहाड़ी पर गा लेना।” पर गधे ने उसकी बात नहीं मानी।"),
            p("donkey", "“Hee-haw! Hee-haw!” sang the donkey. A lamp lit up in the farmhouse. The farmer came out and chased them both away.", "गधा गाने लगा, “ढेंचू! ढेंचू!” किसान के घर में दीया जल उठा। किसान बाहर आया और उसने दोनों को खेत से भगा दिया।"),
            p("donkey jackal", "The next evening the donkey sang on the far hill, where nobody minded at all. The jackal clapped, and the moon listened kindly.", "अगली शाम गधे ने दूर पहाड़ी पर गाया, जहाँ किसी को कोई परेशानी नहीं थी। सियार ने ताली बजाई और चाँद प्यार से सुनता रहा।"),
        ),
        tale(
            "four-friends", Scene.FOREST, Mood.FRIENDSHIP, "deer crow mouse tortoise",
            "The Four Friends", "चार दोस्त",
            "True friends stand by each other.", "सच्चे दोस्त हमेशा साथ निभाते हैं।",
            p("deer crow mouse tortoise", "A deer, a crow, a mouse and a tortoise were the best of friends. Every evening they met beside the lake to talk.", "एक हिरन, एक कौआ, एक चूहा और एक कछुआ पक्के दोस्त थे। वे हर शाम झील के किनारे मिलकर बातें करते थे।"),
            p("crow mouse tortoise", "One evening the deer did not come. The crow flew high to look, and saw the deer caught in a hunter's net.", "एक शाम हिरन नहीं आया। कौआ ऊँचा उड़कर देखने गया तो उसने हिरन को एक शिकारी के जाल में फँसा पाया।"),
            p("crow mouse deer", "The crow carried the mouse on its back. The mouse nibbled the net quickly, and the deer jumped free.", "कौआ चूहे को अपनी पीठ पर बिठाकर ले आया। चूहे ने जल्दी-जल्दी जाल कुतरा और हिरन आज़ाद हो गया।"),
            p("tortoise deer", "But the slow tortoise had come to help too, and now the hunter picked up the tortoise. So the deer lay down on the path and pretended to sleep.", "पर धीमा कछुआ भी मदद करने आ पहुँचा था और शिकारी ने उसे उठा लिया। तब हिरन रास्ते में लेट गया और सोने का नाटक करने लगा।"),
            p("deer crow mouse tortoise", "The hunter put down the tortoise and ran after the deer. The deer raced away, the tortoise slipped into the lake, and all four friends were safe together.", "शिकारी कछुए को नीचे रखकर हिरन की ओर दौड़ा। हिरन तेज़ी से भाग गया, कछुआ झील में खिसक गया और चारों दोस्त फिर से साथ और सुरक्षित थे।"),
        ),
        tale(
            "two-cats-monkey", Scene.MEADOW, Mood.FRIENDSHIP, "cat monkey cat",
            "The Two Cats and the Monkey", "दो बिल्लियाँ और बंदर",
            "When friends quarrel, both of them lose.", "आपस में झगड़ने से दोनों का नुक़सान होता है।",
            p("cat cat", "Two cats found a big, round piece of bread. “I saw it first,” said one. “No, I did,” said the other.", "दो बिल्लियों को एक बड़ी, गोल रोटी मिली। एक बोली, “मैंने पहले देखी।” दूसरी बोली, “नहीं, मैंने देखी।”"),
            p("cat monkey cat", "A monkey heard them. “Do not fight,” said the monkey. “I will share it fairly for you.” The cats agreed.", "एक बंदर ने उनकी बात सुनी। वह बोला, “झगड़ो मत। मैं तुम्हारे लिए बराबर-बराबर बाँट देता हूँ।” बिल्लियाँ मान गईं।"),
            p("monkey", "The monkey broke the bread in two. “Oh, this piece is bigger,” said the monkey, and took a bite. Then the other piece was bigger.", "बंदर ने रोटी के दो टुकड़े किए। वह बोला, “अरे, यह टुकड़ा बड़ा है,” और एक निवाला खा लिया। अब दूसरा टुकड़ा बड़ा हो गया।"),
            p("cat monkey cat", "Bite by bite, the monkey made the pieces even, until almost nothing was left. The cats looked at each other.", "एक-एक निवाला खाकर बंदर टुकड़े बराबर करता रहा, जब तक कि लगभग कुछ बचा ही नहीं। बिल्लियों ने एक-दूसरे को देखा।"),
            p("cat cat", "“We should have shared it ourselves,” they said. The next day they found a new piece of bread and broke it happily in half.", "वे बोलीं, “हमें ख़ुद ही बाँट लेनी चाहिए थी।” अगले दिन उन्हें एक और रोटी मिली और उन्होंने ख़ुशी-ख़ुशी उसे आधा-आधा बाँट लिया।"),
        ),
        tale(
            "dog-reflection", Scene.RIVER, Mood.WISDOM, "puppy",
            "The Dog and the Reflection", "कुत्ता और परछाईं",
            "Be happy with what you have.", "जो अपने पास है, उसी में ख़ुश रहो।",
            p("puppy", "A little dog found a piece of roti and was very pleased. He carried it carefully in his mouth, all the way home.", "एक छोटे कुत्ते को रोटी का एक टुकड़ा मिला और वह बहुत ख़ुश हुआ। वह उसे मुँह में दबाकर सँभालकर घर ले चला।"),
            p("puppy", "On the way he crossed a small bridge over a river. He looked down into the still water.", "रास्ते में उसे नदी पर बना एक छोटा पुल पार करना था। उसने नीचे ठहरे हुए पानी में झाँका।"),
            p("puppy", "There, in the water, was another dog with another roti! “That one looks bigger,” thought the little dog. “I want that one too.”", "वहाँ पानी में एक और कुत्ता था, और उसके मुँह में भी रोटी थी! छोटे कुत्ते ने सोचा, “वह रोटी तो बड़ी लगती है। मुझे वह भी चाहिए।”"),
            p("puppy", "He opened his mouth to bark. Splash! His own roti fell into the river and floated away.", "उसने भौंकने के लिए मुँह खोला। छपाक! उसकी अपनी रोटी नदी में गिर गई और बहती चली गई।"),
            p("puppy house", "The little dog understood. The other dog was only himself. At home there was warm milk waiting, and he was glad for every drop.", "छोटा कुत्ता समझ गया। वह दूसरा कुत्ता तो वही ख़ुद था। घर पर गरम दूध रखा था और उसने हर बूँद के लिए शुक्र मनाया।"),
        ),
        tale(
            "boy-wolf", Scene.MEADOW, Mood.WISDOM, "boy sheep sheep",
            "The Boy Who Cried Wolf", "भेड़िया आया, भेड़िया आया",
            "If you always tell the truth, people will always believe you.", "जो हमेशा सच बोलता है, उसी पर सब भरोसा करते हैं।",
            p("boy sheep sheep", "A boy looked after the village sheep on a quiet hill. Some days he felt bored, with nobody to talk to.", "एक लड़का शांत पहाड़ी पर गाँव की भेड़ें चराता था। कभी-कभी वह ऊब जाता, क्योंकि बात करने को कोई नहीं था।"),
            p("boy", "One day he shouted, “Wolf! Wolf!” The villagers ran up the hill to help. There was no wolf, and the boy laughed.", "एक दिन वह चिल्लाया, “भेड़िया आया! भेड़िया आया!” गाँव वाले मदद के लिए पहाड़ी पर दौड़े आए। भेड़िया कहीं नहीं था और लड़का हँसने लगा।"),
            p("boy sheep", "The next day he did it again. Again the villagers came running, and again there was no wolf. They walked home, shaking their heads.", "अगले दिन उसने फिर वही किया। गाँव वाले फिर दौड़े आए और फिर भेड़िया नहीं था। वे सिर हिलाते हुए घर लौट गए।"),
            p("wolf sheep boy", "Then one evening a real wolf came. “Wolf! Wolf!” cried the boy. But this time nobody came. The wolf frightened the sheep far across the hill.", "फिर एक शाम सचमुच भेड़िया आ गया। लड़का चिल्लाया, “भेड़िया आया!” पर इस बार कोई नहीं आया। भेड़िये ने भेड़ों को डराकर पहाड़ी के पार भगा दिया।"),
            p("boy sheep sheep", "The boy ran to the village and said sorry. Everyone helped bring the sheep home. From then on the boy told only the truth, and everyone believed him.", "लड़का गाँव दौड़ा और उसने माफ़ी माँगी। सबने मिलकर भेड़ें वापस घर पहुँचाईं। उस दिन से लड़का सिर्फ़ सच बोलने लगा और सब उसका भरोसा करने लगे।"),
        ),
        tale(
            "crow-snake", Scene.JUNGLE, Mood.COURAGE, "crow snake",
            "The Crow and the Snake", "कौआ और साँप",
            "A clever plan can solve a big problem.", "सूझ-बूझ से बड़ी से बड़ी मुश्किल हल हो जाती है।",
            p("crow crow", "Two crows built a nest in a tall banyan tree. But a snake lived at the foot of the tree and kept frightening them.", "दो कौओं ने बरगद के ऊँचे पेड़ पर घोंसला बनाया। पर पेड़ की जड़ में एक साँप रहता था जो उन्हें बार-बार डराता था।"),
            p("crow jackal", "The crows asked their friend the jackal for help. The jackal thought for a while. “You do not need to fight,” said the jackal. “You need a plan.”", "कौओं ने अपने दोस्त सियार से मदद माँगी। सियार ने कुछ देर सोचा। वह बोला, “लड़ने की ज़रूरत नहीं है। एक तरकीब चाहिए।”"),
            p("crow star", "Next morning the crow flew to the palace pond. A princess had left her shining necklace on the bank. The crow picked it up.", "अगली सुबह कौआ महल के तालाब पर गया। एक राजकुमारी ने अपना चमकता हार किनारे पर रखा था। कौए ने उसे उठा लिया।"),
            p("crow snake", "The palace guards followed the crow. The crow dropped the necklace right beside the snake's hole and flew up into the tree.", "महल के पहरेदार कौए के पीछे-पीछे आए। कौए ने हार ठीक साँप के बिल के पास गिरा दिया और पेड़ पर जा बैठा।"),
            p("crow crow", "The guards came for the necklace, and the snake slid quickly away to find a new home far from there. The crows lived in peace in their tree.", "पहरेदार हार लेने आए तो साँप झट से सरककर बहुत दूर नया घर ढूँढने चला गया। कौए अपने पेड़ पर चैन से रहने लगे।"),
        ),
        tale(
            "cap-seller", Scene.JUNGLE, Mood.WISDOM, "capseller monkey",
            "The Cap Seller and the Monkeys", "टोपी वाला और बंदर",
            "Stay calm and think, and you will find the answer.", "शांत रहकर सोचो तो हल मिल ही जाता है।",
            p("capseller", "A cap seller walked from village to village with a basket of bright caps. One hot afternoon he sat under a tree and fell asleep.", "एक टोपी वाला रंग-बिरंगी टोपियों की टोकरी लेकर गाँव-गाँव घूमता था। एक गरम दोपहर वह एक पेड़ के नीचे बैठा और सो गया।"),
            p("monkey monkey", "When he woke up, the basket was empty! He looked up. The tree was full of monkeys, and every monkey was wearing a cap.", "जब वह जागा तो टोकरी ख़ाली थी! उसने ऊपर देखा। पेड़ बंदरों से भरा था और हर बंदर ने टोपी पहन रखी थी।"),
            p("capseller monkey", "“Give them back!” he called, and shook his hand. The monkeys shook their hands too. He clapped. The monkeys clapped.", "वह चिल्लाया, “मेरी टोपियाँ वापस दो!” और हाथ हिलाया। बंदरों ने भी हाथ हिलाए। उसने ताली बजाई। बंदरों ने भी ताली बजाई।"),
            p("capseller", "The cap seller smiled. Now he understood. The monkeys copied everything. He took off his own cap and threw it on the ground.", "टोपी वाला मुस्कुराया। अब वह समझ गया। बंदर हर बात की नक़ल करते हैं। उसने अपनी टोपी उतारी और ज़मीन पर फेंक दी।"),
            p("capseller monkey", "Down came all the caps, one after another! He gathered them up, waved goodbye to the monkeys, and walked happily on his way.", "एक-एक करके सारी टोपियाँ नीचे आ गिरीं! उसने उन्हें समेटा, बंदरों को हाथ हिलाकर विदा कहा और ख़ुशी-ख़ुशी आगे चल दिया।"),
        ),
        tale(
            "fox-crane", Scene.RIVER, Mood.KINDNESS, "fox crane",
            "The Fox and the Crane", "लोमड़ी और सारस",
            "Treat others the way you would like to be treated.", "दूसरों से वैसा ही बर्ताव करो जैसा तुम अपने लिए चाहते हो।",
            p("fox crane", "A fox invited a crane to dinner. The crane came gladly, feeling hungry and happy.", "एक लोमड़ी ने एक सारस को खाने पर बुलाया। सारस ख़ुशी-ख़ुशी आया। उसे ज़ोर की भूख लगी थी।"),
            p("fox crane", "The fox served soup on a flat plate. The fox lapped it up quickly. But the crane's long beak could not pick up a single drop.", "लोमड़ी ने चपटी थाली में सूप परोसा। लोमड़ी ने उसे झटपट चाट लिया। पर सारस की लंबी चोंच से एक बूँद भी नहीं उठी।"),
            p("crane fox", "The crane said nothing. “Come to my home tomorrow,” said the crane politely. The next day the fox came, licking its lips.", "सारस ने कुछ नहीं कहा। वह विनम्रता से बोला, “कल मेरे घर आना।” अगले दिन लोमड़ी होंठ चाटती हुई पहुँच गई।"),
            p("crane pitcher fox", "The crane served soup in a tall jar with a narrow neck. The crane drank easily. The fox could only lick the rim.", "सारस ने लंबी, पतली गर्दन वाली सुराही में सूप परोसा। सारस आराम से पीता रहा। लोमड़ी सिर्फ़ किनारा ही चाट सकी।"),
            p("fox crane", "The fox understood and said sorry. After that they always set out a plate and a jar, and both friends ate well.", "लोमड़ी समझ गई और उसने माफ़ी माँगी। इसके बाद वे हमेशा एक थाली और एक सुराही रखते और दोनों दोस्त पेट भरकर खाते।"),
        ),
        tale(
            "fox-grapes", Scene.MEADOW, Mood.CALM, "fox grapes",
            "The Fox and the Grapes", "लोमड़ी और अंगूर",
            "It is easy to scorn what you cannot have. Try again another way.", "जो न मिले उसे बुरा कहना आसान है। कोई और तरीक़ा आज़माकर देखो।",
            p("fox", "A fox was walking through a garden on a warm evening. The fox was thirsty, and its tummy rumbled.", "एक गरम शाम एक लोमड़ी एक बगीचे से गुज़र रही थी। उसे प्यास लगी थी और पेट में चूहे दौड़ रहे थे।"),
            p("fox grapes", "High on a vine hung a bunch of ripe, purple grapes. “Those look delicious,” said the fox.", "ऊँची बेल पर पके हुए, बैंगनी अंगूरों का एक गुच्छा लटक रहा था। लोमड़ी बोली, “ये तो बड़े स्वादिष्ट लगते हैं।”"),
            p("fox grapes", "The fox jumped. Too low. The fox ran and jumped again. Still too low. Again and again the fox tried.", "लोमड़ी उछली। नहीं पहुँची। वह दौड़कर फिर उछली। फिर भी नहीं पहुँची। उसने बार-बार कोशिश की।"),
            p("fox", "At last the fox walked away. “Those grapes are surely sour,” said the fox. But the grapes were sweet, and the fox knew it.", "आख़िर लोमड़ी चल दी। वह बोली, “ये अंगूर तो ज़रूर खट्टे होंगे।” पर अंगूर मीठे थे और लोमड़ी यह जानती थी।"),
            p("fox grapes", "So the fox came back, rolled an old log under the vine, and climbed up. The grapes were the sweetest ever, and the fox slept with a full tummy.", "इसलिए लोमड़ी लौटी, एक पुराना लट्ठा बेल के नीचे लुढ़काया और उस पर चढ़ गई। अंगूर बहुत मीठे निकले और लोमड़ी भरे पेट चैन से सो गई।"),
        ),
    )
}
