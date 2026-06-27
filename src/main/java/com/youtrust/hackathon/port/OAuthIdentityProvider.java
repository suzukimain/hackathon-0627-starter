package com.youtrust.hackathon.port;

import com.youtrust.hackathon.model.OAuthProfile;
import com.youtrust.hackathon.oauth.OAuthProvider;

/**
 * OAuth プロバイダのアダプタが実装するポート。認可コードを正規化済みプロフィールに変換する。
 * 新しいプロバイダ（Google/LINE）はこの interface を実装して Registry に登録するだけで追加できる。
 */
public interface OAuthIdentityProvider {

    OAuthProvider provider();

    OAuthProfile fetchProfile(String authorizationCode);
}
