package com.lawsonmorel.ServeurGestionReservationSallesMunicipalesUCAO.controllers;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.lawsonmorel.ServeurGestionReservationSallesMunicipalesUCAO.exceptions.ResourceNotFoundException;
import com.lawsonmorel.ServeurGestionReservationSallesMunicipalesUCAO.models.ERole;
import com.lawsonmorel.ServeurGestionReservationSallesMunicipalesUCAO.models.Role;
import com.lawsonmorel.ServeurGestionReservationSallesMunicipalesUCAO.models.User;
import com.lawsonmorel.ServeurGestionReservationSallesMunicipalesUCAO.payload.request.LoginRequest;
import com.lawsonmorel.ServeurGestionReservationSallesMunicipalesUCAO.payload.request.SignupRequest;
import com.lawsonmorel.ServeurGestionReservationSallesMunicipalesUCAO.payload.response.JwtResponse;
import com.lawsonmorel.ServeurGestionReservationSallesMunicipalesUCAO.payload.response.MessageResponse;
import com.lawsonmorel.ServeurGestionReservationSallesMunicipalesUCAO.repository.RoleRepository;
import com.lawsonmorel.ServeurGestionReservationSallesMunicipalesUCAO.repository.UserRepository;
import com.lawsonmorel.ServeurGestionReservationSallesMunicipalesUCAO.security.jwt.JwtUtils;
import com.lawsonmorel.ServeurGestionReservationSallesMunicipalesUCAO.security.services.UserDetailsImpl;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	JwtUtils jwtUtils;
	
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getNom(),
												 userDetails.getPrenom(),
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getNom(),
				              signUpRequest.getPrenom(),
				            signUpRequest.getUsername(), 
							 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "employe":
					Role employeRole = roleRepository.findByName(ERole.ROLE_EMPLOYE)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(employeRole);

					break;
				case "gardien":
					Role gardienRole = roleRepository.findByName(ERole.ROLE_GARDIEN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(gardienRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	
	 @GetMapping("/usersl")
	  public List<User> getAllUser() {
	    System.out.println("Get all Utilisateur...");
	 
	    List<User> user = new ArrayList<>();
	    userRepository.findAll().forEach(user::add);
	 
	    return user;
	  }

	 @PostMapping("/users")
		public User createUtilisateur(@Valid @RequestBody User user) {
			return userRepository.save(user);
		}
	 
	 @DeleteMapping("/users/{id}")
		public Map<String, Boolean> deleteUtilisateur(@PathVariable(value = "id") Long userId)
				throws ResourceNotFoundException {
			User user = userRepository.findById(userId)
					.orElseThrow(() -> new ResourceNotFoundException("Utilisateur not found  id :: " + userId));

			userRepository.delete(user);
			Map<String, Boolean> response = new HashMap<>();
			response.put("deleted", Boolean.TRUE);
			return response;
		}
		  
		 
		  @DeleteMapping("/users/delete")
		  public ResponseEntity<String> deleteAllUtilisateur() {
		    System.out.println("Delete All Utilisateur...");
		 
		    userRepository.deleteAll();
		 
		    return new ResponseEntity<>("All Utilisateurs have been deleted!", HttpStatus.OK);
		  }
		 
	 
	 

}
