/*
 * Steganography utility to hide messages into cover files
 * Author: Samir Vaidya (mailto:syvaidya@gmail.com)
 * Copyright (c) Samir Vaidya
 */

package com.DIRP.desktop.plugin.template.image;

import com.DIRP.desktop.DIRPConfig;
import com.DIRP.desktop.DIRPException;
import com.DIRP.desktop.WatermarkingPlugin;
import com.DIRP.desktop.ui.DIRPFrame;
import com.DIRP.desktop.ui.PluginEmbedOptionsUI;
import com.DIRP.desktop.util.ImageHolder;
import com.DIRP.desktop.util.ImageUtil;
import com.DIRP.desktop.util.cmd.CmdLineOptions;

import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Template plugin for DIRP which implements image based steganography for watermarking
 */
public abstract class WMImagePluginTemplate extends WatermarkingPlugin<DIRPConfig> {
    /**
     * Static list of supported read formats
     */
    protected static List<String> readFormats = null;

    /**
     * Static list of supported write formats
     */
    protected static List<String> writeFormats = null;

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
    @Override
    public final byte[] getDiff(byte[] stegoData, String stegoFileName, byte[] coverData, String coverFileName, String diffFileName)
            throws DIRPException {
        ImageHolder stegoImage;
        ImageHolder coverImage;
        ImageHolder diffImage;

        stegoImage = ImageUtil.byteArrayToImage(stegoData, stegoFileName);
        coverImage = ImageUtil.byteArrayToImage(coverData, coverFileName);
        diffImage = ImageUtil.getDiffImage(stegoImage, coverImage);

        return ImageUtil.imageToByteArray(diffImage, diffFileName, this);
    }

    /**
     * Method to get the list of supported file extensions for reading
     *
     * @return List of supported file extensions for reading
     */
    @Override
    public List<String> getReadableFileExtensions() {
        if (readFormats != null) {
            return readFormats;
        }

        String format;
        String[] formats;
        readFormats = new ArrayList<>();

        formats = ImageIO.getReaderFormatNames();
        for (String s : formats) {
            format = s.toLowerCase();
            if (format.contains("jpeg") && format.contains("2000")) {
                format = "jp2";
            }
            if (!readFormats.contains(format)) {
                readFormats.add(format);
            }
        }

        Collections.sort(readFormats);
        return readFormats;
    }

    /**
     * Method to get the list of supported file extensions for writing
     *
     * @return List of supported file extensions for writing
     * @throws DIRPException Processing issues
     */
    @Override
    public List<String> getWritableFileExtensions() throws DIRPException {
        if (writeFormats != null) {
            return writeFormats;
        }

        String format;
        String[] formats;
        writeFormats = new ArrayList<>();

        formats = ImageIO.getWriterFormatNames();
        for (String s : formats) {
            format = s.toLowerCase();
            if (format.contains("jpeg") && format.contains("2000")) {
                format = "jp2";
            }
            if (!writeFormats.contains(format)) {
                writeFormats.add(format);
            }
        }

        Collections.sort(writeFormats);
        return writeFormats;
    }

    /**
     * Method to populate the standard command-line options used by this plugin
     *
     * @param options Existing command-line options. Plugin-specific options will get added to this list
     */
    @Override
    public void populateStdCmdLineOptions(CmdLineOptions options) {
    }

    /**
     * Method to create default configuration data (specific to this plugin)
     *
     * @return Configuration data
     */
    @Override
    protected DIRPConfig createConfig() {
        return new DIRPConfig();
    }

    /**
     * Method to create configuration data (specific to this plugin) based on the command-line options
     *
     * @param options Command-line options
     * @return Configuration data
     * @throws DIRPException Processing issues
     */
    @Override
    protected DIRPConfig createConfig(CmdLineOptions options) throws DIRPException {
        DIRPConfig config = new DIRPConfig();
        config.initialize(options);
        return config;
    }
}
