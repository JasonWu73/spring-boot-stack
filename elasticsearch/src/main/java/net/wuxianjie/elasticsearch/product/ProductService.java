package net.wuxianjie.elasticsearch.product;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import net.wuxianjie.myspringbootstarter.exception.ApiException;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addProduct(Product product) {
        productRepository.save(product);
    }

    public Product getProduct(long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND, "未找到 id 为 %s 的商品".formatted(id)
            ));
    }

    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }
}
