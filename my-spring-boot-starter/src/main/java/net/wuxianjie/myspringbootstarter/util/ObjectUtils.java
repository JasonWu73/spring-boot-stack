package net.wuxianjie.myspringbootstarter.util;

public class ObjectUtils {

    /**
     * 判断对象是否为指定类的实例。
     *
     * @param object 需要判断的对象
     * @param className 指定类的全限定名
     * @return 如果对象是指定类的实例则返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isInstanceOf(Object object, String className) {
        if (object == null) return false;
        return object.getClass().getName().equals(className);
    }
}
