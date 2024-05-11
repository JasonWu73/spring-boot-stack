package net.wuxianjie.myspringbootstarter.util;

public class ObjUtils {

    /**
     * 判断对象是否为指定类的实例。
     *
     * @param obj 需要判断的对象
     * @param className 指定类的全限定名
     * @return 如果对象是指定类的实例则返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isInstanceOf(Object obj, String className) {
        if (obj == null) return false;
        return obj.getClass().getName().equals(className);
    }
}
