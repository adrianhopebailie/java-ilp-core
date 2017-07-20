package org.interledger.psk;

import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

/**
 * Convenience header representing the public Encryption header found in PSK messages.
 */
public class PskEncryptionHeader extends PskMessage.Header {

  private final byte[] authTag;
  private final PskEncryptionType type;

  private PskEncryptionHeader() {
    super(WellKnown.ENCRYPTION, PskEncryptionType.NONE.toString());
    this.type = PskEncryptionType.NONE;
    this.authTag = null;
  }

  private PskEncryptionHeader(byte[] authTag) {
    super(WellKnown.ENCRYPTION,
        new StringBuilder().append(PskEncryptionType.AES_256_GCM.toString())
            .append(" ")
            .append(Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(authTag))
            .toString());
    this.type = PskEncryptionType.AES_256_GCM;
    this.authTag = Arrays.copyOf(authTag, authTag.length);
  }

  /**
   * Constructs an instance of the header with the encryption type NONE.
   */
  public static PskEncryptionHeader none() {
    return new PskEncryptionHeader();
  }

  /**
   * Constructs an instance of the header with the AES-GCM encryption type and provided
   * authentication tag value.
   *
   * @param authenticationTag The authentication tag value. May be null.
   */
  public static PskEncryptionHeader aesGcm(final byte[] authenticationTag) {
    Objects.requireNonNull(authenticationTag);
    return new PskEncryptionHeader(authenticationTag);
  }

  /**
   * Constructs an instance of the header of an existing header with the appropriate values.
   *
   * @param header An existing header
   *
   * @return An encryption header
   *
   * @throws a RuntimeException if an encryption header can't be constructed of the given header
   */
  public static PskEncryptionHeader fromHeader(PskMessage.Header header) {

    String value = header.getValue()
        .trim();

    if (value.equalsIgnoreCase(PskEncryptionType.NONE.toString())) {
      return none();
    }

    String[] tokens = value.split(" ");
    if (tokens[0].equalsIgnoreCase(PskEncryptionType.AES_256_GCM.toString())) {
      if (tokens.length == 1) {
        throw new RuntimeException("Invalid AES GCM encryption header. No auth tag.");
      }
      return aesGcm(Base64.getUrlDecoder()
          .decode(tokens[1]));
    }

    throw new RuntimeException("Invalid encryption header value.");
  }

  /**
   * Convenience method to retrieve the authentication tag value, if it is present in the header.
   */
  public byte[] getAuthenticationTag() {
    if (type == PskEncryptionType.NONE) {
      return null;
    }
    return Arrays.copyOf(authTag, authTag.length);
  }

  /**
   * Returns the encryption type indicated in the header.
   */
  public PskEncryptionType getEncryptionType() {
    return type;
  }

  @Override
  public String getName() {
    return PskMessage.Header.WellKnown.ENCRYPTION;
  }

  @Override
  public String getValue() {
    if (getEncryptionType() == PskEncryptionType.NONE) {
      return PskEncryptionType.NONE.toString();
    }

    return PskEncryptionType.AES_256_GCM.toString() + " "
        + Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(this.authTag);
  }

}
