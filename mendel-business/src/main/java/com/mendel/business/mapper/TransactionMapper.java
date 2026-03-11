package com.mendel.business.mapper;

import com.mendel.model.entity.TransactionEntity;
import com.mendel.dto.api.v1.TransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    TransactionDto toDto(TransactionEntity entity);

    @Mapping(target = "id", ignore = true)
    TransactionEntity toEntity(TransactionDto dto);
}
