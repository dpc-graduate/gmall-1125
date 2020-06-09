package com.atguigu.gmall.ums.service.impl;

import com.atguigu.gmall.ums.config.UserException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.ums.mapper.UserMapper;
import com.atguigu.gmall.ums.entity.UserEntity;
import com.atguigu.gmall.ums.service.UserService;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<UserEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<UserEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public Boolean checkData(String data, Integer type) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        switch (type) {
            case 1:
                wrapper.eq("username", data);
                break;
            case 2:
                wrapper.eq("phone", data);
                break;
            case 3:
                wrapper.eq("email", data);
                break;
            default:
                return null;
        }
        return this.count(wrapper) == 0;
    }

    @Override
    public void register(UserEntity userEntity, String code) {
        String salt = UUID.randomUUID().toString().substring(0, 6);
        userEntity.setSalt(salt);
        //加盐加密
        userEntity.setPassword(DigestUtils.md5Hex(userEntity.getPassword() + salt));
        userEntity.setLevelId(1L);
        userEntity.setSourceType(1);
        userEntity.setIntegration(1000);
        userEntity.setGrowth(1000);
        userEntity.setStatus(1);
        userEntity.setCreateTime(new Date());
        this.save(userEntity);
    }

    @Override
    public UserEntity queryUser(String username, String password) {
        // 1.先根据用户名查询出用户信息
        UserEntity userEntity = this.getOne(new QueryWrapper<UserEntity>().eq("username", username).or().eq("phone", username).or().eq("email", username));
        if (userEntity == null) {
            throw new UserException("用户名输入不合法！！");
        }

        // 2.对用户输入的密码加盐加密
        password = DigestUtils.md5Hex(password + userEntity.getSalt());

        // 3.比较密码是否一致
        if (!StringUtils.equals(password, userEntity.getPassword())){
            throw new UserException("用户输入的密码不合法！！");
        }

        return userEntity;
    }

}