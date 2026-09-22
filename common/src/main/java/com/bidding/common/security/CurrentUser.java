package com.bidding.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.bidding.common.web.ApiException;

public final class CurrentUser {

	private CurrentUser() {
	}

	public static String clerkUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof JwtAuthenticationToken jwt) {
			return jwt.getToken().getSubject();
		}
		if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
			return authentication.getName();
		}
		throw ApiException.unauthorized("Missing Clerk session");
	}

	public static String email() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof JwtAuthenticationToken jwt) {
			String email = jwt.getToken().getClaimAsString("email");
			return email == null ? "" : email;
		}
		return "";
	}

	public static String displayName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof JwtAuthenticationToken jwt) {
			String name = jwt.getToken().getClaimAsString("name");
			if (name != null && !name.isBlank()) {
				return name;
			}
		}
		return "";
	}
}
