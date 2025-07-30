package com.tanishk.project.service;

import org.springframework.data.domain.Pageable; 
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tanishk.project.exceptions.APIException;
import com.tanishk.project.exceptions.ResourceNotFoundException;
import com.tanishk.project.model.Cart;
import com.tanishk.project.model.Category;
import com.tanishk.project.model.Product;
import com.tanishk.project.payload.CartDTO;
import com.tanishk.project.payload.ProductDTO;
import com.tanishk.project.payload.ProductResponse;
import com.tanishk.project.repo.CartRepository;
import com.tanishk.project.repo.CategoryRepo;
import com.tanishk.project.repo.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepo categoryRepo;
	
	@Autowired
	private CartRepository cartRepo;

	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private CartService cartService;
	
	@Value("${project.image}")
    private String path;

    @Value("${image.base.url}")
    private String imageBaseUrl;

	@Override
	public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
		Category category = categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
		
		boolean isNotPresent = true;
		
		List<Product> products = category.getProducts();
		for (Product value :products) {
			if(value.getProductName().equals(productDTO.getProductName())) {
				isNotPresent = false;
				break;
			}
		}
		if(isNotPresent) {
			Product product = mapper.map(productDTO, Product.class);
			product.setImage(null);
			product.setCategory(category);
			double specialPrice = product.getPrice() - (product.getDiscount() * 0.01) * product.getPrice();
			product.setSpecialPrice(specialPrice);
	
			Product savedProduct = productRepository.save(product);
			return mapper.map(savedProduct, ProductDTO.class);
		} else {
			throw new APIException("Product already exists!");
		}
	}

	@Override
	public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder,String keyword,String category) {
		
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") 
				? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		
		Pageable pageDetails = (Pageable) PageRequest.of(pageNumber, pageSize,sortByAndOrder);
//		Provides a flexible way in Spring Data JPA to build database queries dynamically based on the condition.
		Specification<Product> spec = Specification.where(null);
		if(keyword!=null && !keyword.isEmpty()) {
			spec = spec.and((root,query,criteriaBuilder) -> 
			criteriaBuilder.like(
					criteriaBuilder.lower(
							root.get(
									"productName")),
					"%"+keyword.toLowerCase()+"%"));
		}
		if(category!=null && !category.isEmpty()) {
			spec = spec.and((root,query,criteriaBuilder) -> 
			criteriaBuilder.like(
							root.get(
									"category").get("categoryName"),
					category));
		}
		Page<Product> pageProducts = productRepository.findAll(spec,pageDetails);
		
		List<Product> products = pageProducts.getContent();
		List<ProductDTO> productDTOs = products.stream()
				                      .map(product -> {
				                    	  ProductDTO productDTO = mapper.map(product, ProductDTO.class);
				                    	  productDTO.setImage(constructImageUrl(product.getImage()));
				                    	  return productDTO;
				                      }).toList();
		
		if(products.isEmpty()) {
			throw new APIException("No products exist!");
		}
		ProductResponse productResponse = new ProductResponse();
		
		productResponse.setContent(productDTOs);
		productResponse.setPageNumber(pageProducts.getNumber());
		productResponse.setPageSize(pageProducts.getSize());
		productResponse.setTotalElements(pageProducts.getTotalElements());
		productResponse.setTotalPages(pageProducts.getTotalPages());
		productResponse.setLastPage(pageProducts.isLast());

		return productResponse;
	}
	
	private String constructImageUrl(String imageName) {
        return imageBaseUrl.endsWith("/") ? imageBaseUrl + imageName : imageBaseUrl + "/" + imageName;
    }

	@Override
	public ProductResponse searchByCategory(Long categoryId,Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Category category = categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
		
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") 
				? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		
		Pageable pageDetails = (Pageable) PageRequest.of(pageNumber, pageSize,sortByAndOrder);
		Page<Product> pageProducts = productRepository.findByCategory(category,pageDetails);
		
		List<Product> products = pageProducts.getContent();
		
		if(products.isEmpty()) {
			throw new APIException(category.getCategoryName() + "category does not have products!");
		}
		
		List<ProductDTO> productDTOs = products.stream().map(product -> mapper.map(product, ProductDTO.class)).toList();
		ProductResponse productResponse = new ProductResponse();
		productResponse.setContent(productDTOs);
		productResponse.setContent(productDTOs);
		productResponse.setPageNumber(pageProducts.getNumber());
		productResponse.setPageSize(pageProducts.getSize());
		productResponse.setTotalElements(pageProducts.getTotalElements());
		productResponse.setTotalPages(pageProducts.getTotalPages());
		productResponse.setLastPage(pageProducts.isLast());
		return productResponse;
	}

	@Override
	public ProductResponse searchByKeyword(String keyword,Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") 
				? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		
		Pageable pageDetails = (Pageable) PageRequest.of(pageNumber, pageSize,sortByAndOrder);
		Page<Product> pageProducts = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%',pageDetails);
		
		List<Product> products = pageProducts.getContent();
		
		List<ProductDTO> productDTOs = products.stream().map(product -> mapper.map(product, ProductDTO.class)).toList();
		
		if(products.size()==0) {
			throw new APIException("Products not found with keyword: " + keyword);
		}
		
		ProductResponse productResponse = new ProductResponse();
		productResponse.setContent(productDTOs);
		productResponse.setContent(productDTOs);
		productResponse.setPageNumber(pageProducts.getNumber());
		productResponse.setPageSize(pageProducts.getSize());
		productResponse.setTotalElements(pageProducts.getTotalElements());
		productResponse.setTotalPages(pageProducts.getTotalPages());
		productResponse.setLastPage(pageProducts.isLast());
		return productResponse;
	}

	@Override
	public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
//		Getting the exisiting product from the database
		Product productFromDB = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));
		
		Product product = mapper.map(productDTO, Product.class);
//		Update the product info with the one in request body
		productFromDB.setProductName(product.getProductName());
		productFromDB.setDescription(product.getDescription());
		productFromDB.setQuantity(product.getQuantity());
		productFromDB.setDiscount(product.getDiscount());
		productFromDB.setPrice(product.getPrice());
		productFromDB.setSpecialPrice(product.getSpecialPrice());
		
//		Save to database
		Product savedProduct = productRepository.save(productFromDB);
		
//      Finding carts by ProductId
		List<Cart> carts = cartRepo.findCartsByProductId(productId);

//		Mapping each Cart to CartDTO and cartItem to productDTO
        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = mapper.map(cart, CartDTO.class);

        List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> mapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

            cartDTO.setProducts(products);

            return cartDTO;

        }).collect(Collectors.toList());

        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

        return mapper.map(savedProduct, ProductDTO.class);
	}

	@Override
	public ProductDTO deleteProduct(Long productId) {
		Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
		List<Cart> carts = cartRepo.findCartsByProductId(productId);
		carts.forEach(cart -> cartService.deleteFromCart(cart.getCartId(),productId));
		
		productRepository.delete(product);
		return mapper.map(product, ProductDTO.class);
	}

	@Override
	public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
		//Get product from database
		Product productFromDB = productRepository.findById(productId)
				.orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));
		//Uploading the image to server
		//Get the file name of uploaded image
		String path = "images/";
		String fileName = fileService.uploadImage(path,image);
		//Updating the new file name to the product
		productFromDB.setImage(fileName);
		//Save the updated product
		Product updatedProduct = productRepository.save(productFromDB);
		return mapper.map(updatedProduct,ProductDTO.class);
	}

	 
	
}
