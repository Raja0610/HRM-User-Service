package com.hrm.project.user_service.assembler;

public interface BaseAssembler <E, T>{

    T toDto(E e);
    E toEntity(T t);
}
