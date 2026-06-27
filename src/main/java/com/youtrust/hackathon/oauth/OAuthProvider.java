package com.youtrust.hackathon.oauth;

/**
 * 対応する（または将来対応予定の）OAuth プロバイダ。
 * GOOGLE / LINE は将来追加予定。アダプタを実装して登録するだけで有効化できる（Open/Closed）。
 */
public enum OAuthProvider {
    GITHUB,
    GOOGLE,
    LINE
}
