package org.interledger.wire.codecs.packets;

import java.net.URI;
import org.interledger.wire.InterledgerPacketType;
import org.interledger.wire.InterledgerPacketType.AbstractInterledgerPacketType;

/**
 * An implementation of {@link InterledgerPacketType} for ILQP quote responses.
 */
public class QuoteByDestinationResponsePacketType extends
    AbstractInterledgerPacketType implements InterledgerPacketType {

  /**
   * No-args Constructor.
   */
  public QuoteByDestinationResponsePacketType() {
    super(ILQP_QUOTE_BY_DESTINATION_AMOUNT_RESPONSE_TYPE,
        URI.create("https://interledger.org/ilqp/quote_by_destination_amount_response"));
  }

}