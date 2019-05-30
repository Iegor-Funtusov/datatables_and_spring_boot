package com.example.datatables.persistence.repository;

import com.example.datatables.persistence.entities.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Date;

@NoRepositoryBean
public interface AbstractJpaRepository<E extends AbstractEntity> extends JpaRepository<E, Long> {

    @Query("select min (ae.createTime) from #{#entityName} ae")
    Date findMinCreateTime();

    @Query("select max (ae.createTime) from #{#entityName} ae")
    Date findMaxCreateTime();
}
