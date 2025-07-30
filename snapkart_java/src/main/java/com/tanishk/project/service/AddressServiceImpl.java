package com.tanishk.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tanishk.project.exceptions.ResourceNotFoundException;
import com.tanishk.project.model.Address;
import com.tanishk.project.model.User;
import com.tanishk.project.payload.AddressDTO;
import com.tanishk.project.repo.AddressRepository;
import com.tanishk.project.repo.UserRepository;

@Service
public class AddressServiceImpl implements AddressService{

	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	AddressRepository addressRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	public AddressDTO createAddress(AddressDTO addressDTO, User user) {
		Address address = modelMapper.map(addressDTO, Address.class);
		List<Address> addressList = user.getAddresses();
		addressList.add(address);
		user.setAddresses(addressList);
		address.setUser(user);
		Address savedAddress = addressRepository.save(address);
		return modelMapper.map(savedAddress, AddressDTO.class);
	}

	@Override
	public List<AddressDTO> getAddresses() {
		List<Address> addresses = addressRepository.findAll();
		List<AddressDTO> addressDTOs = addresses.stream()
		.map(address -> modelMapper.map(address, AddressDTO.class))
		.collect(Collectors.toList());	
		return addressDTOs;
	}

	@Override
	public AddressDTO getAddressById(Long addressId) {
		Address address = addressRepository.findById(addressId)
				.orElseThrow(() -> new ResourceNotFoundException("Address","addressId",addressId));
		return modelMapper.map(address, AddressDTO.class);
	}

	@Override
	public List<AddressDTO> getUserAddresses(User user) {
		List<Address> addresses = user.getAddresses();
		List<AddressDTO> addressDTOs = addresses.stream()
		.map(address -> modelMapper.map(address, AddressDTO.class))
		.collect(Collectors.toList());	
		return addressDTOs;
	}

	@Override
	public AddressDTO updateAddress(Long addressId,AddressDTO addressDTO) {
		Address addressFromDb = addressRepository.findById(addressId)
				.orElseThrow(() -> new ResourceNotFoundException("Address","addressId",addressId));
		addressFromDb.setCity(addressDTO.getCity());
		addressFromDb.setPincode(addressDTO.getPincode());
		addressFromDb.setState(addressDTO.getState());
		addressFromDb.setCountry(addressDTO.getCountry());
		addressFromDb.setStreet(addressDTO.getStreet());
		addressFromDb.setBuildingName(addressDTO.getBuildingName());

		Address updatedAddress = addressRepository.save(addressFromDb);
		User user = addressFromDb.getUser();
		user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
		user.getAddresses().add(updatedAddress);
		
		userRepository.save(user);
		
		return modelMapper.map(updatedAddress, AddressDTO.class);
		
	}

	@Override
	public String deleteAddress(Long addressId) {
		Address addressFromDb = addressRepository.findById(addressId)
				.orElseThrow(() -> new ResourceNotFoundException("Address","addressId",addressId));
		User user = addressFromDb.getUser();
		user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
		userRepository.save(user);
		addressRepository.delete(addressFromDb);
		return "Address Deleted Successfully with AddressID: " + addressId;
	}

} 
