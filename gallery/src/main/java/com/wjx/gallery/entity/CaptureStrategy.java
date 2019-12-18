package com.wjx.gallery.entity;

public class CaptureStrategy {

    public final boolean isPublic;
    public final String authority;
    public final String directory;

    public CaptureStrategy(boolean isPublic, String authority) {
        this(isPublic, authority, null);
    }

    /**
     * @param isPublic  是否将图片保存在共有目录下
     * @param authority FileProvider 路径
     * @param directory 图片具体保存目录
     */
    public CaptureStrategy(boolean isPublic, String authority, String directory) {
        this.isPublic = isPublic;
        this.authority = authority;
        this.directory = directory;
    }
}
