package com.example.backspark.integration_test;

import com.example.backspark.Util;
import com.example.backspark.model.SocksTransaction;
import com.example.backspark.service.SocksService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UpdateTest {
    private final SocksService socksService;
    private final EntityManager em;

    @Test
    public void validUpdateTest() {
        var id = 1L;
        String color = "red", newColor = "blue";
        int cottonPart = 10, newCottonPart = 20;
        int quantity = 15, newQuantity = 10;

        Util.insertToDbNative(em, new SocksTransaction(id, color, cottonPart, quantity));

        socksService.update(id, newColor, newCottonPart, newQuantity);

        var dbResult = em
                .createQuery("select st from SocksTransaction st where st.id = id", SocksTransaction.class)
                .getSingleResult();

        Assertions.assertEquals(dbResult.getColor(), newColor);
        Assertions.assertEquals(dbResult.getCottonPart(), newCottonPart);
        Assertions.assertEquals(dbResult.getQuantity(), newQuantity);
    }

    @Test
    public void validUpdateDecreaseTest() {
        var color = "red";
        int cottonPart = 30, newQuantity = -10;

        Util.insertToDbNative(em, new SocksTransaction(1L, color, cottonPart, 5));
        Util.insertToDbNative(em, new SocksTransaction(2L, color, cottonPart, 5));
        Util.insertToDbNative(em, new SocksTransaction(3L, color, cottonPart, -1));

        socksService.update(3L, color, cottonPart, -10);

        var dbResult = em
                .createQuery("select st from SocksTransaction st where st.id = 3", SocksTransaction.class)
                .getSingleResult();

        Assertions.assertEquals(dbResult.getColor(), color);
        Assertions.assertEquals(dbResult.getCottonPart(), cottonPart);
        Assertions.assertEquals(dbResult.getQuantity(), newQuantity);
    }

    @Test
    public void invalidUpdateOutOfStockTest() {
        var color = "red";
        int cottonPart = 30, newQuantity = -11;

        Util.insertToDbNative(em, new SocksTransaction(1L, color, cottonPart, 5));
        Util.insertToDbNative(em, new SocksTransaction(2L, color, cottonPart, 5));
        Util.insertToDbNative(em, new SocksTransaction(3L, color, cottonPart, -1));

        Assertions.assertThrows(
                RuntimeException.class,
                () -> socksService.update(3L, color, cottonPart, newQuantity),
                "Not enough socks in stock");
    }
}
