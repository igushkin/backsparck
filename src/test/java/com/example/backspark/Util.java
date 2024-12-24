package com.example.backspark;

import com.example.backspark.model.SocksTransaction;
import jakarta.persistence.EntityManager;

public class Util {
    public static void insertToDbNative(EntityManager em, SocksTransaction st) {
        final var query = "insert into socks_transaction (cotton_part, quantity, id, color) values (%d,%d,%d,'%s')";

        em.createNativeQuery(
                String.format(query, st.getCottonPart(), st.getQuantity(), st.getId(), st.getColor())
        ).executeUpdate();
    }
}
