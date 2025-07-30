package com.tanishk.project.service;

import java.util.List;
import com.tanishk.project.payload.CartItemsDTO;
import com.tanishk.project.payload.CartDTO;

public interface CartService {

	 CartDTO addProductToCart(Long productId, Integer quantity);

	List<CartDTO> getAllCarts();

	CartDTO getCart(String emailId, Long cartId);

	CartDTO updateProductQuantityInCart(Long productId, Integer delete);

	String deleteFromCart(Long cartId, Long productId);

	void updateProductInCarts(Long cartId, Long productId);
	
	String createOrUpdateCartWithItems(List<CartItemsDTO> cartItems);

}
