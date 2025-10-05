package com.dobbinsoft.gus.distribution.service;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.data.dto.user.UserSearchDTO;
import com.dobbinsoft.gus.distribution.data.dto.user.UserStatusUpdateDTO;
import com.dobbinsoft.gus.distribution.data.dto.user.UserWechatMpLoginDTO;
import com.dobbinsoft.gus.distribution.data.vo.user.AuthResultVO;
import com.dobbinsoft.gus.distribution.data.vo.user.UserVO;

public interface UserService {

    UserVO current();

    AuthResultVO wechatMiniLogin(UserWechatMpLoginDTO userWechatMpLoginDTO, String tenantId);

    /**
     * 分页查询用户列表
     */
    PageResult<UserVO> page(UserSearchDTO searchDTO);

    /**
     * 更新用户信息
     */
    void update(UserStatusUpdateDTO updateDTO);

}
