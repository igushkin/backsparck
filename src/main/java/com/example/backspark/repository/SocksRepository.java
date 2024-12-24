package com.example.backspark.repository;

import com.example.backspark.model.ISocks;
import com.example.backspark.model.SocksTransaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocksRepository extends JpaRepository<SocksTransaction, Long> {

    @Query("select sum(st.quantity) " +
            "from SocksTransaction st " +
            "where st.color = ?1 and st.cottonPart >= ?2 and st.cottonPart <= ?3")
    Optional<Integer> countByColorAndCottonPart(String color, Integer minCottonPart, Integer maxCottonPart);

    default Optional<Integer> countByColorAndCottonPart(String color, Integer cottonPart) {
        return countByColorAndCottonPart(color, cottonPart, cottonPart);
    }

    @Query("select st.color as color, st.cottonPart as cottonPart, sum(st.quantity) as count " +
            "from SocksTransaction st " +
            "where st.cottonPart >= ?1 and st.cottonPart <= ?2 " +
            "group by st.color, st.cottonPart")
    List<ISocks> findAll(Integer minCottonPart, Integer maxCottonPart, Sort sort);
}