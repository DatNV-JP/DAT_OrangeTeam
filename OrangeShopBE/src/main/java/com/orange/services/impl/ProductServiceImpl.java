package com.orange.services.impl;

import com.orange.utils.AccountUtils;
import com.orange.utils.FillterPrice;
import com.orange.utils.StringUtils;
import com.orange.domain.dto.ProductDTO;
import com.orange.domain.dto.ProductDetailDTO;
import com.orange.domain.dto.VariationDTO;
import com.orange.domain.dto.VariationOptionDTO;
import com.orange.domain.mapper.IProductDetailMapper;
import com.orange.domain.mapper.IProductMapper;
import com.orange.domain.mapper.IVariationMapper;
import com.orange.domain.mapper.IVariationOptionMapper;
import com.orange.domain.model.*;
import com.orange.exception.EntityNotFoundException;
import com.orange.repositories.*;
import com.orange.services.IProductService;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final IProductRepository productRepository;
    private final IProductMapper productMapper;
    private final IProductDetailRepository productDetailRepository;
    private final IProductDetailMapper productDetailMapper;
    private final IVariationRepository variationRepository;
    private final IVariationMapper variationMapper;
    private final IVariationOptionRepository variationOptionRepository;
    private final IVariationOptionMapper variationOptionMapper;
    private final ICategoryRepository categoryRepository;
    @PersistenceUnit
    private EntityManagerFactory emf;

    @Transactional
    @Override
    public ProductDTO create(ProductDTO dto) {
        dto.setStatus(true);
        Product rerult = productRepository.save(productMapper.toEntity(dto));
        return productMapper.toDto(this.addProductDetail(dto, rerult));
    }


    @Transactional
    @Override
    public ProductDTO update(ProductDTO dto) {
        Product result = productRepository.findById(dto.getId()).orElse(null);
        if (result == null) {
            throw new EntityNotFoundException("Sản phẩm " + dto.getId() + ". " + dto.getName() + " chưa tồn tại!");
        }
        String name = AccountUtils.getUsername();
        dto.setModifiedBy(name);
        result = productRepository.save(productMapper.toEntity(dto));
        return productMapper.toDto(this.addProductDetail(dto, result));
    }

    @Transactional
    @Override
    public ProductDTO delete(ProductDTO dto) {
        Product result = productRepository.findById(dto.getId()).orElse(null);
        if (result == null) {
            throw new EntityNotFoundException("Sản phẩm " + dto.getId() + ". " + dto.getName() + " chưa tồn tại!");
        }
        String name = AccountUtils.getUsername();
        dto.setModifiedBy(name);
        String username = AccountUtils.getUsername();
        dto.setModifiedDate(new Date());
        dto.setStatus(false);
        dto.setModifiedBy(username);
        result = productMapper.toEntity(dto);
        return productMapper.toDto(productRepository.save(result));
    }

    @Transactional
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            productRepository.deleteProduct(id);
        }
    }

    @Override
    public com.orange.common.payload.Page<?> fillAll(Pageable pageable) {
        Page<Product> result = this.productRepository.findAllByStatusIsTrue(pageable);
        return getPageProductButNoProDetailFalse(result);
    }

    private com.orange.common.payload.Page<?> getPageProductButNoProDetailFalse(Page<Product> result) {
        List<Product> products = new ArrayList<>();
        for (Product product : result.getContent()) {
            List<ProductDetail> filteredProductDetails = new ArrayList<>();
            for (ProductDetail detail : product.getProductDetails()) {
                if (detail.getStatus()) {
                    filteredProductDetails.add(detail);
                }
            }
            product.setProductDetails(new HashSet<>(filteredProductDetails));
            products.add(product);
        }
        return getPage(result, products);
    }

    @Override
    public com.orange.common.payload.Page<?> getAllProductsByCategoryId(Pageable pageable, Long cid) {
        Page<Product> result = this.productRepository.findByCategoryIdAndStatusIsTrue(cid, pageable);
        return getPage(result);
    }

    @Override
    public com.orange.common.payload.Page<?> findProductsByCategory(Pageable pageable, Long cid) {
        Optional<Category> category = categoryRepository.findById(cid);
        if (category.isPresent()) {
            Page<Product> result = this.productRepository.findByCategoryIdOrCategoryIds(cid, getCateChildrenIds(category.get()), pageable);
            return getPageProductButNoProDetailFalse(result);
        } else {
            throw new EntityNotFoundException("Không tìm thấy danh mục này!");
        }
    }

    private List<Long> getCateChildrenIds(Category category) {
        List<Long> listCateChildrenIds = new ArrayList<>();

        setListCateChildrenIds(listCateChildrenIds, category);

        return listCateChildrenIds;
    }

    private void setListCateChildrenIds(List<Long> listCateChildrenIds, Category category) {
        listCateChildrenIds.add(category.getId());
            for (Category c : category.getChildren()) {
                setListCateChildrenIds(listCateChildrenIds, c);
            }

    }

    private com.orange.common.payload.Page<?> getPage(Page<Product> result) {
        int totalPages = result.getTotalPages();
        List<Product> productList = result.toList();
        List<ProductDTO> DTOList = productMapper.toDtoList(productList);
        com.orange.common.payload.Page pageProduct = new com.orange.common.payload.Page();
        pageProduct.setResult(DTOList);
        pageProduct.setTotalPages(totalPages);
        pageProduct.setTotalItems((int) result.getTotalElements());
        pageProduct.setPageSize(result.getSize());
        pageProduct.setPageNumber(result.getNumber());

        return pageProduct;
    }

    private com.orange.common.payload.Page<?> getPage(Page<Product> result, List<Product> products) {
        int totalPages = result.getTotalPages();
        List<ProductDTO> DTOList = productMapper.toDtoList(products);
        com.orange.common.payload.Page pageProduct = new com.orange.common.payload.Page();
        pageProduct.setResult(DTOList);
        pageProduct.setTotalPages(totalPages);
        pageProduct.setTotalItems((int) result.getTotalElements());
        pageProduct.setPageSize(result.getSize());
        pageProduct.setPageNumber(result.getNumber());

        return pageProduct;
    }

    @Override
    public ProductDTO findById(Long id) {
//        Product product = this.productRepository.findById(id).orElse(null);
        Product product = this.productRepository.findByIdAndStatusTrueAndProductDetails_StatusTrue(id).orElse(null);
        if (product == null) {
            throw new EntityNotFoundException("Sản phẩm " + id + " chưa tồn tại!");
        }
        return productMapper.toDto(product);
    }

    private Product addProductDetail(ProductDTO dto, Product rerult) {
        Set<ProductDetailDTO> productDetailDTOSet = dto.getProductDetails();
        Set<ProductDetail> productDetailSet = new HashSet<>();
        for (ProductDetailDTO productDetailDTO : productDetailDTOSet) {
            Set<VariationOptionDTO> variationOptionDTOS = productDetailDTO.getVariationOptions();
            Set<VariationOption> variationOptionSet = new HashSet<>();
            VariationOption variationOption = null;
            for (VariationOptionDTO variationOptionDTO : variationOptionDTOS) {
                VariationDTO variationDTO = variationOptionDTO.getVariation();
                Variation variation = variationRepository.save(variationMapper.toEntity(variationDTO));
                variationOptionDTO.setVariation(variationMapper.toDto(variation));
                variationOption = variationOptionRepository.save(variationOptionMapper.toEntity(variationOptionDTO));
                variationOption.setVariation(variation);
                variationOptionSet.add(variationOption);
            }
            ProductDetail pd = productDetailMapper.toEntity(productDetailDTO);
            pd.setProduct(rerult);
            ProductDetail productDetail = productDetailRepository.save(pd);
            productDetail.setVariationOptions(variationOptionSet);
            productDetail = productDetailRepository.save(productDetail);
            productDetailSet.add(productDetail);
        }
        rerult.setProductDetails(productDetailSet);
        return rerult;
    }

    @Override
    public Product getOneProductByCategoryID(Long idCategory, Boolean status) {
        return productRepository.find1ProductByCategory(idCategory, status);
    }

    @Override
    public com.orange.common.payload.Page<?> getProductByFillter(Long idCategory, Double fromPrice, Double toPrice, String namePr,Pageable pageable){
        Page<Product> result = productRepository.fillterProduct(idCategory, fromPrice, toPrice, namePr,pageable);
        return getPage(result);
    }

    @Override
    public com.orange.common.payload.Page<?> search(Long categoryId, String keyWord, FillterPrice filterPrice, Pageable pageable) {
        EntityManager em = emf.createEntityManager();

        try {
            SearchSession searchSession = Search.session(em);
            int offset = pageable.getPageNumber() * pageable.getPageSize();
            int limit = pageable.getPageSize();
            SearchResult<Product> result = searchSession.search(Product.class)
                    .where(f -> f.bool(b -> {
                        b.must(f.match().field("status").matching(true));
                        b.must(f.match().field("productDetails.status").matching(true));
                        if (categoryId != null) {
                            b.must(f.match().field("category.id").matching(categoryId));
                        }
                        if (!StringUtils.isNullOrEmpty(keyWord)) {
                            String wildcard = "*" + keyWord + "*";
                            b.must(f.bool()
                                    .should(f.wildcard().field("name_search").matching(wildcard))
                                    .should(f.match().field("category.name_search").matching(wildcard))
                            );
                        }
                        if (filterPrice != null && filterPrice.getFrom() > 0 && filterPrice.getTo() > 0) {
                            b.must(f.range().field("productDetails.priceSale").between(filterPrice.getFrom(), filterPrice.getTo()));
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
            List<Product> hits = result.hits();
            Long totalHitCount = result.total().hitCount();
            return com.orange.common.payload.Page.of(
                    pageable.getPageSize(),
                    pageable.getPageNumber(),
                    com.orange.common.payload.Page.calTotalPages(Math.toIntExact(totalHitCount), pageable.getPageSize()),
                    Math.toIntExact(totalHitCount),
                    productMapper.toDtoList(hits));
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
        MassIndexer indexer = searchSession.massIndexer(Product.class)
                .threadsToLoadObjects(7);
        indexer.startAndWait();
    }

}
