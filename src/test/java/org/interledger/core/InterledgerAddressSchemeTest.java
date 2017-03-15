package org.interledger.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link InterledgerAddressBuilder} schemes.
 */
@RunWith(Parameterized.class)
public class InterledgerAddressSchemeTest {

    private static final String EXPECTED_ERROR_MESSAGE = "Invalid characters in address.  Reference RFC 15 for proper format.";

    @Parameters(name = "{index}: scheme({0})")
    public static Iterable<Object[]> schemes() {
        return Arrays.asList(
                new Object[][]{{"g"}, {"private"}, {"example"}, {"peer"}, {"self"}, {"test1"}, {"test2"}, {"test3"}});
    }

    private final String scheme;

    public InterledgerAddressSchemeTest(final String scheme) {
        this.scheme = scheme;
    }

    /**
     * Assert that something like "g.foo.bob" is valid.
     */
    @Test
    public void test_scheme_with_neighborhood_and_account_as_address() throws Exception {
        final InterledgerAddress address = InterledgerAddressBuilder.builder().withValue(
                this.scheme + ".foo.bob").build();
        assertThat(address.getValue(), is(this.scheme + ".foo.bob"));
        assertThat(address.isLedgerPrefix(), is(false));
    }

    /**
     * Assert that something like "g.foo.bob." is valid.
     */
    @Test
    public void test_scheme_with_neighborhood_and_ledger_identifier_as_prefix() throws Exception {
        final InterledgerAddress addressPrefix = InterledgerAddressBuilder.builder().withValue(
                this.scheme + ".foo.bob.").build();
        assertThat(addressPrefix.getValue(), is(this.scheme + ".foo.bob."));
        assertThat(addressPrefix.isLedgerPrefix(), is(true));
    }

    /**
     * Assert that something like "g.foo" is valid.
     */
    @Test
    public void test_scheme_with_only_address() throws Exception {
        final InterledgerAddress address = InterledgerAddressBuilder.builder().withValue(
                this.scheme + ".foo").build();
        assertThat(address.getValue(), is(this.scheme + ".foo"));
        assertThat(address.isLedgerPrefix(), is(false));
    }

    /**
     * Assert that something like "g.foo." is valid.
     */
    @Test
    public void test_scheme_with_neighborhood_as_prefix() throws Exception {
        final InterledgerAddress addressPrefix = InterledgerAddressBuilder.builder().withValue(
                this.scheme + ".foo.").build();
        assertThat(addressPrefix.getValue(), is(this.scheme + ".foo."));
        assertThat(addressPrefix.isLedgerPrefix(), is(true));
    }

    /**
     * Assert that something like "g." is valid.
     */
    @Test
    public void test_address_with_only_scheme_prefix() throws Exception {
        final InterledgerAddress address = InterledgerAddressBuilder.builder().withValue(this.scheme + ".").build();
        assertThat(address.getValue(), is(this.scheme + "."));
        assertThat(address.isLedgerPrefix(), is(true));
    }

    /**
     * Assert that something like "g" is invalid.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_address_with_only_scheme_address() throws Exception {
        try {
            InterledgerAddressBuilder.builder().withValue(this.scheme).build();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is(EXPECTED_ERROR_MESSAGE));
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_destination_address_with_invalid_scheme() throws Exception {
        try {
            InterledgerAddressBuilder.builder().withValue(this.scheme + "1.foo").build();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is(EXPECTED_ERROR_MESSAGE));
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_prefix_with_invalid_scheme() throws Exception {
        try {
            InterledgerAddressBuilder.builder().withValue(this.scheme + "1.foo.").build();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is(EXPECTED_ERROR_MESSAGE));
            throw e;
        }
    }
}