/*
 * Steganography utility to hide messages into cover files
 * Author: Samir Vaidya (mailto:syvaidya@gmail.com)
 * Copyright (c) Samir Vaidya
 */

package com.DIRP.desktop.plugin.dwtdugad;

import com.DIRP.desktop.DIRPException;

/**
 * Class to store error codes for DWT Xie plugin
 */
public class DWTDugadErrors {
    /**
     * Error Code - No cover file given
     */
    public static final int ERR_NO_COVER_FILE = 1;

    /**
     * Error Code - Invalid signature file provided
     */
    public static final int ERR_SIG_NOT_VALID = 2;

    /**
     * Error Code - file size not enough to embed watermark
     */
    public static final int ERR_FILE_TOO_SMALL = 3;

    /**
     * Initialize the error code - message key map
     */
    public static void init() {
        DIRPException.addErrorCode(DWTDugadPlugin.NAMESPACE, ERR_NO_COVER_FILE, "err.cover.missing");
        DIRPException.addErrorCode(DWTDugadPlugin.NAMESPACE, ERR_SIG_NOT_VALID, "err.signature.invalid");
        DIRPException.addErrorCode(DWTDugadPlugin.NAMESPACE, ERR_FILE_TOO_SMALL, "err.file.too.small");
    }
}
