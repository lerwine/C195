/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 *
 * @author Leonard T. Erwine
 */
public class MergedResourceBundle  extends ResourceBundle {
    private final ResourceBundle innerBundle;
    private final Locale locale;
    
    public static final ResourceBundle getBundle(String baseName, String parentBaseName, Locale locale) {
        return new MergedResourceBundle(ResourceBundle.getBundle(baseName, locale), ResourceBundle.getBundle(parentBaseName, locale), locale);
    }
    
    public static final ResourceBundle getBundle(ResourceBundle source, String parentBaseName, Locale locale) {
        return new MergedResourceBundle(source, ResourceBundle.getBundle(parentBaseName, locale), locale);
    }
    
    public static final ResourceBundle getBundle(String baseName, Locale locale, ResourceBundle parent) {
        return new MergedResourceBundle(ResourceBundle.getBundle(baseName, locale), parent, locale);
    }
    
    private MergedResourceBundle(ResourceBundle source, ResourceBundle parent, Locale locale) {
        innerBundle = source;
        this.locale = locale;
        super.setParent(parent);
    }
    
    public MergedResourceBundle(ResourceBundle source, ResourceBundle parent) {
        this(source, parent, source.getLocale());
    }
    
    @Override
    protected Object handleGetObject(String key) {
        return (innerBundle.containsKey(key)) ? innerBundle.getObject(key) : parent.getObject(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return new Enumeration<String>() {
            final Iterator<String> iterator;
            {
                iterator = Stream.concat(innerBundle.keySet().stream(), innerBundle.keySet().stream().filter((String k) -> {
                    return !innerBundle.containsKey(k);
                })).iterator();
            }
            
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public String nextElement() {
                return iterator.next();
            }
        };
    }

    @Override
    public boolean containsKey(String key) {
        return innerBundle.containsKey(key) || parent.containsKey(key);
    }

    @Override
    public Locale getLocale() { return locale; }
}
