package org.interledger.codecs.packettypes;

import java.net.URI;

import org.interledger.codecs.packettypes.InterledgerPacketType.AbstractInterledgerPacketType;

/**
 * An implementation of {@link InterledgerPacketType} for ILP Error packets.
 */
public class InterledgerErrorPacketType extends AbstractInterledgerPacketType
    implements InterledgerPacketType {

  /**
   * No-args Constructor.
   */
  public InterledgerErrorPacketType() {
    super(INTERLEDGER_PROTOCOL_ERROR, URI.create("https://interledger.org/protocol_error"));
  }
}
