package com.wipro.wega.fraud_transaction_adapter.transformer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.wipro.wega.fraud_transaction_adapter.model.CreditTransaction;

class CobolTransactionTransformerTest {

    private final CobolTransactionTransformer transformer = new CobolTransactionTransformer();

    @Test
    void parsesAllFieldsFromFixedWidthRecord() {
        String record = new RecordBuilder()
                .add("TXN0000000000001", 16)
                .add("000123456789", 12)
                .add("4111111111111111", 16)
                .add("000000150000", 12)   // 1500.00
                .add("USD", 3)
                .add("MERCH0000000001", 15)
                .add("ACME CORP", 25)
                .add("20260605", 8)
                .add("143015", 6)
                .add("US", 2)
                .build();

        CreditTransaction txn = transformer.transform(record);

        assertThat(txn.transactionId()).isEqualTo("TXN0000000000001");
        assertThat(txn.accountNumber()).isEqualTo("000123456789");
        assertThat(txn.cardNumber()).isEqualTo("4111111111111111");
        assertThat(txn.amount()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(txn.currencyCode()).isEqualTo("USD");
        assertThat(txn.merchantId()).isEqualTo("MERCH0000000001");
        assertThat(txn.merchantName()).isEqualTo("ACME CORP");
        assertThat(txn.transactionDate()).isEqualTo(LocalDate.of(2026, 6, 5));
        assertThat(txn.transactionTime()).isEqualTo(LocalTime.of(14, 30, 15));
        assertThat(txn.countryCode()).isEqualTo("US");
    }

    @Test
    void rejectsRecordShorterThanLayout() {
        assertThatThrownBy(() -> transformer.transform("too-short"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNullRecord() {
        assertThatThrownBy(() -> transformer.transform(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /** Builds a fixed-width record by right-padding each field to its width. */
    private static final class RecordBuilder {
        private final StringBuilder sb = new StringBuilder();

        RecordBuilder add(String value, int width) {
            if (value.length() > width) {
                throw new IllegalArgumentException("value '" + value + "' exceeds width " + width);
            }
            sb.append(value);
            sb.append(" ".repeat(width - value.length()));
            return this;
        }

        String build() {
            return sb.toString();
        }
    }
}
