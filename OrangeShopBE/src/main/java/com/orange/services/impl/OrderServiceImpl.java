package com.orange.services.impl;

import com.orange.domain.dto.NotificationDTO;
import com.orange.utils.AccountUtils;
import com.orange.utils.StringUtils;
import com.orange.common.payload.Page;
import com.orange.common.template.EmailTemplate;
import com.orange.common.template.EmailTemplateOrder;
import com.orange.domain.dto.OrderDTO;
import com.orange.domain.dto.OrderViewDTO;
import com.orange.domain.mapper.IOrderMapper;
import com.orange.domain.mapper.IOrderViewMapper;
import com.orange.domain.model.*;
import com.orange.domain.model.ghn.GHNItem;
import com.orange.domain.model.ghn.GHNShippingOrder;
import com.orange.exception.*;
import com.orange.payload.request.CancelOrderRequest;
import com.orange.payload.request.ReturnOrderRequest;
import com.orange.payload.request.UpdateOrderStatus;
import com.orange.repositories.*;
import com.orange.services.ICartService;
import com.orange.services.IOrderService;
import com.orange.services.IShippingService;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements IOrderService {

    private final IOrderRepository orderRepository;
    private final IOrderDetailRepository orderDetailRepository;
    private final IOrderMapper orderMapper;
    private final IOrderViewMapper orderViewMapper;
    private final IShippingService shippingService;
    private final IProductDetailRepository productDetailRepository;
    private final ICartService cartService;
    private final IVoucherRepository voucherRepository;
    private final IOrderStatRepository orderStatRepository;
    private final IUserRepository userRepository;
    private final IAddressRepository addressRepository;
    private final RefundService refundService;
    private final AccountUtils accountUtils;
    private final MaillerServiceImpl mailerService;
    private final WebsocketNotificationService wsn;
    @PersistenceUnit
    private EntityManagerFactory emf;


    @Override
    public OrderDTO create(OrderDTO dto, Long userId) {
        dto.setUserId(userId);
        dto.setShippingMethod(new ShippingMethod(2L));
        return createForUser(dto);
    }

    @Override
    public OrderDTO create(OrderDTO dto) {
        Order result = checkAndCreate(dto);
        if (result.getShippingMethod().getId() >= 2) {
            String shippingCode = this.shippingService.createShippingOrder(getGHNShippingOrder(result)).getData().getOrder_code();
            result.setShippingCode(shippingCode);
            result.setConfirmedTime(new Date());
            result.setModifiedDate(new Date());
            result.setModifiedBy(AccountUtils.getUsername());
            result.setOrderStatus(new OrderStatus(EOrderStatus.CONFIRMED.getId()));
            updateQtyProductDetailAfterCreatedOrder(result);
            sendNotificationToCustomer(result);
            return orderMapper.toDto(orderRepository.saveAndFlush(result));
        }
        if (result.getShippingMethod().getId() == 1) {
            result.setCompletedTime(new Date());
            result.setModifiedDate(new Date());
            result.setModifiedBy(AccountUtils.getUsername());
            result.setPayTime(new Date());
            result.setOrderStatus(new OrderStatus(EOrderStatus.COMPLETED.getId()));
            updateQtyProductDetailAfterCreatedOrder(result);
            sendNotificationToCustomer(result);
            return orderMapper.toDto(orderRepository.saveAndFlush(result));
        }
        sendNotificationToCustomer(result);
        return orderMapper.toDto(result);
    }

    @Override
    public OrderDTO createForUser(OrderDTO dto) {
        Order result = checkAndCreate(dto);
        sendNotificationToCustomer(result);
        return orderMapper.toDto(result);
    }

    private Order checkAndCreate(OrderDTO dto) {
        if (dto.getOrderStatus() == null || dto.getOrderStatus().getId() == 0L) {
            dto.setOrderStatus(new OrderStatus(EOrderStatus.WAIT_FOR_CONFIRMATION.getId()));
            dto.setWaitForConfirmationTime(new Date());
        }
        dto.setCreateBy(AccountUtils.getUsername());
        dto.setCreateDate(new Date());
        Order result = orderRepository.saveAndFlush(orderMapper.toEntity(dto));
        Set<OrderDetail> orderDetails = result.getOrderDetails();

        List<Long> productDetailIds = orderDetails.stream().map(OrderDetail::getProductDetail).map(ProductDetail::getId).collect(Collectors.toList());

        Map<Long, ProductDetail> productDetailMap = productDetailRepository.findAllById(productDetailIds)
                .stream()
                .collect(Collectors.toMap(ProductDetail::getId, Function.identity()));

        List<OrderDetail> updatedOrderDetails = orderDetails.stream()
                .map(orderDetail -> {
                    Long productDetailId = orderDetail.getProductDetail().getId();
                    Integer quantity = orderDetail.getQuantity();

                    ProductDetail productDetail = productDetailMap.get(productDetailId);

                    if (productDetail == null) {
                        throw new EntityNotFoundException("Không tìm thấy sản phẩm có ID: " + productDetailId);
                    }

                    if (productDetail.getQuantity() < quantity) {
                        throw new NotEnoughStockException("Sản phẩm đã hết hàng");
                    }

                    orderDetail.setProductDetail(productDetail);
                    orderDetail.setOrder(result);

                    return orderDetail;
                }).collect(Collectors.toList());

        orderDetailRepository.saveAll(updatedOrderDetails);
        result.setOrderDetails(new HashSet<>(updatedOrderDetails));

        // remove shopping cart after create order success
        if (accountUtils.getUserOrAnonymousUser() != null) {
            String[] cartItems = productDetailIds.stream()
                    .map(Object::toString)
                    .toArray(String[]::new);
            cartService.deleteCartItemByIds(cartItems);
        }

        if (result.getVoucher() != null) {
            Optional<Voucher> optionalVoucher = voucherRepository.findById(result.getVoucher().getId());
            if (optionalVoucher.isPresent()) {
                Voucher updateQtyVoucher = optionalVoucher.get();
                int used = updateQtyVoucher.getUsed() + 1;
                if (used > updateQtyVoucher.getUsageLimit())
                    throw new VoucherInvalidException("Số lượt sử dụng voucher đã hết");
                updateQtyVoucher.setUsed(used);
                voucherRepository.save(updateQtyVoucher);
            }
        }
        return result;
    }

    @Override
    public OrderDTO update(OrderDTO dto) {
        Order order = getOrderById(dto.getId());
        if (order.getPaymentType().getId() == 1L) {
            return orderMapper.toDto(orderRepository.save(orderMapper.toEntity(dto)));
        } else {
            throw new OrderUpdateException("Không thể sửa đơn hàng đã thanh toán trả trước");
        }
    }

    @Override
    public OrderDTO delete(OrderDTO dto) {
        orderRepository.delete(orderMapper.toEntity(dto));
        return dto;
    }

    @Override
    public Page<?> fillAll(Pageable pageable) {
        org.springframework.data.domain.Page<Order> result = this.orderRepository.findAllForUser(accountUtils.getUserId(), pageable);
        return getPage(result);
    }

    @Override
    public Page<?> filter(Long uId, Long id, String keyWord, Long statusId, String createDate, Pageable pageable) {
        if (id == null && statusId == null && keyWord == null && createDate == null){
            if (uId != null) {
                org.springframework.data.domain.Page<Order> result = this.orderRepository.findAllForUser(uId, pageable);
                return this.getPage(result);
            }
            org.springframework.data.domain.Page<Order> result = this.orderRepository.findAll(pageable);
            return this.getPage(result);
        }
        LocalDate date = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (!StringUtils.isNullOrEmpty(createDate)) {
            date = LocalDate.parse(createDate, formatter);
        }
        org.springframework.data.domain.Page<Order> result =  this.orderRepository.findfilter(uId, id, keyWord, statusId, date, pageable);
        return this.getPage(result);
    }

    @Override
    public Page<?> fillAllByStatus(Long oStatusId, Pageable pageable) {
        org.springframework.data.domain.Page<Order> result = this.orderRepository.findByOrderStatus_IdAndUser_Id(oStatusId, accountUtils.getUserId(), pageable);
        return getPage(result);
    }

    @Override
    public Page<?> fillAllByStatusForAdmin(Long oStatusId, Pageable pageable) {
        org.springframework.data.domain.Page<Order> result = this.orderRepository.findByOrderStatus_Id(oStatusId, pageable);
        return getPage(result);
    }

    @Override
    public Page<?> fillAllForAdmin(Pageable pageable) {
        org.springframework.data.domain.Page<Order> result = this.orderRepository.findAll(pageable);
        return getPage(result);
    }

    @Override
    public OrderDTO findById(Long id) {
        Order order = getOrderById(id);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDTO findOneByUserAndOrderId(Long oId, User user) {
        Optional<Order> order;
        if( user != null) {
            order = orderRepository.findByUser_IdAndId(user.getId(), oId);
        } else {
            order = orderRepository.findById(oId);
        }
        if (order.isPresent()) {
            if (order.get().getUser() != null && user == null) {
                throw new EntityNotFoundException("Không tìm thấy order này");
            }
            return orderMapper.toDto(order.get());
        } else {
            throw new EntityNotFoundException("Không tìm thấy order này");
        }
    }

    private Order getOrderById(Long id) {
        return this.orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Order!"));
    }

    @Transactional
    @Override
    public OrderDTO updateOrderStatus(UpdateOrderStatus orderStatus) {
        Long updateStatusId = orderStatus.getOrderStatusId();
        if (orderStatus.getOrderId() == null) {
            throw new EntityIsEmptyException("Không được để trống trường order id");
        }
        if (updateStatusId == null) {
            throw new EntityIsEmptyException("Không được để trống trạng thái");
        }

        try {

            Order order = this.getOrderById(orderStatus.getOrderId());
            if (Objects.equals(order.getOrderStatus().getId(), EOrderStatus.DELIVERY_IN_PROGRESS.getId()) && updateStatusId.equals(EOrderStatus.CANCEL.getId())) {
                throw new OrderUpdateException("Không thể hủy đơn hàng đang được giao!");
            } else if (!Objects.equals(order.getOrderStatus().getId(), EOrderStatus.COMPLETED.getId()) && updateStatusId.equals(EOrderStatus.WAIT_FOR_RETURN.getId())) {
                throw new OrderUpdateException("Không thể trả đơn hàng chưa được giao");
            }

            order.setModifiedBy(AccountUtils.getUsername());
            order.setModifiedDate(new Date());
            order.setOrderStatus(new OrderStatus(updateStatusId));

            if (updateStatusId.equals(EOrderStatus.COMPLETED.getId())) {
                order.setCompletedTime(new Date());
                if (order.getUser() != null) {
                    Optional<OrderStat> orderStatOptional = orderStatRepository.findByUser_Id(order.getUser().getId());
                    if (orderStatOptional.isPresent()) {
                        OrderStat orderStat = orderStatOptional.get();
                        BigDecimal totalAmount = orderStat.getTotalAmount().add(BigDecimal.valueOf(order.getOrderTotal()));
                        orderStat.setTotalOrders(orderStat.getTotalOrders() + 1);
                        orderStat.setTotalAmount(totalAmount);
                        orderStatRepository.save(orderStat);
                    } else {
                        OrderStat orderStat = OrderStat.builder()
                                .user(order.getUser())
                                .totalOrders(1)
                                .totalAmount(BigDecimal.valueOf(order.getOrderTotal()))
                                .build();
                        orderStatRepository.save(orderStat);
                    }
                }
            } else if (updateStatusId.equals(EOrderStatus.TRANSPORTING.getId())) {
                order.setDeliveryInProgressTime(new Date());
            } else if (updateStatusId.equals(EOrderStatus.DELIVERY_IN_PROGRESS.getId())) {
                order.setDeliveryInProgressTime(new Date());
            } else if (updateStatusId.equals(EOrderStatus.WAIT_FOR_CANCEL.getId())) {
                order.setWaitForCancelTime(new Date());
            } else if (updateStatusId.equals(EOrderStatus.WAIT_FOR_RETURN.getId())) {
                order.setWaitForReturnTime(new Date());
            } else if (updateStatusId.equals(EOrderStatus.PACKAGING.getId())) {
                order.setPackagingTime(new Date());
            } else if (updateStatusId.equals(EOrderStatus.WAIT_FOR_PAY.getId())) {
                order.setWaitForPayTime(new Date());
            } else if (updateStatusId.equals(EOrderStatus.WAIT_FOR_CONFIRMATION.getId())) {
                if (order.getPaymentType().getId() > 1) {
                    String shippingCode = this.shippingService.createShippingOrder(getGHNShippingOrder(order)).getData().getOrder_code();
                    order.setShippingCode(shippingCode);
                    order.setWaitForConfirmationTime(new Date());
                    order.setPayTime(new Date());
                    order.setConfirmedTime(new Date());
                    order.setOrderStatus(new OrderStatus(EOrderStatus.CONFIRMED.getId()));
                    updateQtyProductDetailAfterCreatedOrder(order);
                }
                order.setWaitForConfirmationTime(new Date());
            } else {
                throw new OrderUpdateException("Trạng thái đơn hàng không hợp lệ");
            }
            Order result = this.orderRepository.save(order);
            sendNotificationToCustomer(result);
            return orderMapper.toDto(result);
        } catch (DataAccessException e) {
            throw new OrderUpdateException("Có lỗi xảy ra trong quá trình cập nhật trạng thái");
        }
    }
    @Override
    public OrderDTO approveReturnOrder(Long id) {
        Order order = getOrderById(id);
        order.setReturnTime(new Date());
        /*...Return order...*/
        return this.orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDTO confirmAndOrderShipping(Long id) {
        Order order = this.getOrderById(id);
        if (order.getOrderStatus().getId().equals(EOrderStatus.WAIT_FOR_CONFIRMATION.getId())) {
            String shippingCode = this.shippingService.createShippingOrder(getGHNShippingOrder(order)).getData().getOrder_code();
            order.setShippingCode(shippingCode);
            order.setConfirmedTime(new Date());
            order.setModifiedDate(new Date());
            order.setModifiedBy(AccountUtils.getUsername());
            order.setOrderStatus(new OrderStatus(EOrderStatus.CONFIRMED.getId()));
            Order updatedOrder = orderRepository.save(order);
            updateQtyProductDetailAfterCreatedOrder(order);
            sendNotificationToCustomer(order);
            return this.orderMapper.toDto(updatedOrder);
        } else {
            throw new OrderUpdateException("Yêu cầu xác nhận đơn hàng không hợp lệ");
        }
    }

    @Override
    public OrderDTO approveCancelOrder(Long id) {
        Order order = this.getOrderById(id);
        if (order.getOrderStatus().getId().equals(EOrderStatus.WAIT_FOR_CANCEL.getId()) ||
                order.getOrderStatus().getId().equals(EOrderStatus.WAIT_FOR_CONFIRMATION.getId()) ||
                order.getOrderStatus().getId().equals(EOrderStatus.WAIT_FOR_PAY.getId()) ||
                order.getOrderStatus().getId().equals(EOrderStatus.CONFIRMED.getId()) ||
                order.getOrderStatus().getId().equals(EOrderStatus.PACKAGING.getId())) {
            if (order.getPaymentType().getId() > 1) {
                /*if (order.getPaymentType().getValue().equalsIgnoreCase("VNPAY")) {
                    try {
                        refundService.doVNPAYRefund(order.getId());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
//                        throw new RuntimeException(e);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
//                        throw new RuntimeException(e);
                    }
                }*/
            }
            List<ProductDetail> productDetails = order.getOrderDetails().stream()
                    .map(od -> {
                        ProductDetail pd = od.getProductDetail();
                        pd.setQuantity(pd.getQuantity() + od.getQuantity());
                        return pd;
                    }).toList();
            this.productDetailRepository.saveAll(productDetails);
            order.setCancelTime(new Date());
            order.setOrderStatus(new OrderStatus(EOrderStatus.CANCEL.getId()));
            order.setPreOrderStatus(null);
            Order result = orderRepository.save(order);
            if (result.getVoucher() != null) {
                Voucher reSetQtyVoucher = result.getVoucher();
                reSetQtyVoucher.setUsageLimit(reSetQtyVoucher.getUsageLimit() + 1);
                voucherRepository.save(reSetQtyVoucher);
            }
            sendNotificationToCustomer(order);
            return this.orderMapper.toDto(result);
        } else {
            throw new OrderUpdateException("Yêu cầu xác nhận hủy đơn hàng không hợp lệ");
        }
    }

    @Override
    public OrderDTO requestCancelOrder(CancelOrderRequest cancelOrderRequest) {
        Order order = this.getOrderById(cancelOrderRequest.getOrderId());
        if (Objects.equals(order.getOrderStatus().getId(), EOrderStatus.DELIVERY_IN_PROGRESS.getId())) {
            throw new OrderUpdateException("Không thể hủy đơn hàng đang được giao!");
        } else if (Objects.equals(order.getOrderStatus().getId(), EOrderStatus.COMPLETED.getId())) {
            throw new OrderUpdateException("Không thể hủy đơn hàng đã giao thành công!");
        } else if (Objects.equals(order.getOrderStatus().getId(), EOrderStatus.PACKAGING.getId())) {
            throw new OrderUpdateException("Không thể hủy đơn hàng đã xác nhận giao hàng!");
        }
        order.setPreOrderStatus(order.getOrderStatus().getId());
        order.setOrderStatus(new OrderStatus(EOrderStatus.WAIT_FOR_CANCEL.getId()));
        order.setWaitForCancelTime(new Date());
        order.setCancelNote(cancelOrderRequest.getReason());
        Order result = orderRepository.save(order);
        sendNotificationToCustomer(result);
        return orderMapper.toDto(result);
    }

    @Override
    public String undoCancelOrderRequest(Long orderId) {
        Order order = this.getOrderById(orderId);
        if (Objects.equals(order.getOrderStatus().getId(), EOrderStatus.WAIT_FOR_CANCEL.getId())) {
            order.setOrderStatus(new OrderStatus(order.getPreOrderStatus()));
            order.setCancelNote("");
            order.setWaitForCancelTime(null);
            order.setPreOrderStatus(null);
            orderRepository.save(order);
            if (AccountUtils.isAdmin()) {
                trackingPageNotificationToCustomer(order,
                        "Shop không chấp nhấn yêu cầu hủy đơn hàng: " + order.getId(),
                        "[THÔNG BÁO] Không chấp nhận yêu cầu hủy đơn");
            }
            trackingPageNotificationToAdmin(order, "Đơn hàng: " + order.getId() + " đã thu hồi yêu cầu hủy", "[THÔNG BÁO]");
            return "Thu hồi yêu cầu hủy thành công";
        } else {
            return "Yêu cầu không hợp lệ";
        }
    }

    @Override
    public OrderDTO requestReturnOrder(ReturnOrderRequest returnOrderRequest) {
        return null;
    }

    @Override
    public OrderViewDTO findOrderViewById(Long id) {
        return orderViewMapper.toDto(getOrderById(id));
    }

    private Page<OrderViewDTO> getPage(org.springframework.data.domain.Page<Order> result) {
        List<OrderViewDTO> viewDTOList = result.getContent()
                .stream()
                .map(orderViewMapper::toDto)
                .collect(Collectors.toList());
        return Page.of(result.getSize(), result.getNumber(), result.getTotalPages(), Math.toIntExact(result.getTotalElements()), viewDTOList);
    }

    private GHNShippingOrder getGHNShippingOrder(Order order) {
        String toAddress, toWard, toDistrict, toProvince;
        if (order.getAddress() != null) {
            Optional<Address> optional = addressRepository.findById(order.getAddress().getId());
            if (optional.isPresent()) {
                Address address = optional.get();
                toAddress = address.getAddressLine1();
                toWard = address.getVillage().getName();
                toDistrict = address.getVillage().getDistrict().getName();
                toProvince = address.getVillage().getDistrict().getCity().getName();
            } else {
                throw new EntityNotFoundException("Không tìm thấy địa chỉ này");
            }
        } else {
            toAddress = order.getToAddress();
            toWard = order.getToWard();
            toDistrict = order.getToDistrict();
            toProvince = order.getToProvince();
        }
        GHNShippingOrder ghnShippingOrder = new GHNShippingOrder();
        List<GHNItem> items = order.getOrderDetails().stream()
                .map(od -> GHNItem.builder()
                        .name(od.getProductDetail().getProduct().getName())
                        .quantity(od.getQuantity())
                        .build()).collect(Collectors.toList());
        ghnShippingOrder.setTo_name(order.getConsigneeName());
        ghnShippingOrder.setTo_phone(order.getConsigneePhone());
        ghnShippingOrder.setTo_address(toAddress);
        ghnShippingOrder.setTo_ward_name(toWard);
        ghnShippingOrder.setTo_district_name(toDistrict);
        ghnShippingOrder.setTo_province_name(toProvince);
        ghnShippingOrder.setWeight(200);
        ghnShippingOrder.setLength(1);
        ghnShippingOrder.setWidth(19);
        ghnShippingOrder.setHeight(10);
        ghnShippingOrder.setService_type_id(2);
        ghnShippingOrder.setService_id(0);
        ghnShippingOrder.setPayment_type_id(1);
        ghnShippingOrder.setRequired_note("KHONGCHOXEMHANG");
        ghnShippingOrder.setItems(items);
        return ghnShippingOrder;
    }

    /* Send email to customer */
    private void sendNotificationToCustomer(Order order) {
        Long status = order.getOrderStatus().getId();
        boolean emailIsPresent = !StringUtils.isNullOrEmpty(order.getConsigneeEmail());
        if (status.equals(EOrderStatus.WAIT_FOR_CONFIRMATION.getId())) {
            trackingPageNotificationToAdmin(order, "Đơn hàng: " + order.getId() + " đang chờ xử lý", "Bạn có đơn hàng cần xử lý!");
            if (order.getUser() != null) {
                String content = "Chúng tôi đã nhận được yêu cầu đăt hàng của bạn, chúng tôi sẽ xử lý trong thời gian sớm nhất!";
                String title ="[THÔNG BÁO] Xác nhận yêu cầu đặt đơn hàng: " + order.getId();
                trackingPageNotificationToCustomer(order, content, title);
            }
            if (emailIsPresent) {
                sendMailAfterOrder(order);
            }
        } else if (status.equals(EOrderStatus.CONFIRMED.getId())) {
            if (order.getUser() != null) {
                String content = "Chúng tôi đã xác nhận đơn hàng của bạn và gửi yêu cầu đến bên vận chuyển!";
                String title ="[THÔNG BÁO] xác nhận đơn hàng: " + order.getId();
                trackingPageNotificationToCustomer(order, content, title);
            }
            if (emailIsPresent) {
                sendMailAfterConfirmAndShippingOrder(order);
            }
        } else if (status.equals(EOrderStatus.WAIT_FOR_CANCEL.getId())) {
            trackingPageNotificationToAdmin(order, "Yêu cầu hủy đơn hàng: " + order.getId() + " đã được tạo", "[THÔNG BÁO] Khách hàng yêu cầu hủy đơn");
            if (emailIsPresent) {
                sendMailAfterRequestCancelOrder(order);
            }
        } else if (status.equals(EOrderStatus.CANCEL.getId())) {
            if (order.getUser() != null) {
                String content = "Chúng tôi đã xác nhận hủy đơn hàng của bạn!";
                String title ="[THÔNG BÁO] hủy đơn hàng: " + order.getId();
                trackingPageNotificationToCustomer(order, content, title);
            }
            if (emailIsPresent) {
                sendMailAfterApproveCancelOrder(order);
            }
        } else if (status.equals(EOrderStatus.DELIVERY_IN_PROGRESS.getId())) {
            if (order.getUser() != null) {
                String content = "Đơn hàng " + order.getId() + " đang được giao tới bạn!";
                String title ="[THÔNG BÁO] Đang giao đơn hàng: " + order.getId();
                trackingPageNotificationToCustomer(order, content, title);
            }
            if (emailIsPresent) {
                sendMailAfterDelivery(order);
            }
        } else if (status.equals(EOrderStatus.TRANSPORTING.getId())) {
            if (order.getUser() != null) {
                String content = "Đơn hàng của bạn đã được bên vận chuyển giao hàng!";
                String title ="[THÔNG BÁO] giao hàng: " + order.getId();
                trackingPageNotificationToCustomer(order, content, title);
            }
            if (emailIsPresent) {
                sendMailAfterTransport(order);
            }
        } else if (status.equals(EOrderStatus.WAIT_FOR_RETURN.getId())) {
            if (emailIsPresent) {
                sendMailAfterRequestReturl(order);
            }
        } else if (status.equals(EOrderStatus.PACKAGING.getId())) {
            if (order.getUser() != null) {
                String content = "Chúng tôi đang xử lý và đóng gói đơn hàng của bạn!";
                String title ="[THÔNG BÁO] đóng gói đơn hàng: " + order.getId();
                trackingPageNotificationToCustomer(order, content, title);
            }
            if (emailIsPresent) {
                sendMailAfterPackaging(order);
            }
        } else if (status.equals(EOrderStatus.COMPLETED.getId())) {
            if (order.getUser() != null) {
                String content = "Đơn hàng của bạn đã được giao thành công!";
                if (order.getShippingMethod().getId() == 1) {
                    content = "Đơn hàng của bạn đã hoàn thành!";
                }
                    String title ="[THÔNG BÁO] đơn hàng: " + order.getId() + " đã hoàn thành";
                trackingPageNotificationToCustomer(order, content, title);
            }
            if (emailIsPresent) {
                sendMailAfterCompleted(order);
            }
            trackingPageNotificationToAdmin(order,
                    "Đơn hàng: " + order.getId() + " đã giao thành công",
                        "[ĐÃ BÁN] đơn hàng đã hoàn thành!");
        }
    }

    @Override
    public void trackingPageNotificationToCustomer(Order order, String content, String title) {
        Optional<User> optional = userRepository.findById(order.getUser().getId());
        if (optional.isPresent()) {
            User user = optional.get();
            wsn.pushAndSaveNotification(NotificationDTO.builder()
                    .content(content)
                    .title(title)
                    .url("/user/tracking-page/" + order.getId())
                    .isRead(false)
                    .toTopic(user.getUsername())
                    .topicUrl("/topic/" + user.getUsername())
                    .build());
        }
    }

    @Override
    public void trackingPageNotificationToAdmin(Order order, String content, String title) {
        wsn.pushAndSaveNotification(NotificationDTO.builder()
                .content(content)
                .title(title)
                .url("/order-management/tracking-page/" + order.getId())
                .isRead(false)
                .toTopic("private")
                .topicUrl("/topic/private/admin")
                .build());
    }

    @Override
    public Page<?> search(String keyWord, Optional<Long> statusId, Pageable pageable) {
        EntityManager em = emf.createEntityManager();

        try {
            SearchSession searchSession = Search.session(em);
            int offset = pageable.getPageNumber() * pageable.getPageSize();
            int limit = pageable.getPageSize();
            SearchResult<Order> result = searchSession.search(Order.class)
                    .where(f -> f.bool(b -> {
                        if (!StringUtils.isNullOrEmpty(keyWord)) {
                            String wildcard = "*" + keyWord + "*";
                            b.must(f.bool()
                                    .should(f.wildcard().field("consigneeName").matching(wildcard))
                                    .should(f.wildcard().field("shippingCode").matching(wildcard))
                            );
                        }
                        if (statusId.isPresent()) {
                            b.must(f.match().field("orderStatus.id").matching(statusId.get()));
                        } else {
                            b.must(f.matchAll());
                        }
                    }))
                    .sort(f -> f.composite(b -> {
                        if (pageable != null && pageable.getSort() != null) {
                            for (Sort.Order order : pageable.getSort()) {
                                if (order.isAscending()) {
                                    b.add(f.field(order.getProperty()).asc());
                                } else {
                                    b.add(f.field(order.getProperty()).desc());
                                }
                            }
                        } else {
                            b.add(f.field("id").desc());
                        }
                    }))
                    .fetch(offset, limit);
            List<Order> hits = result.hits();
            Long totalHitCount = result.total().hitCount();
            return com.orange.common.payload.Page.of(
                    pageable.getPageSize(),
                    pageable.getPageNumber(),
                    com.orange.common.payload.Page.calTotalPages(Math.toIntExact(totalHitCount), pageable.getPageSize()),
                    Math.toIntExact(totalHitCount),
                    orderViewMapper.toDtoList(hits));
        } catch (Exception e) {
            e.printStackTrace();
            return com.orange.common.payload.Page.of(0, 0, 0, 0, new ArrayList<>());
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void indexData() throws InterruptedException {
        EntityManager em = emf.createEntityManager();
        SearchSession searchSession = Search.session(em);
        MassIndexer indexer = searchSession.massIndexer(Order.class)
                .threadsToLoadObjects(7);
        indexer.startAndWait();
    }

    private void sendMailAfterCompleted(Order order) {
        String body = EmailTemplateOrder.generateHtmlOrderAfterCompleted(order);
        mailerService.queue(order.getConsigneeEmail(), "[THÔNG BÁO] Giao hàng thành công từ  " + EmailTemplate.SHOP_NAME, body);
    }

    private void sendMailAfterOrder(Order order) {
        String body = EmailTemplateOrder.generateHtmlOrderCreatedEmail(order);
        mailerService.queue(order.getConsigneeEmail(), "[THÔNG BÁO] Yêu cầu xác nhận đơn hàng từ " + EmailTemplate.SHOP_NAME, body);
    }
    private void sendMailAfterRequestCancelOrder(Order order) {
        String body = EmailTemplateOrder.generateHtmlOrderAfterRequestCancel(order);
        mailerService.queue(order.getConsigneeEmail(), "[THÔNG BÁO] Yêu cầu hủy đơn hàng từ " + EmailTemplate.SHOP_NAME, body);
    }
    private void sendMailAfterApproveCancelOrder(Order order) {
        String body = EmailTemplateOrder.generateHtmlOrderAfterApproveCancel(order);
        mailerService.queue(order.getConsigneeEmail(), "[THÔNG BÁO] Xác nhận yêu cầu hủy đơn", body);
    }
    private void sendMailAfterConfirmAndShippingOrder(Order order) {
        String body = EmailTemplateOrder.generateHtmlOrderAfterConfirmAndShippingEmail(order);
        mailerService.queue(order.getConsigneeEmail(), "[THÔNG BÁO] Xác nhận đơn hàng từ "+ EmailTemplate.SHOP_NAME, body);
    }
    private void sendMailAfterDelivery(Order order) {
        String body = EmailTemplateOrder.generateHtmlOrderAfterDeliveryEmail(order);
        mailerService.queue(order.getConsigneeEmail(), "[THÔNG BÁO] Xác nhận đơn hàng từ "+ EmailTemplate.SHOP_NAME +" đang được giao tới Quý khách", body);
    }
    private void sendMailAfterTransport(Order order) {
        String body = EmailTemplateOrder.generateHtmlOrderAfterTransport(order);
        mailerService.queue(order.getConsigneeEmail(), "[THÔNG BÁO] Xác nhận đơn hàng từ "+ EmailTemplate.SHOP_NAME +" đã được bàn giao cho phía giao hàng", body);
    }
    private void sendMailAfterPackaging(Order order) {
        String body = EmailTemplateOrder.generateHtmlOrderAfterPackaging(order);
        mailerService.queue(order.getConsigneeEmail(), "[THÔNG BÁO] đơn hàng từ "+ EmailTemplate.SHOP_NAME +" đang được đóng gói và chờ vận chuyển", body);
    }
    private void sendMailAfterRequestReturl(Order order) {
        String body = EmailTemplateOrder.generateHtmlOrderAfterRequestReturlEmail(order);
        mailerService.queue(order.getConsigneeEmail(), "[THÔNG BÁO] Xác nhận yêu cầu hoàn hàng từ "+ EmailTemplate.SHOP_NAME, body);
    }
    private void updateQtyProductDetailAfterCreatedOrder(Order order) {
        Set<ProductDetail> productDetails = order.getOrderDetails().stream()
                .map(od -> {
                    ProductDetail productDetail = od.getProductDetail();
                    productDetail.setQuantity(productDetail.getQuantity() - od.getQuantity());
                    productDetail.setModifiedBy(AccountUtils.getUsername());
                    productDetail.setModifiedDate(new Date());
                    return productDetail;
                }).collect(Collectors.toSet());
        productDetailRepository.saveAll(productDetails);
    }
}
