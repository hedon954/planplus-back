package com.hedon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedon.service.IDidaUserService;
import common.code.ResultCode;
import common.entity.DidaUser;
import common.exception.ServiceException;
import common.mapper.DidaUserMapper;
import common.util.PhoneFormatCheckUtils;
import common.vo.common.ResponseBean;
import common.vo.common.UserBaiduInfo;
import common.vo.response.DidaUserResponseVo;
import lombok.val;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-23
 */
@Service
public class DidaUserServiceImpl extends ServiceImpl<DidaUserMapper, DidaUser> implements IDidaUserService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    DidaUserMapper didaUserMapper;

    /**
     * 根据ID查询用户信息
     *
     * @author hedon
     * @create 2020.10.23
     * @param userId
     * @return
     */
    @Override
    public DidaUser getUserById(Integer userId) {
        DidaUser didaUser = didaUserMapper.selectById(userId);
        //若用户为空在，则抛出异常->用户不存在
        if (didaUser == null){
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
        return didaUser;
    }

    /**
     * 修改用户信息
     *
     * @param didaUser
     * @author Ruolin
     * @create 2020.10.29
     */
    @Override
    public void updateUserInfoById(DidaUser didaUser) {
        try{
            Integer updateCount = didaUserMapper.updateById(didaUser);
            if(updateCount!=1){
                throw new ServiceException(ResultCode.DATABASE_ERROR);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new ServiceException(ResultCode.ERROR);
        }
    }


    /**
     * [已废弃]
     *
     * 登录
     *
     * @author Jiahan Wang
     * @create 2020.11.1
     * @param phoneNumber  手机号
     * @param password     密码
     * @return
     */
    @Deprecated
    @Override
    public DidaUserResponseVo login(String phoneNumber, String password) {
        //先判断用户是否存在
        QueryWrapper<DidaUser> phoneQuery = new QueryWrapper<>();
        phoneQuery.eq("user_phone",phoneNumber);
        List<DidaUser> didaUsers = didaUserMapper.selectList(phoneQuery);
        //查不到，说明用户不存在
        if (didaUsers.size() < 1){
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
        //查到了，判断密码是否正确
        DidaUser didaUser = didaUsers.get(0);
        if (!StringUtils.equals(didaUser.getUserPassword(),password)){
            throw new ServiceException(ResultCode.ERROR_PASSWORD);
        }
        DidaUserResponseVo didaUserResponseVo = new DidaUserResponseVo(didaUser);
        return didaUserResponseVo;
    }

    /**
     * @param userId 用户id
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @author Ruolin
     * @create 2020.11.2
     */
    @Override
    public void updatePassword(Integer userId, String oldPwd, String newPwd) {
        //判断新旧密码是否为空
        if(oldPwd==null||newPwd==null) {
            throw new ServiceException(ResultCode.EMPTY_PASSWORD);
        }
        //获取用户信息
        DidaUser didaUser = didaUserMapper.selectById(userId);
        if (didaUser == null){
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
        //检查密码是否正确
        if (!passwordEncoder.matches(oldPwd,didaUser.getUserPassword())){
            throw new ServiceException(ResultCode.ERROR_PASSWORD);
        }
        //加密密码
        newPwd = passwordEncoder.encode(newPwd);
        //更新密码
        didaUser.setUserPassword(newPwd);
        try{
            didaUserMapper.updateById(didaUser);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServiceException(ResultCode.ERROR);
        }
    }

    /**
     * 存储用户百度信息
     * @param userId        用户ID
     * @param userBaiduInfo 里面有 openId 和 sessionKey
     */
    @Override
    public void saveUserBaiduInfo(Integer userId, UserBaiduInfo userBaiduInfo) {
        DidaUser didaUser = didaUserMapper.selectById(userId);
        if (didaUser == null){
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
        didaUser.setUserOpenId(userBaiduInfo.getOpenid());
        didaUser.setUserSessionKey(userBaiduInfo.getSession_key());
        didaUserMapper.updateById(didaUser);
    }

    /**
     * 上传文件
     *
     * @param userId 用户id
     * @param file   文件
     */
    @Override
    public void uploadAvatar(Integer userId, MultipartFile file) throws IOException {
        //创建存储路径
        try{
            Path path = Paths.get("C:\\planplus\\avatar",userId.toString());
            Files.createDirectories(path);
            String originName = org.springframework.util.StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String extension = FilenameUtils.getExtension(originName);
            String fileName = String.format("%s.%s", userId.toString(), extension);
            Path filePath = path.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            DidaUser didaUser = new DidaUser();
            didaUser.setUserId(userId);
            didaUser.setUserAvatarUrl(filePath.toString());
            didaUserMapper.updateById(didaUser);
        }catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.ERROR);
        }
    }

    /**
     *
     * @param userId 用户id
     * @return
     */
    @Override
    public Resource loadAvatar(Integer userId) {
        try {
            //获取图片路径
            Path path = Paths.get(didaUserMapper.selectById(userId).getUserAvatarUrl());
            Path filePath = path.resolve(userId.toString()).normalize();
            //获取图片
            Resource resource = new UrlResource(path.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new ServiceException(ResultCode.ERROR);
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            throw new ServiceException(ResultCode.ERROR);
        }
    }

    /**
     * 通过手机号和密码进行注册
     *
     * @author Jiahan Wang
     * @create 2020.11.26
     * @param phoneNumber
     * @param password
     */
    @Override
    public void registerByPhoneAndPwd(String phoneNumber, String password) {
        //判断手机格式是否正确
        if (!PhoneFormatCheckUtils.isPhoneLegal(phoneNumber)){
            throw new ServiceException(ResultCode.PHONE_FORMAT_ERROR);
        }
        //判断密码是否为空
        if (StringUtils.isBlank(password)) {
            throw new ServiceException(ResultCode.EMPTY_PASSWORD);
        }
        //注册用户
        DidaUser didaUser = new DidaUser();
        didaUser.setUserPassword(phoneNumber);
        didaUser.setUserPassword(passwordEncoder.encode(password));
        didaUserMapper.insert(didaUser);
    }
}
