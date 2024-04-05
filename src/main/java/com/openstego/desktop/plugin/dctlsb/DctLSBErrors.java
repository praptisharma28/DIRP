/*
 * Steganography utility to hide messages into cover files
 * Author: Samir Vaidya (mailto:syvaidya@gmail.com)
 * Copyright (c) Samir Vaidya
 */

package com.DIRP.desktop.plugin.dctlsb;

import com.DIRP.desktop.DIRPException;

/**
 * Class to store error codes for DCT LSB plugin
 */
public class DctLSBErrors {
    /**
     * Error Code - Error while reading image data
     */
    public static final int ERR_IMAGE_DATA_READ = 1;

    /**
     * Error Code - Image size insufficient for data
     */
    public static final int IMAGE_SIZE_INSUFFICIENT = 2;

    /**
     * Initialize the error code - message key map
     */
    public static void init() {
        DIRPException.addErrorCode(DctLSBPlugin.NAMESPACE, ERR_IMAGE_DATA_READ, "err.image.read");
        DIRPException.addErrorCode(DctLSBPlugin.NAMESPACE, IMAGE_SIZE_INSUFFICIENT, "err.image.insufficientSize");
    }
}
