package com.orange.services;

import com.orange.common.payload.Page;
import com.orange.domain.dto.PromotionDTO;
import com.orange.domain.model.Product;
import com.orange.domain.model.Promotion;
import com.orange.payload.response.PromotionRespone;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface IPromotionService extends BaseService<PromotionDTO, Long> {

    Set<Promotion> checkPromotionList(PromotionDTO dto);

    PromotionDTO createAfterCheck(PromotionDTO dto);

    Page<?> fillAllByIsStatusTrue(Pageable pageable);

    PromotionRespone findDetail(Long id);

    void startPromotion(Boolean isDate);

    void stopWorkingPromotion(Boolean isDate);

    void stopWorkingPromotionInActive(Boolean isDate);

    Page<?> search(String keyWord, Integer statusFilter, Pageable pageable);

    void indexData() throws InterruptedException;

    PromotionDTO resetPromotion(PromotionDTO promotionDTO);
}
