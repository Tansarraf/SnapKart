package com.tanishk.project.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.tanishk.project.exceptions.APIException;
import com.tanishk.project.exceptions.ResourceNotFoundException;
import com.tanishk.project.model.Cart;
import com.tanishk.project.model.CartItem;
import com.tanishk.project.model.Product;
import com.tanishk.project.payload.CartDTO;
import com.tanishk.project.payload.CartItemsDTO;
import com.tanishk.project.payload.ProductDTO;
import com.tanishk.project.repo.CartItemRepository;
import com.tanishk.project.repo.CartRepository;
import com.tanishk.project.repo.ProductRepository;
import com.tanishk.project.util.AuthUtil;

import jakarta.transaction.Transactional;

@Service
public class CartServiceImpl implements CartService{
	
	@Autowired
	private CartRepository cartRepo;
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private CartItemRepository cartItemRepo;
	
	@Autowired
	private AuthUtil authUtil;
	
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public CartDTO addProductToCart(Long productId, Integer quantity) {
		Cart cart = createCart();
		Product product = productRepo.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));
		CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if (cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        }

        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepo.save(newCartItem);

        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        cartRepo.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;
		}

	
	private Cart createCart() {
		Cart userCart = cartRepo.findCartByEmail(authUtil.loggedInEmail());
		if(userCart!=null) {
			return userCart;
		}
		
		Cart cart = new Cart();
		cart.setTotalPrice(0.0);
		cart.setUser(authUtil.loggedInUser());
		Cart newCart = cartRepo.save(cart);
		return newCart;
	}


	@Override
	public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepo.findAll();

        if (carts.size() == 0) {
            throw new APIException("No cart exists");
        }

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream().map(cartItem -> {
                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                productDTO.setQuantity(cartItem.getQuantity()); 
                return productDTO;
            }).collect(Collectors.toList());


            cartDTO.setProducts(products);

            return cartDTO;

        }).collect(Collectors.toList());

        return cartDTOs;
    }


	@Override
	public CartDTO getCart(String emailId, Long cartId) {
		//Fetching the cart 
		Cart cart = cartRepo.findCartByEmailAndCartId(emailId,cartId);
		if(cart == null) {
			throw new ResourceNotFoundException("Cart","cartId",cartId);
		}
		//Mapping the cart to DTO
		CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
		//Update the quantity 
		cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));
		List<ProductDTO> products = cart.getCartItems().stream()
				.map(p -> modelMapper.map(p.getProduct(), 
						ProductDTO.class)).toList();
		cartDTO.setProducts(products);
		return cartDTO;
		
	}


	@Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepo.findCartByEmail(emailId);
        Long cartId  = userCart.getCartId();

        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
        }

        // Calculate new quantity
        int newQuantity = cartItem.getQuantity() + quantity;

        // Validation to prevent negative quantities
        if (newQuantity < 0) {
            throw new APIException("The resulting quantity cannot be negative.");
        }

        if (newQuantity == 0){
            deleteFromCart(cartId, productId);
        } else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepo.save(cart);
        }

        CartItem updatedItem = cartItemRepo.save(cartItem);
        if(updatedItem.getQuantity() == 0){
            cartItemRepo.deleteById(updatedItem.getCartItemId());
        }


        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO prd = modelMapper.map(item.getProduct(), ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });


        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }


	@Override
	public String deleteFromCart(Long cartId, Long productId) {
		//Get the cart
		Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
		
		//Get the cart item 
        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }
        
        //Update the price 
        cart.setTotalPrice(cart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity()));
        
        
        cartItemRepo.deleteCartItemByProductIdAndCartId(cartId, productId);

        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!!";
    }


	@Override
	public void updateProductInCarts(Long cartId, Long productId) {
		//Fetch the cart
		Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

		//Retrieve the item
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
        }

        
        double cartPrice = cart.getTotalPrice()
                - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice
                + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItem = cartItemRepo.save(cartItem);	
	}
	
	 @Transactional
	    @Override
	    public String createOrUpdateCartWithItems(List<CartItemsDTO> cartItems) {
	        // Get user's email
	        String emailId = authUtil.loggedInEmail();

	        // Check if an existing cart is available or create a new one
	        Cart existingCart = cartRepo.findCartByEmail(emailId);
	        if (existingCart == null) {
	            existingCart = new Cart();
	            existingCart.setTotalPrice(0.00);
	            existingCart.setUser(authUtil.loggedInUser());
	            existingCart = cartRepo.save(existingCart);
	        } else {
	            // Clear all current items in the existing cart
	            cartItemRepo.deleteAllByCartId(existingCart.getCartId());
	        }

	        double totalPrice = 0.00;

	        // Process each item in the request to add to the cart
	        for (CartItemsDTO cartItemsDTO : cartItems) {
	            Long productId = cartItemsDTO.getProductId();
	            Integer quantity = cartItemsDTO.getQuantity();

	            // Find the product by ID
	            Product product = productRepo.findById(productId)
	                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

	            // Directly update product stock and total price
	            // product.setQuantity(product.getQuantity() - quantity);
	            totalPrice += product.getSpecialPrice() * quantity;

	            // Create and save cart item
	            CartItem cartItem = new CartItem();
	            cartItem.setProduct(product);
	            cartItem.setCart(existingCart);
	            cartItem.setQuantity(quantity);
	            cartItem.setProductPrice(product.getSpecialPrice());
	            cartItem.setDiscount(product.getDiscount());
	            cartItemRepo.save(cartItem);
	        }

	        // Update the cart's total price and save
	        existingCart.setTotalPrice(totalPrice);
	        cartRepo.save(existingCart);
	        return "Cart created/updated with the new items successfully";
	    }

}
