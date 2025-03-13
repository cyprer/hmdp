package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.hmdp.utils.RedisConstants.CACHE_TYPELIST_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public Result queryTypeList() {

        //1.从redis中查询
        String shopType =stringRedisTemplate.opsForValue().get(CACHE_TYPELIST_KEY);
        //2.判断是否存在
        if (StrUtil.isNotBlank(shopType)) {
            //3.存在，直接返回
            return Result.ok(JSONUtil.toList(shopType, ShopType.class));
        }

        //4.不存在，根据id查询数据库
        List<ShopType> list = query().orderByAsc("sort").list();
        //5.数据库不存在，返回错误
        if (list == null) {
            return Result.fail("店铺不存在");
        }
        //6.存在，写入redis
        String typeJson = JSONUtil.toJsonStr(list);
        stringRedisTemplate.opsForValue().set(CACHE_TYPELIST_KEY,typeJson);
        //7.返回
        return Result.ok(list);
    }
}
/*String key = RedisConstants.CACHE_TYPELIST_KEY;
String shopType = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(shopType)) {
        return JSONUtil.toList(shopType, ShopType.class);
        }
List<ShopType> list = query().orderByAsc("sort").list();
String typeJson = JSONUtil.toJsonStr(list);
        stringRedisTemplate.opsForValue().set(key, typeJson, RedisConstants.CACHE_TYPELIST_TTL, TimeUnit.MINUTES);
        return list;*/