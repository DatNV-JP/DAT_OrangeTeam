package com.orange.controller.admin;

import com.orange.common.payload.Result;
import com.orange.domain.dto.CategoryDTO;
import com.orange.services.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final ICategoryService categoryService;

    //coment
    @PostMapping("/create")
    public Result<?> createC(@RequestBody CategoryDTO dto){
        CategoryDTO categoryDTO = categoryService.create(dto);
        return Result.result(HttpStatus.OK.value(), "Tạo mới category thành công!", categoryDTO);
    }

    @GetMapping("/find-by-id")
    public Result<?> getById( @RequestParam Long id ){
        CategoryDTO result = categoryService.findById(id);
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu category thành công!", result);
    }
    @PutMapping("/update")
    public Result<?> update(@RequestBody CategoryDTO dto) {
        CategoryDTO result = this.categoryService.findById(dto.getId());
        if (result == null) {
            return Result.result(HttpStatus.NOT_FOUND.value(),"Cant find id!",null);
        }
        if (dto.getStatus() == null) {
            dto.setStatus(true);
        }
        CategoryDTO categoryDTO = this.categoryService.update(dto);
        return Result.result(HttpStatus.OK.value(), "Update category thành công!", categoryDTO);
    }
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable("id") Long id) {
        CategoryDTO categoryDTO = this.categoryService.findById(id);
        if (categoryDTO == null) {
            return Result.result(HttpStatus.NOT_FOUND.value(),"Cant find id!",categoryDTO.getId());
        }
        this.categoryService.delete(categoryDTO);
        return Result.result(HttpStatus.OK.value(), "Xóa thành công", categoryDTO);
    }
}
