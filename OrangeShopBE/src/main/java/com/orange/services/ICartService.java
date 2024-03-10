package com.orange.services;

import com.orange.domain.dto.CartDTO;

import java.util.List;

public interface ICartService {

    CartDTO add(CartDTO cartDTO);

    abstract void clear();

    CartDTO update(Long id, Integer quantity);

    List<CartDTO> getAll(String key);

    CartDTO findCartItemById(Long id);

    String deleteCartItemByIds(String... ids);
}
