package org.interledger.ilp;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import org.interledger.InterledgerAddress;
import org.interledger.InterledgerAddressBuilder;

/**
 * @REF: REF: https://interledger.org/rfcs/0003-interledger-protocol/#errors
 */
public class InterledgerError {

  /**
   * Valid error codes that might be encountered during an Interledger payment.
   */
  enum ErrorCode {
    F00_BAD_REQUEST("F00", "BAD REQUEST"),
    F01_INVALID_PAQUET("F01", "INVALID PACKET"),
    F02_UNREACHABLE("F02", "UNREACHABLE"),
    F03_INVALID_AMOUNT("F03", "INVALID AMOUNT"),
    F04_INSUFFICIENT_DST_AMOUNT("F04", "INSUFFICIENT DST. AMOUNT"),
    F05_WRONG_CONDITION("F05", "WRONG CONDITION"),
    F06_UNEXPECTED_PAYMENT("F06", "UNEXPECTED PAYMENT"),
    F07_CANNOT_RECEIVE("F07", "CANNOT RECEIVE"),
    F99_APPLICATION_ERROR("F99", "APPLICATION ERROR"),
    T00_INTERNAL_ERROR("T00", "INTERNAL ERROR"),
    T01_LEDGER_UNREACHABLE("T01", "LEDGER UNREACHABLE"),
    T02_LEDGER_BUSY("T02", "LEDGER BUSY"),
    T03_CONNECTOR_BUSY("T03", "CONNECTOR BUSY"),
    T04_INSUFFICIENT_LIQUIDITY("T04", "INSUFFICIENT LIQUIDITY"),
    T05_RATE_LIMITED("T05", "RATE LIMITED"),
    T99_APPLICATION_ERROR("T99", "APPLICATION ERROR"),
    R00_TRANSFER_TIMED_OUT("R00", "TRANSFER TIMED OUT"),
    R01_INSUFFICEINT_SOURCE_AMOUNT("R01", "INSUFFICIENT SOURCE AMOUNT"),
    R02_INSUFFICIENT_TIMEOUT("R02", "INSUFFICIENT TIMEOUT"),
    R99_APPLICATION_ERROR("R99", "APPLICATION ERROR");

    /**
     * The type of an {@link ErrorCode}, which is determined by the first letter of the error code's
     * code value.
     */
    enum ErrorType {
      FINAL('F'),
      TEMPORARY('T'),
      RELATIVE('R');

      private final String t;

      private ErrorType(char t) {
        this.t = Character.toString(t);
      }

      @Override
      public String toString() {
        return t;
      }

    }

    final String code;
    final String name;
    private final ErrorType type;

    ErrorCode(final String code, final String name) {
      this.code = Objects.requireNonNull(code, "code MUST not be null!").trim();
      this.name = Objects.requireNonNull(name, "name MUST not be null!").trim();

      if (code.length() < 3) {
        throw new RuntimeException(
            "Per IL-RFC-3, error code length must be at least 3 characters!");
      }

      // NOTE: Per the R99_APPLICATION_ERROR, applications may use custom names, so no validation
      // should be performed on names.

      switch (code.charAt(0)) {
        case 'F':
          this.type = ErrorType.FINAL;
          break;
        case 'T':
          this.type = ErrorType.TEMPORARY;
          break;
        case 'R':
          this.type = ErrorType.RELATIVE;
          break;
        default:
          throw new IllegalArgumentException("code must start with 'F', 'T' or 'R'.");
      }
    }

    @Override
    public String toString() {
      return String.format("%s - %s", this.code, this.name);
    }

    /**
     * <p>Accessor for this ErrorCode's {@code code} property.</p>
     *
     * <p>Per IL-RFC-3: "Implementations SHOULD NOT depend on the name instead of the code. The
     * name is primarily provided as a convenience to facilitate debugging by humans. If the name
     * does not match the code, the code is the definitive identifier of the error."</p>
     */
    public String getCode() {
      return this.code;
    }

    /**
     * Returns the {@link Type} of this {@link ErrorCode}.
     */
    public ErrorType getType() {
      return this.type;
    }
  }

  final ErrorCode errorCode;
  final InterledgerAddress triggeredBy;
  final ZonedDateTime triggeredAt;
  final List<InterledgerAddress> forwardedBy;
  final String data;

  // TRIGGERING_ILP_NODE is used as a "mark" to avoid nulls.
  // This way our code can differentiate between a coding error
  // (developer sent a null by mistake) and the real intention of not
  // sending selfAddress.
  static final InterledgerAddress TRIGGERING_ILP_NODE = new InterledgerAddressBuilder()
      .value("g.selfAddressNONE").build();

  /**
   * Constructor used by ILP Connectors.
   */
  private InterledgerError(
      final ErrorCode errorCode,
      final InterledgerAddress triggeredBy,
      final ZonedDateTime triggeredAt,
      List<InterledgerAddress> forwardedBy,
      final InterledgerAddress selfAddress,
      final String data) {
    this.errorCode = Objects.requireNonNull(errorCode, "errorCode   can not be null");
    this.triggeredBy = Objects.requireNonNull(triggeredBy, "triggeredBy can not be null");
    this.triggeredAt = Objects.requireNonNull(triggeredAt, "triggeredAt can not be null");
    this.data = Objects.requireNonNull(data, "data        can not be null");

    if (TRIGGERING_ILP_NODE.getValue().equals(selfAddress.getValue())) {
      this.forwardedBy = forwardedBy; // Ignore selfAddress
    } else {
      for (InterledgerAddress forwardedByConnector : forwardedBy) {
        if (forwardedByConnector.getValue().equals(selfAddress)) {
          // TODO:(0) Recheck next claim:
          //   On the way back of forwardedBy-connectors our ilpConnector selfAddress
          //   has already been found. This means that the error is
          //   "running-in-circles" trying to reach the client. This must never happen.
          //   launch a RuntimeException to break the loop.
          throw new RuntimeException("CRITICAL, InterledgerError: " + selfAddress.getValue()
              + "was already found in the forwardedBy list");
        }
      }
      forwardedBy.add(selfAddress);
      this.forwardedBy = forwardedBy;
    }
  }

  /**
   * Constructs an instance of <code>InterledgerException</code>
   * with default parameters for forwardedBy (Empty list) and
   * triggeredAt (ZonedDateTime.now()).
   * In most situations such values match the default ones
   * when triggering a new exception (vs an exception received
   * from another ILP node that is being forwarded back to
   * originating request clients)
   *
   * Check the RFC https://interledger.org/rfcs/0003-interledger-protocol/#errors
   * for the newest updated doc.
   */
  public InterledgerError(
      ErrorCode errorCode,
      InterledgerAddress triggeredBy,
      String data) {
    this(errorCode, triggeredBy, ZonedDateTime.now(), new java.util.ArrayList<InterledgerAddress>(),
        TRIGGERING_ILP_NODE, "");
  }


  public ErrorCode getErrCode() {
    return errorCode;
  }

  /**
   * @return ErrorType categorizes the error as FINAL, TEMPORARY or RELATIVE
   * (See RFC for more info)
   */
  public ErrorCode.ErrorType getErrorType() {
    return this.errorCode.getType();
  }

  public InterledgerAddress getTriggeredBy() {
    return triggeredBy;
  }

  public List<InterledgerAddress> getForwardedBy() {
    return forwardedBy;
  }

  public ZonedDateTime getTriggeredAt() {
    return triggeredAt;
  }

  public String getData() {
    return data;
  }
}