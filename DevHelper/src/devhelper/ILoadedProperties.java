/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devhelper;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Locale;

/**
 *
 * @author lerwi
 */
public interface ILoadedProperties extends Map<String, String> {
    Locale getLocale();
    void list(PrintStream out);
    void list(PrintWriter out);
    String get(String key, String defaultValue);
}
