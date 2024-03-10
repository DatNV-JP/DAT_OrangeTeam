package com.orange.services.impl;

import com.orange.utils.FillterPrice;
import com.orange.utils.StringUtils;
import com.orange.common.constans.Constants;
import com.orange.common.payload.Page;
import com.orange.domain.dto.ProductDetailDTO;
import com.orange.domain.mapper.IProductDetailMapper;
import com.orange.domain.mapper.IVariationOptionMapper;
import com.orange.domain.model.Product;
import com.orange.domain.model.ProductDetail;
import com.orange.domain.model.VariationOption;
import com.orange.exception.EntityNotFoundException;
import com.orange.payload.response.ProductDetailForPromotionResponse;
import com.orange.repositories.IProductDetailRepository;
import com.orange.repositories.IProductRepository;
import com.orange.services.IProductDetailService;
import com.orange.utils.FillterPrice;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductDetailServiceImpl implements IProductDetailService {
    private final IProductDetailRepository productDetailRepository;
    private final IProductDetailMapper mapper;
    private final IProductRepository productRepository;
    private final IProductDetailMapper productDetailMapper;
    private final IVariationOptionMapper variationOptionMapper;
    @PersistenceUnit
    private EntityManagerFactory emf;

    @Override
    public ProductDetailDTO create(ProductDetailDTO dto) {
        ProductDetail productDetail = productDetailRepository.save(productDetailMapper.toEntity(dto));
        return productDetailMapper.toDto(productDetail);
    }

    @Override
    public ProductDetailDTO update(ProductDetailDTO dto) {
        ProductDetail productDetail = productDetailRepository.findById(dto.getId()).orElse(null);
        if (productDetail == null) {
            throw new EntityNotFoundException("Sản phẩm "+ dto.getId()+ ". " + dto.getId() + " chưa tồn tại!");
        }
        productDetail = productDetailMapper.toEntity(dto);
        Product product = productRepository.findById(dto.getProductId()).get();
        productDetail.setProduct(product);
        productDetail.setModifiedDate(new Date());
        productDetail = productDetailRepository.save(productDetail);
        return productDetailMapper.toDto(productDetail);
    }


    @Transactional
    @Override
    public ProductDetailDTO delete(ProductDetailDTO dto) {
        ProductDetail productDetail = productDetailRepository.findById(dto.getId()).orElse(null);
        if (productDetail == null) {
            throw new EntityNotFoundException("Sản phẩm "+ dto.getId()+ " chưa tồn tại!");
        }
        if (productDetail.getStatus().equals(Constants.Delete.InActive)) {
            throw new EntityNotFoundException("Sản phẩm "+ productDetail.getId() + " đã xóa rồi!");
        }
        productDetail.setStatus(Constants.Delete.InActive);
        productDetailRepository.save(productDetail);
        return productDetailMapper.toDto(productDetail);
    }

    @Override
    public Page<?> fillAll(Pageable pageable) {
        org.springframework.data.domain.Page<ProductDetail> result = productDetailRepository.findAll(pageable);
        return this.getPage(result);
    }

    @Override
    public ProductDetailDTO findById(Long aLong) {
        ProductDetail productDetail = productDetailRepository.findById(aLong).orElse(null);
        if (productDetail == null) throw new EntityNotFoundException("ProductDetail Id not found!");
        return mapper.toDto(productDetail);
    }

    @Override
    public Page<?> findByPrice(Double fromPrice, Double toPrice, Pageable pageable){
        org.springframework.data.domain.Page<ProductDetail> result = productDetailRepository.findByPriceRange(fromPrice,toPrice,pageable);
        List<ProductDetailDTO> productDetailDTOS = result.getContent()
                .stream().map(c->this.mapper.toDto(c))
                .collect(Collectors.toList());
        return Page.of(result.getSize(),result.getNumber(),result.getTotalPages(),Math.toIntExact(result.getTotalElements()),productDetailDTOS);
    }

    @Override
    public Page<?> findProductDetailsByProduct(Pageable pageable, Long cid) {
        org.springframework.data.domain.Page<ProductDetail> result = productDetailRepository.findByProductIdAndStatusIsTrue(cid, pageable);
        return this.getPage(result);
    }
    private Page<?> getPage(org.springframework.data.domain.Page<ProductDetail> result) {
        int totalPages = result.getTotalPages();
        List<ProductDetail> productDetailList = result.toList();
        List<ProductDetailDTO> dtoList = productDetailMapper.toDtoList(productDetailList);
        Page pageProductDetail = new Page();
        pageProductDetail.setResult(dtoList);
        pageProductDetail.setTotalPages(totalPages);
        pageProductDetail.setTotalItems((int) result.getTotalElements());
        pageProductDetail.setPageSize(result.getSize());
        pageProductDetail.setPageNumber(result.getNumber());
        return pageProductDetail;
    }

    @Override
    public Page<?> getPageByFillter(Long idCategory, Double fromPrice, Double toPrice, String namePr, Pageable pageable){
        if (idCategory == null && fromPrice == null && toPrice == null && namePr == null ){
            org.springframework.data.domain.Page<ProductDetail> result = productDetailRepository.findAll(pageable);
            return this.getPage(result);
        }
        org.springframework.data.domain.Page<ProductDetail> result =  productDetailRepository.fillterProduct(idCategory, fromPrice, toPrice, namePr, pageable);
        return this.getPage(result);
    }

    @Override
    public Page<?> findUnpopularProductDetails(LocalDateTime startDate, LocalDateTime endDate, Integer totalQuantity, Pageable pageable) {
        org.springframework.data.domain.Page<ProductDetail> result = productDetailRepository.findUnpopularProductDetails(startDate, endDate, totalQuantity, pageable);
        return getPageProductDetailForPromotion(result);
    }
    @Override
    public Page<?> findForPromotion(Long idCategory, Double fromPrice, Double toPrice, String namePr, Pageable pageable){
        org.springframework.data.domain.Page<ProductDetail> result;
        if (idCategory == null && fromPrice == null && toPrice == null && namePr == null ){
            result = productDetailRepository.findAll(pageable);
        } else {
            result = productDetailRepository.fillterProduct(idCategory, fromPrice, toPrice, namePr, pageable);
        }
        return this.getPageProductDetailForPromotion(result);
    }

    @Override
    public Page<?> search(Long categoryId, String keyWord, FillterPrice filterPrice, Pageable pageable) {
        EntityManager em = emf.createEntityManager();

        try {
            SearchSession searchSession = Search.session(em);
            int offset = pageable.getPageNumber() * pageable.getPageSize();
            int limit = pageable.getPageSize();
            SearchResult<ProductDetail> result = searchSession.search(ProductDetail.class)
                    .where(f -> f.bool(b -> {
                        b.must(f.match().field("status").matching(true));
//                        b.must(f.match().field("product.status").matching(true));
                        if (categoryId != null) {
                            b.must(f.match().field("product.category.id").matching(categoryId));
                        }
                        if (!StringUtils.isNullOrEmpty(keyWord)) {
                            String wildcard = "*" + keyWord + "*";
                            b.must(f.bool()
                                    .should(f.wildcard().field("product.name").matching(wildcard))
                                    .should(f.match().field("product.category.name_search").matching(wildcard))
                            );
                        }
                        if (filterPrice != null && filterPrice.getFrom() > 0 && filterPrice.getTo() > 0) {
                            b.must(f.range().field("priceSale").between(filterPrice.getFrom(), filterPrice.getTo()));
                        }
                    }))
                    .sort(f -> f.composite(b -> {
                        if (pageable != null && pageable.getSort() != null) {
                            if (pageable.getSort().iterator().next().isAscending()) {
                                b.add(f.field(pageable.getSort().iterator().next().getProperty()).asc());
                            } else {
                                b.add(f.field(pageable.getSort().iterator().next().getProperty()).desc());
                            }
                        } else {
                            b.add(f.field("id").desc());
                        }
                    }))
                    .fetch(offset, limit);
            List<ProductDetail> hits = result.hits();
            Long totalHitCount = result.total().hitCount();
            return com.orange.common.payload.Page.of(
                    pageable.getPageSize(),
                    pageable.getPageNumber(),
                    com.orange.common.payload.Page.calTotalPages(Math.toIntExact(totalHitCount), pageable.getPageSize()),
                    Math.toIntExact(totalHitCount),
                    productDetailMapper.toDtoList(hits));
        } catch (Exception e) {
            e.printStackTrace();
            return com.orange.common.payload.Page.of(0,0,0,0, new ArrayList<>());
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
        MassIndexer indexer = searchSession.massIndexer(ProductDetail.class)
                .threadsToLoadObjects(7);
        indexer.startAndWait();
    }
    private Page<?> getPageProductDetailForPromotion(org.springframework.data.domain.Page<ProductDetail> result) {
        int totalPages = result.getTotalPages();
        List<ProductDetail> productDetailList = result.toList();
        List<ProductDetailForPromotionResponse> dtoList = productDetailList.stream()
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
                }).toList();
        Page pageProductDetail = new Page();
        pageProductDetail.setResult(dtoList);
        pageProductDetail.setTotalPages(totalPages);
        pageProductDetail.setTotalItems((int) result.getTotalElements());
        pageProductDetail.setPageSize(result.getSize());
        pageProductDetail.setPageNumber(result.getNumber());
        return pageProductDetail;
    }

}
