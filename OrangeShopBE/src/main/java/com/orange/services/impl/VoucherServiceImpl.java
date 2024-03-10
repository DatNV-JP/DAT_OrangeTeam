package com.orange.services.impl;

import com.orange.utils.AccountUtils;
import com.orange.utils.StringUtils;
import com.orange.common.constans.Constants;
import com.orange.common.payload.Page;
import com.orange.common.payload.Result;
import com.orange.domain.dto.VoucherDto;
import com.orange.domain.mapper.IVoucherMapper;
import com.orange.domain.model.*;
import com.orange.exception.EntityAlreadyExistException;
import com.orange.exception.EntityInvalid;
import com.orange.exception.EntityNotFoundException;
import com.orange.exception.VoucherInvalidException;
import com.orange.payload.request.CheckVoucherRequest;
import com.orange.payload.response.UserOrderStatResponse;
import com.orange.repositories.IGroupCustomerRepository;
import com.orange.repositories.IOrderStatRepository;
import com.orange.repositories.IUserRepository;
import com.orange.repositories.IVoucherRepository;
import com.orange.services.IVoucherService;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VoucherServiceImpl implements IVoucherService {

    private final IVoucherMapper voucherMapper;
    private final IVoucherRepository voucherRepository;
    private final IUserRepository userRepository;
    private final IOrderStatRepository orderStatRepository;
    private final MaillerServiceImpl mailerService;
    private final AccountUtils accountUtils;
    private final IGroupCustomerRepository groupCustomerRepository;
    @PersistenceUnit
    private EntityManagerFactory emf;

    @Override
    public VoucherDto create(VoucherDto dto) {
        Set<User> users = new HashSet<>();
        validateVoucher(dto);

        if (!dto.getIsGlobal()) {
            if (!dto.getUserIds().isEmpty()) {
                users = new HashSet<>(userRepository.findAllById(dto.getUserIds()));
            } else {
                throw new EntityInvalid("Cần có danh sách khách hàng để tạo voucher theo nhóm khách hàng");
            }
        }
        /*if (Objects.equals(dto.getVoucherTypeId(), Constants.VoucherType.FOR_GROUP_CUSTOMER)) {
            dto.setIsGlobal(false);
            if (!dto.getUserIds().isEmpty()) {
                users = new HashSet<>(userRepository.findAllById(dto.getUserIds()));
            } else {
                throw new EntityInvalid("Cần có danh sách khách hàng để tạo voucher theo nhóm khách hàng");
            }
        } else if (Objects.equals(dto.getVoucherTypeId(), Constants.VoucherType.FOR_ALL_ORDER)) {
            dto.setIsGlobal(true);
        }*/

        Voucher voucher = voucherMapper.toEntity(dto);
        voucher.setUsed(0);
        voucher.setUsers(users);
        voucher.setStatus(1);
        voucher.setCreateDate(new Date());
        voucher.setCreateBy(AccountUtils.getUsername());
        Voucher result = voucherRepository.save(voucher);
        if (!voucher.getIsGlobal()) {
            sendVoucherCodeToCustomer(result);
        }
        return voucherMapper.toDto(result);
    }

    @Override
    public VoucherDto update(VoucherDto dto) {
        Voucher voucher = getAndCheckNullVoucherById(dto.getId());
        validateVoucher(dto);
        Voucher newVoucher = voucherMapper.toEntity(dto);
        voucher.setDescription(newVoucher.getDescription());
        voucher.setIsPercent(newVoucher.getIsPercent());
        voucher.setDiscountAmount(newVoucher.getDiscountAmount());
        voucher.setMaxDiscountAmount(newVoucher.getMaxDiscountAmount());
        voucher.setStartDate(newVoucher.getStartDate());
        voucher.setEndDate(newVoucher.getEndDate());
        voucher.setMinimumOrderValue(newVoucher.getMinimumOrderValue());
        voucher.setModifiedDate(new Date());
        voucher.setModifiedBy(AccountUtils.getUsername());
        return voucherMapper.toDto(voucherRepository.save(voucher));
    }

    @Override
    public VoucherDto delete(VoucherDto dto) {
        return null;
    }

    @Override
    public VoucherDto delete(Long id) {
        Voucher voucher = getAndCheckNullVoucherById(id);
        voucher.setStatus(2);
        return voucherMapper.toDto(voucherRepository.save(voucher));
    }

    @Override
    public Page<?> fillAllForAdmin(Long status, Pageable pageable) {
        try {
            if (status.equals(Constants.VoucherStatus.ALL)) {
                org.springframework.data.domain.Page<Voucher> result = voucherRepository.findVoucherForAdmin(pageable);
                return getPage(result);
            } else if (status.equals(Constants.VoucherStatus.UPCOMING)) {
                org.springframework.data.domain.Page<Voucher> result = voucherRepository.findUpcomingVoucherForAdmin(getCurrentTime(), pageable);
                return getPage(result);
            } else if (status.equals(Constants.VoucherStatus.ONGOING)) {
                org.springframework.data.domain.Page<Voucher> result = voucherRepository.findOngoingVoucherForAdmin(getCurrentTime(), pageable);
                return getPage(result);
            } else if (status.equals(Constants.VoucherStatus.EXPIRED)) {
                org.springframework.data.domain.Page<Voucher> result = voucherRepository.findExpiredVoucherForAdmin(getCurrentTime(), pageable);
                return getPage(result);
            }
            return Page.of(0, 0, 0, 0, new ArrayList<>());
        } catch (Exception e) {
            e.printStackTrace();
            return Page.of(0, 0, 0, 0, new ArrayList<>());
        }
    }

    @Override
    public List<GroupCustomer> fillAllGroupCustomer() {
        return groupCustomerRepository.findAll();
    }

    @Override
    public Page<?> fillAllUserByGroupForAdmin(Long groupId, Pageable pageable) {
        org.springframework.data.domain.Page<OrderStat> result;
        if (groupId == 0) {
            result = orderStatRepository.findUserNotOrderYet(pageable);
        } else {
            Optional<GroupCustomer> groupCustomer = groupCustomerRepository.findById(groupId);
            if (groupCustomer.isPresent()) {
                BigDecimal require_value = BigDecimal.valueOf(groupCustomer.get().getRequiredConditions());
                result = orderStatRepository.findAllByTotalAmountLessThanEqual(require_value, pageable);
            } else {
                return Page.of(0, 0, 0, 0, new ArrayList<>());
            }
        }
        List<UserOrderStatResponse> listRespone = result.getContent().stream()
                .map(os -> UserOrderStatResponse.builder()
                        .userId(os.getUser().getId())
                        .username(os.getUser().getUsername())
                        .totalAmount(os.getTotalAmount())
                        .totalOrders(os.getTotalOrders())
                        .email(os.getUser().getEmail())
                        .build())
                .collect(Collectors.toList());
        return Page.of(result.getSize(), result.getNumber(), result.getTotalPages(), Math.toIntExact(result.getTotalElements()), listRespone);
    }

    @Override
    public List<VoucherDto> adminFindVoucherForUser(Long userId) {
        if (userId != null) {
            return voucherMapper.toDtoList(voucherRepository.findVoucherForUser(
                    accountUtils.getUserId(),
                    getCurrentTime()));
        }
        return voucherMapper.toDtoList(voucherRepository.findOngoingGlobalVoucher(getCurrentTime()));
    }

    @Override
    public Page<?> search(Integer status, String keyWord, Pageable pageable) {
        EntityManager em = emf.createEntityManager();

        try {
            SearchSession searchSession = Search.session(em);
            int offset = pageable.getPageNumber() * pageable.getPageSize();
            int limit = pageable.getPageSize();
            SearchResult<Voucher> result = searchSession.search(Voucher.class)
                    .where(f -> f.bool(b -> {
                        if (!StringUtils.isNullOrEmpty(keyWord)) {
                            String wildcard = "*" + keyWord + "*";
                            b.must(f.bool()
                                    .should(f.wildcard().field("name_search").matching(wildcard))
                                    .should(f.wildcard().field("code").matching(wildcard))
                            );
                        }
                        if (status != null) {

                            switch (status) {
                                case 1: { // Is All
                                    b.must(f.matchAll());
                                    break;
                                }
                                case 2: {// Is INACTIVE
//                                    b.must(f.match().field("status").matching(2));
                                    b.must(f.bool()
                                            .must(f.match().field("status").matching(2))
//                                            .must(f.range().field("startDate").lessThan(getCurrentTime()))
                                            .must(f.range().field("endDate").greaterThan(getCurrentTime()))
                                    );
                                    break;
                                }
                                case 3: {// Is UPCOMING
                                    b.must(f.bool()
                                            .must(f.match().field("status").matching(1))
                                            .must(f.range().field("startDate").greaterThan(getCurrentTime()))
                                    );
                                    break;
                                }
                                case 4: {// Is ONGOING
                                    b.must(f.bool()
                                            .must(f.match().field("status").matching(1))
                                            .must(f.range().field("startDate").lessThan(getCurrentTime()))
                                            .must(f.range().field("endDate").greaterThan(getCurrentTime()))
                                    );
                                    break;
                                }
                                case 5: {// Is EXPIRED
                                    b.must(f.bool()
//                                            .must(f.match().field("status").matching(1))
                                            .must(f.range().field("endDate").lessThan(getCurrentTime()))
                                    );
                                    break;
                                }
                                default:
                                    break;
                            }
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
            List<Voucher> hits = result.hits();
            Long totalHitCount = result.total().hitCount();
            return com.orange.common.payload.Page.of(
                    pageable.getPageSize(),
                    pageable.getPageNumber(),
                    com.orange.common.payload.Page.calTotalPages(Math.toIntExact(totalHitCount), pageable.getPageSize()),
                    Math.toIntExact(totalHitCount),
                    voucherMapper.toDtoList(hits));
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
    public Page<?> fillAll(Pageable pageable) {
        if (accountUtils.getUserOrAnonymousUser() == null) {
            return getPage(voucherRepository.findOngoingGlobalVoucher(getCurrentTime(), pageable));
        }
        return getPage(voucherRepository.findVoucherForUser(accountUtils.getUserId(), getCurrentTime(), pageable));
    }

    private Page<VoucherDto> getPage(org.springframework.data.domain.Page<Voucher> result) {
        List<VoucherDto> viewDTOList = result.getContent()
                .stream()
                .map(voucherMapper::toDto)
                .collect(Collectors.toList());
        return Page.of(result.getSize(), result.getNumber(), result.getTotalPages(), Math.toIntExact(result.getTotalElements()), viewDTOList);
    }

    @Override
    public VoucherDto findById(Long aLong) {
        return voucherMapper.toDto(getAndCheckNullVoucherById(aLong));
    }


    private void validateVoucher(VoucherDto dto) {
        Optional<Voucher> voucherCode = voucherRepository.findByCode(dto.getCode());
        if (voucherCode.isPresent()) {
            throw new EntityAlreadyExistException("Mã voucher đã tồn tại, vui lòng chọn mã khác!");
        }
        if (dto.getDiscountAmount() == null) {
            throw new VoucherInvalidException("Giá trị giảm giá là bắt buộc");
        }
        if (dto.getIsPercent()) {
            if (dto.getDiscountAmount() > 100) {
                throw new VoucherInvalidException("không thể giảm giá quá 100%");
            } else if (dto.getDiscountAmount() <= 0) {
                throw new VoucherInvalidException("Số % phải lớn hơn 0!");
            }
        } else {
            if (dto.getDiscountAmount() <= 0) {
                throw new VoucherInvalidException("Giá trị phải lớn hơn 0!");
            }
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new VoucherInvalidException("Ngày hết hạn của voucher phải lớn hơn ngày bắt đầu!");
        }
    }

    @Override
    public Result<?> checkVoucher(CheckVoucherRequest checkVoucherRequest) {
        Optional<Voucher> voucherOptional = voucherRepository.findById(checkVoucherRequest.getVoucherId());
        if (voucherOptional.isPresent()) {
            Voucher voucher = voucherOptional.get();
            if (!voucher.getIsGlobal()) {
                if (StringUtils.isNullOrEmpty(checkVoucherRequest.getUsername()) ||
                        checkVoucherRequest.getUsername().equalsIgnoreCase("anonymousUser")) {
                    return Result.result(HttpStatus.FORBIDDEN.value(), "Hãy đăng ký thành viên để sử dụng ưu đãi này!", false);
                }
                Optional<User> byUsername = userRepository.findByUsername(checkVoucherRequest.getUsername());
                if (byUsername.isPresent()) {
                    User user = byUsername.get();
                    if (voucher.getGroup() != null) {
                        BigDecimal customer_value = user.getOrderStat().getTotalAmount();
                        BigDecimal required_value = BigDecimal.valueOf(voucher.getGroup().getRequiredConditions());
                        if (required_value.compareTo(customer_value) > 0) {
                            return Result.result(HttpStatus.FORBIDDEN.value(), "Bạn chưa đủ điều kiện để sử dụng voucher này!", false);
                        }
                    } else {
                        if (user.getOrderStat().getTotalOrders() > 0) {
                            return Result.result(HttpStatus.FORBIDDEN.value(), "Voucher này chỉ áp dụng cho khách hàng lần đầu tiên mua tại cửa hàng!", false);
                        }
                    }
                } else {
                    return Result.result(HttpStatus.FORBIDDEN.value(), "Hãy đăng ký thành viên để sử dụng ưu đãi này!", false);
                }
            }
            if (voucher.getEndDate().isBefore(Instant.now())) {
                return Result.result(HttpStatus.FORBIDDEN.value(), "Voucher này đã hết hạn sử dụng", false);
            } else if (voucher.getStartDate().isAfter(Instant.now())) {
                return Result.result(HttpStatus.FORBIDDEN.value(), "Voucher này hiện chưa có hiệu lực, hãy đợi thêm nhé", false);
            } else if (voucher.getUsageLimit() <= voucher.getUsed()) {
                return Result.result(HttpStatus.FORBIDDEN.value(), "Voucher này đã hết số lượt sử dụng", false);
            } else if (voucher.getMinimumOrderValue() > checkVoucherRequest.getOrderValue()) {
                return Result.result(HttpStatus.FORBIDDEN.value(), "Giá trị đơn hàng chưa đạt điều kiện để sử dụng voucher này", false);
            }
            return Result.result(HttpStatus.OK.value(), "Voucher khả dụng", true);
        } else {
            return Result.result(HttpStatus.FORBIDDEN.value(), "Voucher này không tồn tại", false);
        }
    }

    private Voucher getAndCheckNullVoucherById(Long id) {
        return voucherRepository.findById(id).orElseThrow(() -> new VoucherInvalidException("Không tồn tại voucher này"));
    }

    private static Instant getCurrentTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }


    private void sendVoucherCodeToCustomer(Voucher voucher) {
        for (User u : voucher.getUsers()) {

            String body = "<div style=\"white-space: pre-line;\">Kính gửi quý khách " + u.getFirstName() + ",\n\n"
                    + "Quý khách vừa nhận được một voucher giảm giá:\n\n"
                    + "Mã Voucher: " + voucher.getCode() + "\n\n"
                    + "Hạn sử dụng đến ngày: " + voucher.getEndDate().toString() + "\n\n"
                    + "Ưu đãi giảm: " + voucher.getDiscountAmount() + (voucher.getIsPercent() ? " %" : " VND") + "\n\n"
                    + "Áp dụng cho các đơn hàng có giá trị từ: " + voucher.getMinimumOrderValue() + (voucher.getIsPercent() ? " %" : " VND") + "\n\n"
                    + "Giảm tối đa: " + voucher.getMaxDiscountAmount() + "VND" + "\n\n"
                    + "Quý khách có thể sử dụng hoặc chia sẻ cho người thân và bạn bè voucher này khi mua hàng có giá trị đạt điều kiện\n\n"
                    + "Nếu quý khách gặp vấn đề xin hãy liên hệ với chúng tôi qua email hoặc số điện thoại được cung cấp trên trang web.\n\n"
                    + "Sanvadio Xin cảm ơn quý khách đã tin tưởng và lựa chọn mua hàng của chúng tôi!\n\n"
                    + "Kính chúc quý khách một ngày tốt lành!\n\n"
                    + "Trân trọng,\n"
                    + "Sanvadio.</div>";
            mailerService.queue(u.getEmail(), "Quà Tặng Voucher", body);
        }
    }

    @Override
    public void indexData() throws InterruptedException {
        EntityManager em = emf.createEntityManager();
        SearchSession searchSession = Search.session(em);
        MassIndexer indexer = searchSession.massIndexer(Voucher.class)
                .threadsToLoadObjects(7);
        indexer.startAndWait();
    }

    @Override
    public VoucherDto reactivate(Long id) {
        Optional<Voucher> optional = voucherRepository.findById(id);
        if (optional.isPresent()) {
            Voucher voucher = optional.get();
            voucher.setStatus(1);
            return voucherMapper.toDto(voucherRepository.save(voucher));
        } else {
            throw new EntityNotFoundException("Không tìm thấy voucher");
        }
    }
}
