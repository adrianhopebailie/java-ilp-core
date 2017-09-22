package org.interledger.codecs;

import org.interledger.Condition;
import org.interledger.Fulfillment;
import org.interledger.InterledgerAddress;
import org.interledger.btp.BilateralTransferProtocolError;
import org.interledger.btp.BilateralTransferProtocolErrorTypes;
import org.interledger.btp.BilateralTransferProtocolFulfill;
import org.interledger.btp.BilateralTransferProtocolMessage;
import org.interledger.btp.BilateralTransferProtocolPacket;
import org.interledger.btp.BilateralTransferProtocolPrepare;
import org.interledger.btp.BilateralTransferProtocolReject;
import org.interledger.btp.BilateralTransferProtocolResponse;
import org.interledger.codecs.btp.BilateralTransferProtocolCodecContext;
import org.interledger.codecs.oer.OerGeneralizedTimeCodec;
import org.interledger.codecs.oer.OerGeneralizedTimeCodec.OerGeneralizedTime;
import org.interledger.codecs.oer.OerIA5StringCodec;
import org.interledger.codecs.oer.OerIA5StringCodec.OerIA5String;
import org.interledger.codecs.oer.OerLengthPrefixCodec;
import org.interledger.codecs.oer.OerLengthPrefixCodec.OerLengthPrefix;
import org.interledger.codecs.oer.OerOctetStringCodec;
import org.interledger.codecs.oer.OerOctetStringCodec.OerOctetString;
import org.interledger.codecs.oer.OerSequenceOfAddressCodec;
import org.interledger.codecs.oer.OerSequenceOfAddressCodec.OerSequenceOfAddress;
import org.interledger.codecs.oer.OerSequenceOfSubProtocolDataCodec;
import org.interledger.codecs.oer.OerSequenceOfSubProtocolDataCodec.OerSequenceOfSubProtocolData;
import org.interledger.codecs.oer.OerUint128Codec;
import org.interledger.codecs.oer.OerUint128Codec.OerUint128;
import org.interledger.codecs.oer.OerUint256Codec;
import org.interledger.codecs.oer.OerUint256Codec.OerUint256;
import org.interledger.codecs.oer.OerUint32Codec;
import org.interledger.codecs.oer.OerUint32Codec.OerUint32;
import org.interledger.codecs.oer.OerUint64Codec;
import org.interledger.codecs.oer.OerUint64Codec.OerUint64;
import org.interledger.codecs.oer.OerUint8Codec;
import org.interledger.codecs.oer.OerUint8Codec.OerUint8;
import org.interledger.codecs.oer.btp.BilateralTransferProtocolErrorOerCodec;
import org.interledger.codecs.oer.btp.BilateralTransferProtocolFulfillOerCodec;
import org.interledger.codecs.oer.btp.BilateralTransferProtocolMessageOerCodec;
import org.interledger.codecs.oer.btp.BilateralTransferProtocolPrepareOerCodec;
import org.interledger.codecs.oer.btp.BilateralTransferProtocolRejectOerCodec;
import org.interledger.codecs.oer.btp.BilateralTransferProtocolResponseOerCodec;
import org.interledger.codecs.oer.ilp.ConditionOerCodec;
import org.interledger.codecs.oer.ilp.InterledgerAddressOerCodec;
import org.interledger.codecs.oer.ilp.InterledgerPacketTypeOerCodec;
import org.interledger.codecs.oer.ilp.InterledgerPaymentOerCodec;
import org.interledger.codecs.oer.ilp.InterledgerProtocolProtocolErrorOerCodec;
import org.interledger.codecs.oer.ilqp.QuoteByDestinationAmountRequestOerCodec;
import org.interledger.codecs.oer.ilqp.QuoteByDestinationAmountResponseOerCodec;
import org.interledger.codecs.oer.ilqp.QuoteBySourceAmountRequestOerCodec;
import org.interledger.codecs.oer.ilqp.QuoteBySourceAmountResponseOerCodec;
import org.interledger.codecs.oer.ilqp.QuoteLiquidityRequestOerCodec;
import org.interledger.codecs.oer.ilqp.QuoteLiquidityResponseOerCodec;
import org.interledger.codecs.oer.ipr.InterledgerPaymentRequestOerCodec;
import org.interledger.codecs.packettypes.InterledgerPacketType;
import org.interledger.codecs.psk.PskMessageBinaryCodec;
import org.interledger.ilp.InterledgerPayment;
import org.interledger.ilp.InterledgerProtocolError;
import org.interledger.ilqp.QuoteByDestinationAmountRequest;
import org.interledger.ilqp.QuoteByDestinationAmountResponse;
import org.interledger.ilqp.QuoteBySourceAmountRequest;
import org.interledger.ilqp.QuoteBySourceAmountResponse;
import org.interledger.ilqp.QuoteLiquidityRequest;
import org.interledger.ilqp.QuoteLiquidityResponse;
import org.interledger.ipr.InterledgerPaymentRequest;
import org.interledger.psk.PskMessage;

/**
 * A factory class for constructing a CodecContext that can read and write Interledger objects using
 * ASN.1 OER encoding.
 */
public class CodecContextFactory {

  /**
   * Create an instance of {@link CodecContext} that encodes and decodes Interledger packets using
   * ASN.1 OER encoding.
   */
  public static CodecContext interledger() {

    // OER Base...
    return new CodecContext()
      .register(OerUint8.class, new OerUint8Codec())
      .register(OerUint32.class, new OerUint32Codec())
      .register(OerUint64.class, new OerUint64Codec())
      .register(OerUint256.class, new OerUint256Codec())
      .register(OerLengthPrefix.class, new OerLengthPrefixCodec())
      .register(OerIA5String.class, new OerIA5StringCodec())
      .register(OerOctetString.class, new OerOctetStringCodec())
      .register(OerGeneralizedTime.class, new OerGeneralizedTimeCodec())
      .register(OerSequenceOfAddress.class, new OerSequenceOfAddressCodec())

      // ILP
      .register(InterledgerAddress.class, new InterledgerAddressOerCodec())
      .register(InterledgerPayment.class, new InterledgerPaymentOerCodec())
      .register(InterledgerProtocolError.class, new InterledgerProtocolProtocolErrorOerCodec())
      .register(InterledgerPaymentRequest.class, new InterledgerPaymentRequestOerCodec())
      .register(Condition.class, new ConditionOerCodec())

      // ILQP
      .register(QuoteByDestinationAmountRequest.class,
        new QuoteByDestinationAmountRequestOerCodec())
      .register(QuoteByDestinationAmountResponse.class,
        new QuoteByDestinationAmountResponseOerCodec())
      .register(QuoteBySourceAmountRequest.class, new QuoteBySourceAmountRequestOerCodec())
      .register(QuoteBySourceAmountResponse.class, new QuoteBySourceAmountResponseOerCodec())
      .register(QuoteLiquidityRequest.class, new QuoteLiquidityRequestOerCodec())
      .register(QuoteLiquidityResponse.class, new QuoteLiquidityResponseOerCodec())

      // PSK
      .register(PskMessage.class, new PskMessageBinaryCodec());
  }

  /**
   * Create an instance of {@link CodecContext} that encodes and decodes Interledger packets using
   * ASN.1 OER encoding.
   */
  public static CodecContext bilateralTransferProtocol() {

    // OER Base...
    return new BilateralTransferProtocolCodecContext()
        .register(OerUint8.class, new OerUint8Codec())
        .register(OerUint32.class, new OerUint32Codec())
        .register(OerUint64.class, new OerUint64Codec())
        .register(OerUint128.class, new OerUint128Codec())
        .register(OerUint256.class, new OerUint256Codec())
        .register(OerLengthPrefix.class, new OerLengthPrefixCodec())
        .register(OerIA5String.class, new OerIA5StringCodec())
        .register(OerOctetString.class, new OerOctetStringCodec())
        .register(OerGeneralizedTime.class, new OerGeneralizedTimeCodec())
        .register(OerSequenceOfSubProtocolData.class, new OerSequenceOfSubProtocolDataCodec())

        // BTP
        .register(BilateralTransferProtocolError.class,
            new BilateralTransferProtocolErrorOerCodec())
        .register(BilateralTransferProtocolFulfill.class,
            new BilateralTransferProtocolFulfillOerCodec())
        .register(BilateralTransferProtocolMessage.class,
            new BilateralTransferProtocolMessageOerCodec())
        .register(BilateralTransferProtocolPrepare.class,
            new BilateralTransferProtocolPrepareOerCodec())
        .register(BilateralTransferProtocolReject.class,
            new BilateralTransferProtocolRejectOerCodec())
        .register(BilateralTransferProtocolResponse.class,
            new BilateralTransferProtocolResponseOerCodec());
  }

  public static CodecContext interledgerJson() {
    throw new RuntimeException("Not yet implemented!");
  }

  public static CodecContext interledgerProtobuf() {
    throw new RuntimeException("Not yet implemented!");
  }

}
