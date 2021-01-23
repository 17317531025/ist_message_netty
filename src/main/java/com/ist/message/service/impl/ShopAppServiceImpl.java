package com.ist.message.service.impl;

import com.ist.message.dao.ShopAppMapper;
import com.ist.message.domain.ShopApp;
import com.ist.message.domain.ShopAppExample;
import com.ist.message.service.ShopAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "shopAppService")
public class ShopAppServiceImpl extends BaseServiceImpl implements ShopAppService {
    @Autowired
    private ShopAppMapper shopAppMapper;

    @Override
    public ShopApp queryShopApp(String appId) {
        ShopAppExample example = new ShopAppExample();
        ShopAppExample.Criteria criteria = example.createCriteria();
        criteria.andAppIdEqualTo(appId);
        List<ShopApp> shopApps = shopAppMapper.selectByExample(example);
        if (shopApps!=null && shopApps.size()>0){
            return shopApps.get(0);
        }
        return null;
    }
}
