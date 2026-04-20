package com.feiyi.feiyiwenhua.service;

import com.feiyi.feiyiwenhua.entity.Heritage;

import java.util.List;

public interface HeritageService {
    List<Heritage> findAll();
    Heritage findById(Long id);
    List<Heritage> findByCategoryId(Long categoryId);
    List<Heritage> findByMasterId(Long masterId);
    List<Heritage> findByLevel(Integer level);
    Heritage save(Heritage heritage);
    void deleteById(Long id);
}