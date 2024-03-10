package com.orange.utils;

import com.orange.exception.EntityInvalid;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.ArrayList;
import java.util.List;

public class SortUtils {
    public static Sort getSort(String[] sort) {
        try {
            List<Order> orders = new ArrayList<>();

            if (sort[0].contains(",")) {
                // will sort more than 2 fields
                // sortOrder="field, direction"
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    Order order = new Order(getSortDirection(sort[1]), sort[0]);
                    orders.add(order);
                }
            } else {
                // sort=[field, direction]
                orders.add(new Order(getSortDirection(sort[1]), sort[0]));
            }
            return Sort.by(orders);
        } catch (Exception e) {
            throw new EntityInvalid("Invalid Params Sort");
        }
    }

    private static Sort.Direction getSortDirection(String sort) {
        Sort.Direction dire = sort.contains("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return dire;
    }
}
