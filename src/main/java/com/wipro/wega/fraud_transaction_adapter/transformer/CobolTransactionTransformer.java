package com.wipro.wega.fraud_transaction_adapter.transformer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.wipro.wega.fraud_transaction_adapter.logging.Loggable;
import com.wipro.wega.fraud_transaction_adapter.model.CreditTransaction;

/**
 * Parses an inbound COBOL fixed-width credit transaction record into the
 * canonical {@link CreditTransaction} model.
 *
 * <p>Record layout (MQ-CREDIT-TXNS), total length {@value #RECORD_LENGTH}:
 * <pre>
 *   Field             Offset  Len  Notes
 *   transactionId      0       16  alphanumeric, right-padded
 *   accountNumber     16       12  numeric
 *   cardNumber        28       16  numeric
 *   amount            44       12  numeric, last 2 digits are implied decimals
 *   currencyCode      56        3  ISO 4217 alpha
 *   merchantId        59       15  alphanumeric
 *   merchantName      74       25  alphanumeric
 *   transactionDate   99        8  YYYYMMDD
 *   transactionTime  107        6  HHMMSS
 *   countryCode      113        2  ISO 3166 alpha-2
 * </pre>
 */
@Component
public class CobolTransactionTransformer {

    static final int RECORD_LENGTH = 115;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmmss");

    @Loggable
    public CreditTransaction transform(String record) {
        if (record == null || record.length() < RECORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid COBOL record: expected at least " + RECORD_LENGTH
                            + " characters but got " + (record == null ? 0 : record.length()));
        }

        try {
            return new CreditTransaction(
                    field(record, 0, 16),
                    field(record, 16, 28),
                    field(record, 28, 44),
                    amount(record, 44, 56),
                    field(record, 56, 59),
                    field(record, 59, 74),
                    field(record, 74, 99),
                    LocalDate.parse(field(record, 99, 107), DATE_FORMAT),
                    LocalTime.parse(field(record, 107, 113), TIME_FORMAT),
                    field(record, 113, 115));
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date or time in COBOL record", e);
        }
    }

    private String field(String record, int start, int end) {
        return record.substring(start, end).trim();
    }

    /** Reads a zoned-numeric amount with two implied decimal places. */
    private BigDecimal amount(String record, int start, int end) {
        String raw = field(record, start, end);
        if (!raw.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("Invalid amount format");
        }
        return new BigDecimal(raw).movePointLeft(2);
    }
}
