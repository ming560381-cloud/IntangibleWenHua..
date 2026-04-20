package com.feiyi.feiyiwenhua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feiyi.feiyiwenhua.entity.Master;
import com.feiyi.feiyiwenhua.repository.MasterRepository;
import com.feiyi.feiyiwenhua.service.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MasterServiceImpl implements MasterService {

    @Autowired
    private MasterRepository masterRepository;

    @Override
    public List<Master> findAll() {
        return masterRepository.selectList(null);
    }

    @Override
    public Master findById(Long id) {
        return masterRepository.selectById(id);
    }

    @Override
    public Master save(Master master) {
        if (master.getId() != null) {
            masterRepository.updateById(master);
        } else {
            masterRepository.insert(master);
        }
        return master;
    }

    @Override
    public void deleteById(Long id) {
        masterRepository.deleteById(id);
    }

    // 新增方法：根据名称查找传承人
    public Master findByName(String name) {
        LambdaQueryWrapper<Master> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Master::getName, name);
        return masterRepository.selectOne(queryWrapper);
    }
}