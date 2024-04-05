/*
 * Steganography utility to hide messages into cover files
 * Author: Samir Vaidya (mailto:syvaidya@gmail.com)
 * Copyright (c) Samir Vaidya
 */

package com.DIRP.desktop.plugin.dwtkim;

import com.DIRP.desktop.DIRPException;

/**
 * Class to store error codes for DWT Kim plugin
 */
public class DWTKimErrors {
    /**
     * Error Code - No cover file given
     */
    public static final int ERR_NO_COVER_FILE = 1;

    /**
     * Error Code - Image decomposition levels are not enough
     */
    public static final int ERR_DECOMP_LEVEL_NOT_ENOUGH = 2;

    /**
     * Error Code - Invalid signature file provided
     */
    public static final int ERR_SIG_NOT_VALID = 3;

    /**
     * Error Code - file size not enough to embed watermark
     */
    public static final int ERR_FILE_TOO_SMALL = 3;

    /**
     * Initialize the error code - message key map
     */
    public static void init() {
        DIRPException.addErrorCode(DWTKimPlugin.NAMESPACE, ERR_NO_COVER_FILE, "err.cover.missing");
        DIRPException.addErrorCode(DWTKimPlugin.NAMESPACE, ERR_DECOMP_LEVEL_NOT_ENOUGH, "err.image.decompLevel.notEnough");
        DIRPException.addErrorCode(DWTKimPlugin.NAMESPACE, ERR_SIG_NOT_VALID, "err.signature.invalid");
    }
}
