package com.youtrust.hackathon.model;

import com.youtrust.hackathon.oauth.OAuthProvider;

/**
 * 各 OAuth プロバイダから取得したプロフィールを正規化した表現。
 * プロバイダ差をここで吸収し、後続処理はこの共通形だけを扱う。
 */
public class OAuthProfile {

    private final OAuthProvider provider;
    private final String providerUserId;
    private final String email;
    private final String name;

    public OAuthProfile(OAuthProvider provider, String providerUserId, String email, String name) {
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.email = email;
        this.name = name;
    }

    public OAuthProvider getProvider() {
        return provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
