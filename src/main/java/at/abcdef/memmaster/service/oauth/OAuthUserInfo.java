package at.abcdef.memmaster.service.oauth;

/**
 * Normalized user information extracted from an OAuth provider's token.
 * Each OAuth provider (Google, Apple, Microsoft) returns different field names,
 * so this class provides a unified interface.
 * @param sub  Unique subject ID from provider
 * @param email  User's email
 * @param firstName  First name
 * @param lastName  Last name
 * @param emailVerified  Whether email is verified by provider
 */
public record OAuthUserInfo(String sub, String email, String firstName, String lastName, boolean emailVerified) {

}

