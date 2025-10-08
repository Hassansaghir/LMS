package com.LMS.Controller;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Maplist {

    private final ModelMapper modelMapper;

    public Maplist(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <S, D> List<D> mapList(List<S> source, Class<D> targetClass) {
        return source.stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }
}
