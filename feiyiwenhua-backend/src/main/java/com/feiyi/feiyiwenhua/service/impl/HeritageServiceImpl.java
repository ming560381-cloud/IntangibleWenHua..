package com.feiyi.feiyiwenhua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feiyi.feiyiwenhua.entity.Heritage;
import com.feiyi.feiyiwenhua.repository.HeritageRepository;
import com.feiyi.feiyiwenhua.service.HeritageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class HeritageServiceImpl implements HeritageService {

    @Autowired
    private HeritageRepository heritageRepository;

    @Override
    public List<Heritage> findAll() {
        return heritageRepository.selectList(null);
    }

    @Override
    public Heritage findById(Long id) {
        return heritageRepository.selectById(id);
    }

    @Override
    public List<Heritage> findByCategoryId(Long categoryId) {
        LambdaQueryWrapper<Heritage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Heritage::getCategoryId, categoryId);
        return heritageRepository.selectList(queryWrapper);
    }

    @Override
    public List<Heritage> findByMasterId(Long masterId) {
        LambdaQueryWrapper<Heritage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Heritage::getMasterId, masterId);
        return heritageRepository.selectList(queryWrapper);
    }

    @Override
    public List<Heritage> findByLevel(Integer level) {
        LambdaQueryWrapper<Heritage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Heritage::getLevel, level);
        return heritageRepository.selectList(queryWrapper);
    }

    @Override
    public Heritage save(Heritage heritage) {
        Date now = new Date();
        if (heritage.getId() != null) {
            heritage.setUpdateTime(now);
            heritageRepository.updateById(heritage);
        } else {
            heritage.setCreateTime(now);
            heritage.setUpdateTime(now);
            heritageRepository.insert(heritage);
        }
        return heritage;
    }

    @Override
    public void deleteById(Long id) {
        heritageRepository.deleteById(id);
    }
}
