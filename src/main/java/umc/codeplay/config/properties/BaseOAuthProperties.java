package umc.codeplay.config.properties;

import lombok.Data;

@Data
public class BaseOAuthProperties {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scope;
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String additionalParameters;

    public String getUrl() {
        return authorizationUri
                + "?client_id="
                + clientId
                + "&redirect_uri="
                + redirectUri
                + "&response_type=code"
                + "&scope="
                + scope
                + additionalParameters;
    }
}
