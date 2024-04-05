/*
 * Steganography utility to hide messages into cover files
 * Author: Samir Vaidya (mailto:syvaidya@gmail.com)
 * Copyright (c) 2017 Samir Vaidya
 */
package com.DIRP.desktop.util;

import com.DIRP.desktop.DIRP;
import com.DIRP.desktop.DIRPErrors;
import com.DIRP.desktop.DIRPException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * User preferences manager
 */
public class UserPreferences {
    private static final String PREF_FILENAME = "DIRP.cfg";
    private static final String DEFAULT_PREF_FILENAME = "DIRP.default.cfg";
    private static Properties prefs = null;

    /**
     * Protected constructor. Expose only static methods
     */
    protected UserPreferences() {
        // Do nothing
    }

    /**
     * Initialize the preferences
     *
     * @throws DIRPException Processing issues
     */
    public static void init() throws DIRPException {
        if (prefs != null) {
            return;
        }

        prefs = new Properties();

        try {
            // Find the path where config file should be stored
            String userHome = System.getProperty("user.home");
            String configHome = System.getenv("XDG_CONFIG_HOME");
            if (configHome == null || configHome.trim().length() == 0) {
                configHome = userHome + File.separator + ".config";
            }

            // Create config directory if it does not exist
            Path configPath = Paths.get(configHome, "DIRP");
            if (Files.notExists(configPath)) {
                Files.createDirectories(configPath);
            }

            // Create preference file if it does not exist
            Path prefFile = configPath.resolve(PREF_FILENAME);
            if (Files.notExists(prefFile)) {
                // First check if old style "DIRP.ini" file is present in user home
                Path oldPrefFile = Paths.get(userHome, "DIRP.ini");
                if (Files.exists(oldPrefFile)) {
                    Files.copy(oldPrefFile, prefFile);
                    Files.delete(oldPrefFile);
                } else {
                    // Otherwise use the default template from application bundle
                    try (InputStream tmplIS = UserPreferences.class.getResourceAsStream("/" + DEFAULT_PREF_FILENAME)) {
                        assert tmplIS != null;
                        Files.copy(tmplIS, prefFile, REPLACE_EXISTING);
                    }
                }
            }

            try (InputStream prefFileIS = Files.newInputStream(prefFile)) {
                prefs.load(prefFileIS);
            }
        } catch (IOException e) {
            throw new DIRPException(e);
        }
    }

    /**
     * Returns the user preference in form of string
     *
     * @param key Preference key
     * @return value
     */
    public static String getString(String key) {
        String val = prefs.getProperty(key);
        if (val == null) {
            return null;
        }
        return val.trim();
    }

    /**
     * Returns the user preference in form of integer
     *
     * @param key Preference key
     * @return value
     * @throws DIRPException Processing issues
     */
    @SuppressWarnings("unused")
    public static Integer getInteger(String key) throws DIRPException {
        String val = getString(key);
        if (val == null) {
            return null;
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            throw new DIRPException(null, DIRP.NAMESPACE, DIRPErrors.USERPREF_INVALID_INT, key);
        }
    }

    /**
     * Returns the user preference in form of float
     *
     * @param key Preference key
     * @return value
     * @throws DIRPException Processing issues
     */
    public static Float getFloat(String key) throws DIRPException {
        String val = getString(key);
        if (val == null) {
            return null;
        }
        try {
            return Float.parseFloat(val);
        } catch (NumberFormatException e) {
            throw new DIRPException(null, DIRP.NAMESPACE, DIRPErrors.USERPREF_INVALID_FLOAT, key);
        }
    }

    /**
     * Returns the user preference in form of boolean
     *
     * @param key Preference key
     * @return value
     * @throws DIRPException Processing issues
     */
    @SuppressWarnings("unused")
    public static Boolean getBoolean(String key) throws DIRPException {
        String val = getString(key);
        if (val == null) {
            return null;
        }
        val = val.toLowerCase();
        if ("t".equals(val) || "true".equals(val) || "y".equals(val) || "yes".equals(val) || "1".equals(val)) {
            return true;
        } else if ("f".equals(val) || "false".equals(val) || "n".equals(val) || "no".equals(val) || "0".equals(val)) {
            return false;
        } else {
            throw new DIRPException(null, DIRP.NAMESPACE, DIRPErrors.USERPREF_INVALID_BOOL, key);
        }
    }
}
