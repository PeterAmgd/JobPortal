package com.example.JobPortal.exception;

public class CustomAuthenticationExceptions {

    public abstract static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
    }


    public static class UserAlreadyExistsException extends AuthenticationException {
        public UserAlreadyExistsException(String email) {
            super("Email '" + email + "' is already registered");
        }
    }

    public static class InvalidTokenException extends AuthenticationException {
        public InvalidTokenException() {
            super("Invalid or malformed token");
        }
    }

    public static class TokenExpiredException extends AuthenticationException {
        public TokenExpiredException() {
            super("Token has expired");
        }
    }

    public static class UserNotFoundException extends AuthenticationException {
        public UserNotFoundException(String email) {
            super("User with email '" + email + "' not found");
        }
    }


}