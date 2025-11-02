import java.util.Scanner;

public class Activity3StringApps {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        // Test 1: Remove Duplicate Characters
        System.out.println("Enter a string to remove duplicates:");
        String input1 = in.nextLine();
        System.out.println("Result: " + removeDuplicates(input1));
        System.out.println();

        // Test 2: Find the Most Frequent Word
        System.out.println("--- Most Frequent Word Finder ---");
        System.out.print("Please enter a paragraph: ");
        String paragraph = in.nextLine();
        System.out.println(findMostFrequentWord(paragraph));
        System.out.println();

        // Test 3: Convert String to Title Case
        System.out.println("Enter a string to convert to Title Case:");
        String input3 = in.nextLine();
        System.out.println("Result: " + toTitleCase(input3));

        in.close();
    }

    /**
     * 1. Remove Duplicate Characters from a String
     * Input: "programming"
     * Output: "progamin"
     */
    public static String removeDuplicates(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (result.indexOf(ch) == -1) { 
                result += ch;
            }
        }
        return result;
    }

    /**
     * 2. Find the Most Frequent Word in a Paragraph
     * Input: "the dog and the cat chased the bird"
     * Output: "the" appeared 3 times.
     */
    public static String findMostFrequentWord(String paragraph) {
        String punctuation = ".?!,()[]\"'-";
        String cleanedText = paragraph.toLowerCase();

        
        for (char ch : punctuation.toCharArray()) {
            cleanedText = cleanedText.replace(String.valueOf(ch), "");
        }

        String[] words = cleanedText.split("\\s+");
        java.util.HashMap<String, Integer> wordCounts = new java.util.HashMap<>();

        for (String word : words) {
            if (!word.isEmpty()) {
                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
            }
        }

        if (wordCounts.isEmpty()) {
            return "The input was empty.";
        }

        String mostFrequentWord = null;
        int maxCount = 0;

        for (String word : wordCounts.keySet()) {
            int count = wordCounts.get(word);
            if (count > maxCount) {
                maxCount = count;
                mostFrequentWord = word;
            }
        }

        return "\"" + mostFrequentWord + "\" appeared " + maxCount + " times.";
    }

    /**
     * 3. Convert String to Title Case
     * Input: "hello world"
     * Output: "Hello World"
     */
    public static String toTitleCase(String input) {
        String[] words = input.split(" ");
        String result = "";

        for (String w : words) {
            if (!w.isEmpty()) {
                result += w.substring(0, 1).toUpperCase() + w.substring(1).toLowerCase() + " ";
            }
        }
        return result.trim();
    }
}
