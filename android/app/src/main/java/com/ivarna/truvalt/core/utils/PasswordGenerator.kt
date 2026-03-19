package com.ivarna.truvalt.core.utils

import java.security.SecureRandom

class PasswordGenerator {

    companion object {
        private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
        private const val DIGITS = "0123456789"
        private const val SYMBOLS = "!@#\$%^&*()_+-=[]{}|;:,.<>?"
        private const val AMBIGUOUS = "0O1lI"
        
        private val secureRandom = SecureRandom()
    }

    fun generate(
        length: Int = 16,
        useUppercase: Boolean = true,
        useLowercase: Boolean = true,
        useDigits: Boolean = true,
        useSymbols: Boolean = true,
        excludeAmbiguous: Boolean = false
    ): String {
        var chars = ""
        if (useUppercase) chars += UPPERCASE
        if (useLowercase) chars += LOWERCASE
        if (useDigits) chars += DIGITS
        if (useSymbols) chars += SYMBOLS

        if (excludeAmbiguous) {
            chars = chars.filter { it !in AMBIGUOUS }
        }

        if (chars.isEmpty()) {
            chars = LOWERCASE + DIGITS
        }

        return (1..length).map { chars[secureRandom.nextInt(chars.length)] }.joinToString("")
    }

    fun generatePassphrase(
        wordCount: Int = 4,
        separator: String = "-",
        capitalize: Boolean = false,
        appendNumber: Boolean = false
    ): String {
        val words = (1..wordCount).map {
            val randomWord = EFF_WORDLIST[secureRandom.nextInt(EFF_WORDLIST.size)]
            if (capitalize) randomWord.replaceFirstChar { it.uppercase() } else randomWord
        }

        var passphrase = words.joinToString(separator)

        if (appendNumber) {
            passphrase += separator + secureRandom.nextInt(100).toString()
        }

        return passphrase
    }

    private val EFF_WORDLIST = listOf(
        "acid", "acorn", "acre", "acts", "afar", "affix", "aged", "agent", "agile", "aging",
        "agony", "agree", "ahead", "aide", "aids", "aim", "ajar", "alarm", "alias", "alibi",
        "alien", "align", "alike", "alive", "aloe", "aloft", "alone", "amend", "amino", "ammo",
        "ample", "amuse", "angel", "anger", "angle", "angry", "ankle", "apart", "apex", "aphid",
        "apple", "april", "apron", "aqua", "arbor", "area", "arena", "argue", "arise", "armor",
        "army", "aroma", "array", "arrow", "arson", "artsy", "ascot", "ashen", "aside", "askew",
        "asset", "atlas", "atom", "attic", "audio", "audit", "avoid", "awake", "award", "aware",
        "awful", "bacon", "badge", "badly", "bagel", "baggy", "baked", "baker", "balmy", "banjo",
        "barge", "baron", "basal", "basic", "basin", "basis", "batch", "bathe", "baton", "battle",
        "bauble", "beach", "beard", "beast", "beauty", "became", "become", "beef", "beep", "beer",
        "beet", "begun", "being", "belly", "below", "bench", "berry", "birth", "bison", "black",
        "blade", "blame", "bland", "blank", "blast", "blaze", "bleak", "blend", "blimp", "blink",
        "bliss", "block", "blond", "blood", "bloom", "blown", "blues", "bluff", "blunt", "blurb",
        "blurt", "blush", "board", "boast", "boat", "body", "bogey", "boil", "bold", "bolt",
        "bomb", "bond", "bone", "bonus", "bony", "book", "boost", "boot", "booth", "boots",
        "booze", "bore", "born", "boss", "both", "bowed", "bowl", "box", "brace", "brain",
        "brake", "brand", "brass", "brave", "bread", "break", "bred", "breed", "briar", "brick",
        "bride", "brief", "bring", "brink", "brisk", "broad", "broil", "broke", "brook", "broom",
        "broth", "brown", "brush", "brute", "buddy", "budge", "buggy", "build", "built", "bulge",
        "bulk", "bully", "bunch", "bunny", "burnt", "burst", "busy", "buzz", "cabin", "cable",
        "camel", "cameo", "canal", "candy", "cane", "canoe", "cape", "card", "care", "cargo",
        "carol", "carry", "carve", "case", "cash", "cast", "catch", "cater", "catnap", "caught",
        "cause", "cave", "cease", "cedar", "chain", "chair", "chalk", "champ", "chant", "chaos",
        "charm", "chart", "chase", "chat", "cheap", "cheat", "check", "cheek", "cheer", "chess",
        "chest", "chew", "chick", "chide", "chief", "child", "chill", "chin", "chip", "choice",
        "choose", "chord", "chore", "chose", "chuck", "chump", "chunk", "churn", "cigar", "cinch",
        "circle", "circum", "citrus", "claim", "clamp", "clap", "clash", "clasp", "class", "claw",
        "clay", "clean", "clear", "clerk", "click", "cliff", "climb", "cling", "clip", "cloak",
        "clock", "clone", "close", "cloth", "cloud", "clump", "coach", "coast", "cobra", "cocoa",
        "code", "coil", "coin", "cold", "collar", "colon", "color", "column", "comb", "combat",
        "come", "comet", "comic", "comma", "condo", "cone", "confer", "cope", "copy", "coral",
        "cord", "core", "cork", "corn", "corner", "correct", "cost", "couch", "cough", "could",
        "count", "court", "cover", "crack", "craft", "crane", "crank", "crash", "crate", "crave",
        "crawl", "craze", "crazy", "creak", "cream", "creed", "creek", "creep", "creme", "crest",
        "crew", "crib", "crime", "crisp", "croak", "crock", "crone", "crony", "crook", "crop",
        "cross", "crouch", "crowd", "crown", "crude", "cruel", "crumb", "crush", "crust", "crypt",
        "cube", "cubic", "cumin", "cure", "curl", "curry", "curse", "curve", "curvy", "cut",
        "cycle", "dad", "daily", "dairy", "daisy", "dance", "dandy", "datum", "dawn", "day",
        "daze", "dead", "deal", "dean", "dear", "debt", "deck", "decor", "decoy", "decry",
        "defer", "deity", "delay", "delta", "dense", "depot", "depth", "derby", "deter", "detox",
        "device", "devil", "devote", "dew", "dial", "diary", "dice", "dido", "die", "diet",
        "digit", "dime", "dimly", "diner", "dingo", "dingy", "dinner", "dinosaur", "diorama",
        "dip", "diploma", "dire", "dirt", "disc", "dish", "disk", "dismal", "dispense", "display",
        "dispute", "distant", "ditch", "dive", "diverge", "dizzy", "dock", "doctor", "dodge",
        "does", "dog", "doll", "dolphin", "domain", "donkey", "donor", "donut", "door", "dose",
        "dot", "double", "doubt", "dough", "down", "downtown", "dowry", "doze", "drab", "draft",
        "drag", "drain", "drama", "drape", "draw", "drawl", "dread", "dream", "dress", "dried",
        "drift", "drill", "drink", "drive", "drizzle", "droll", "drone", "drool", "droop", "drop",
        "drove", "drown", "drug", "drum", "drunk", "dry", "dual", "duck", "duct", "dude",
        "duel", "duet", "duke", "dull", "duly", "dumb", "dump", "dune", "dung", "dunk",
        "duo", "duplex", "durable", "during", "dusk", "dust", "duty", "dwarf", "dwell", "dying",
        "each", "eager", "eagle", "ear", "early", "earn", "earth", "ease", "easel", "east",
        "easy", "eat", "eaten", "eater", "ebony", "echo", "edge", "edgy", "edit", "educate",
        "eel", "eerie", "eight", "elate", "elbow", "elder", "elect", "elite", "elope", "elude",
        "elves", "email", "embark", "ember", "emcee", "emerge", "emit", "emotion", "employ", "empty",
        "enact", "end", "endure", "energy", "engage", "engine", "enhance", "enjoy", "enlist", "enough",
        "enrich", "enroll", "ensure", "enter", "entry", "envoy", "envy", "epic", "episode", "equal",
        "equip", "erase", "erect", "err", "erupt", "escape", "essay", "essence", "estate", "eternal",
        "ethics", "etiquette", "evade", "even", "event", "ever", "every", "evict", "evil", "evoke",
        "evolve", "exact", "exam", "exceed", "excel", "except", "excess", "exchange", "excise", "excite",
        "exclude", "excuse", "execute", "exhaust", "exhibit", "exile", "exist", "exit", "exotic", "expand",
        "expect", "expert", "expire", "explain", "explode", "exploit", "explore", "export", "expose",
        "express", "extend", "extra", "exult", "eye", "eyebrow", "fabric", "face", "fact", "fade",
        "fail", "faint", "fair", "faith", "fake", "fall", "false", "fame", "fan", "fancy",
        "fantasy", "far", "fare", "farm", "fashion", "fast", "fate", "fatty", "faucet", "fault",
        "favor", "feast", "feather", "feature", "february", "federal", "fence", "fend", "ferry",
        "fetch", "fever", "few", "fiber", "fiction", "field", "fiend", "fiery", "fifth", "fifty",
        "fight", "figure", "file", "fill", "film", "filth", "final", "finance", "find", "fine",
        "finger", "finish", "fire", "firm", "first", "fish", "fist", "fit", "five", "fix",
        "flag", "flair", "flake", "flame", "flank", "flare", "flash", "flask", "flat", "flavor",
        "fleck", "fled", "flee", "fleece", "fleet", "flesh", "flick", "fling", "flip", "flirt",
        "float", "flock", "flood", "floor", "flora", "flour", "flow", "flu", "fluff", "fluid",
        "flung", "flush", "flute", "fly", "foamy", "focal", "focus", "fog", "foil", "fold",
        "folk", "fond", "food", "fool", "foot", "forbid", "force", "forge", "fork", "form",
        "formal", "format", "former", "fort", "forth", "forty", "forum", "fossil", "found", "four",
        "fox", "foyer", "frail", "frame", "frank", "fraud", "freak", "free", "fresh", "fret",
        "friar", "fried", "friend", "fright", "fringe", "frog", "from", "front", "frost", "frown",
        "froze", "fruit", "fry", "fudge", "fuel", "full", "fun", "fund", "funny", "fur",
        "fury", "fuse", "fuss", "fuzzy", "gadget", "gain", "gait", "gala", "galaxy", "gale",
        "gall", "game", "gamma", "gap", "garage", "garden", "garlic", "gas", "gasp", "gate",
        "gather", "gauge", "gaunt", "gauze", "gave", "gaze", "gear", "gecko", "geek", "gem",
        "gene", "general", "genre", "gentle", "german", "germ", "get", "ghost", "giant", "gift",
        "giggle", "gild", "gill", "gimmick", "ginger", "giraffe", "girl", "give", "glad",
        "glamour", "glance", "gland", "glare", "glass", "glaze", "gleam", "glee", "glen",
        "glide", "glint", "gloat", "globe", "gloom", "glory", "gloss", "glove", "glow", "glue",
        "goal", "goat", "god", "gold", "golf", "gone", "good", "goose", "gore", "gorge",
        "gory", "gosh", "gospel", "got", "govern", "gown", "grab", "grace", "grade", "graft",
        "grain", "gram", "grand", "grant", "grape", "graph", "grasp", "grass", "grave", "graze",
        "great", "greed", "greek", "green", "greet", "grief", "grill", "grim", "grin", "grind",
        "grip", "grit", "groan", "grocery", "groin", "groom", "groove", "gross", "group", "grove",
        "grow", "growl", "growth", "gruel", "gruff", "grump", "grunt", "guard", "guess", "guest",
        "guide", "guild", "guilt", "guitar", "gulf", "gull", "gum", "gun", "guppy", "guru",
        "gush", "gust", "gut", "guy", "gym", "gypsy", "habit", "hacker", "hail", "hair",
        "half", "hall", "halo", "halt", "ham", "hammer", "hand", "handle", "hang", "happen",
        "harbor", "hard", "hardly", "hare", "harm", "harp", "harry", "harsh", "hart", "harvest",
        "hash", "haste", "hat", "hatch", "hate", "haunt", "have", "haven", "havoc", "hawk",
        "hazard", "haze", "hazel", "head", "heal", "health", "heap", "hear", "heard", "heart",
        "heat", "heath", "heave", "heavy", "hedge", "heel", "height", "heir", "helix", "hell",
        "hello", "helm", "help", "hemp", "hen", "herb", "herd", "here", "hero", "heroin",
        "heron", "hertz", "hew", "hex", "hey", "hiatus", "hiccup", "hide", "high", "hill",
        "him", "hint", "hippo", "hire", "his", "hiss", "hit", "hitch", "hive", "hoard",
        "hobby", "hockey", "hold", "hole", "holiday", "hollow", "holly", "home", "honest", "honey",
        "honor", "hood", "hoof", "hook", "hoop", "hop", "hope", "horde", "horn", "horrible",
        "horror", "horse", "hose", "host", "hot", "hotel", "hound", "hour", "house", "hover",
        "how", "however", "howl", "hub", "huddle", "hue", "hug", "huge", "hulk", "hull",
        "hum", "human", "humid", "humor", "hump", "humus", "hunch", "hundred", "hung", "hunger",
        "hunk", "hunt", "hurrah", "hurry", "hurt", "hush", "husk", "husky", "hut", "hybrid",
        "hydra", "hydro", "hyena", "hymn", "hype", "ice", "icing", "icon", "idea", "ideal",
        "idiom", "idiot", "idle", "idol", "ignite", "ignore", "ill", "illegal", "illness",
        "image", "imbue", "imitate", "immense", "impact", "impair", "impart", "impede", "impend",
        "imperfect", "implore", "import", "impose", "impress", "impulse", "impure", "in", "inane",
        "inaugurate", "incapable", "incense", "inch", "incident", "include", "income", "incorrect", "increase", "incur",
        "index", "indicate", "indoor", "inept", "infamous", "infant", "infect", "infer", "inflame",
        "inform", "inhale", "inherit", "initial", "inject", "injure", "inmate", "inner", "innocent",
        "innovate", "input", "insect", "insert", "inside", "insight", "inspire", "install", "intact",
        "integrate", "intend", "intense", "inter", "interest", "interim", "interrupt", "intimate", "into",
        "intrigue", "invade", "invent", "invest", "invite", "involve", "iris", "iron", "irony",
        "island", "isolate", "issue", "item", "ivory", "jab", "jack", "jacket", "jade",
        "jail", "jam", "jar", "jaw", "jazz", "jealous", "jean", "jeep", "jeer", "jelly",
        "jewel", "jiffy", "job", "jockey", "join", "joke", "jolly", "jolt", "journal",
        "joy", "judge", "jug", "juice", "jump", "june", "junk", "jury", "just", "kangaroo",
        "keen", "keep", "kennel", "kept", "kernel", "kettle", "key", "kick", "kid", "kidnap",
        "kidney", "kill", "killer", "kilt", "kind", "king", "kiosk", "kiss", "kit", "kitchen",
        "kite", "kitten", "kiwi", "knee", "kneel", "knife", "knit", "knob", "knock", "knot",
        "know", "koala", "lab", "label", "labor", "lace", "lack", "ladder", "lady", "laid",
        "lake", "lamb", "lamp", "land", "lane", "lap", "large", "laser", "lasso", "last",
        "late", "laugh", "launch", "laundry", "lava", "law", "lawn", "lawsuit", "layer", "lazy",
        "lead", "leaf", "leak", "lean", "leap", "learn", "lease", "leather", "leave", "lecture",
        "led", "left", "leg", "legal", "legend", "lemon", "lemur", "lend", "length", "lens",
        "lent", "leopard", "lesson", "let", "letter", "level", "lever", "liar", "liberal",
        "liberty", "library", "license", "lick", "lie", "life", "lift", "light", "like",
        "limb", "lime", "limit", "limp", "line", "link", "lion", "lip", "list", "listen",
        "lit", "live", "liver", "lizard", "load", "loan", "lobby", "local", "lock", "lodge",
        "loft", "log", "logic", "lonely", "long", "look", "loop", "loose", "lord", "lose",
        "loss", "lost", "lot", "lotus", "loud", "lounge", "love", "low", "loyal", "luck",
        "lucky", "luggage", "lull", "lumber", "lump", "lunch", "lung", "lure", "lurk", "lust",
        "lynx", "lyric", "macabre", "machete", "machine", "mad", "made", "magic", "magnet",
        "maid", "mail", "main", "make", "male", "mall", "malt", "man", "manage", "mandate",
        "mango", "manor", "map", "maple", "march", "mare", "mark", "market", "mars", "marsh",
        "match", "mate", "math", "mauve", "max", "may", "maybe", "mayor", "maze", "meal",
        "mean", "meant", "medal", "media", "meet", "mellow", "melody", "melt", "member", "memo",
        "memoriam", "menace", "mend", "mental", "mentor", "menu", "mercy", "merge", "merit",
        "merry", "mesh", "mess", "message", "metal", "meter", "method", "metro", "microwave",
        "middle", "midnight", "might", "mild", "mile", "milk", "mill", "mime", "mind", "mine",
        "mini", "minion", "mink", "minor", "minus", "minute", "mirror", "mirth", "miss", "mist",
        "mix", "moan", "moat", "mob", "mobile", "mock", "mode", "model", "modem", "moderate",
        "modern", "modest", "modify", "module", "moist", "mold", "mole", "mom", "moment",
        "monitor", "month", "mood", "moon", "moor", "moose", "mop", "moral", "more", "morning",
        "mortal", "mosque", "moss", "most", "moth", "mother", "motion", "motive", "motor",
        "motto", "mount", "mountain", "mouse", "mouth", "move", "much", "mud", "muffin", "mug",
        "multi", "mural", "murder", "muse", "museum", "mushroom", "music", "must", "mutant",
        "mute", "mutter", "mutton", "mutual", "muzzle", "myriad", "myself", "mystery", "myth"
    )
}

class PasswordStrengthMeter {

    fun calculate(password: String): PasswordStrength {
        if (password.isEmpty()) return PasswordStrength.VERY_WEAK
        
        var score = 0
        
        if (password.length >= 8) score += 1
        if (password.length >= 12) score += 1
        if (password.length >= 16) score += 1
        
        if (password.any { it.isUpperCase() }) score += 1
        if (password.any { it.isLowerCase() }) score += 1
        if (password.any { it.isDigit() }) score += 1
        if (password.any { !it.isLetterOrDigit() }) score += 1
        
        val uniqueChars = password.toSet().size
        if (uniqueChars > password.length / 2) score += 1
        
        return when {
            score <= 2 -> PasswordStrength.VERY_WEAK
            score <= 4 -> PasswordStrength.WEAK
            score <= 6 -> PasswordStrength.MEDIUM
            score <= 8 -> PasswordStrength.STRONG
            else -> PasswordStrength.VERY_STRONG
        }
    }
}

enum class PasswordStrength(val label: String, val color: Long) {
    VERY_WEAK("Very Weak", 0xFFBA1A1A),
    WEAK("Weak", 0xFFE69517),
    MEDIUM("Medium", 0xFFE6C300),
    STRONG("Strong", 0xFF4CAF50),
    VERY_STRONG("Very Strong", 0xFF0D7377)
}
