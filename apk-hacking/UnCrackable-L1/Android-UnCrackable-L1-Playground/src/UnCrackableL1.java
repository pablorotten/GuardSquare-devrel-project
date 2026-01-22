import java.util.Base64;

public class UnCrackableL1 {


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
    String str = "TEST";

     // 2. This is uncrackable1.a > a part
    byte[] bArrA;
    byte[] bArr = new byte[0];

    try {
      // Is java.util.Base64 > Base64.getDecoder().decode same as android.util.Base64 > Base64.decode with DEFAULT flag?
      bArrA = vantagepoint.a(b("8d127684cbc37c17616d806cf50473cc"), Base64.getDecoder().decode("5UJiFctbmgbDoLXmpL12mkno8HT4Lv8dlat8FxR2GOc="));
    } catch (Exception e) {
      System.out.println("AES error:" + e.getMessage());
      bArrA = bArr;
    }
    System.out.println(new String(bArrA));

  }
}