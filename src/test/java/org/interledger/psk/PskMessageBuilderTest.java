package org.interledger.psk;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.interledger.psk.model.PskMessage;
import org.interledger.psk.model.PskMessageHeader;
import org.junit.Test;

import java.util.Base64;

/**
 * JUnit to exercise the {@link PskMessageBuilder} implementation.
 */
public class PskMessageBuilderTest {

  @Test
  public void test_NonceValueProvided() {
    PskMessage message = new PskMessageBuilder().withNonce(new byte[16]).toMessage();

    assertNotNull(message);
    assertEquals(1, message.getPublicHeaders(PskMessageHeader.PublicHeaders.NONCE).size());
    assertEquals(Base64.getUrlEncoder().encodeToString(new byte[16]),
        message.getPublicHeaders(PskMessageHeader.PublicHeaders.NONCE).get(0).getValue());
  }

  @Test
  public void test_NonceValueGenerated() {
    PskMessage message = new PskMessageBuilder().withNonce(new byte[16]).toMessage();

    assertNotNull(message);
    assertEquals(1, message.getPublicHeaders(PskMessageHeader.PublicHeaders.NONCE).size());

    PskMessageHeader nonceHeader =
        message.getPublicHeaders(PskMessageHeader.PublicHeaders.NONCE).get(0);
    
    assertEquals(16, Base64.getUrlDecoder().decode(nonceHeader.getValue()).length);
  }
  
  @Test(expected = RuntimeException.class)
  public void test_addPublicHeaderEncryption() {
    PskMessage message = new PskMessageBuilder()
        .addPublicHeader(PskMessageHeader.PublicHeaders.ENCRYPTION, "3DES").toMessage();
  }
  
  @Test
  public void test_addPrivateHeaderEncryption() {
    /* we can add any header we want to the private portion */
    PskMessage message = new PskMessageBuilder()
        .addPrivateHeader(PskMessageHeader.PublicHeaders.ENCRYPTION, "3DES").toMessage();
    
    assertNotNull(message);
    assertEquals(1, message.getPrivateHeaders().size());
    assertEquals("Encryption", message.getPrivateHeaders().get(0).getName());
    assertEquals("3DES", message.getPrivateHeaders().get(0).getValue());
  }
  
  @Test
  public void test() {
    PskMessage message = new PskMessageBuilder()
        .addPublicHeader("public_header", "public_header_value")
        .addPrivateHeader("private_encryption_header", "3DES")
        .withNonce()
        .setApplicationData("Application Data".getBytes()).toMessage();
    
    assertNotNull(message);
    assertEquals(2, message.getPublicHeaders().size());
    assertEquals(1, message.getPublicHeaders("Nonce").size());
    assertEquals("public_header_value",
        message.getPublicHeaders("public_header").get(0).getValue());
    
    assertEquals(1, message.getPrivateHeaders().size());
    assertEquals("private_encryption_header", message.getPrivateHeaders().get(0).getName());
    assertEquals("3DES", message.getPrivateHeaders().get(0).getValue());
    
    assertArrayEquals("Application Data".getBytes(), message.getApplicationData());
  }

}
