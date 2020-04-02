package com.fyp.auth;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Base64;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import khangtran.preferenceshelper.PrefHelper;

public class Decryption {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private KeyStore keyStore;

    public Decryption() throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            IOException {
        initKeyStore();
    }

    private void initKeyStore() throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String decryptData(final String alias, final byte[] encryptedData, final byte[] encryptionIv)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), spec);

        return new String(cipher.doFinal(encryptedData), "UTF-8");
    }

    private SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException,
            UnrecoverableEntryException, KeyStoreException {
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
    }

    public byte[] get(String key) {
        String encryptedKeyB64 = PrefHelper.getStringVal(key, null);
        try {
            byte[] encryptedKey = Base64.decode(encryptedKeyB64, Base64.DEFAULT);
            return encryptedKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public void  getDecryptData() throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException, UnrecoverableEntryException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
//        keyStore = AndroidKeystore.getInstance(ANDROID_KEY_STORE);
//        keyStore.load(null);
//
//        final AndroidKeystore.SecretKeyEntry secretKeyEntry = (AndroidKeystore.SecretKeyEntry) keyStore
//                .getEntry("kydalias", null);
//
//        final SecretKey secretKey = secretKeyEntry.getSecretKey();
//
//        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
//        final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
//        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
//        final byte[] decodedData = cipher.doFinal(encryptedData);
//    }
}
