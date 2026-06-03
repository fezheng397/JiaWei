package com.recipes.api.common;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Instant;

public final class PublicIdGenerator {
  private static final char[] CROCKFORD_BASE32 =
      "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
  private static final BigInteger BASE32_MASK = BigInteger.valueOf(31);
  private static final SecureRandom RANDOM = new SecureRandom();

  private PublicIdGenerator() {}

  public static String generate() {
    char[] id = new char[26];
    long timestamp = Instant.now().toEpochMilli();

    for (int index = 9; index >= 0; index--) {
      id[index] = CROCKFORD_BASE32[(int) (timestamp & 31)];
      timestamp >>>= 5;
    }

    BigInteger random = new BigInteger(80, RANDOM);

    for (int index = 25; index >= 10; index--) {
      id[index] = CROCKFORD_BASE32[random.and(BASE32_MASK).intValue()];
      random = random.shiftRight(5);
    }

    return new String(id);
  }
}
