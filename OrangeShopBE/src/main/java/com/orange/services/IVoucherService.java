package com.orange.services;

import com.orange.common.payload.Page;
import com.orange.common.payload.Result;
import com.orange.domain.dto.VoucherDto;
import com.orange.domain.model.GroupCustomer;
import com.orange.payload.request.CheckVoucherRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IVoucherService extends BaseService<VoucherDto, Long>{

    Result<?> checkVoucher(CheckVoucherRequest checkVoucherRequest);

    VoucherDto delete(Long id);

    Page<?> fillAllForAdmin(Long status, Pageable pageable);

    List<GroupCustomer> fillAllGroupCustomer();

    Page<?> fillAllUserByGroupForAdmin(Long groupId, Pageable pageable);

    List<VoucherDto> adminFindVoucherForUser(Long userId);

    Page<?> search(Integer status, String keyWord, Pageable pageable);

    void indexData() throws InterruptedException;

    VoucherDto reactivate(Long id);
}
