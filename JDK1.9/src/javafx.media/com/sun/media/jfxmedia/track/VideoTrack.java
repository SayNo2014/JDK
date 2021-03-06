/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.sun.media.jfxmedia.track;

import java.util.Locale;

/**
 * A class representing a video track in a media.
 *
 * @see com.sun.media.jfxmedia.Media
 */
public class VideoTrack extends Track
{
    private VideoResolution frameSize;
    private float           encodedFrameRate;
    private boolean         hasAlphaChannel;

    /**
     * Constructor.
     *
     * @param enabled Whether this track is enabled by default or not (if the container supports it)
     * @param trackID A unique identifier for this track.
     * @param name The name of the track.
     * @param locale The language information for this track, can be null.
     * @param encoding The encoding of the track.
     * @param frameSize The dimensions of the video frames in the track.
     * @param encodedFrameRate The encoded frame rate of the track.
     * @param hasAlphaChannel Whether the video frames contain an alpha channel.
     * @throws IllegalArgumentException if <code>name</code>,
     * <code>encoding</code>, or <code>frameSize</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>encodedFrameRate</code>
     * is negative.
     * @throws IllegalArgumentException if either frame dimension is
     * non-positive.
     */
    public VideoTrack(boolean enabled, long trackID, String name, Locale locale,
                      Encoding encoding, VideoResolution frameSize,
                      float encodedFrameRate, boolean hasAlphaChannel)
    {
        super(enabled, trackID, name, locale, encoding);

        if (frameSize == null) {
            throw new IllegalArgumentException("frameSize == null!");
        }
        if (frameSize.width <= 0) {
            throw new IllegalArgumentException("frameSize.width <= 0!");
        }
        if (frameSize.height <= 0) {
            throw new IllegalArgumentException("frameSize.height <= 0!");
        }
        // encodedFrameRate will be zero if it's unknown
        if (encodedFrameRate < 0.0F) {
            throw new IllegalArgumentException("encodedFrameRate < 0.0!");
        }

        this.frameSize = frameSize;
        this.encodedFrameRate = encodedFrameRate;
        this.hasAlphaChannel = hasAlphaChannel;
    }

    /**
     * Retrieve whether the video frames contain an alpha channel.
     *
     * @return Whether video has alpha channel.
     */
    public boolean hasAlphaChannel() {
        return this.hasAlphaChannel;
    }

    /**
     * Retrieve the encoded video frame rate. Note that this may differ from the
     * frame rate at which the track may be played.
     *     *
     * @return The frame rate at which the video was encoded.
     */
    public float getEncodedFrameRate() {
        return this.encodedFrameRate;
    }

    /**
     * Retrieve the dimensions of the video frames in the track.
     *
     * @return The video frame size.
     */
    public VideoResolution getFrameSize() {
        return this.frameSize;
    }


    @Override
    public final String toString() {
        return "VideoTrack {\n"+
                "    name: "+this.getName()+"\n"+
                "    encoding: "+this.getEncodingType()+"\n"+
                "    frameSize: "+frameSize+"\n"+
                "    encodedFrameRate: "+this.encodedFrameRate+"\n"+
                "    hasAlphaChannel: "+this.hasAlphaChannel+"\n"+
                "}";
    }
}
