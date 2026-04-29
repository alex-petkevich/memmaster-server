package at.abcdef.memmaster.security.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * Google OAuth 2.0 token verification service.
 * Verifies ID tokens against Google's tokeninfo endpoint and extracts user information.
 */
@Service
public class GoogleOAuthService implements OAuthService {
    private static final Logger log = LoggerFactory.getLogger(GoogleOAuthService.class);
    private static final String PROVIDER_NAME = "google";
    private static final String TOKENINFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";

    private final ObjectMapper objectMapper;

    public GoogleOAuthService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public OAuthUserInfo verifyAndExtractUserInfo(String idToken) {
        try {
            String encodedToken = URLEncoder.encode(idToken, StandardCharsets.UTF_8);
            String url = TOKENINFO_URL + encodedToken;

            RestTemplate restTemplate = buildTrustingRestTemplate();
            String body = restTemplate.getForObject(url, String.class);

            JsonNode node = objectMapper.readTree(body);
            String email = node.path("email").asText("");
            String sub = node.path("sub").asText("");
            String givenName = node.path("given_name").asText("");
            String familyName = node.path("family_name").asText("");
            boolean emailVerified = Boolean.parseBoolean(node.path("email_verified").asText("false"));

            if (!StringUtils.hasText(email) || !StringUtils.hasText(sub)) {
                throw new IllegalArgumentException("Google token does not contain required fields (email, sub).");
            }

            return new OAuthUserInfo(sub, email, givenName, familyName, emailVerified);
        }
        catch (IllegalArgumentException e) {
            log.warn("Google token validation rejected: {}", e.getMessage());
            throw e;
        }
        catch (org.springframework.web.client.HttpClientErrorException e) {
            log.warn("Google tokeninfo returned error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new IllegalArgumentException("Invalid Google token.");
        }
        catch (Exception e) {
            String details = e.getMessage() != null ? ": " + e.getMessage() : "";
            log.error("Google token verification call failed", e);
            throw new IllegalStateException("Unable to verify Google token (" + e.getClass().getSimpleName() + details + ").", e);
        }
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    /**
     * Builds a RestTemplate with a merged SSL truststore:
     * JVM default cacerts + platform default (Windows Cert Store / macOS Keychain / Linux system certs).
     * Falls back to JVM default only if platform store is unavailable.
     * This resolves PKIX failures when Google's root CA is absent from the JVM cacerts alone.
     */
    private RestTemplate buildTrustingRestTemplate() {
        try {
            // Load JVM default truststore
            TrustManagerFactory defaultTmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            defaultTmf.init((KeyStore) null);
            X509TrustManager defaultTm = findX509TrustManager(defaultTmf);

            // Try to load the platform/OS truststore (Windows-ROOT, KeychainStore, PKCS11, etc.)
            X509TrustManager platformTm = null;
            for (String storeType : new String[]{"Windows-ROOT", "KeychainStore", "PKCS11"}) {
                try {
                    TrustManagerFactory platformTmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    KeyStore platformStore = KeyStore.getInstance(storeType);
                    platformStore.load(null, null);
                    platformTmf.init(platformStore);
                    platformTm = findX509TrustManager(platformTmf);
                    log.info("Google OAuth: using platform truststore type '{}'", storeType);
                    break;
                }
                catch (Exception ignored) { /* not available on this OS */ }
            }

            final X509TrustManager finalDefaultTm = defaultTm;
            final X509TrustManager finalPlatformTm = platformTm;

            // Merge: accept cert if either truststore trusts it
            X509TrustManager mergedTm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    if (finalPlatformTm == null) return finalDefaultTm.getAcceptedIssuers();
                    X509Certificate[] a = finalDefaultTm.getAcceptedIssuers();
                    X509Certificate[] b = finalPlatformTm.getAcceptedIssuers();
                    X509Certificate[] merged = Arrays.copyOf(a, a.length + b.length);
                    System.arraycopy(b, 0, merged, a.length, b.length);
                    return merged;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    finalDefaultTm.checkClientTrusted(chain, authType);
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    try {
                        finalDefaultTm.checkServerTrusted(chain, authType);
                    }
                    catch (CertificateException e) {
                        if (finalPlatformTm != null) {
                            finalPlatformTm.checkServerTrusted(chain, authType);
                        }
                        else {
                            throw e;
                        }
                    }
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{mergedTm}, null);

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory() {
                @Override
                protected java.net.HttpURLConnection openConnection(java.net.@NonNull URL url, java.net.Proxy proxy) throws IOException {
                    java.net.HttpURLConnection conn = super.openConnection(url, proxy);
                    if (conn instanceof javax.net.ssl.HttpsURLConnection) {
                        ((javax.net.ssl.HttpsURLConnection) conn).setSSLSocketFactory(sslContext.getSocketFactory());
                    }
                    return conn;
                }
            };

            return new RestTemplate(factory);
        }
        catch (Exception e) {
            log.warn("Could not build merged SSL context for Google OAuth, using default RestTemplate: {}", e.getMessage());
            return new RestTemplate();
        }
    }

    private X509TrustManager findX509TrustManager(TrustManagerFactory tmf) {
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) return (X509TrustManager) tm;
        }
        throw new IllegalStateException("No X509TrustManager found");
    }
}

