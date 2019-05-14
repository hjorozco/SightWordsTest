package com.weebly.hectorjorozco.sightwordstest.utils;

import com.weebly.hectorjorozco.sightwordstest.models.Word;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class methods are located in:
 * https://commons.apache.org/proper/commons-text/jacoco/org.apache.commons.text/WordUtils.java.html
 */
public final class WordUtils {

    private static final String[] DOLCH_PRE_K = {"a", "and", "away", "big", "blue", "can", "come",
            "down", "find", "for", "funny", "go", "help", "here", "I", "in", "is", "it", "jump",
            "little", "look", "make", "me", "my", "not", "one", "play", "red", "run", "said", "see",
            "the", "three", "to", "two", "up", "we", "where", "yellow", "you"};

    private static final String[] DOLCH_K = {"all", "am", "are", "at", "ate", "be", "black", "brown",
            "but", "came", "did", "do", "eat", "four", "get", "good", "have", "he", "into", "like",
            "must", "new", "no", "now", "on", "our", "out", "please", "pretty", "ran", "ride", "saw",
            "say", "she", "so", "soon", "that", "there", "they", "this", "too", "under", "want", "was",
            "well", "went", "what", "white", "who", "will", "with", "yes"};

    private static final String[] DOLCH_1ST = {"after", "again", "an", "any", "as", "ask", "by",
            "could", "every", "fly", "from", "give", "going", "had", "has", "her", "him", "his",
            "how", "just", "know", "let", "live", "may", "of", "old", "once", "open", "over", "put",
            "round", "some", "stop", "take", "thank", "them", "then", "think", "walk", "were", "when"};

    private static final String[] DOLCH_2ND = {"always", "around", "because", "been", "before",
            "best", "both", "buy", "call", "cold", "does", "don't", "fast", "first", "five", "found",
            "gave", "goes", "green", "its", "made", "many", "off", "or", "pull", "read", "right",
            "sing", "sit", "sleep", "tell", "their", "these", "those", "upon", "us", "use", "very",
            "wash", "which", "why", "wish", "work", "would", "write", "your"};

    private static final String[] DOLCH_3RD = {"about", "better", "bring", "carry", "clean", "cut",
            "done", "draw", "drink", "eight", "fall", "far", "full", "got", "grow", "hold", "hot",
            "hurt", "if", "keep", "kind", "laugh", "light", "long", "much", "myself", "never", "only",
            "own", "pick", "seven", "shall", "show", "six", "small", "start", "ten", "today",
            "together", "try", "warm"};

    private static final String[] DOLCH_NOUNS = {"apple", "baby", "back", "ball", "bear", "bed",
            "bell", "bird", "birthday", "boat", "box", "boy", "bread", "brother", "cake", "car",
            "cat", "chair", "chicken", "children", "Christmas", "coat", "corn", "cow", "day", "dog",
            "doll", "door", "duck", "egg", "eye", "farm", "farmer", "father", "feet", "fire", "fish",
            "floor", "flower", "game", "garden", "girl", "goodbye", "grass", "ground", "hand",
            "head", "hill", "home", "horse", "house", "kitty", "leg", "letter", "man", "men", "milk",
            "money", "morning", "mother", "name", "nest", "night", "paper", "party", "picture",
            "pig", "rabbit", "rain", "ring", "robin", "Santa Claus", "school", "seed", "sheep",
            "shoe", "sister", "snow", "song", "squirrel", "stick", "street", "sun", "table", "thing",
            "time", "top", "toy", "tree", "watch", "water", "way", "wind", "window", "wood"};

    private static final String[] FRY_1ST_100 = {"a", "about", "all", "an", "and", "are", "as",
            "at", "be", "been", "but", "by", "called", "can", "come", "could", "day", "did", "do",
            "down", "each", "find", "first", "for", "from", "get", "go", "had", "has", "have", "he",
            "her", "him", "his", "how", "I", "if", "in", "into", "is", "it", "like", "long", "look",
            "made", "make", "many", "may", "more", "my", "no", "not", "now", "number", "of", "oil",
            "on", "one", "or", "other", "out", "part", "people", "said", "see", "she", "sit", "so",
            "some", "than", "that", "the", "their", "them", "then", "there", "these", "they", "this",
            "time", "to", "two", "up", "use", "was", "water", "way", "we", "were", "what", "when",
            "which", "who", "will", "with", "words", "would", "write", "you", "your"};

    private static final String[] FRY_2ND_100 = {"after", "again", "air", "also", "America",
            "animal", "another", "answer", "any", "around", "ask", "away", "back", "because",
            "before", "big", "boy", "came", "change", "different", "does", "end", "even", "follow",
            "form", "found", "give", "good", "great", "hand", "help", "here", "home", "house",
            "just", "kind", "know", "land", "large", "learn", "letter", "line", "little", "live",
            "man", "me", "means", "men", "most", "mother", "move", "much", "must", "name", "need",
            "new", "off", "old", "only", "our", "over", "page", "picture", "place", "play", "point",
            "put", "read", "right", "same", "say", "sentence", "set", "should", "show", "small",
            "sound", "spell", "still", "study", "such", "take", "tell", "things", "think", "three",
            "through", "too", "try", "turn", "us", "very", "want", "well", "went", "where", "why",
            "work", "world", "years"};

    private static final String[] FRY_3RD_100 = {"above", "add", "almost", "along", "always",
            "began", "begin", "being", "below", "between", "book", "both", "car", "carry",
            "children", "city", "close", "country", "cut", "don't", "earth", "eat", "enough",
            "every", "example", "eyes", "face", "family", "far", "father", "feet", "few", "food",
            "four", "girl", "got", "group", "grow", "hard", "head", "hear", "high", "idea",
            "important", "Indian", "it's", "keep", "last", "late", "leave", "left", "let", "life",
            "light", "list", "might", "mile", "miss", "mountains", "near", "never", "next", "night",
            "often", "once", "open", "own", "paper", "plant", "real", "river", "run", "saw",
            "school", "sea", "second", "seem", "side", "something", "sometimes", "song", "soon",
            "start", "state", "stop", "story", "talk", "those", "thought", "together", "took",
            "tree", "under", "until", "walk", "watch", "while", "white", "without", "young"};

    private static final String[] FRY_4TH_100 = {"across", "against", "area", "become", "best",
            "better", "birds", "black", "body", "certain", "cold", "color", "complete", "covered",
            "cried", "didn't", "dog", "door", "draw", "during", "early", "easy", "ever", "fall",
            "farm", "fast", "field", "figure", "fire", "fish", "five", "friends", "ground",
            "happened", "heard", "himself", "hold", "horse", "hours", "however", "hundred", "I'll",
            "king", "knew", "listen", "low", "map", "mark", "measure", "money", "morning", "music",
            "north", "notice", "numeral", "order", "passed", "pattern", "piece", "plan", "problem",
            "products", "pulled", "questions", "reached", "red", "remember", "rock", "room", "seen",
            "several", "ship", "short", "since", "sing", "slowly", "south", "space", "stand",
            "step", "sun", "sure", "table", "today", "told", "top", "toward", "town", "travel",
            "true", "unit", "upon", "usually", "voice", "vowel", "war", "waves", "whole", "wind",
            "wood"};

    private static final String[] FRY_5TH_100 = {"able", "ago", "am", "among", "ball", "base",
            "became", "behind", "boat", "box", "bring", "brought", "building", "built", "cannot",
            "carefully", "check", "circle", "class", "clear", "common", "contain", "correct",
            "course", "dark", "decided", "deep", "done", "dry", "English", "equation", "explain",
            "fact", "feel", "filled", "finally", "fine", "fly", "force", "front", "full", "game",
            "gave", "government", "green", "half", "heat", "heavy", "hot", "inches", "include",
            "inside", "island", "known", "language", "less", "machine", "material", "minutes",
            "note", "nothing", "noun", "object", "ocean", "oh", "pair", "person", "plane", "power",
            "produce", "quickly", "ran", "rest", "road", "round", "rule", "scientists", "shape",
            "shown", "six", "size", "special", "stars", "stay", "stood", "street", "strong",
            "surface", "system", "ten", "though", "thousands", "understand", "verb", "wait", "warm",
            "week", "wheels", "yes", "yet"};

    private static final String[] FRY_6TH_100 = {"anything", "arms", "beautiful", "believe", "beside",
            "bill", "blue", "brother", "can't", "cause", "cells", "center", "clothes", "dance",
            "describe", "developed", "difference", "direction", "discovered", "distance", "divided",
            "drive", "drop", "edge", "eggs", "energy", "Europe", "exercise", "farmers", "felt",
            "finished", "flowers", "forest", "general", "gone", "grass", "happy", "heart", "held",
            "instruments", "interest", "job", "kept", "lay", "legs", "length", "love", "main",
            "matter", "meet", "members", "million", "mind", "months", "moon", "paint", "paragraph",
            "past", "perhaps", "picked", "present", "probably", "race", "rain", "raised", "ready",
            "reason", "record", "region", "represent", "return", "root", "sat", "shall", "sign",
            "simple", "site", "sky", "soft", "square", "store", "subject", "suddenly", "sum",
            "summer", "syllables", "teacher", "test", "third", "train", "wall", "weather", "west",
            "whether", "wide", "wild", "window", "winter", "wish", "written"};

    private static final String[] FRY_7TH_100 = {"act", "Africa", "age", "already", "although",
            "amount", "angle", "appear", "baby", "bear", "beat", "bed", "bottom", "bright", "broken",
            "build", "buy", "care", "case", "cat", "century", "consonant", "copy", "couldn't",
            "count", "cross", "dictionary", "died", "dress", "either", "everyone", "everything",
            "exactly", "factors", "fight", "fingers", "floor", "fraction", "free", "French", "gold",
            "hair", "hill", "hole", "hope", "ice", "instead", "iron", "jumped", "killed", "lake",
            "laughed", "lead", "let's", "lot", "melody", "metal", "method", "middle", "milk",
            "moment", "nation", "natural", "outside", "per", "phrase", "poor", "possible", "pounds",
            "pushed", "quiet", "quite", "remain", "result", "ride", "rolled", "sail", "scale",
            "section", "sleep", "smiled", "snow", "soil", "solve", "someone", "son", "speak",
            "speed", "spring", "stone", "surprise", "tall", "temperature", "themselves", "tiny",
            "trip", "type", "village", "within", "wonder"};

    private static final String[] FRY_8TH_100 = {"alone", "art", "bad", "bank", "bit", "break",
            "brown", "burning", "business", "captain", "catch", "caught", "cents", "child", "choose",
            "clean", "climbed", "cloud", "coast", "continued", "control", "cool", "cost", "decimal",
            "desert", "design", "direct", "drawing", "ears", "east", "else", "engine", "England",
            "equal", "experiment", "express", "feeling", "fell", "flow", "foot", "garden", "gas",
            "glass", "God", "grew", "history", "human", "hunting", "increase", "information",
            "itself", "joined", "key", "lady", "law", "least", "lost", "maybe", "mouth", "party",
            "pay", "period", "plains", "please", "practice", "president", "received", "report", "ring",
            "rise", "row", "save", "seeds", "sent", "separate", "serve", "shouted", "single", "skin",
            "statement", "stick", "straight", "strange", "students", "suppose", "symbols", "team",
            "touch", "trouble", "uncle", "valley", "visit", "wear", "whose", "wire", "woman", "wrote",
            "yard", "you're", "yourself"};

    private static final String[] FRY_9TH_100 = {"addition", "army", "bell", "belong", "block",
            "blood", "blow", "board", "bones", "branches", "cattle", "chief", "compare", "compound",
            "consider", "cook", "corner", "crops", "crowd", "current", "doctor", "dollars", "eight",
            "electric", "elements", "enjoy", "entered", "except", "exciting", "expect", "famous",
            "fit", "flat", "fruit", "fun", "guess", "hat", "hit", "indicate", "industry", "insects",
            "interesting", "Japanese", "lie", "lifted", "loud", "major", "mall", "meat", "mine",
            "modern", "movement", "necessary", "observe", "park", "particular", "planets", "poem",
            "pole", "position", "process", "property", "provide", "rather", "rhythm", "rich", "safe",
            "sand", "science", "sell", "send", "sense", "seven", "sharp", "shoulder", "sight", "silent",
            "soldiers", "spot", "spread", "stream", "string", "suggested", "supply", "swim", "terms",
            "thick", "thin", "thus", "tied", "tone", "trade", "tube", "value", "wash", "wasn't",
            "weight", "wife", "wings", "won't"};

    private static final String[] FRY_10TH_100 = {"action", "actually", "adjective", "afraid",
            "agreed", "ahead", "allow", "apple", "arrived", "born", "bought", "British", "capital",
            "chance", "chart", "church", "column", "company", "conditions", "corn", "cotton", "cows",
            "create", "dead", "deal", "death", "details", "determine", "difficult", "division",
            "doesn't", "effect", "entire", "especially", "evening", "experience", "factories",
            "fair", "fear", "fig", "forward", "France", "fresh", "Greek", "gun", "hoe", "huge",
            "isn't", "led", "level", "located", "march", "match", "molecules", "northern", "nose",
            "office", "opposite", "oxygen", "plural", "prepared", "pretty", "printed", "radio",
            "repeated", "rope", "rose", "score", "seat", "settled", "shoes", "shop", "similar",
            "sir", "sister", "smell", "solution", "southern", "steel", "stretched", "substances",
            "suffix", "sugar", "tools", "total", "track", "triangle", "truck", "underline",
            "various", "view", "Washington", "we'll", "western", "win", "women", "workers",
            "wouldn't", "wrong", "yellow"};

    private static final int DOLCH_PRE_K_NUMBER_OF_WORDS = 40;
    private static final int DOLCH_K_NUMBER_OF_WORDS = 52;
    private static final int DOLCH_1ST_NUMBER_OF_WORDS = 41;
    private static final int DOLCH_2ND_NUMBER_OF_WORDS = 46;
    private static final int DOLCH_3RD_NUMBER_OF_WORDS = 41;
    private static final int DOLCH_NOUNS_NUMBER_OF_WORDS = 95;
    private static final int FRY_NUMBER_OF_WORDS = 100;

    private static final boolean WORD_INITIAL_PRESSED_STATE = false;

    /**
     * Converts all the whitespace separated words in a String into capitalized words,
     * that is each word is made up of a titlecase character and then a series of
     * lowercase characters.
     *
     * @param str the String to capitalize, may be null
     * @return capitalized String, null if null String input
     */
    public static String capitalizeFully(final String str) {
        return capitalizeFully(str, (char[]) null);
    }

    /**
     * Converts all the delimiter separated words in a String into capitalized words,
     * that is each word is made up of a title case character and then a series of
     * lowercase characters.
     *
     * @param str        the String to capitalize, may be null
     * @param delimiters set of characters to determine capitalization, null means whitespace
     * @return capitalized String, null  if null String input
     */
    private static String capitalizeFully(String str, final char... delimiters) {
        if (str.isEmpty()) {
            return str;
        }
        str = str.toLowerCase();
        return capitalize(str, delimiters);
    }


    /**
     * Capitalizes all the delimiter separated words in a String.
     * Only the first character of each word is changed.
     *
     * @param str        the String to capitalize, may be null
     * @param delimiters set of characters to determine capitalization, null means whitespace
     * @return capitalized String, null if null String input
     */
    private static String capitalize(final String str, final char... delimiters) {
        if (str.isEmpty()) {
            return str;
        }
        final Set<Integer> delimiterSet = generateDelimiterSet(delimiters);
        final int strLen = str.length();
        final int[] newCodePoints = new int[strLen];
        int outOffset = 0;

        boolean capitalizeNext = true;
        for (int index = 0; index < strLen; ) {
            final int codePoint = str.codePointAt(index);

            if (delimiterSet.contains(codePoint)) {
                capitalizeNext = true;
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            } else if (capitalizeNext) {
                final int titleCaseCodePoint = Character.toTitleCase(codePoint);
                newCodePoints[outOffset++] = titleCaseCodePoint;
                index += Character.charCount(titleCaseCodePoint);
                capitalizeNext = false;
            } else {
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            }
        }
        return new String(newCodePoints, 0, outOffset);
    }


    /**
     * Converts an array of delimiters to a hash set of code points. Code point of space(32) is added as the default
     * value if delimiters is null. The generated hash set provides O(1) lookup time.
     *
     * @param delimiters set of characters to determine capitalization, null means whitespace
     * @return Set<Integer>
     */
    private static Set<Integer> generateDelimiterSet(final char[] delimiters) {
        final Set<Integer> delimiterHashSet = new HashSet<>();
        if (delimiters == null || delimiters.length == 0) {
            if (delimiters == null) {
                delimiterHashSet.add(Character.codePointAt(new char[]{' '}, 0));
            }

            return delimiterHashSet;
        }

        for (int index = 0; index < delimiters.length; index++) {
            delimiterHashSet.add(Character.codePointAt(delimiters, index));
        }
        return delimiterHashSet;
    }


    // Returns the number of words in the list of words that will be used to test the student
    // depending on the test type.
    public static int getNumberOfWordsOnList(int testType) {
        int numberOfWordsOnList;
        switch (testType) {
            case 0:
                numberOfWordsOnList = DOLCH_PRE_K_NUMBER_OF_WORDS;
                break;
            case 1:
                numberOfWordsOnList = DOLCH_K_NUMBER_OF_WORDS;
                break;
            case 2:
                numberOfWordsOnList = DOLCH_1ST_NUMBER_OF_WORDS;
                break;
            case 3:
                numberOfWordsOnList = DOLCH_2ND_NUMBER_OF_WORDS;
                break;
            case 4:
                numberOfWordsOnList = DOLCH_3RD_NUMBER_OF_WORDS;
                break;
            case 5:
                numberOfWordsOnList = DOLCH_NOUNS_NUMBER_OF_WORDS;
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                numberOfWordsOnList = FRY_NUMBER_OF_WORDS;
                break;
            default:
                numberOfWordsOnList = 0;
                break;

        }
        return numberOfWordsOnList;
    }

    // Returns the list of words that will be used to test the student depending on the test type.
    public static List<Word> getListOfWords(int testType) {
        if (testType == -1) {
            return null;
        } else {
            List<Word> wordsList = new ArrayList<>();

            String[] wordsArray;
            switch (testType) {
                case 0:
                    wordsArray = DOLCH_PRE_K;
                    break;
                case 1:
                    wordsArray = DOLCH_K;
                    break;
                case 2:
                    wordsArray = DOLCH_1ST;
                    break;
                case 3:
                    wordsArray = DOLCH_2ND;
                    break;
                case 4:
                    wordsArray = DOLCH_3RD;
                    break;
                case 5:
                    wordsArray = DOLCH_NOUNS;
                    break;
                case 6:
                    wordsArray = FRY_1ST_100;
                    break;
                case 7:
                    wordsArray = FRY_2ND_100;
                    break;
                case 8:
                    wordsArray = FRY_3RD_100;
                    break;
                case 9:
                    wordsArray = FRY_4TH_100;
                    break;
                case 10:
                    wordsArray = FRY_5TH_100;
                    break;
                case 11:
                    wordsArray = FRY_6TH_100;
                    break;
                case 12:
                    wordsArray = FRY_7TH_100;
                    break;
                case 13:
                    wordsArray = FRY_8TH_100;
                    break;
                case 14:
                    wordsArray = FRY_9TH_100;
                    break;
                case 15:
                    wordsArray = FRY_10TH_100;
                    break;
                default:
                    wordsArray = null;
            }

            if (wordsArray != null) {
                for (String word : wordsArray) {
                    wordsList.add(new Word(word, WORD_INITIAL_PRESSED_STATE));
                }
            } else {
                wordsList = null;
            }
            return wordsList;
        }
    }

}
