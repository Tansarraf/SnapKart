package com.tanishk.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tanishk.project.model.Cart;
import com.tanishk.project.payload.CartDTO;
import com.tanishk.project.repo.CartRepository;
import com.tanishk.project.payload.CartItemsDTO;
import com.tanishk.project.service.CartService;
import com.tanishk.project.util.AuthUtil;

@RestController
@RequestMapping("/api")
public class CartController {
	
	@Autowired
	private CartRepository cartRepo;
	
	@Autowired
	private AuthUtil authUtil;
	
	@Autowired
	public CartService cartService;
	
	 @PostMapping("/cart/create")
	    public ResponseEntity<String> createOrUpdateCart(@RequestBody List<CartItemsDTO> cartItems){
	        String response = cartService.createOrUpdateCartWithItems(cartItems);
	        return new ResponseEntity<>(response, HttpStatus.CREATED);
	    }
	
	@PostMapping("/carts/products/{productId}/quantity/{quantity}")
	public ResponseEntity<CartDTO> addToCart(@PathVariable Long productId,
			                                 @PathVariable Integer quantity){
		CartDTO cartDTO = cartService.addProductToCart(productId,quantity);
		return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.CREATED);
	}
	
	@GetMapping("/carts")
	public ResponseEntity<List<CartDTO>> getCarts(){
		List<CartDTO> cartDTOs = cartService.getAllCarts();
		return new ResponseEntity<List<CartDTO>>(cartDTOs,HttpStatus.FOUND);
	}
	
	@GetMapping("/carts/users/cart")
	public ResponseEntity<CartDTO> getCartByID(){
		String emailId = authUtil.loggedInEmail();
		Cart cart = cartRepo.findCartByEmail(emailId);
		Long cartId = cart.getCartId();
		CartDTO cartDTO = cartService.getCart(emailId,cartId);
		return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.OK);
	}
	
	@PutMapping("/cart/products/{productId}/quantity/{operation}")
	public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId, @PathVariable String operation){
		CartDTO cartDTO = cartService.updateProductQuantityInCart(productId,
				operation.equalsIgnoreCase("delete") ? -1 : 1);
		return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.OK);
	}
	
	@DeleteMapping("/carts/{cartId}/product/{productId}")
	public ResponseEntity<String> deleteFromCart(@PathVariable Long cartId, @PathVariable Long productId){
		String status = cartService.deleteFromCart(cartId,productId);
		return new ResponseEntity<String>(status,HttpStatus.OK);
	}
}
