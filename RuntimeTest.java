// RuntimeTest.java
public class RuntimeTest {

    static final int MOD = 26;

    static int[][] multiplyMatrix(int[][] key, int[][] block) {
        int n = key.length;
        int[][] result = new int[n][1];
        for (int i = 0; i < n; i++)
            for (int k = 0; k < n; k++)
                result[i][0] = (result[i][0] + key[i][k] * block[k][0]) % MOD;
        return result;
    }

    static int modInverse(int a) {
        a = ((a % MOD) + MOD) % MOD;
        for (int x = 1; x < MOD; x++)
            if ((a * x) % MOD == 1) return x;
        return -1;
    }

    static int determinant(int[][] m, int n) {
        if (n == 1) return m[0][0];
        if (n == 2) return m[0][0] * m[1][1] - m[0][1] * m[1][0];
        int det = 0;
        int[][] sub = new int[n-1][n-1];
        for (int col = 0; col < n; col++) {
            int si = 0;
            for (int i = 1; i < n; i++) {
                int sj = 0;
                for (int j = 0; j < n; j++) {
                    if (j == col) continue;
                    sub[si][sj++] = m[i][j];
                }
                si++;
            }
            det += (int) Math.pow(-1, col) * m[0][col] * determinant(sub, n - 1);
        }
        return det;
    }

    static int[][] adjugate(int[][] m, int n) {
        int[][] adj = new int[n][n];
        if (n == 1) { adj[0][0] = 1; return adj; }
        int[][] sub = new int[n-1][n-1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int si = 0;
                for (int r = 0; r < n; r++) {
                    if (r == i) continue;
                    int sj = 0;
                    for (int c = 0; c < n; c++) {
                        if (c == j) continue;
                        sub[si][sj++] = m[r][c];
                    }
                    si++;
                }
                int cofactor = (int) Math.pow(-1, i + j) * determinant(sub, n - 1);
                adj[j][i] = ((cofactor % MOD) + MOD) % MOD;
            }
        }
        return adj;
    }

    static int[][] inverseMatrix(int[][] key, int n) {
        int det = ((determinant(key, n) % MOD) + MOD) % MOD;
        int detInv = modInverse(det);
        if (detInv == -1) return null;
        int[][] adj = adjugate(key, n);
        int[][] inv = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                inv[i][j] = (adj[i][j] * detInv) % MOD;
        return inv;
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

    static String decrypt(String ciphertext, int[][] key) {
        int n = key.length;
        int[][] keyInv = inverseMatrix(key, n);
        if (keyInv == null) return "";
        StringBuilder plain = new StringBuilder();
        for (int i = 0; i < ciphertext.length(); i += n) {
            int[][] block = new int[n][1];
            for (int j = 0; j < n; j++) block[j][0] = ciphertext.charAt(i + j) - 'A';
            int[][] dec = multiplyMatrix(keyInv, block);
            for (int j = 0; j < n; j++) plain.append((char)(dec[j][0] + 'A'));
        }
        return plain.toString();
    }

    static void runTest(String label, String plaintext, int[][] key) {
        // Warm up JVM
        for (int i = 0; i < 100; i++) encrypt(plaintext, key);
        for (int i = 0; i < 100; i++) decrypt(encrypt(plaintext, key), key);

        long encStart = System.nanoTime();
        String cipher = encrypt(plaintext, key);
        long encTime = System.nanoTime() - encStart;

        long decStart = System.nanoTime();
        decrypt(cipher, key);
        long decTime = System.nanoTime() - decStart;

        System.out.printf("%-10s | Length: %2d | Encrypt: %,d ns | Decrypt: %,d ns%n",
                label, plaintext.length(), encTime, decTime);
    }

    public static void main(String[] args) {
        System.out.println("=== Runtime Test ===");
        System.out.println("Matrix     | PT Len    | Encrypt Time       | Decrypt Time");
        System.out.println("-----------|-----------|--------------------|-----------------");

        runTest("2x2", "HELLOWORLD", new int[][]{{3,3},{2,5}});
        runTest("3x3", "ACTGFDXYZ", new int[][]{{6,24,1},{13,16,10},{20,17,15}});
        runTest("4x4", "MATHANDCIPHER123", new int[][]{{1,2,3,4},{5,6,7,8},{9,10,11,12},{2,3,4,6}});
        runTest("5x5", "CRYPTOGRAPHY", new int[][]{{1,2,3,4,5},{6,7,8,9,10},{11,12,13,14,15},{16,17,18,19,21},{22,23,24,25,2}});
    }
}