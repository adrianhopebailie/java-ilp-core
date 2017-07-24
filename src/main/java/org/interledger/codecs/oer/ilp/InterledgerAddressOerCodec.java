package org.interledger.codecs.oer.ilp;

import org.interledger.InterledgerAddress;
import org.interledger.codecs.Codec;
import org.interledger.codecs.CodecContext;
import org.interledger.codecs.InterledgerAddressCodec;
import org.interledger.codecs.oer.OerIA5StringCodec.OerIA5String;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * An implementation of {@link Codec} that reads and writes instances of {@link InterledgerAddress}.
 */
public class InterledgerAddressOerCodec implements InterledgerAddressCodec {

  @Override
  public InterledgerAddress read(final CodecContext context, final InputStream inputStream)
      throws IOException {
    Objects.requireNonNull(context);
    Objects.requireNonNull(inputStream);
    final String value = context.read(OerIA5String.class, inputStream)
        .getValue();
    return InterledgerAddress.of(value);
  }

  @Override
  public void write(final CodecContext context, final InterledgerAddress instance,
      final OutputStream outputStream) throws IOException {
    Objects.requireNonNull(context);
    Objects.requireNonNull(instance);
    Objects.requireNonNull(outputStream);

    context.write(OerIA5String.class, new OerIA5String(instance.getValue()), outputStream);
  }

}
