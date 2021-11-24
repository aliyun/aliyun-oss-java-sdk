package com.aliyun.oss.crypto;

import java.util.HashMap;
import java.util.Map;

public class CryptoSchemeBaseFactory {
    private static Map<ContentCryptoMode, CryptoModuleBase> cryptoSchemeBaseMap = new HashMap<ContentCryptoMode, CryptoModuleBase>();
    public CryptoSchemeBaseFactory(OSSDirect OSS,
                                   EncryptionMaterials encryptionMaterials,
                                   CryptoConfiguration cryptoConfig){
        cryptoSchemeBaseMap.put(ContentCryptoMode.AES_CTR_MODE, new CryptoModuleAesCtr(OSS, encryptionMaterials, cryptoConfig));
        cryptoSchemeBaseMap.put(ContentCryptoMode.SM4_CTR_MODE, new CryptoModuleSm4Ctr(OSS, encryptionMaterials, cryptoConfig));
    }

    public static CryptoModuleBase buildCryptoSchemeBase(ContentCryptoMode cryptoMode){
        return cryptoSchemeBaseMap.get(cryptoMode);
    }
}
