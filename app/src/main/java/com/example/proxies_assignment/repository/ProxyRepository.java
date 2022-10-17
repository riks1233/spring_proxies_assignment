package com.example.proxies_assignment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.proxies_assignment.model.Proxy;

public interface ProxyRepository extends JpaRepository<Proxy, Long> {
    @Query(value = """
                SELECT *
                FROM proxies p
                WHERE
                    p.name LIKE %:name%
                    AND type LIKE %:type%
            """, nativeQuery = true)
    List<Proxy> findAllFilteredByNameAndTypeLike(@Param("name") String name, @Param("type") String type);
}
