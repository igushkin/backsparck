package com.example.backspark.integration_test;

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
public class IncomeTest {
    private final SocksService socksService;
    private final EntityManager em;

    @ParameterizedTest
    @CsvSource({"red,10,10", "red,0,1", "red,1,1"})
    public void testValidIncome(String color, int cottonPart, int quantity) {
        Assertions.assertDoesNotThrow(() -> socksService.doIncome(color, cottonPart, quantity));

        TypedQuery<SocksTransaction> query = em.createQuery("select st from SocksTransaction st", SocksTransaction.class);
        var dbResult = query.getResultList();

        Assertions.assertEquals(dbResult.size(), 1);
        Assertions.assertEquals(dbResult.get(0).getColor(), color);
        Assertions.assertEquals(dbResult.get(0).getCottonPart(), cottonPart);
        Assertions.assertEquals(dbResult.get(0).getQuantity(), quantity);
    }
}