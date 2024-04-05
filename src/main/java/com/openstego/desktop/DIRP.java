/*
 * Steganography utility to hide messages into cover files
 * Author: Samir Vaidya (mailto:syvaidya@gmail.com)
 * Copyright (c) Samir Vaidya
 */

package com.DIRP.desktop;

import com.DIRP.desktop.ui.DIRPUI;
import com.DIRP.desktop.util.CommonUtil;
import com.DIRP.desktop.util.LabelUtil;
import com.DIRP.desktop.util.PluginManager;
import com.DIRP.desktop.util.UserPreferences;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This is the main class for DIRP. It includes the {@link #main(String[])} method which provides the
 * command line interface for the tool. It also has API methods which can be used by external programs
 * when using DIRP as a library.
 */
public class DIRP {

    /**
     * Constant for the namespace for labels
     */
    public static final String NAMESPACE = "DIRP";

    /**
     * Configuration data
     */
    private final DIRPConfig config;

    /**
     * Stego plugin to use for embedding / extracting data
     */
    private final DIRPPlugin<?> plugin;

    static {
        LabelUtil.addNamespace(NAMESPACE, "i18n.DIRPLabels");
        DIRPErrors.init();
    }

    /**
     * Constructor using {@link DIRPConfig} object
     *
     * @param plugin Stego plugin to use
     * @param config DIRPConfig object with configuration data
     * @throws DIRPException Processing issues
     */
    public DIRP(DIRPPlugin<?> plugin, DIRPConfig config) throws DIRPException {
        // Plugin is mandatory
        if (plugin == null) {
            throw new DIRPException(null, NAMESPACE, DIRPErrors.NO_PLUGIN_SPECIFIED);
        }
        this.plugin = plugin;
        // Config is mandatory
        assert config != null;
        this.config = config;
    }

    /**
     * Method to embed the message data into the cover data
     *
     * @param msg           Message data to be embedded
     * @param msgFileName   Name of the message file
     * @param cover         Cover data into which message data needs to be embedded
     * @param coverFileName Name of the cover file
     * @param stegoFileName Name of the output stego file
     * @return Stego data containing the embedded message
     * @throws DIRPException Processing issues
     */
    public byte[] embedData(byte[] msg, String msgFileName, byte[] cover, String coverFileName, String stegoFileName) throws DIRPException {
        if (!this.plugin.getPurposes().contains(DIRPPlugin.Purpose.DATA_HIDING)) {
            throw new DIRPException(null, DIRP.NAMESPACE, DIRPErrors.PLUGIN_DOES_NOT_SUPPORT_DH);
        }

        try {
            // Compress data, if requested
            if (this.config.isUseCompression()) {
                try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); GZIPOutputStream zos = new GZIPOutputStream(bos)) {
                    zos.write(msg);
                    zos.finish();
                    zos.flush();
                    msg = bos.toByteArray();
                }
            }

            // Encrypt data, if requested
            if (this.config.isUseEncryption()) {
                DIRPCrypto crypto = new DIRPCrypto(this.config.getPassword(), this.config.getEncryptionAlgorithm());
                msg = crypto.encrypt(msg);
            }

            return this.plugin.embedData(msg, msgFileName, cover, coverFileName, stegoFileName);
        } catch (DIRPException osEx) {
            throw osEx;
        } catch (Exception ex) {
            throw new DIRPException(ex);
        }
    }

    /**
     * Method to embed the message data into the cover data (alternate API)
     *
     * @param msgFile       File containing the message data to be embedded
     * @param coverFile     Cover file into which data needs to be embedded
     * @param stegoFileName Name of the output stego file
     * @return Stego data containing the embedded message
     * @throws DIRPException Processing issues
     */
    public byte[] embedData(File msgFile, File coverFile, String stegoFileName) throws DIRPException {
        String filename = null;

        // If no message file is provided, then read the data from stdin
        try (InputStream is = (msgFile == null ? System.in : Files.newInputStream(msgFile.toPath()))) {
            if (msgFile != null) {
                filename = msgFile.getName();
            }

            return embedData(CommonUtil.streamToBytes(is), filename,
                    coverFile == null ? null : CommonUtil.fileToBytes(coverFile),
                    coverFile == null ? null : coverFile.getName(), stegoFileName);
        } catch (IOException ioEx) {
            throw new DIRPException(ioEx);
        }
    }

    /**
     * Method to embed the watermark signature data into the cover data
     *
     * @param sig           Signature data to be embedded
     * @param sigFileName   Name of the signature file
     * @param cover         Cover data into which signature data needs to be embedded
     * @param coverFileName Name of the cover file
     * @param stegoFileName Name of the output stego file
     * @return Stego data containing the embedded signature
     * @throws DIRPException Processing issues
     */
    public byte[] embedMark(byte[] sig, String sigFileName, byte[] cover, String coverFileName, String stegoFileName) throws DIRPException {
        if (!this.plugin.getPurposes().contains(DIRPPlugin.Purpose.WATERMARKING)) {
            throw new DIRPException(null, DIRP.NAMESPACE, DIRPErrors.PLUGIN_DOES_NOT_SUPPORT_WM);
        }

        try {
            // No compression and encryption should be done as this is signature data
            return this.plugin.embedData(sig, sigFileName, cover, coverFileName, stegoFileName);
        } catch (DIRPException osEx) {
            throw osEx;
        } catch (Exception ex) {
            throw new DIRPException(ex);
        }
    }

    /**
     * Method to embed the watermark signature data into the cover data (alternate API)
     *
     * @param sigFile       File containing the signature data to be embedded
     * @param coverFile     Cover file into which data needs to be embedded
     * @param stegoFileName Name of the output stego file
     * @return Stego data containing the embedded signature
     * @throws DIRPException Processing issues
     */
    public byte[] embedMark(File sigFile, File coverFile, String stegoFileName) throws DIRPException {
        String filename = null;

        // If no signature file is provided, then read the data from stdin
        try (InputStream is = (sigFile == null ? System.in : Files.newInputStream(sigFile.toPath()))) {
            if (sigFile != null) {
                filename = sigFile.getName();
            }

            return embedMark(CommonUtil.streamToBytes(is), filename, coverFile == null ? null : CommonUtil.fileToBytes(coverFile),
                    coverFile == null ? null : coverFile.getName(), stegoFileName);
        } catch (IOException ioEx) {
            throw new DIRPException(ioEx);
        }
    }

    /**
     * Method to extract the message data from stego data
     *
     * @param stegoData     Stego data from which the message needs to be extracted
     * @param stegoFileName Name of the stego file
     * @return Extracted message (List's first element is filename and second element is the message as byte array)
     * @throws DIRPException Processing issues
     */
    public List<?> extractData(byte[] stegoData, String stegoFileName) throws DIRPException {
        if (!this.plugin.getPurposes().contains(DIRPPlugin.Purpose.DATA_HIDING)) {
            throw new DIRPException(null, DIRP.NAMESPACE, DIRPErrors.PLUGIN_DOES_NOT_SUPPORT_DH);
        }

        byte[] msg;
        List<Object> output = new ArrayList<>();

        try {
            // Add file name as first element of output list
            output.add(this.plugin.extractMsgFileName(stegoData, stegoFileName));
            msg = this.plugin.extractData(stegoData, stegoFileName, null);

            // Decrypt data, if required
            if (this.config.isUseEncryption()) {
                DIRPCrypto crypto = new DIRPCrypto(this.config.getPassword(), this.config.getEncryptionAlgorithm());
                msg = crypto.decrypt(msg);
            }

            // Decompress data, if required
            if (this.config.isUseCompression()) {
                try (ByteArrayInputStream bis = new ByteArrayInputStream(msg); GZIPInputStream zis = new GZIPInputStream(bis)) {
                    msg = CommonUtil.streamToBytes(zis);
                } catch (IOException ioEx) {
                    throw new DIRPException(ioEx, DIRP.NAMESPACE, DIRPErrors.CORRUPT_DATA);
                }
            }

            // Add message as second element of output list
            output.add(msg);
        } catch (DIRPException osEx) {
            throw osEx;
        } catch (Exception ex) {
            throw new DIRPException(ex);
        }

        return output;
    }

    /**
     * Method to extract the message data from stego data (alternate API)
     *
     * @param stegoFile Stego file from which message needs to be extracted
     * @return Extracted message (List's first element is filename and second element is the message as byte array)
     * @throws DIRPException Processing issues
     */
    public List<?> extractData(File stegoFile) throws DIRPException {
        return extractData(CommonUtil.fileToBytes(stegoFile), stegoFile.getName());
    }

    /**
     * Method to check the correlation for the given image and the original signature
     *
     * @param stegoData     Stego data containing the watermark
     * @param stegoFileName Name of the stego file
     * @param origSigData   Original signature data
     * @return Correlation
     * @throws DIRPException Processing issues
     */
    public double checkMark(byte[] stegoData, String stegoFileName, byte[] origSigData) throws DIRPException {
        if (!this.plugin.getPurposes().contains(DIRPPlugin.Purpose.WATERMARKING)) {
            throw new DIRPException(null, DIRP.NAMESPACE, DIRPErrors.PLUGIN_DOES_NOT_SUPPORT_WM);
        }

        double correl = this.plugin.checkMark(stegoData, stegoFileName, origSigData);
        if (Double.isNaN(correl)) {
            correl = 0.0;
        }
        return correl;
    }

    /**
     * Method to check the correlation for the given image and the original signature (alternate API)
     *
     * @param stegoFile   Stego file from which watermark needs to be extracted
     * @param origSigFile Original signature file
     * @return Correlation
     * @throws DIRPException Processing issues
     */
    public double checkMark(File stegoFile, File origSigFile) throws DIRPException {
        return checkMark(CommonUtil.fileToBytes(stegoFile), stegoFile.getName(), CommonUtil.fileToBytes(origSigFile));
    }

    /**
     * Method to generate the signature data using the given plugin
     *
     * @return Signature data
     * @throws DIRPException Processing issues
     */
    public byte[] generateSignature() throws DIRPException {
        if (!this.plugin.getPurposes().contains(DIRPPlugin.Purpose.WATERMARKING)) {
            throw new DIRPException(null, DIRP.NAMESPACE, DIRPErrors.PLUGIN_DOES_NOT_SUPPORT_WM);
        }

        if (this.config.getPassword() == null || this.config.getPassword().trim().length() == 0) {
            throw new DIRPException(null, DIRP.NAMESPACE, DIRPErrors.PWD_MANDATORY_FOR_GENSIG);
        }

        return this.plugin.generateSignature();
    }

    /**
     * Method to get difference between original cover file and the stegged file
     *
     * @param stegoData     Stego data containing the embedded data
     * @param stegoFileName Name of the stego file
     * @param coverData     Original cover data
     * @param coverFileName Name of the cover file
     * @param diffFileName  Name of the output difference file
     * @return Difference data
     * @throws DIRPException Processing issues
     */
    public byte[] getDiff(byte[] stegoData, String stegoFileName, byte[] coverData, String coverFileName, String diffFileName)
            throws DIRPException {
        return this.plugin.getDiff(stegoData, stegoFileName, coverData, coverFileName, diffFileName);
    }

    /**
     * Method to get difference between original cover file and the stegged file
     *
     * @param stegoFile    Stego file containing the embedded data
     * @param coverFile    Original cover file
     * @param diffFileName Name of the output difference file
     * @return Difference data
     * @throws DIRPException Processing issues
     */
    public byte[] getDiff(File stegoFile, File coverFile, String diffFileName) throws DIRPException {
        return getDiff(CommonUtil.fileToBytes(stegoFile), stegoFile.getName(), CommonUtil.fileToBytes(coverFile), coverFile.getName(), diffFileName);
    }

    /**
     * Get method for configuration data
     *
     * @return Configuration data
     */
    public DIRPConfig getConfig() {
        return this.config;
    }

    /**
     * Main method for calling DIRP from command line.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            // Load the stego plugins
            PluginManager.loadPlugins();
            // Initialize preferences
            UserPreferences.init();

            if (args.length == 0) { // Start GUI
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    // Ignore
                }
                // Determine default DH and WM plugins
                DIRPPlugin<?> dhPlugin = PluginManager.getPluginByName("RandomLSB");
                DIRPPlugin<?> wmPlugin = PluginManager.getPluginByName("DWTDugad");
                new DIRPUI(dhPlugin, wmPlugin).setVisible(true);
            } else {
                DIRPCmd.execute(args);
            }
        } catch (DIRPException osEx) {
            if (osEx.getErrorCode() == DIRPException.UNHANDLED_EXCEPTION) {
                osEx.printStackTrace(System.err);
            } else {
                System.err.println(osEx.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

}
