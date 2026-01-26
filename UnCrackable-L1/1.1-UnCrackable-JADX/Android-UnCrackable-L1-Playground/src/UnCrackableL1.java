import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class UnCrackableL1 {

  // 3. This is vantagepoint.a class code
  public static byte[] a(byte[] bArr, byte[] bArr2) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    // SecretKeySpec should be created with the algorithm name only
    SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "AES");
    // Use a full transformation string supported by the JVM
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
    return cipher.doFinal(bArr2);
  }

  // 2. This is uncrackable1.a > b part
  public static byte[] b(String str) {
    int length = str.length();
    byte[] bArr = new byte[length / 2];
    for (int i = 0; i < length; i += 2) {
      bArr[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
    }
    return bArr;
  }

  public static void main(String[] args) {
    // 1. This simulates the part where MainActivity.verify get's the string to compare against
    // 2. This is uncrackable1.a > a part
    byte[] bArrA;
    byte[] bArr = new byte[0];

    try {
      // java.util.Base64.getDecoder().decode it's the same as android.util.Base64.decode with DEFAULT flag
      bArrA = a(b("8d127684cbc37c17616d806cf50473cc"), Base64.getDecoder().decode("5UJiFctbmgbDoLXmpL12mkno8HT4Lv8dlat8FxR2GOc="));
    } catch (Exception e) {
      System.out.println("AES error:" + e.getMessage());
      bArrA = bArr;
    }
    System.out.println(new String(bArrA));

  }
}