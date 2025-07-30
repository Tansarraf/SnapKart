package com.tanishk.project.service;

import com.stripe.model.PaymentIntent;
import com.tanishk.project.payload.StripePaymentDTO;

public interface StripeService {

	PaymentIntent paymentIntent(StripePaymentDTO stripePaymentDTO);
	
	

}
