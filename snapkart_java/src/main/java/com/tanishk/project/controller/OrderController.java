package com.tanishk.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.model.PaymentIntent;
import com.tanishk.project.payload.OrderDTO;
import com.tanishk.project.payload.OrderRequestDTO;
import com.tanishk.project.payload.StripePaymentDTO;
import com.tanishk.project.service.OrderService;
import com.tanishk.project.service.StripeService;
import com.tanishk.project.util.AuthUtil;

@RestController
@RequestMapping("/api")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private AuthUtil authUtil;
	
	@Autowired
	private StripeService stripeService;
	
	@PostMapping("/order/users/payments/{paymentMethod}")
	public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod,
			@RequestBody OrderRequestDTO orderRequestDTO){
		String emailId = authUtil.loggedInEmail();
		System.out.println("orderRequestDTO Data: " + orderRequestDTO);
		OrderDTO order = orderService.placeOrder(emailId,
				orderRequestDTO.getAddressId(),
				paymentMethod,
				orderRequestDTO.getPgName(),
				orderRequestDTO.getPgPaymentId(),
				orderRequestDTO.getPgStatus(),
				orderRequestDTO.getPgResponseMessage()
				);
		return new ResponseEntity<>(order,HttpStatus.CREATED);
	}
	
	@PostMapping("/order/stripe-client-secret")
	public ResponseEntity<String> createStripeClient(@RequestBody StripePaymentDTO stripePaymentDTO){
		PaymentIntent paymentIntent = stripeService.paymentIntent(stripePaymentDTO);
		return new ResponseEntity<>(paymentIntent.getClientSecret(),HttpStatus.CREATED);
	}
		
}
