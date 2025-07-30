package com.tanishk.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.tanishk.project.payload.StripePaymentDTO;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class StripeServiceImpl implements StripeService  {

	@Value("${stripe.secret.key}")
	private String stripeApiKey;
	
	@PostConstruct
	public void init() {
		Stripe.apiKey = stripeApiKey;

	}
	
	@Override
	public PaymentIntent paymentIntent(StripePaymentDTO stripePaymentDTO) {
				
		PaymentIntentCreateParams params =
				  PaymentIntentCreateParams.builder()
				    .setAmount(stripePaymentDTO.getAmount())
				    .setCurrency(stripePaymentDTO.getCurrency())
				    .setAutomaticPaymentMethods(
				      PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
				        .setEnabled(true)
				        .build()
				    )
				    .build();
					try {
						return PaymentIntent.create(params);
					} catch (StripeException e) {
						throw new RuntimeException("Failed to create Stripe PaymentIntent: " + e.getMessage(), e);	
					}
	}

}
