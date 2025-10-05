package com.dobbinsoft.gus.distribution.service;

import com.dobbinsoft.gus.distribution.data.dto.social.SocialAuthenticatorCreateDTO;
import com.dobbinsoft.gus.distribution.data.dto.social.SocialAuthenticatorUpdateDTO;
import com.dobbinsoft.gus.distribution.data.vo.social.SocialAuthenticatorVO;

import java.util.List;

public interface SocialAuthenticatorService {

    /**
     * Create social authenticator
     * Only one authenticator can exist for each social media type
     */
    SocialAuthenticatorVO create(SocialAuthenticatorCreateDTO createDTO);

    /**
     * Update social authenticator
     */
    SocialAuthenticatorVO update(SocialAuthenticatorUpdateDTO updateDTO);

    /**
     * Delete social authenticator
     */
    void delete(String id);

    /**
     * Get social authenticator by ID
     */
    SocialAuthenticatorVO getById(String id);

    /**
     * Get all social authenticators
     */
    List<SocialAuthenticatorVO> getAll();
} 