package com.dobbinsoft.gus.distribution.controller.bo;

import com.dobbinsoft.gus.common.model.vo.PageResult;
import com.dobbinsoft.gus.distribution.data.dto.user.UserSearchDTO;
import com.dobbinsoft.gus.distribution.data.dto.user.UserStatusUpdateDTO;
import com.dobbinsoft.gus.distribution.data.vo.user.UserVO;
import com.dobbinsoft.gus.distribution.service.UserService;
import com.dobbinsoft.gus.web.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台-用户管理", description = "用户的查询、状态管理等操作")
@RestController
@RequestMapping("/bo/users")
@RequiredArgsConstructor
public class BoUserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户列表", description = "根据条件分页查询用户列表")
    @GetMapping
    public R<PageResult<UserVO>> page(
            @Parameter(description = "查询条件", required = true)
            @Valid UserSearchDTO searchDTO) {
        PageResult<UserVO> result = userService.page(searchDTO);
        return R.success(result);
    }

    @Operation(summary = "更新用户信息", description = "更新用户的信息（目前支持状态更新）")
    @PutMapping
    public R<Void> update(
            @Parameter(description = "用户状态更新信息", required = true,
                content = @Content(examples = {
                    @ExampleObject(name = "启用用户", value = """
                        {
                          "id": "user_123456",
                          "status": 1
                        }
                        """),
                    @ExampleObject(name = "禁用用户", value = """
                        {
                          "id": "user_123456",
                          "status": 0
                        }
                        """)
                }))
            @Valid @RequestBody UserStatusUpdateDTO updateDTO) {
        userService.update(updateDTO);
        return R.success();
    }

}
