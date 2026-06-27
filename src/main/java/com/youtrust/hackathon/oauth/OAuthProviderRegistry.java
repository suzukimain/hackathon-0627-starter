package com.youtrust.hackathon.oauth;

import com.youtrust.hackathon.exception.ValidationException;
import com.youtrust.hackathon.port.OAuthIdentityProvider;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 利用可能な OAuth アダプタを enum で解決する Strategy レジストリ。
 * 新プロバイダの追加は、アダプタ実装をこのレジストリに渡すだけ（コア・入口は無変更）。
 */
public class OAuthProviderRegistry {

    private final Map<OAuthProvider, OAuthIdentityProvider> providers =
            new EnumMap<>(OAuthProvider.class);

    public OAuthProviderRegistry(List<OAuthIdentityProvider> available) {
        for (OAuthIdentityProvider provider : available) {
            providers.put(provider.provider(), provider);
        }
    }

    public OAuthIdentityProvider resolve(OAuthProvider provider) {
        OAuthIdentityProvider found = providers.get(provider);
        if (found == null) {
            throw new ValidationException("未対応の OAuth プロバイダです: " + provider);
        }
        return found;
    }
}
