package org.interledger.wire.codecs.packets;

import java.net.URI;
import org.interledger.wire.InterledgerPacketType;
import org.interledger.wire.InterledgerPacketType.AbstractInterledgerPacketType;

/**
 * An implementation of {@link InterledgerPacketType} for ILQP Liquidity responses.
 */
public class QuoteLiquidityResponsePacketType extends AbstractInterledgerPacketType implements
    InterledgerPacketType {

  /**
   * No-args Constructor.
   */
  public QuoteLiquidityResponsePacketType() {
    super(ILQP_QUOTE_LIQUIDITY_RESPONSE_TYPE,
        URI.create("https://interledger.org/ilqp/quote_liquidity_response"));
  }

}