package cn.chen.second.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class DBResultUtils {
    private static HashMap<Method, String> getMethodMap(Class c) {
        HashMap<Method, String> methodMap = new HashMap<>();
        for (Method method : c.getMethods()) {
            if (!method.getName().startsWith("set")) {
                continue;
            }
            StringBuilder sb = new StringBuilder(method.getName().substring(3));
//            sb.setCharAt(0, (char) (sb.charAt(0) + 32));
            for (int i = 0; i < sb.length(); i++) {
                char ch = sb.charAt(i);
                if (ch >= 65 && ch <= 90) {
                    // 大写字母
                    sb.setCharAt(i,  (char) (sb.charAt(i) + 32));
                    if (i == 0) {
                        continue;
                    }
                    sb.insert(i, '_');
                }
            }
            methodMap.put(method, sb.toString());
        }
        return methodMap;
    }
    private static <T> T getRowObject (ResultSet resultSet, Class<T> c) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, InstantiationException, SQLException {
        HashMap<Method, String> methodMap = getMethodMap(c);
        T o = c.getConstructor().newInstance();
        Set<Method> set = methodMap.keySet();
        for (Method key : set) {
            if ("int".equals(key.getParameterTypes()[0].getName())) {
                key.invoke(o, resultSet.getInt(methodMap.get(key)));
            } else if ("java.lang.String".equals(key.getParameterTypes()[0].getName())) {
                key.invoke(o, resultSet.getString(methodMap.get(key)));
            }
        }
        return o;
    }
    public static <T> T getResultObject(ResultSet resultSet, Class<T> c) {
        T o = null;
        try {
            if (resultSet.next()) {
                o = getRowObject(resultSet, c);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException | SQLException e) {
            e.printStackTrace();
        }
        return o;
    }
    public static <T> List<T> getResultObjectList(ResultSet resultSet, Class<T> c) {
        T o = null;
        List<T> list = new LinkedList<>();
        try {
            while (resultSet.next()) {
                list.add(getRowObject(resultSet, c));
            }
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException | SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
