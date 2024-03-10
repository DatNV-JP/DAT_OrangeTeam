package com.orange.controller.admin;

import com.orange.exception.GlobalException;
import com.orange.utils.SortUtils;
import com.orange.common.payload.Result;
import com.orange.domain.dto.UserDTO;
import com.orange.exception.EntityIsEmptyException;
import com.orange.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("test/admin/user")
@RequiredArgsConstructor
public class AdminUserController {
    private final IUserService userService;

    @GetMapping("search")
    public Result<?> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username,desc") String[] sort,
            @RequestParam Optional<String> keyWord) {
        if (keyWord.isPresent()) {
            return Result.result(
                    HttpStatus.OK.value(),
                    "lấy dữ liệu thành công",
                    userService.search(keyWord.get(), PageRequest.of(page, size, SortUtils.getSort(sort))));
        } else {
            throw GlobalException.throwException("error.missing.parameter");
        }
    }

    @GetMapping("find-username")
    public Result<?> findUsername(
            @RequestParam Optional<String> keyWord) {
        if (keyWord.isPresent()) {
            return Result.result(
                    HttpStatus.OK.value(),
                    "lấy dữ liệu thành công",
                    userService.findByUsernameOrName(keyWord.get()));
        } else {
            throw GlobalException.throwException("error.missing.parameter");
        }
    }

    @PutMapping("update-user")
    public Result<?> updateUser (@RequestBody UserDTO dto) {
        System.out.println(dto);
        return Result.result(HttpStatus.OK.value(),"Update thành công",userService.update(dto));
    }

    @GetMapping("dis-or-act-user")
    public Result<?> deleteUser (@RequestParam Long id,@RequestParam(defaultValue = "false") Boolean status) {
        return Result.result(HttpStatus.OK.value(),"Xóa thành công",userService.disableOrActiveAccount(id,status));
    }

    @GetMapping("/reindex")
    public Result<?> reindexProduct() throws InterruptedException {
        userService.indexData();
        return Result.success();
    }
}
