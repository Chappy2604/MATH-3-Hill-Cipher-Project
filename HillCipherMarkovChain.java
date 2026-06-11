import java.util.*;

public class HillCipherMarkovChain {
    
    public static int charToNum(char c) {
        char upper = Character.toUpperCase(c);
        if (upper >= 'A' && upper <= 'Z') return upper - 'A';
        return -1;
    }
    
    public static char numToChar(int n) {
        int normalized = ((n % 26) + 26) % 26;
        return (char)(normalized + 'A');
    }
    
    public static String sanitize(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) sb.append(Character.toUpperCase(c));
        }
        return sb.toString();
    }
    
    public static int[][] generateKeyMatrix(int size) {
        Random rand = new Random();
        int[][] key = new int[size][size];
        
        if (size == 2) {
            // 2x2 key from your PDF
            key[0][0] = 3; key[0][1] = 3;
            key[1][0] = 2; key[1][1] = 5;
        } 
        else if (size == 3) {
            // 3x3 invertible key modulo 26
            key[0][0] = 2; key[0][1] = 4; key[0][2] = 5;
            key[1][0] = 1; key[1][1] = 3; key[1][2] = 6;
            key[2][0] = 4; key[2][1] = 5; key[2][2] = 7;
        }
        else if (size == 4) {
            // 4x4 invertible key modulo 26
            key[0][0] = 3; key[0][1] = 5; key[0][2] = 7; key[0][3] = 9;
            key[1][0] = 1; key[1][1] = 2; key[1][2] = 3; key[1][3] = 4;
            key[2][0] = 5; key[2][1] = 6; key[2][2] = 1; key[2][3] = 2;
            key[3][0] = 8; key[3][1] = 9; key[3][2] = 4; key[3][3] = 5;
        }
        else {
            // 5x5 invertible key modulo 26
            key[0][0] = 2; key[0][1] = 3; key[0][2] = 5; key[0][3] = 7; key[0][4] = 9;
            key[1][0] = 1; key[1][1] = 4; key[1][2] = 6; key[1][3] = 8; key[1][4] = 2;
            key[2][0] = 3; key[2][1] = 5; key[2][2] = 7; key[2][3] = 1; key[2][4] = 4;
            key[3][0] = 6; key[3][1] = 8; key[3][2] = 2; key[3][3] = 4; key[3][4] = 6;
            key[4][0] = 1; key[4][1] = 9; key[4][2] = 3; key[4][3] = 5; key[4][4] = 7;
        }
        
        return key;
    }
    
    public static String encrypt(String plaintext, int[][] key, int size) {
        String processed = sanitize(plaintext);
        while (processed.length() % size != 0) processed += "X";
        
        StringBuilder ciphertext = new StringBuilder();
        for (int i = 0; i < processed.length(); i += size) {
            for (int j = 0; j < size; j++) {
                int sum = 0;
                for (int k = 0; k < size; k++) {
                    sum += key[j][k] * charToNum(processed.charAt(i + k));
                }
                ciphertext.append(numToChar(sum % 26));
            }
        }
        return ciphertext.toString();
    }
    
    public static double[][] buildTransitionMatrix(String text) {
        String cleanText = sanitize(text);
        int[][] transitions = new int[26][26];
        int[] total = new int[26];
        
        for (int i = 0; i < cleanText.length() - 1; i++) {
            int from = charToNum(cleanText.charAt(i));
            int to = charToNum(cleanText.charAt(i + 1));
            if (from >= 0 && to >= 0) {
                transitions[from][to]++;
                total[from]++;
            }
        }
        
        double[][] prob = new double[26][26];
        for (int i = 0; i < 26; i++) {
            if (total[i] > 0) {
                for (int j = 0; j < 26; j++) {
                    prob[i][j] = (double) transitions[i][j] / total[i];
                }
            }
        }
        return prob;
    }
    
    public static double calculateEntropy(double[][] matrix) {
        double totalEntropy = 0.0;
        int rowCount = 0;
        
        for (int i = 0; i < 26; i++) {
            double rowEntropy = 0.0;
            boolean hasTransitions = false;
            for (int j = 0; j < 26; j++) {
                if (matrix[i][j] > 0) {
                    hasTransitions = true;
                    rowEntropy += -matrix[i][j] * (Math.log(matrix[i][j]) / Math.log(2));
                }
            }
            if (hasTransitions) {
                totalEntropy += rowEntropy;
                rowCount++;
            }
        }
        return rowCount > 0 ? totalEntropy / rowCount : 0;
    }
    
    public static void main(String[] args) {
        String plaintext = "CRYPTOGRAPHY IS THE PRACTICE OF SECURE COMMUNICATION IN THE PRESENCE OF THIRD PARTIES CALLED ADVERSARIES THE HILL CIPHER WAS DEVELOPED BY LESTER HILL IN NINETEEN TWENTY NINE AND USES MATRIX MULTIPLICATION FOR ENCRYPTION AND DECRYPTION THIS MAKES IT AN EXCELLENT EXAMPLE OF LINEAR ALGEBRA IN CRYPTOGRAPHY";
        
        String cleanPlain = sanitize(plaintext);
        double plainEntropy = calculateEntropy(buildTransitionMatrix(cleanPlain));
        
        System.out.println("\nMARKOV CHAIN ANALYSIS RESULTS - MULTIPLE KEY SIZES");
        System.out.println("--------------------------------------------------");
        System.out.println("Plaintext length: " + cleanPlain.length() + " characters");
        System.out.printf("Plaintext entropy: %.4f bits/transition\n\n", plainEntropy);
        
        System.out.println("Key Size | Ciphertext Entropy | Entropy Increase");
        System.out.println("--------------------------------------------------");
        
        int[] sizes = {2, 3, 4, 5};
        
        for (int size : sizes) {
            int[][] key = generateKeyMatrix(size);
            String ciphertext = encrypt(plaintext, key, size);
            double[][] cipherMatrix = buildTransitionMatrix(ciphertext);
            double cipherEntropy = calculateEntropy(cipherMatrix);
            double increase = ((cipherEntropy - plainEntropy) / plainEntropy) * 100;
            
            System.out.printf("   %dx%d   |      %.4f        |      %.2f%%\n", 
                            size, size, cipherEntropy, increase);
        }
        
    }
}