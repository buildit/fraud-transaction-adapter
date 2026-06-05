package com.wipro.wega.fraud_transaction_adapter.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

import com.wipro.wega.fraud_transaction_adapter.client.CreditScoreClient;
import com.wipro.wega.fraud_transaction_adapter.model.CreditReply;
import com.wipro.wega.fraud_transaction_adapter.model.CreditScoreResponse;
import com.wipro.wega.fraud_transaction_adapter.model.CreditTransaction;
import com.wipro.wega.fraud_transaction_adapter.producer.CreditReplyProducer;
import com.wipro.wega.fraud_transaction_adapter.transformer.CobolTransactionTransformer;

@ExtendWith(MockitoExtension.class)
class CreditTransactionServiceImplTest {

    @Mock
    private CobolTransactionTransformer transformer;
    @Mock
    private CreditScoreClient creditScoreClient;
    @Mock
    private CreditReplyProducer creditReplyProducer;

    @InjectMocks
    private CreditTransactionServiceImpl service;

    @Test
    void publishesReplyBuiltFromScoreResponse() {
        CreditTransaction txn = new CreditTransaction(
                "TXN1", "ACC1", "CARD1", new BigDecimal("10.00"), "USD",
                "M1", "Acme", LocalDate.of(2026, 6, 5), LocalTime.NOON, "US");
        CreditScoreResponse response = new CreditScoreResponse("TXN1", 720, "APPROVED", "ok");

        when(transformer.transform(any())).thenReturn(txn);
        when(creditScoreClient.requestScore(txn))
                .thenReturn(CompletableFuture.completedFuture(response));

        service.process("raw-cobol-record");

        ArgumentCaptor<CreditReply> captor = ArgumentCaptor.forClass(CreditReply.class);
        verify(creditReplyProducer).publish(captor.capture());

        CreditReply reply = captor.getValue();
        assertThat(reply.transactionId()).isEqualTo("TXN1");
        assertThat(reply.accountNumber()).isEqualTo("ACC1");
        assertThat(reply.score()).isEqualTo(720);
        assertThat(reply.decision()).isEqualTo("APPROVED");
        assertThat(reply.reason()).isEqualTo("ok");
        assertThat(reply.scoredAt()).isNotNull();
    }
}
