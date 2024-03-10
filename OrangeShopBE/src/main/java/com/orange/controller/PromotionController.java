package com.orange.controller;

import com.orange.exception.GlobalException;
import com.orange.utils.SortUtils;
import com.orange.common.payload.Page;
import com.orange.common.payload.Result;
import com.orange.domain.dto.PromotionDTO;
import com.orange.domain.mapper.IPromotionMapper;
import com.orange.domain.model.Promotion;
import com.orange.exception.EntityIsEmptyException;
import com.orange.exception.EntityNotFoundException;
import com.orange.services.IPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/promotion")
@RequiredArgsConstructor
public class PromotionController {
    private final IPromotionService promotionService;
    private final IPromotionMapper promotionMapper;

    @GetMapping("promotion-list")
    public Result<?> getAllPromotion(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<?> pages = promotionService.fillAll(PageRequest.of(page, size));
        return Result.result(HttpStatus.OK.value(), "Lấy tất cả các khuyến mại thành công!", pages);
    }

    @GetMapping("promotion-list-active")
    public Result<?> getAllPromotionByIsStatusTrue(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<?> pages = promotionService.fillAllByIsStatusTrue(PageRequest.of(page, size));
        return Result.result(HttpStatus.OK.value(), "Lấy tất cả các khuyến mại đang hoạt động thành công!", pages);
    }

    @GetMapping("search")
    public Result<?> search(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String keyWord, @RequestParam(required = false) Integer statusFilter, @RequestParam(defaultValue = "createDate,desc") String[] sort) {
        Page<?> pages = promotionService.search(keyWord, statusFilter, PageRequest.of(page, size, SortUtils.getSort(sort)));
        return Result.result(HttpStatus.OK.value(), "Lấy tất cả các khuyến mại đang hoạt động thành công!", pages);
    }

    @GetMapping("/promotion")
    public Result<?> getPromotionById(@RequestParam(value = "id", defaultValue = "0") Optional<Long> id) {
        if (id.isPresent()) {
            return Result.result(HttpStatus.OK.value(), "Lấy thông tin khuyến mại thành công!", promotionService.findDetail(id.get()));
        } else {
            throw GlobalException.throwException("address.user.id.notfound");
        }
    }

    @PostMapping("/check-exit-promotion")
    public Result<?> getPromotionExit(@RequestBody PromotionDTO dto) {
        Set<Promotion> promotionList = promotionService.checkPromotionList(dto);
        if (promotionList.isEmpty()) {
            return Result.result(HttpStatus.OK.value(), "Không bị trùng thời gian, có thể tạo khuyến mại!", new ArrayList<>());
        } else {
            return Result.result(HttpStatus.ALREADY_REPORTED.value(), "Bị trùng thời gian khuyến mại!", promotionList);
        }
    }


    @PostMapping("/promotion-create")
    public Result<?> createPromotion(@RequestBody PromotionDTO dto) {
        PromotionDTO result = promotionService.create(dto);
        return Result.result(HttpStatus.OK.value(), "Thêm khuyến mại mới thành công!", result);
    }

    @PostMapping("/promotion-create/promotion-duplicate")
    public Result<?> createPromotionAfterCheck(@RequestBody PromotionDTO dto) {
        PromotionDTO result = promotionService.createAfterCheck(dto);
        return Result.result(HttpStatus.OK.value(), "Thêm khuyến mại mới thành công!", result);
    }

    @PutMapping("/promotion-update")
    public Result<?> updatePromotion(@RequestBody PromotionDTO promotionDTO) {
        if (promotionService.findById(promotionDTO.getId()) == null) {
            throw GlobalException.throwException("promotion.error.notfound");
        }
        PromotionDTO result = promotionService.update(promotionDTO);
        return Result.result(HttpStatus.OK.value(), "Cập nhập khuyến mại thành công!", result);
    }

    @DeleteMapping("/promotion-delete")
    public Result<?> deleteProduct(@RequestParam(value = "id") Optional<Promotion> promotion) {
        if (!promotion.isPresent()) {
            throw GlobalException.throwException("promotion.error.notfound");
        }
        PromotionDTO result = promotionService.delete(promotionMapper.toDto(promotion.get()));
        return Result.result(HttpStatus.OK.value(), "Xóa khuyến mại thành công!", result);
    }

    @PutMapping("/promotion-reset")
    public Result<?> resetPromotion(@RequestBody Optional<PromotionDTO> promotionDTO) {
        if (promotionDTO.isPresent()) {
            PromotionDTO result = promotionService.resetPromotion(promotionDTO.get());
            return Result.result(HttpStatus.OK.value(), "Cập nhập khuyến mại thành công!", result);
        } else {
            throw GlobalException.throwException("address.user.id.notfound");
        }
    }

    @GetMapping("/reindex")
    public Result<?> reindexProduct() throws InterruptedException {
        promotionService.indexData();
        return Result.success();
    }
}
