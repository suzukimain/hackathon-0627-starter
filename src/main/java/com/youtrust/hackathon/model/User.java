package com.youtrust.hackathon.model;

public class User {

    private String id;
    private String email;
    private String name;
    /** パスワード登録のときのみ設定。OAuth 登録では null。 */
    private String password;
    /** OAuth 登録のときに設定（例: "GITHUB"）。パスワード登録では null。 */
    private String provider;
    /** OAuth プロバイダ側のユーザーID。パスワード登録では null。 */
    private String providerUserId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }
}
