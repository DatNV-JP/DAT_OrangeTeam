package com.orange.services.impl;

import com.orange.utils.AccountUtils;
import com.orange.utils.Constants;
import com.orange.utils.StringUtils;
import com.orange.common.payload.Page;
import com.orange.common.template.EmailTemplate;
import com.orange.common.template.EmailTemplatePromotion;
import com.orange.domain.dto.PromotionDTO;
import com.orange.domain.mapper.IPromotionMapper;
import com.orange.domain.mapper.IVariationOptionMapper;
import com.orange.domain.model.ProductDetail;
import com.orange.domain.model.Promotion;
import com.orange.domain.model.User;
import com.orange.domain.model.VariationOption;
import com.orange.exception.DuplicateEntityException;
import com.orange.exception.EntityInvalid;
import com.orange.exception.EntityNotFoundException;
import com.orange.exception.PromotionException;
import com.orange.payload.response.ProductDetailForPromotionResponse;
import com.orange.payload.response.PromotionRespone;
import com.orange.repositories.IProductDetailRepository;
import com.orange.repositories.IPromotionRepository;
import com.orange.repositories.IUserRepository;
import com.orange.services.IPromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class PromotionServiceImpl implements IPromotionService {
    private final IPromotionRepository promotionRepository;
    private final IPromotionMapper promotionMapper;
    private final IUserRepository userRepository;
    private final IProductDetailRepository productDetailRepository;
    private final IVariationOptionMapper variationOptionMapper;

    private final MaillerServiceImpl mailerService;
    @PersistenceUnit
    private EntityManagerFactory emf;

    @Override
    public Set<Promotion> checkPromotionList(PromotionDTO dto) {
        this.validator(dto);
        Promotion result = promotionMapper.toEntity(dto);
        Set<ProductDetail> productDetailSet = result.getProductDetails();
        Set<Promotion> promotionList = new HashSet<>();
        for (ProductDetail productDetail : productDetailSet) {
            if (productDetailRepository.findById(productDetail.getId()).isEmpty()) {
                throw new EntityNotFoundException("Product detail không tồn tại!");
            }
            if (!promotionRepository.promotionAlreadyExistList(productDetail.getId(), result.getIsDate(), result.getStartDate(), result.getEndDate()).isEmpty()) {
                promotionList.addAll(promotionRepository.promotionAlreadyExistList(productDetail.getId(), result.getIsDate(), result.getStartDate(), result.getEndDate()));
            }
        }
        return promotionList;
    }

    @Override
    public PromotionDTO create(PromotionDTO dto) {
        this.validator(dto);
        Promotion result = promotionMapper.toEntity(dto);
        Set<ProductDetail> productDetailSet = result.getProductDetails();
        for (ProductDetail productDetail : productDetailSet) {
            if (productDetailRepository.findById(productDetail.getId()).isEmpty()) {
                throw new EntityNotFoundException("Sản phẩm không tồn tại!");
            }
            if (promotionRepository.countPromotionProductDetail(productDetail.getId(), dto.getIsDate(), dto.getStartDate(), dto.getEndDate()) > 0) {
                throw new DuplicateEntityException("Sản phẩm: \"" + productDetail.getId() + "\" đã được áp dụng khuyến mại trong thời gian này! Vui lòng kiểm tra lại thời gian!");
            }
            if (productDetail.getMaxSale() > productDetail.getPriceDefault()) {
                throw new EntityInvalid("Giá sale không hợp lệ!");
            }
        }
        result.setProductDetails(productDetailSet);
        // lưu max sale trong product_detail
        for (ProductDetail productDetail : result.getProductDetails()) {
            productDetailRepository.save(productDetail);
        }
        result.setStatus(true);
        result = promotionRepository.save(result);
        return promotionMapper.toDto(result);
    }

    @Override
    public PromotionDTO createAfterCheck(PromotionDTO dto) {
        this.validator(dto);
        Promotion result = promotionMapper.toEntity(dto);
        Set<ProductDetail> productDetails = result.getProductDetails();
        for (ProductDetail productDetail : productDetails) {
            if (productDetailRepository.findById(productDetail.getId()).isEmpty()) {
                throw new EntityNotFoundException("Product detail không tồn tại!");
            }
        }
        result.setProductDetails(productDetails);

        // lưu max sale trong product_detail
        for (ProductDetail productDetail : result.getProductDetails()) {
            productDetailRepository.save(productDetail);
        }
        result.setStatus(true);
        result = promotionRepository.save(result);
        return promotionMapper.toDto(result);
    }

    @Transactional
    @Override
    public PromotionDTO update(PromotionDTO dto) {
        this.validator(dto);
        Promotion promotion = promotionRepository.findById(dto.getId()).orElse(null);
        if (promotion == null) {
            throw new EntityNotFoundException("Promtion Không tồn tại!");
        }
        if (dto.getProductDetailDTOs() == null || dto.getProductDetailDTOs().isEmpty()) {
            throw new PromotionException("Vui lòng điền thông tin đối tượng áp dụng khuyến mại!");
        }
        Promotion result = promotionMapper.toEntity(dto);
        Set<ProductDetail> productDetailSet = result.getProductDetails();
        for (ProductDetail productDetail : productDetailSet) {
            if (productDetailRepository.findById(productDetail.getId()).isEmpty()) {
                throw new EntityNotFoundException("Product detail không tồn tại!");
            }
            if (promotionRepository.getCountPromotionByPromotionIdAndProductDetailId(productDetail.getId(), result.getId(), result.getIsDate(), result.getStartDate(), result.getEndDate()) > 0) {
                throw new DuplicateEntityException("Product detail: \"" + productDetail.getId() + "\" đã được áp dụng khuyến mại thời gian này! Vui lòng check lại thời gian!");
            }
        }
        productDetailRepository.stopSaleOffByProduct(promotion.getId());
        if (promotion.getIsDate().equals(Constants.IS_DATE.IS_DATE)) {
            this.stopWorkingPromotion(Constants.IS_DATE.IS_DATE);
            this.stopWorkingPromotionInActive(Constants.IS_DATE.IS_DATE);
        } else {
            this.stopWorkingPromotion(Constants.IS_DATE.IS_HOURS);
            this.stopWorkingPromotionInActive(Constants.IS_DATE.IS_HOURS);
        }
        result.setProductDetails(productDetailSet);
        String name = AccountUtils.getUsername();
        result.setModifiedBy(name);
        result = promotionRepository.save(result);

        // cập nhật lại giá sale
        this.startPromotion(Constants.IS_DATE.IS_DATE);
        this.startPromotion(Constants.IS_DATE.IS_HOURS);
        return promotionMapper.toDto(result);
    }

    @Transactional
    @Override
    public PromotionDTO delete(PromotionDTO dto) {
        String name = AccountUtils.getUsername();
        dto.setModifiedBy(name);
        dto.setStatus(false);
        if (dto.getIsDate().equals(Constants.IS_DATE.IS_DATE)) {
            this.stopWorkingPromotion(Constants.IS_DATE.IS_DATE);
            this.stopWorkingPromotionInActive(Constants.IS_DATE.IS_DATE);
        } else {
            this.stopWorkingPromotion(Constants.IS_DATE.IS_HOURS);
            this.stopWorkingPromotionInActive(Constants.IS_DATE.IS_HOURS);
        }
        this.startPromotion(Constants.IS_DATE.IS_DATE);
        this.startPromotion(Constants.IS_DATE.IS_HOURS);
        Promotion result = promotionMapper.toEntity(dto);
        return promotionMapper.toDto(promotionRepository.save(result));
    }

    @Override
    public Page<?> fillAll(Pageable pageable) {
        org.springframework.data.domain.Page<Promotion> result = promotionRepository.findAll(pageable);
        List<PromotionDTO> dtos = result.getContent()
                .stream()
                .map(promotionMapper::toDto)
                .collect(Collectors.toList());
        return Page.of(result.getSize(), result.getNumber(), result.getTotalPages(), Math.toIntExact(result.getTotalElements()), dtos);
    }

    @Override
    public Page<?> fillAllByIsStatusTrue(Pageable pageable) {
        org.springframework.data.domain.Page<Promotion> result = promotionRepository.findAllByStatusIsTrue(pageable);
        List<PromotionDTO> dtos = result.getContent()
                .stream()
                .map(promotionMapper::toDto)
                .collect(Collectors.toList());
        return Page.of(result.getSize(), result.getNumber(), result.getTotalPages(), Math.toIntExact(result.getTotalElements()), dtos);
    }

    @Override
    public PromotionRespone findDetail(Long id) {
        Optional<Promotion> optional = this.promotionRepository.findById(id);
        if (optional.isPresent()) {
            Promotion promotion = optional.get();
            Set<ProductDetailForPromotionResponse> listProductDetail = promotion.getProductDetails()
                    .stream()
                    .map(pd -> {
                        StringBuilder nameProduct = new StringBuilder();
                        nameProduct.append(pd.getProduct().getName());
                        for (VariationOption vo: pd.getVariationOptions()) {
                            nameProduct.append(" ")
                                    .append(vo.getValue());
                        }
                        return ProductDetailForPromotionResponse.builder()
                                .id(pd.getId())
                                .productId(pd.getProduct().getId())
                                .nameProduct(nameProduct.toString())
                                .priceInput(pd.getPriceInput())
                                .priceDefault(pd.getPriceDefault())
                                .images(pd.getImages())
                                .status(pd.getStatus())
                                .maxSale(pd.getMaxSale())
                                .quantity(pd.getQuantity())
                                .variationOptions(variationOptionMapper.toDtoSet(pd.getVariationOptions()))
                                .priceSale(pd.getPriceSale())
                                .build();
                    }).collect(Collectors.toSet());
            return PromotionRespone.builder()
                    .id(promotion.getId())
                    .description(promotion.getDescription())
                    .discount(promotion.getDiscount())
                    .endDate(promotion.getEndDate())
                    .startDate(promotion.getStartDate())
                    .isPercent(promotion.getIsPercent())
                    .isDate(promotion.getIsDate())
                    .name(promotion.getName())
                    .status(promotion.getStatus())
                    .productDetailDTOs(listProductDetail)
                    .build();
        } else {
            throw new EntityNotFoundException("khong tim thay khuyen mai");
        }
    }

    @Override
    public PromotionDTO findById(Long id) {
        return promotionMapper.toDto(this.getPromotionById(id));
    }

    private Promotion getPromotionById(Long id) {
        return this.promotionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khuyến mại!"));
    }

    /**
     * Hàm bắt đầu chương trình khuyến mãi
     */
    @Override
    public void startPromotion(Boolean isDate) {
        List<Promotion> promotionList = new ArrayList<>();
        if (isDate.equals(Constants.IS_DATE.IS_DATE)) {
            promotionList = promotionRepository.findAllByDate(new Date());
        } else {
            promotionList = promotionRepository.findAllByHoursAsc(new Date());
        }
        if (promotionList != null || !promotionList.isEmpty()) {
            for (Promotion promotion : promotionList) {
                Set<ProductDetail> productDetails = promotion.getProductDetails();
                this.startSaleOfProductDetail(promotion, productDetails);
//                this.sendMailToUser(promotion);
            }
        }
    }

    /**
     * Hàm bắt đầu sale sản phẩm
     *
     * @param promotion
     * @param productDetails
     */

    private void startSaleOfProductDetail(Promotion promotion, Set<ProductDetail> productDetails) {
        if (!productDetails.isEmpty()) {
            for (ProductDetail productDetail : productDetails) {
                double priceSale;
                if (promotion.getDiscount() < 0) {  // Đảm bảo rằng giá sale không bao giờ bị âm
                    promotion.setDiscount(0);
                    promotionRepository.save(promotion);
                }
                if (promotion.getIsPercent().equals(Constants.PROMOTION_PERCENT.PERCENT)) { // giảm theo %
                    if (promotion.getDiscount() >= 100) {  // Đảm bảo rằng giá sale không bao giờ lớn hơn giá cố định hoặc bị âm
                        promotion.setDiscount(0);
                        promotionRepository.save(promotion);
                    }
                    priceSale = (productDetail.getPriceDefault() / 100) * promotion.getDiscount() > productDetail.getMaxSale() ? productDetail.getPriceDefault() - productDetail.getMaxSale() : (productDetail.getPriceDefault() / 100) * (100 - promotion.getDiscount());
                } else {
                    priceSale = (promotion.getDiscount() > productDetail.getMaxSale()) ? (productDetail.getPriceDefault() - productDetail.getMaxSale()) : (productDetail.getPriceDefault() - promotion.getDiscount());
                }
                productDetailRepository.startSaleOffProduct(priceSale, productDetail.getId());
            }
        }
    }

    /**
     * Hàm dừng chương trình khuyến mại
     */
    @Override
    public void stopWorkingPromotion(Boolean isDate) {
        List<Promotion> promotionList = new ArrayList<>();
        if (isDate.equals(Constants.IS_DATE.IS_HOURS)) {
            promotionList = promotionRepository.findAllByStatusEqualsAndEndDateLessThan(new Date());
        } else {
            promotionList = promotionRepository.findAllPromotionEndDateLessThan(new Date());
        }
        if (!promotionList.isEmpty()) {
            for (Promotion promotion : promotionList) {
                promotionRepository.updatePromotionExpired(promotion.getId());
                Set<ProductDetail> productDetails = promotion.getProductDetails();
                if (!productDetails.isEmpty()) {
                    this.stopSaleOffProduct(productDetails);
                }
            }
        }
    }

    /**
     * Ngừng sale sản phẩm chi tiết
     *
     * @param productDetails
     */
    private void stopSaleOffProduct(Set<ProductDetail> productDetails) {
        for (ProductDetail productDetail : productDetails) {
            productDetailRepository.stopSaleOffProduct(productDetail.getId());
        }
    }

    /**
     * Ngừng sale sản phẩm chi tiết với khuyến mại đã xóa
     * @param isDate
     */
    @Override
    public void stopWorkingPromotionInActive(Boolean isDate) {
        List<Promotion> promotionList = promotionRepository.findAllByInActice(new Date());
        if (!promotionList.isEmpty()) {
            for (Promotion promotion : promotionList) {
                Set<ProductDetail> productDetails = promotion.getProductDetails();
                if (!productDetails.isEmpty()) {
                    this.stopSaleOffProduct(productDetails);
                }
            }
        }
    }

    @Override
    public Page<?> search(String keyWord, Integer statusFilter, Pageable pageable) {
        EntityManager em = emf.createEntityManager();

        try {
            SearchSession searchSession = Search.session(em);
            int offset = pageable.getPageNumber() * pageable.getPageSize();
            int limit = pageable.getPageSize();
            SearchResult<Promotion> result = searchSession.search(Promotion.class)
                    .where(f -> f.bool(b -> {
                        if (!StringUtils.isNullOrEmpty(keyWord)) {
                            String wildcard = "*" + keyWord + "*";
                            b.must(f.bool()
                                    .should(f.wildcard().field("name_search").matching(wildcard))
                            );
                        }
                        if (statusFilter != null) {
                            switch (statusFilter) {
                                case 1: { // Is All
                                    b.must(f.matchAll());
                                    break;
                                }
                                case 2: {// Is INACTIVE
//                                    b.must(f.match().field("status").matching(false));
                                    b.must(f.bool()
                                            .must(f.match().field("status").matching(false))
//                                            .must(f.range().field("startDate").lessThan(new Date()))
                                            .must(f.range().field("endDate").greaterThan(new Date()))
                                    );
                                    break;
                                }
                                case 3: {// Is UPCOMING
                                    b.must(f.bool()
                                            .must(f.match().field("status").matching(true))
                                            .must(f.range().field("startDate").greaterThan(new Date()))
                                    );
                                    break;
                                }
                                case 4: {// Is ONGOING
                                    b.must(f.bool()
                                            .must(f.match().field("status").matching(true))
                                            .must(f.range().field("startDate").lessThan(new Date()))
                                            .must(f.range().field("endDate").greaterThan(new Date()))
                                    );
                                    break;
                                }
                                case 5: {// Is EXPIRED
                                    b.must(f.bool()
//                                            .must(f.match().field("status").matching(true))
                                            .must(f.range().field("endDate").lessThan(new Date()))
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
            List<Promotion> hits = result.hits();
            Long totalHitCount = result.total().hitCount();
            return com.orange.common.payload.Page.of(
                    pageable.getPageSize(),
                    pageable.getPageNumber(),
                    com.orange.common.payload.Page.calTotalPages(Math.toIntExact(totalHitCount), pageable.getPageSize()),
                    Math.toIntExact(totalHitCount),
                    promotionMapper.toDtoList(hits));
        } catch (Exception e) {
            e.printStackTrace();
            return com.orange.common.payload.Page.of(0, 0, 0, 0, new ArrayList<>());
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    private void validator(PromotionDTO dto) {
        if (dto.getStartDate().before(new Date()) || dto.getEndDate().before(dto.getStartDate())) {
            throw new EntityInvalid("Ngày tháng không hợp lệ!");
        }
        if (dto.getIsPercent().equals(Constants.PROMOTION_PERCENT.PERCENT)) {
            if (dto.getDiscount() < 0 || dto.getDiscount() > 100) {
                throw new EntityInvalid("Phần trăm giảm không hợp lệ!");
            }
        } else {
            if (dto.getDiscount() < 1000) {
                throw new EntityInvalid("Số tiền giảm khuyến mại tối thiểu là 1000 đ!");
            }
        }
        if (dto.getProductDetailDTOs().isEmpty()) {
            throw new EntityInvalid("Chưa chọn sản phẩm áp dụng khuyến mại!");
        }
    }

    private static Instant getCurrentTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    @Override
    public void indexData() throws InterruptedException {
        EntityManager em = emf.createEntityManager();
        SearchSession searchSession = Search.session(em);
        MassIndexer indexer = searchSession.massIndexer(Promotion.class)
                .threadsToLoadObjects(7);
        indexer.startAndWait();
    }

    @Override
    public PromotionDTO resetPromotion(PromotionDTO promotionDTO) {
        Optional<Promotion> optionalPromotion = this.promotionRepository.findById(promotionDTO.getId());
        Promotion promotion;
        if (optionalPromotion.isPresent()) {
            promotion = optionalPromotion.get();
            promotion.setStatus(true);
            this.promotionRepository.save(promotion);
        } else {
            throw new EntityNotFoundException("Khong tim thay khuen mai nay");
        }
        return this.promotionMapper.toDto(promotion);
    }


    private void sendMailToUser(Promotion promotion) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            String body = EmailTemplatePromotion.generateHtmlPromotionCreatedEmail(promotion, user);
            if (user.getEmail() != null && !user.getEmail().isEmpty() && user.getEmail().length() > 10){
                mailerService.queue(user.getEmail(), "[THÔNG BÁO] Thông báo chương trình khuyến mại từ " + EmailTemplate.SHOP_NAME, body);
            }
        }
    }
}
