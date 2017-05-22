package org.interledger.codecs.oer;

import org.interledger.codecs.Codec;
import org.interledger.codecs.CodecContext;
import org.interledger.codecs.oer.OerIA5StringCodec.OerIA5String;
import org.interledger.codecs.oer.OerLengthPrefixCodec.OerLengthPrefix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * An extension of {@link Codec} for reading and writing an ASN.1 OER IA5String.
 * <p>The encoding of IA5String types depends on the size constraint present in the type, if any.
 * Interledger's usage of IA5String always uses a dynamic size constraint, so the encoding of the
 * string value consists of a length prefix followed by the encodings of each character.</p>
 * <p>After encoding a length-prefix using an instance of {@link OerLengthPrefixCodec}, each
 * character
 * in the supplied {@link String} will be encoded in one octet with the highest-order bit set to
 * zero.</p>
 */
public class OerIA5StringCodec implements Codec<OerIA5String> {

  @Override
  public OerIA5String read(
      final CodecContext context, final InputStream inputStream
  ) throws IOException {
    Objects.requireNonNull(context);
    Objects.requireNonNull(inputStream);

    // Detect the length of the encoded IA5String, and move the buffer index to the correct spot.
    final int length = context.read(OerLengthPrefix.class, inputStream).getLength();
    final String result = this.toString(inputStream, length);

    return new OerIA5String(result);
  }

  @Override
  public void write(
      final CodecContext context, final OerIA5String instance, final OutputStream outputStream
  ) throws IOException {

    Objects.requireNonNull(context);
    Objects.requireNonNull(instance);
    Objects.requireNonNull(outputStream);

    // Write the length-prefix, and move the buffer index to the correct spot.
    final int numOctets = instance.getValue().getBytes().length;
    context.write(OerLengthPrefix.class, new OerLengthPrefix(numOctets), outputStream);

    // Write the String bytes to the buffer.
    outputStream.write(instance.getValue().getBytes());
  }

  /**
   * Convert an {@link InputStream} into a {@link String}.  Reference the SO below for an
   * interesting performance comparison of various InputStream to String methodologies.
   *
   * @param inputStream An instance of {@link InputStream}.
   * @return A {@link String}
   * @throws IOException If the {@code inputStream} is unable to be read properly.
   * @see "http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string"
   */
  private String toString(final InputStream inputStream, final int lengthToRead)
      throws IOException {
    Objects.requireNonNull(inputStream);
    ByteArrayOutputStream result = new ByteArrayOutputStream();

    // Read lengthToRead bytes from the inputStream into the buffer...
    byte[] buffer = new byte[lengthToRead];
    inputStream.read(buffer);
    result.write(buffer, 0, lengthToRead);
    return result.toString(StandardCharsets.US_ASCII.name());
  }


  /**
   * A typing mechanism for registering multiple codecs that operate on the same type (in this case,
   * {@link String}).
   */
  public static class OerIA5String {

    private final String value;

    public OerIA5String(final String value) {
      this.value = Objects.requireNonNull(value);
    }

    /**
     * Accessor for the value of this IA5String, as a {@link String}.
     *
     * @return An instance of {@link String}.
     */
    public String getValue() {
      return value;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }

      OerIA5String oerIA5String = (OerIA5String) obj;

      return value.equals(oerIA5String.value);
    }

    @Override
    public int hashCode() {
      return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder("IA5String{");
      sb.append("value='").append(value).append('\'');
      sb.append('}');
      return sb.toString();

    }
  }
}
