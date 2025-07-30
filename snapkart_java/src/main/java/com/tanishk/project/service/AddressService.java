package com.tanishk.project.service;

import java.util.List;

import com.tanishk.project.model.User;
import com.tanishk.project.payload.AddressDTO;

public interface AddressService {

	AddressDTO createAddress(AddressDTO addressDTO, User user);

	List<AddressDTO> getAddresses();

	AddressDTO getAddressById(Long addressId);

	List<AddressDTO> getUserAddresses(User user);

	AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);
	
	String deleteAddress(Long addressId);

}
