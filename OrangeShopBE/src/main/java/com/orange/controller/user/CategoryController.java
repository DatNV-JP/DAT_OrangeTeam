package com.orange.controller.user;

import com.orange.common.payload.Page;
import com.orange.common.payload.Result;
import com.orange.domain.dto.CategoryDTO;
import com.orange.services.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;
    
    @GetMapping("")
    public Result<?> getAll(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size){
        List<?> pages = this.categoryService.findByStatusTrue();
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu category thành công!", pages);
    }

}
