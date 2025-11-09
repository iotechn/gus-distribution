package com.dobbinsoft.gus.distribution.controller.bo;

import com.dobbinsoft.gus.distribution.data.dto.social.SocialAuthenticatorCreateDTO;
import com.dobbinsoft.gus.distribution.data.dto.social.SocialAuthenticatorUpdateDTO;
import com.dobbinsoft.gus.distribution.data.vo.social.SocialAuthenticatorVO;
import com.dobbinsoft.gus.web.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Backoffice - Social Authenticator Management", description = "Manage social media authenticators for user login")
@RestController
@RequestMapping("/bo/social-authenticator")
public class BoSocialAuthenticatorController {

    @Autowired
    private SocialAuthenticatorService socialAuthenticatorService;

    @Operation(summary = "Create Social Authenticator", description = "Create a new social media authenticator. Only one authenticator can exist for each social media type.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public R<SocialAuthenticatorVO> create(
            @Parameter(description = "Social authenticator creation information", required = true,
                content = @Content(examples = {
                    @ExampleObject(name = "Create WeChat Mini Program Authenticator", value = """
                        {
                          "src": "WECHAT_MINI",
                          "appId": "wx1234567890abcdef",
                          "appSecret": "abcdef1234567890abcdef1234567890"
                        }
                        """),
                    @ExampleObject(name = "Create Password Authenticator", value = """
                        {
                          "src": "PASSWORD",
                          "appId": "password_auth",
                          "appSecret": "password_secret_key"
                        }
                        """)
                }))
            @Valid @RequestBody SocialAuthenticatorCreateDTO createDTO) {
        SocialAuthenticatorVO authenticatorVO = socialAuthenticatorService.create(createDTO);
        return R.success(authenticatorVO);
    }

    @Operation(summary = "Update Social Authenticator", description = "Update an existing social authenticator by ID")
    @PutMapping("/{id}")
    public R<SocialAuthenticatorVO> update(
            @Parameter(description = "Social authenticator ID", required = true, example = "auth_123456")
            @PathVariable String id,
            @Parameter(description = "Social authenticator update information", required = true)
            @Valid @RequestBody SocialAuthenticatorUpdateDTO updateDTO) {
        updateDTO.setId(id);
        SocialAuthenticatorVO authenticatorVO = socialAuthenticatorService.update(updateDTO);
        return R.success(authenticatorVO);
    }

    @Operation(summary = "Delete Social Authenticator", description = "Delete a social authenticator by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public R<Void> delete(
            @Parameter(description = "Social authenticator ID", required = true, example = "auth_123456")
            @PathVariable String id) {
        socialAuthenticatorService.delete(id);
        return R.success();
    }

    @Operation(summary = "Get Social Authenticator by ID", description = "Retrieve a social authenticator by its ID")
    @GetMapping("/{id}")
    public R<SocialAuthenticatorVO> getById(
            @Parameter(description = "Social authenticator ID", required = true, example = "auth_123456")
            @PathVariable String id) {
        SocialAuthenticatorVO authenticatorVO = socialAuthenticatorService.getById(id);
        return R.success(authenticatorVO);
    }

    @Operation(summary = "Get All Social Authenticators", description = "Retrieve all social authenticators")
    @GetMapping
    public R<List<SocialAuthenticatorVO>> getAll() {
        List<SocialAuthenticatorVO> authenticatorVOList = socialAuthenticatorService.getAll();
        return R.success(authenticatorVOList);
    }
}
