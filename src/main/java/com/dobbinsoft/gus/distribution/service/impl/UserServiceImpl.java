package com.dobbinsoft.gus.distribution.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dobbinsoft.gus.common.utils.context.GenericRequestContextHolder;
import com.dobbinsoft.gus.common.utils.context.bo.IdentityContext;
import com.dobbinsoft.gus.distribution.client.configcenter.ConfigCenterClient;
import com.dobbinsoft.gus.distribution.client.configcenter.vo.ConfigContentVO;
import com.dobbinsoft.gus.distribution.client.oauth.WechatMpAuthenticator;
import com.dobbinsoft.gus.distribution.data.dto.user.UserSearchDTO;
import com.dobbinsoft.gus.distribution.data.dto.user.UserStatusUpdateDTO;
import com.dobbinsoft.gus.distribution.data.dto.user.UserWechatMpLoginDTO;
import com.dobbinsoft.gus.distribution.data.enums.StatusType;
import com.dobbinsoft.gus.distribution.data.enums.UserSrcType;
import com.dobbinsoft.gus.distribution.data.exception.DistributionErrorCode;
import com.dobbinsoft.gus.distribution.data.po.UserPO;
import com.dobbinsoft.gus.distribution.data.po.UserSocialPO;
import com.dobbinsoft.gus.distribution.data.properties.DistributionProperties;
import com.dobbinsoft.gus.distribution.data.util.JwtUtils;
import com.dobbinsoft.gus.distribution.data.vo.user.AuthResultVO;
import com.dobbinsoft.gus.distribution.data.vo.user.UserVO;
import com.dobbinsoft.gus.distribution.data.vo.user.UserWechatMpLoginVO;
import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.StringUtils;
import com.dobbinsoft.gus.distribution.mapper.UserMapper;
import com.dobbinsoft.gus.distribution.mapper.UserSocialMapper;
import com.dobbinsoft.gus.distribution.service.UserService;
import com.dobbinsoft.gus.distribution.utils.UuidWorker;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import com.dobbinsoft.gus.web.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private WechatMpAuthenticator wechatMpAuthenticator;
    @Autowired
    private DistributionProperties distributionProperties;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserSocialMapper userSocialMapper;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private ConfigCenterClient configCenterClient;

    @Override
    public UserVO current() {
        IdentityContext identityContext = GenericRequestContextHolder.getIdentityContext().get();
        String userId = identityContext.getUserId();
        // 查询用户基本信息
        UserPO userPO = userMapper.selectById(userId);
        if (userPO == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE);
        }
        
        // 查询用户社交账号关联信息
        List<UserSocialPO> userSocialPOs = userSocialMapper.selectList(
                new LambdaQueryWrapper<UserSocialPO>()
                        .eq(UserSocialPO::getUserId, userId)
        );
        
        // 转换为VO对象
        return convertToUserVO(userPO, userSocialPOs);
    }

    @Override
    @Transactional
    public AuthResultVO wechatMiniLogin(UserWechatMpLoginDTO userWechatMpLoginDTO, String tenantId) {
        // 1. 获取配置
        ConfigContentVO configContentVO = configCenterClient.getBrandAllConfigContent();

        // 2. 调用微信API获取用户信息
        UserWechatMpLoginVO userWechatMpLoginVO = wechatMpAuthenticator.authenticate(
                configContentVO.getSecret().getWechatMiniAppId(),
                configContentVO.getSecret().getWechatMiniSecret(),
                userWechatMpLoginDTO.getCode(), 
                "authorization_code");
        if (userWechatMpLoginVO.getErrcode() != null && userWechatMpLoginVO.getErrcode() != 0) {
            throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, userWechatMpLoginVO.getErrmsg());
        }
        
        // 3. 查找已有的社交账号关联
        UserSocialPO userSocialPO = userSocialMapper.selectOne(new LambdaQueryWrapper<UserSocialPO>()
                .eq(UserSocialPO::getSrc, UserSrcType.DISTRIBUTION_WECHAT_WEB)
                .eq(UserSocialPO::getSocialId, userWechatMpLoginVO.getOpenid()));
        
        String userId;
        if (userSocialPO == null) {
            // 4. 创建新用户和社交账号关联
            userId = createUserWithSocial(userWechatMpLoginVO);
        } else {
            // 5. 使用已有用户ID
            userId = userSocialPO.getUserId();
        }
        
        // 6. 更新用户最后登录信息
        updateUserLastLogin(userId);
        
        // 7. 构建并返回会话信息
        // JWT payload
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("tid", tenantId);
        claims.put("src", UserSrcType.DISTRIBUTION_WECHAT_WEB.getCode());
        String token = jwtUtils.generateToken(distributionProperties.getIss(), claims, distributionProperties.getExpiresIn(), distributionProperties.getPrivateKey());
        return generateAuthResult(token, claims);
    }
    
    /**
     * 创建用户和社交账号关联
     */
    private String createUserWithSocial(UserWechatMpLoginVO wechatLoginVO) {
        // 创建新用户
        UserPO newUser = new UserPO();
        String userId = UuidWorker.nextId();
        newUser.setId(userId);
        newUser.setStatus(StatusType.ENABLED.getCode());
        newUser.setNickname("微信用户"); // 默认昵称
        newUser.setLastLoginTime(LocalDateTime.now());
        userMapper.insert(newUser);
        
        // 创建社交账号关联
        UserSocialPO userSocialPO = new UserSocialPO();
        userSocialPO.setUserId(userId);
        userSocialPO.setSrc(UserSrcType.DISTRIBUTION_WECHAT_WEB.getCode());
        userSocialPO.setSocialId(wechatLoginVO.getOpenid());
        userSocialMapper.insert(userSocialPO);
        
        return userId;
    }
    
    /**
     * 更新用户最后登录信息
     */
    private void updateUserLastLogin(String userId) {
        UserPO userUpdate = new UserPO();
        userUpdate.setId(userId);
        userUpdate.setLastLoginTime(LocalDateTime.now());
        // 这里可以根据需要设置IP地址，从请求上下文中获取
        GenericRequestContextHolder.getTraceContext().ifPresent(traceContext -> {
            userUpdate.setLastLoginIp(traceContext.getIp());
        });
        userMapper.updateById(userUpdate);
    }

    /**
     * 生成包含accessToken和refreshToken的认证结果
     */
    private AuthResultVO generateAuthResult(String accessToken, Map<String, Object> claims) {
        String privateKey = distributionProperties.getPrivateKey();

        // 生成refreshToken，包含用户ID和类型标识
        Map<String, Object> refreshClaims = new HashMap<>();
        refreshClaims.put("uid", claims.get("uid"));
        refreshClaims.put("type", "refresh");
        String refreshToken = jwtUtils.generateToken(distributionProperties.getIss(), refreshClaims, distributionProperties.getRefreshExpiresIn(), privateKey);

        AuthResultVO result = new AuthResultVO();
        result.setAccessToken(accessToken);
        result.setRefreshToken(refreshToken);
        result.setTokenType("Bearer");
        result.setExpiresIn(distributionProperties.getExpiresIn());
        result.setRefreshExpiresIn(distributionProperties.getRefreshExpiresIn());

        return result;
    }
    
    /**
     * 将UserPO和UserSocialPO列表转换为UserVO对象
     */
    private UserVO convertToUserVO(UserPO userPO, List<UserSocialPO> userSocialPOs) {
        UserVO userVO = new UserVO();
        
        // 复制基本用户信息
        userVO.setId(userPO.getId());
        userVO.setStatus(userPO.getStatus());
        userVO.setNickname(userPO.getNickname());
        userVO.setAvatar(userPO.getAvatar());
        userVO.setGender(userPO.getGender());
        userVO.setBirthday(userPO.getBirthday());
        userVO.setLastLoginTime(userPO.getLastLoginTime());
        userVO.setLastLoginIp(userPO.getLastLoginIp());
        userVO.setCreatedTime(userPO.getCreatedTime());
        userVO.setCreatedBy(userPO.getCreatedBy());
        userVO.setModifiedTime(userPO.getModifiedTime());
        userVO.setModifiedBy(userPO.getModifiedBy());
        
        // 转换社交账号信息
        if (userSocialPOs != null && !userSocialPOs.isEmpty()) {
            List<UserVO.Social> socials = userSocialPOs.stream()
                    .map(socialPO -> {
                        UserVO.Social social = new UserVO.Social();
                        social.setSocialId(socialPO.getSocialId());
                        social.setSrc(socialPO.getSrc());
                        return social;
                    })
                    .collect(java.util.stream.Collectors.toList());
            userVO.setSocials(socials);
        }
        
        return userVO;
    }

    @Override
    public PageResult<UserVO> page(UserSearchDTO searchDTO) {
        Page<UserPO> page = new Page<>(searchDTO.getPageNum(), searchDTO.getPageSize());
        
        LambdaQueryWrapper<UserPO> queryWrapper = new LambdaQueryWrapper<>();
        
        // 用户昵称查询
        if (StringUtils.hasText(searchDTO.getKeyword())) {
            queryWrapper.like(UserPO::getNickname, searchDTO.getKeyword());
        }
        
        // 用户状态查询
        if (searchDTO.getStatus() != null) {
            queryWrapper.eq(UserPO::getStatus, searchDTO.getStatus());
        }
        
        // 性别查询
        if (searchDTO.getGender() != null) {
            queryWrapper.eq(UserPO::getGender, searchDTO.getGender());
        }
        
        // 创建时间范围查询
        if (searchDTO.getCreateTimeStart() != null) {
            queryWrapper.ge(UserPO::getCreatedTime, searchDTO.getCreateTimeStart());
        }
        if (searchDTO.getCreateTimeEnd() != null) {
            queryWrapper.le(UserPO::getCreatedTime, searchDTO.getCreateTimeEnd());
        }

        // 按创建时间倒序排列
        queryWrapper.orderByDesc(UserPO::getCreatedTime);
        
        Page<UserPO> userPage = userMapper.selectPage(page, queryWrapper);
        
        // 转换为VO列表
        List<UserVO> userVOList = userPage.getRecords().stream()
                .map(userPO -> {
                    // 查询用户社交账号关联信息
                    List<UserSocialPO> userSocialPOs = userSocialMapper.selectList(
                            new LambdaQueryWrapper<UserSocialPO>()
                                    .eq(UserSocialPO::getUserId, userPO.getId())
                    );
                    return convertToUserVO(userPO, userSocialPOs);
                })
                .collect(java.util.stream.Collectors.toList());
        
        return PageResult.<UserVO>builder()
                .totalCount(userPage.getTotal())
                .totalPages(userPage.getPages())
                .pageNumber((int) userPage.getCurrent())
                .pageSize((int) userPage.getSize())
                .hasMore(userPage.hasNext())
                .data(userVOList)
                .build();
    }

    @Override
    public void update(UserStatusUpdateDTO updateDTO) {
        // 验证用户是否存在
        UserPO existingUser = userMapper.selectById(updateDTO.getId());
        if (existingUser == null) {
            throw new ServiceException(BasicErrorCode.NO_RESOURCE, "用户不存在");
        }
        
        // 验证状态值是否有效
        if (updateDTO.getStatus() != StatusType.ENABLED.getCode() && 
            updateDTO.getStatus() != StatusType.DISABLED.getCode()) {
            throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, "无效的用户状态");
        }
        
        // 更新用户状态
        UserPO updateUser = new UserPO();
        updateUser.setId(updateDTO.getId());
        updateUser.setStatus(updateDTO.getStatus());
        
        int result = userMapper.updateById(updateUser);
        if (result <= 0) {
            throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, "更新用户状态失败");
        }
    }
}
