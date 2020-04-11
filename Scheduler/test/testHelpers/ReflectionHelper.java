/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testHelpers;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ReflectionHelper {
    public static final Function<Type, String> NULL_TYPE_PARAMETER = (t) -> String.format("(%s)null", t.getTypeName());
    public static final Function<Type, String> TYPE_NAME_PARAMETER = (t) -> t.getTypeName();
    
    private static final Pattern STRING_ENCODE = Pattern.compile("[\"\\u0000-\\u0019\\u007f-\\uffff\\\\]");
    
    public static <T extends Enum> String toJavaLiteral(T value) {
        if (null == value)
            return "null";
        return String.format("%s.%s", value.getClass().getTypeName(), value.name());
    }
    
    public static String toJavaLiteral(String value) {
        if (null == value)
            return "null";
        if (value.isEmpty())
            return "\"\"";
        
        StringBuffer sb = new StringBuffer();
        sb.append('"');
        Matcher matcher = STRING_ENCODE.matcher(value);
        while (matcher.find()) {
            String r = matcher.group(0);
            switch (r) {
                case "\r":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\\r"));
                    break;
                case "\n":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\\n"));
                    break;
                case "\t":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\\t"));
                    break;
                case "\b":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\\b"));
                    break;
                case "\f":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\\f"));
                    break;
                case "\"":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\\\""));
                    break;
                case "\\":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\\\\"));
                    break;
                default:
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(String.format("\\u%04x", matcher.group(0).codePointAt(0))));
                    break;
            }
        }
        matcher.appendTail(sb);
        return sb.append('"').toString();
    }
    
    public static String toJavaLiteral(Number value) {
        if (null == value)
            return "null";
        return String.format("%d", value);
    }
    
    public static String toJavaLiteral(LocalDateTime value) {
        if (null == value)
            return "null";
        return String.format("LocalDateTime.of(%04d, %02d, %02d, %02d, %02d, %02d, %d)", value.getYear(), value.getMonth(), value.getDayOfMonth(),
                value.getHour(), value.getMinute(), value.getSecond(), value.getNano());
    }
    
    public static String toJavaLiteral(Timestamp value) {
        if (null == value)
            return "null";
        
        return String.format("Timestamp.valueOf(\"%s\")", value.toString());
    }
    
    public static String toJavaLiteral(LocalDate value) {
        if (null == value)
            return "null";
        return String.format("LocalDate.of(%04d, %02d, %02d)", value.getYear(), value.getMonth(), value.getDayOfMonth());
    }
    
    public static String toJavaLiteral(boolean value) {
        return (value) ? "true" : "false";
    }
    
    public static <T extends Enum> Function<Type, String> toTypeParameter(T value) {
        if (null == value)
            return NULL_TYPE_PARAMETER;
        String text = toJavaLiteral(value);
        return (t) -> text;
    }
    
    public static Function<Type, String> toTypeParameter(Number value) {
        if (null == value)
            return NULL_TYPE_PARAMETER;
        String text = toJavaLiteral(value);
        return (t) -> text;
    }
    
    public static Function<Type, String> toTypeParameter(String value) {
        if (null == value)
            return NULL_TYPE_PARAMETER;
        String text = toJavaLiteral(value);
        return (t) -> text;
    }
    
    public static Function<Type, String> toTypeParameter(boolean value) {
        String text = toJavaLiteral(value);
        return (t) -> text;
    }
    
    public static Function<Type, String> toTypeParameter(LocalDateTime value) {
        if (null == value)
            return NULL_TYPE_PARAMETER;
        String text = toJavaLiteral(value);
        return (t) -> text;
    }
    
    public static Function<Type, String> toTypeParameter(LocalDate value) {
        if (null == value)
            return NULL_TYPE_PARAMETER;
        String text = toJavaLiteral(value);
        return (t) -> text;
    }
    
    public static Function<Type, String> toTypeParameter(Timestamp value) {
        if (null == value)
            return NULL_TYPE_PARAMETER;
        String text = toJavaLiteral(value);
        return (t) -> text;
    }
    
    public static String methodToString(Method method, Class<?> targetClass, Function<Type, String> ...parameterValues) {
        try {
            StringBuilder sb = new StringBuilder();
            Iterator<String> iterator = Arrays.stream(method.getTypeParameters()).map((t) -> t.toString()).iterator();
            if (iterator.hasNext()) {
                sb.append('<').append(iterator.next());
                while (iterator.hasNext()) {
                    sb.append(",").append(iterator.next());
                }
                sb.append("> ");
            }

            Type genRetType = method.getGenericReturnType();
            sb.append(genRetType.getTypeName()).append(' ').append(targetClass.getTypeName()).append('#').append(method.getName()).append('(');
            iterator = Arrays.stream(method.getGenericParameterTypes()).map(new Function<Type, String>() {
                private int index = -1;
                @Override
                public String apply(Type t) {
                    index++;
                    if (index < parameterValues.length)
                        return parameterValues[index].apply(t);
                    return t.getTypeName();
                }
            }).iterator();
            if (iterator.hasNext()) {
                String lastArg = iterator.next();
                if (iterator.hasNext()) {
                    sb.append(lastArg);
                    lastArg = iterator.next();
                    while (iterator.hasNext()) {
                        sb.append(",").append(lastArg);
                        lastArg = iterator.next();
                    }
                    sb.append(",");
                }
                if (method.isVarArgs())
                    sb.append(lastArg.replaceFirst("\\[\\]$", "..."));
                else
                    sb.append(lastArg);
            }
            return sb.append(')').toString();
        } catch (Exception e) {
            return "<" + e + ">";
        }
    }

    public static String methodToString(Method method, Function<Type, String> ...parameterValues) {
        return methodToString(method, method.getDeclaringClass(), parameterValues);
    }
    
}
