package com.feiyi.feiyiwenhua.service;

import com.feiyi.feiyiwenhua.entity.Master;

import java.util.List;

public interface MasterService {
    List<Master> findAll();
    Master findById(Long id);
    Master save(Master master);
    void deleteById(Long id);
}