package org.formentor.magnolia.ai.domain;

public enum ImageSize {
    Size256 ("256x256"),
    Size512 ("512x512"),
    Size1024 ("1024x1024");

    public final String value;
    ImageSize (String value) {
        this.value = value;
    }
}
