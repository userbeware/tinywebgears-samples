package com.tinywebgears.relayme.util;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.GoogleApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;


public class OAuthHelperV1 implements OAuthHelper
{
    private static final String TAG = "OAuthHelperV1";
    public static final String OAUTH_APP_KEY = "anonymous";
    public static final String OAUTH_APP_SECRET = "anonymous";
    public static final String URL_OAUTH_AUTHORIZE = "https://www.google.com/accounts/OAuthAuthorizeToken?oauth_token=";
    public static final String OAUTH_PARAM_VERIFIER = "oauth_verifier";

    private OAuthService service;
    private Token requestToken;

    private static OAuthHelperV1 oauthHelper;

    static
    {
        if (oauthHelper == null)
        {
            String scope = OAUTH_SCOPE_EMAIL + " " + OAUTH_SCOPE_GMAIL;
            oauthHelper = new OAuthHelperV1(OAUTH_APP_KEY, OAUTH_APP_SECRET, scope, OAUTH_CALLBACK_URL, OAUTH_APP_NAME);
        }
    }

    public static OAuthHelperV1 get()
    {
        return oauthHelper;
    }

    public OAuthHelperV1(String consumerKey, String consumerSecret, String scope, String callbackUrl, String appname)
    {
        service = new ServiceBuilder().provider(GoogleApi.class).apiKey(consumerKey).apiSecret(consumerSecret)
                .scope(scope).callback(callbackUrl).build();
    }

    @Override
    public String getAuthorizationUrl()
    {
        requestToken = service.getRequestToken();
        return URL_OAUTH_AUTHORIZE + requestToken.getToken();
    }

    @Override
    public String extractVerifier(String uri)
    {
        if (uri.indexOf(OAUTH_PARAM_VERIFIER + "=") != -1)
            return UriUtil.getParam(uri, OAUTH_PARAM_VERIFIER);
        return null;
    }

    @Override
    public Token getAccessToken(String verifier)
    {
        Token accessToken = service.getAccessToken(requestToken, new Verifier(verifier));
        return accessToken;
    }

    @Override
    public String makeSecuredRequest(Token accessToken, String url)
    {
        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        service.signRequest(accessToken, request);
        request.addHeader("GData-Version", "3.0");
        Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getCode());
        System.out.println(response.getBody());
        return response.getBody();
    }

    @Override
    public void signRequest(Token accessToken, OAuthRequest request)
    {
        service.signRequest(accessToken, request);
    }
}
