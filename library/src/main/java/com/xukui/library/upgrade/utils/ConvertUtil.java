package com.xukui.library.upgrade.utils;

public class ConvertUtil {

    /**
     * KB与Byte的倍数
     */
    public static final int KB = 1024;
    /**
     * MB与Byte的倍数
     */
    public static final int MB = 1048576;
    /**
     * GB与Byte的倍数
     */
    public static final int GB = 1073741824;

    /**
     * 字节数转合适内存大小
     * <p>保留3位小数</p>
     *
     * @param byteNum 字节数
     * @return 合适内存大小
     */
    public static String byte2FitMemorySize(long byteNum) {
        if (byteNum < 0) {
            return byteNum + "";

        } else if (byteNum < KB) {
            return String.format("%.3fB", byteNum + 0.0005);

        } else if (byteNum < MB) {
            return String.format("%.3fKB", byteNum / KB + 0.0005);

        } else if (byteNum < GB) {
            return String.format("%.3fMB", byteNum / MB + 0.0005);

        } else {
            return String.format("%.3fGB", byteNum / GB + 0.0005);
        }
    }

}