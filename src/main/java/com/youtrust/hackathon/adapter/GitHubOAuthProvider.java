package com.youtrust.hackathon.adapter;

import com.youtrust.hackathon.exception.ValidationException;
import com.youtrust.hackathon.model.OAuthProfile;
import com.youtrust.hackathon.oauth.OAuthProvider;
import com.youtrust.hackathon.port.OAuthIdentityProvider;

/**
 * GitHub OAuth アダプタ（モック）。
 *
 * 本来は authorizationCode をアクセストークンに交換し GitHub API でプロフィールを取得するが、
 * ハッカソンではシークレット・実HTTP を持たないため擬似プロフィールを返す。
 * 実認可フローへの差し替えは DECISIONS.md に「次の一手」として記録する。
 */
public class GitHubOAuthProvider implements OAuthIdentityProvider {

    @Override
    public OAuthProvider provider() {
        return OAuthProvider.GITHUB;
    }

    @Override
    public OAuthProfile fetchProfile(String authorizationCode) {
        if (authorizationCode == null || authorizationCode.trim().isEmpty()) {
            throw new ValidationException("GitHub の認可コードが空です");
        }
        String providerUserId = "gh_" + authorizationCode;
        String email = providerUserId + "@users.noreply.github.com";
        String name = "GitHub User " + authorizationCode;
        return new OAuthProfile(OAuthProvider.GITHUB, providerUserId, email, name);
    }
}
