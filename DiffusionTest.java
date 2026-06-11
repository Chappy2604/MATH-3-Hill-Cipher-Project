// DiffusionTest.java
public class DiffusionTest {

    static final int MOD = 26;

    static int[][] multiplyMatrix(int[][] key, int[][] block) {
        int n = key.length;
        int[][] result = new int[n][1];
        for (int i = 0; i < n; i++)
            for (int k = 0; k < n; k++)
                result[i][0] = (result[i][0] + key[i][k] * block[k][0]) % MOD;
        return result;
    }

    static String encrypt(String plaintext, int[][] key) {
        int n = key.length;
        plaintext = plaintext.toUpperCase().replaceAll("[^A-Z]", "");
        while (plaintext.length() % n != 0) plaintext += "X";
        StringBuilder cipher = new StringBuilder();
        for (int i = 0; i < plaintext.length(); i += n) {
            int[][] block = new int[n][1];
            for (int j = 0; j < n; j++) block[j][0] = plaintext.charAt(i + j) - 'A';
            int[][] enc = multiplyMatrix(key, block);
            for (int j = 0; j < n; j++) cipher.append((char)(enc[j][0] + 'A'));
        }
        return cipher.toString();
    }

    static String diffusionMeter(double pct) {
        if (pct < 30)        return "Weak";
        else if (pct < 50)   return "Moderate";
        else if (pct < 70)   return "Good";
        else                 return "Strong";
    }

    static void runDiffusionTest(int testNo, String plaintext, int[][] key, int changeIndex) {
        String original = encrypt(plaintext, key);

        // Change one character at changeIndex
        char[] modified = plaintext.toCharArray();
        modified[changeIndex] = (modified[changeIndex] == 'A') ? 'B' : 'A';
        String modifiedCipher = encrypt(new String(modified), key);

        int changed = 0;
        int len = Math.min(original.length(), modifiedCipher.length());
        for (int i = 0; i < len; i++)
            if (original.charAt(i) != modifiedCipher.charAt(i)) changed++;

        double pct = (double) changed / original.length() * 100;
        String meter = diffusionMeter(pct);

        System.out.printf("Test %-2d | PT: %-15s | Key: %dx%d | Original CT: %-15s | Modified CT: %-15s | Changed: %d | Diffusion: %5.2f%% | %s%n",
                testNo,
                plaintext,
                key.length, key.length,
                original,
                modifiedCipher,
                changed,
                pct,
                meter);
    }

    public static void main(String[] args) {
        System.out.println("=== Diffusion Effect (Avalanche) Test ===");
        System.out.println("Change index = position of the 1 altered character in plaintext\n");

        runDiffusionTest(1, "HELLOWORLD", new int[][]{{3,3},{2,5}},                                                    0);
        runDiffusionTest(2, "ACTGFDXYZ",  new int[][]{{6,24,1},{13,16,10},{20,17,15}},                                 0);
        runDiffusionTest(3, "MATHANDCIPHER", new int[][]{{1,2,3,4},{5,6,7,8},{9,10,11,12},{2,3,4,6}},                  0);
        runDiffusionTest(4, "CRYPTOGRAPHY", new int[][]{{1,2,3,4,5},{6,7,8,9,10},{11,12,13,14,15},{16,17,18,19,21},{22,23,24,25,2}}, 0);
    }
}