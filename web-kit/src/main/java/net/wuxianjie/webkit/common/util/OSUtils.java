package net.wuxianjie.webkit.common.util;

/**
 * 操作系统工具类。
 */
public class OSUtils {

    // 获取操作系统名称，然后根据名称中包含的关键字来判断具体是哪种操作系统
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

    /**
     * 判断当前系统是否为 Windows。
     *
     * @return 是否为 Windows 系统
     */
    public static boolean isWindows() {
        return OS_NAME.contains("win");
    }

    /**
     * 判断当前系统是否为 macOS。
     *
     * @return 是否为 macOS 系统
     */
    public static boolean isMac() {
        return OS_NAME.contains("mac");
    }

    /**
     * 判断当前系统是否为 Linux。
     *
     * @return 是否为 Linux 系统
     */
    public static boolean isLinux() {
        return OS_NAME.contains("linux");
    }

    public static String getOsName() {
        if (isWindows()) {
            return "Windows";
        }
        if (isMac()) {
            return "macOS";
        }
        if (isLinux()) {
            return "Linux";
        }
        return "其他";
    }

}
