package at.abcdef.memmaster.security.oauth;

/**
 * Contract for OAuth provider authentication.
 * Implementations handle provider-specific token verification and user info extraction.
 */
public interface OAuthService {

    /**
     * Verifies the OAuth provider's ID token and extracts user information.
     *
     * @param idToken the ID token from the OAuth provider
     * @return user info extracted from the token
     * @throws IllegalArgumentException if token is invalid or required fields are missing
     * @throws IllegalStateException if token verification fails
     */
    OAuthUserInfo verifyAndExtractUserInfo(String idToken);

    /**
     * Returns the provider name (e.g., "google", "apple", "microsoft").
     */
    String getProviderName();
}

