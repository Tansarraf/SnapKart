package com.tanishk.project.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tanishk.project.exceptions.APIException;
import com.tanishk.project.exceptions.ResourceNotFoundException;
import com.tanishk.project.model.Address;
import com.tanishk.project.model.Cart;
import com.tanishk.project.model.CartItem;
import com.tanishk.project.model.Order;
import com.tanishk.project.model.OrderItem;
import com.tanishk.project.model.Payment;
import com.tanishk.project.model.Product;
import com.tanishk.project.payload.OrderDTO;
import com.tanishk.project.payload.OrderItemDTO;
import com.tanishk.project.repo.AddressRepository;
import com.tanishk.project.repo.CartRepository;
import com.tanishk.project.repo.OrderItemRepository;
import com.tanishk.project.repo.OrderRepository;
import com.tanishk.project.repo.PaymentRepository;
import com.tanishk.project.repo.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	CartRepository cartRepository;

	@Autowired
	AddressRepository addressRepository;
	
	@Autowired
	PaymentRepository paymentRepository;
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	OrderItemRepository orderItemRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	CartService cartService;
	
	@Autowired
	ModelMapper modelMapper;
	
	@Override
	@Transactional
	public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId,
			String pgStatus, String pgResponseMessage) {
		
		//Getting user cart
		Cart cart = cartRepository.findCartByEmail(emailId);
		if(cart==null) {
			throw new ResourceNotFoundException("Cart","email",emailId);
		}
		
		Address address = addressRepository.findById(addressId)
				.orElseThrow(() -> new ResourceNotFoundException("Address","addressId",addressId));
		
		//Create a new order withpayment info 
		Order order = new Order();
		order.setEmail(emailId);
		order.setOrderDate(LocalDate.now());
		order.setTotalAmount(cart.getTotalPrice());
		order.setOrderStatus("Order Accepted!");
		order.setAddress(address);
		
		Payment payment = new Payment(paymentMethod,pgPaymentId,pgStatus,pgResponseMessage,pgName);
		payment.setOrder(order);
		payment = paymentRepository.save(payment);
		order.setPayment(payment);
		
		Order savedOrder = orderRepository.save(order);
		
		//Get items from cart into the order items
		List<CartItem> cartItems = cart.getCartItems();
		if(cartItems.isEmpty()) {
			throw new APIException("Cart is empty");
		}
		
		List<OrderItem> orderItems = new ArrayList<>();
		for(CartItem cartItem : cartItems) {
			OrderItem orderItem = new OrderItem();
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setDiscount(cartItem.getDiscount());
			orderItem.setOrderedProductPrice(cartItem.getProductPrice());
			orderItem.setOrder(savedOrder);
			orderItems.add(orderItem);
		}
		
		orderItems = orderItemRepository.saveAll(orderItems);
		
		//Update product stock
		cart.getCartItems().forEach(item -> {
			int quantity = item.getQuantity();
			Product product = item.getProduct();
			product.setQuantity(product.getQuantity()-quantity);
			productRepository.save(product);
			
			//Clear the cart
			cartService.deleteFromCart(cart.getCartId(), item.getProduct().getProductId());
		});
		
		//Send back the order summary
		OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
		orderItems.forEach(item-> 
			orderDTO.getOrderItems()
			.add(modelMapper
					.map(item, OrderItemDTO.class)
		));
		orderDTO.setAddressId(addressId);
		return orderDTO;
	}

}
