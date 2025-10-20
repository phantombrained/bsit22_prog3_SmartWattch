import java.util.Scanner;

public class Activity2Prog3 {

  
    public static void main(String[] args) {
        // Test 1: Fibonacci with start value 5 and range 50
       System.out.println("Fibonacci Test: " + generateFibonacciSequence(5, 50));

    //     // Test 2: Factorial of 7
    //     System.out.println("Factorial Test: " + calculateFactorial(7));
        
    //     // Test 3: Grade for a score of 84
    //     System.out.println("Grade App Test: " + assignLetterGrade(84));
     }


    /**
     * 1. Fibonacci Sequence App
     */
    public static String generateFibonacciSequence(int svalue, int vrange) {
        StringBuilder sequence = new StringBuilder();
        
        int firstV = svalue;
        int nextV = svalue + 1;
        
        sequence.append(firstV).append(", ");
        
        if (nextV <= vrange) {
            sequence.append(nextV).append(", ");
        }

        int fiboSeq = firstV + nextV;
        
        while (fiboSeq <= vrange) {
            sequence.append(fiboSeq).append(", ");
            firstV = nextV;
            nextV = fiboSeq;
            fiboSeq = firstV + nextV;
        }
        
        if (sequence.length() > 2) {
            sequence.setLength(sequence.length() - 2);
        }
        
        return sequence.toString();
    }


    /**
     * 2. Factorial of a Number App
     */
    public static long calculateFactorial(int num) {
        if (num < 0) return -1;
        
        long fact = 1;
        for (int i = 1; i <= num; i++) {
            fact *= i;      
        }
        return fact;
    }


    /**
     * 3. Letter Grade Assignment App
     */
    public static char assignLetterGrade(int score) {
        char grade;

        if (score >= 90) grade = 'A';
        else if (score >= 80) grade = 'B';
        else if (score >= 70) grade = 'C';
        else if (score >= 60) grade = 'D';
        else grade = 'F';

        return grade;
    }
}

