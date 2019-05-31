package com.example.datatables.service;

import com.example.datatables.persistence.entities.AbstractEntity;
import com.example.datatables.persistence.repository.AbstractJpaRepository;
import com.example.datatables.utils.DateUtil;
import com.example.datatables.utils.EntityConstUtil;

import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.Search;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class RepositoryProcessService<E extends AbstractEntity> {

    public void generateStartEndTime(Column column, AbstractJpaRepository<E> repository) {
        if (Objects.equals(column.getData(), EntityConstUtil.CREATE_TIME)) {
            Date start = repository.findMinCreateTime();
            Date end = repository.findMaxCreateTime();
            column.setSearch(new Search(DateUtil.generateDateRangeModel(start, end), false));
        }
        if (Objects.equals(column.getData(), EntityConstUtil.UPDATE_TIME)) {
            Date start = repository.findMinUpdateTime();
            Date end = repository.findMaxUpdateTime();
            column.setSearch(new Search(DateUtil.generateDateRangeModel(start, end), false));
        }
    }
}
