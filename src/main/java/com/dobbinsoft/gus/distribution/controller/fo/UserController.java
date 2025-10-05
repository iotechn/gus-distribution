package com.dobbinsoft.gus.distribution.controller.fo;

import com.dobbinsoft.gus.distribution.data.vo.user.UserVO;
import com.dobbinsoft.gus.distribution.service.UserService;
import com.dobbinsoft.gus.web.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fo/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/current")
    public R<UserVO> current() {
        return R.success(userService.current());
    }

}
