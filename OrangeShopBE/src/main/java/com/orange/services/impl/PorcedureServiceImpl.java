package com.orange.services.impl;

import com.orange.domain.dto.ProductDetailDTO;
import com.orange.domain.dto.VariationOptionDTO;
import com.orange.domain.mapper.IVariationOptionMapper;
import com.orange.domain.model.ModelProcedure.*;
import com.orange.repositories.IVariationOptionRepository;
import com.orange.services.IStoreProcedureService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.sql.DataSource;

@Service
@RequiredArgsConstructor
public class PorcedureServiceImpl implements IStoreProcedureService {
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final IVariationOptionRepository variationOptionRepository;
    private final IVariationOptionMapper mapper;


    @Override
    public OgetCountAllStatus getCountAllStatus(String fromDate, String toDate) {
        String query = "CALL get_count_all_status(?, ?)";
        return jdbcTemplate.queryForObject(query, new Object[]{fromDate, toDate}, (rs, rowNum) -> {
            OgetCountAllStatus countAllStatus = new OgetCountAllStatus();
            countAllStatus.setCompleted(rs.getInt("Completed"));
            countAllStatus.setCancel(rs.getInt("Cancel"));
            countAllStatus.setWaitForPay(rs.getInt("Wait For Pay"));
            countAllStatus.setDeliveryInProgress(rs.getInt("Delivery In Progress"));
            countAllStatus.setWaitForConfirmation(rs.getInt("Wait For Confirmation"));
            countAllStatus.setConfirmed(rs.getInt("Confirmed"));
            return countAllStatus;
        });
    }

    @Override
    public List<OgetTopProductByStt> getTopProducts(Integer status, String fromDate, String toDate, Integer top) {
            String query = "CALL get_top_product_by_stt(?, ?, ?, ?)";
            List<OgetTopProductByStt> topProducts = jdbcTemplate.query(query, new Object[]{fromDate, toDate, top, status},
                    (rs, rowNum) -> {
                        OgetTopProductByStt ogetTopProductByStt = new OgetTopProductByStt();
                        ogetTopProductByStt.setProductDetailsId(rs.getLong("product_detail_id"));
                        ogetTopProductByStt.setSum(rs.getInt("Sum"));
                        return ogetTopProductByStt;
                    });
            return topProducts;
    }

    @Override
    public OgetTotalAmountQuantity getTotalAmountByStatus(String fromDate, String toDate, int status) {
        LocalDate today = LocalDate.now();
        if(fromDate == null){
            fromDate = String.valueOf(today);
        }
        if(toDate == null){
            toDate = String.valueOf(today.atTime(23, 59, 59)).replace("T"," ");
        }
        String sql = "CALL total_amount_by_status(?, ?, ?)";

        return jdbcTemplate.queryForObject(sql, new Object[]{fromDate, toDate,status}, (rs, rowNum) -> {
           OgetTotalAmountQuantity ogetTotalAmountQuantity = new OgetTotalAmountQuantity();
           ogetTotalAmountQuantity.setTotalSold(rs.getDouble("total_sold"));
           ogetTotalAmountQuantity.setQuantity(rs.getInt("quantity"));
            return ogetTotalAmountQuantity;
        });

    }

    @Override
    public List<OGetCountOrders> getCountOrdersByWeek() {
        List<OGetCountOrders> result = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             CallableStatement cs = connection.prepareCall("{call count_orders_by_week()}")) {
            boolean hasResult = cs.execute();
            while (hasResult) {
                try (ResultSet rs = cs.getResultSet()) {
                    while (rs.next()) {
                        OGetCountOrders oGetCountOrders = new OGetCountOrders();

                        oGetCountOrders.setOrderDateThisWeek(rs.getString("order_date_this_week"));
                        oGetCountOrders.setOrderCountThisWeek(rs.getObject("order_count_this_week") == null ? null : rs.getInt("order_count_this_week"));
                        oGetCountOrders.setTotalSoldThisWeek(rs.wasNull() ? null : rs.getDouble("total_sold_this_week"));
                        oGetCountOrders.setCountComplatedThisWeek(rs.getObject("count_complated_this_week") == null ? null : rs.getInt("count_complated_this_week"));
                        oGetCountOrders.setCountCancelThisWeek(rs.getObject("count_cancel_this_week") == null ? null : rs.getInt("count_cancel_this_week"));

                        oGetCountOrders.setOrderDateLastWeek(rs.getString("order_date_last_week"));
                        oGetCountOrders.setOrderCountLastWeek(rs.getInt("order_count_last_week"));
                        oGetCountOrders.setTotalSoldLastWeek(rs.getDouble("total_sold_last_week"));
                        oGetCountOrders.setCountComplatedLastWeek(rs.getInt("count_complated_last_week"));
                        oGetCountOrders.setCountCancelLastWeek(rs.getInt("count_cancel_last_week"));
                        result.add(oGetCountOrders);
                    }
                }
                hasResult = cs.getMoreResults();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<Double> getTotalAmountByMonth() {
        List<Double> result = new ArrayList<>();
        jdbcTemplate.execute("CALL get_total_amount_by_month()", (CallableStatementCallback<Void>) cs -> {
            boolean hasResult = cs.execute();
            while (hasResult) {
                try (ResultSet rs = cs.getResultSet()) {
                    while (rs.next()) {
                        double totalAmount = rs.getDouble("total_amount");
                        result.add(rs.wasNull() ? null : totalAmount);
                    }
                }
                hasResult = cs.getMoreResults();
            }
            return null;
        });
        return result;
    }


    @Override
    public List<ProductDetailDTO> getTopProductDetails(String fromDate, String toDate, Integer top, Integer status) {
        String procedureCall = "CALL get_top_product_by_stt(?, ?, ?, ?)";
        List<ProductDetailDTO> products = jdbcTemplate.query(procedureCall, new Object[]{fromDate, toDate, top, status},
                (rs, rowNum) -> {
                    ProductDetailDTO productDetail = new ProductDetailDTO();
                    productDetail.setId(rs.getLong("id"));
                    productDetail.setCreateBy(rs.getString("create_by"));
                    productDetail.setCreateDate(rs.getDate("create_date"));
                    productDetail.setModifiedBy(rs.getString("modified_by"));
                    productDetail.setModifiedDate(rs.getDate("modified_date"));

                    productDetail.setProductId(rs.getLong("product_id"));
                    productDetail.setQuantity(rs.getInt("quantity"));
                    productDetail.setImages(rs.getString("images"));
                    productDetail.setPriceInput(rs.getDouble("price_input"));
                    productDetail.setPriceDefault(rs.getDouble("price_default"));
                    productDetail.setPriceSale(rs.getDouble("price_sale"));
                    productDetail.setStatus(rs.getBoolean("status"));

                    List<VariationOptionDTO> variationOptionsList = mapper.toDtoList(variationOptionRepository.findAllByProductDetailsId(rs.getLong("id")));
                    productDetail.setVariationOptions(new HashSet<>(variationOptionsList));
                    // set other product properties as needed
                    return productDetail;
                });
        return products;
    }

    @Override
    public OgetOrderByUserID getOrderByUserID(Long id) {
        String query = "CALL get_order_by_user_id(?)";
        return jdbcTemplate.queryForObject(query, new Object[]{id}, (rs, rowNum) -> {
            OgetOrderByUserID orderStatistic = new OgetOrderByUserID();
            orderStatistic.setTotalCompleted(rs.getLong("total_completed"));
            orderStatistic.setTotalCancel(rs.getLong("total_cancelled"));
            orderStatistic.setTotalPaid(rs.getDouble("total_paid"));
            return orderStatistic;
        });
    }


}
