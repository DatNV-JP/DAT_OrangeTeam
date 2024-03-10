package com.orange.controller.user;

import com.orange.utils.AccountUtils;
import com.orange.common.payload.Result;
import com.orange.domain.dto.UserDTO;
import com.orange.domain.model.User;
import com.orange.repositories.IUserRepository;
import com.orange.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final IUserRepository userRepository;
    private final AccountUtils accountUtils;

    @GetMapping("")
    private Result<?> getAllUser(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(defaultValue = "true") Boolean status){
        return Result.result(HttpStatus.OK.value(),"Trả Về Thành Công",userService.fillAllByStt(status,PageRequest.of(page, size)));
    }

    @PostMapping("/create")
    private Result<?> create(@RequestBody UserDTO dto){
        if(dto.getUsername().trim().equalsIgnoreCase("anonymoususer")){ return Result.result(HttpStatus.METHOD_FAILURE.value(),"Username đã tồn tại",dto);}
        if (userRepository.existsByUsername(dto.getUsername())) { return Result.result(HttpStatus.METHOD_FAILURE.value(),"Username đã tồn tại",dto);}
        if (userRepository.existsByEmail(dto.getEmail()))       { return Result.result(HttpStatus.METHOD_FAILURE.value(),"Email đã tồn tại",dto);}
        return Result.result(HttpStatus.OK.value(),"Thêm Mới Thành Công",userService.create(dto));
    }

    @GetMapping("/find-by-id/{id}")
    public Result<?> findById(@PathVariable Long id) {
        return Result.result(HttpStatus.OK.value(),"Tìm kiếm thành công",userService.findById(id));
    }

    @PutMapping("/change-password")
    public Result<?> changPass(@RequestBody UserDTO dto) {
        User result = userService.changePass(dto.getUsername(),dto.getPasswordHash());
        return Result.result(HttpStatus.OK.value(),"Đổi mật khẩu thành công",result);
    }

    @GetMapping("forgot-pass")
    public Result<?> forgotPass(@RequestParam String userName) {
       userService.forGotPassWord(userName);
       return Result.result(HttpStatus.OK.value(),"Quên mật khẩu đã chạy thành công",null);
    }

    @GetMapping("confirm-otp")
    public Result<?> confirmOTP(@RequestParam String userName,@RequestParam String OTP){
        Boolean result = userService.checkOTP(userName,OTP);
        return Result.result(HttpStatus.OK.value(),"Đã xác thưc thành công",result);
    }
    @PutMapping("update-user")
    public Result<?> updateUser (@RequestBody UserDTO dto) {
        if (userRepository.existsByEmailNotUseUsernameLogin(dto.getEmail(),accountUtils.getUser().getUsername())){
            return Result.result(HttpStatus.METHOD_FAILURE.value(),"Email đã tồn tại trong hệ thống",dto);
        }
        return Result.result(HttpStatus.OK.value(),"Update thành công",userService.update(dto));
    }
}
