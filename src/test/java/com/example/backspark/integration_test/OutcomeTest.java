package com.example.backspark.integration_test;

import com.example.backspark.Util;
import com.example.backspark.model.SocksTransaction;
import com.example.backspark.service.SocksService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OutcomeTest {

    private final SocksService socksService;
    private final EntityManager em;

    @ParameterizedTest
    @CsvSource({"red,10,-10", "red,0,-1", "red,1,-1"})
    public void testValidOutcome(String color, int cottonPart, int quantity) {

        Util.insertToDbNative(em, new SocksTransaction(0L, color, cottonPart, quantity * -1));

        Assertions.assertDoesNotThrow(() -> socksService.doOutcome(color, cottonPart, quantity));

        TypedQuery<SocksTransaction> query = em.createQuery("select st from SocksTransaction st order by st.id", SocksTransaction.class);
        var dbResult = query.getResultList();

        Assertions.assertEquals(dbResult.size(), 2);
        Assertions.assertEquals(dbResult.get(1).getColor(), color);
        Assertions.assertEquals(dbResult.get(1).getCottonPart(), cottonPart);
        Assertions.assertEquals(dbResult.get(1).getQuantity(), quantity);
    }

    @ParameterizedTest
    @CsvSource({"red,30,10"})
    public void testInvalidOutcome(String color, int cottonPart, int quantity) {
        Util.insertToDbNative(em, (new SocksTransaction(0L, color, cottonPart, quantity)));

        Assertions.assertThrows(RuntimeException.class, () ->
                socksService.doOutcome(color, cottonPart, quantity * -1 - 1), "Not enough socks in stock");
        Assertions.assertThrows(RuntimeException.class, () ->
                socksService.doOutcome(color, cottonPart + 5, -1), "Not enough socks in stock");
    }
}