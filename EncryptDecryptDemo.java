// EncryptDecryptDemo.java
public class EncryptDecryptDemo {

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
        if (keyInv == null) return "ERROR: Key not invertible mod 26.";
        StringBuilder plain = new StringBuilder();
        for (int i = 0; i < ciphertext.length(); i += n) {
            int[][] block = new int[n][1];
            for (int j = 0; j < n; j++) block[j][0] = ciphertext.charAt(i + j) - 'A';
            int[][] dec = multiplyMatrix(keyInv, block);
            for (int j = 0; j < n; j++) plain.append((char)(dec[j][0] + 'A'));
        }
        return plain.toString();
    }

    public static void main(String[] args) {
        // 2x2 — det = 3*5 - 3*2 = 9, gcd(9,26) = 1 ✓
        int[][] key2 = {{3,3},{2,5}};
        String plain2 = "HELLOWORLD";
        String cipher2 = encrypt(plain2, key2);
        System.out.println("=== 2x2 Key Matrix ===");
        System.out.println("Plaintext  : " + plain2);
        System.out.println("Ciphertext : " + cipher2);
        System.out.println("Decrypted  : " + decrypt(cipher2, key2));

        // 3x3 — classic known-invertible Hill key, det = 441, 441 mod 26 = 25, gcd(25,26) = 1 ✓
        int[][] key3 = {{6,24,1},{13,16,10},{20,17,15}};
        String plain3 = "ACTGFD";
        String cipher3 = encrypt(plain3, key3);
        System.out.println("\n=== 3x3 Key Matrix ===");
        System.out.println("Plaintext  : " + plain3);
        System.out.println("Ciphertext : " + cipher3);
        System.out.println("Decrypted  : " + decrypt(cipher3, key3));

        // 4x4 — det = 1, gcd(1,26) = 1 ✓
        int[][] key4 = {
            {1, 0, 0, 5},
            {0, 1, 3, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
        };
        String plain4 = "MATHISBEAUTIFUL";
        String cipher4 = encrypt(plain4, key4);
        System.out.println("\n=== 4x4 Key Matrix ===");
        System.out.println("Plaintext  : " + plain4);
        System.out.println("Ciphertext : " + cipher4);
        System.out.println("Decrypted  : " + decrypt(cipher4, key4));

        // 5x5 — det = 1, gcd(1,26) = 1 ✓
        int[][] key5 = {
            {1, 0, 0, 0, 3},
            {0, 1, 0, 5, 0},
            {0, 0, 1, 0, 0},
            {0, 0, 0, 1, 0},
            {0, 0, 0, 0, 1}
        };
        String plain5 = "CRYPTOGRAPHY";
        String cipher5 = encrypt(plain5, key5);
        System.out.println("\n=== 5x5 Key Matrix ===");
        System.out.println("Plaintext  : " + plain5);
        System.out.println("Ciphertext : " + cipher5);
        System.out.println("Decrypted  : " + decrypt(cipher5, key5));
    }
}