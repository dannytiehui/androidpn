/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.androidpn.server.util;

import java.util.List;

/** 
 * Utility class to obtain the values from configruation.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class Config {

    public static String getString(String key) {
        return ConfigManager.getInstance().getConfig().getString(key);
    }

    public static String getString(String key, String def) {
        return ConfigManager.getInstance().getConfig().getString(key, def);
    }

    public static int getInt(String key) {
        return ConfigManager.getInstance().getConfig().getInt(key);
    }

    public static int getInt(String key, int def) {
        return ConfigManager.getInstance().getConfig().getInt(key, def);
    }

    public static long getLong(String key) {
        return ConfigManager.getInstance().getConfig().getLong(key);
    }

    public static long getLong(String key, long def) {
        return ConfigManager.getInstance().getConfig().getLong(key, def);
    }

    public static float getFloat(String key) {
        return ConfigManager.getInstance().getConfig().getFloat(key);
    }

    public static float getFloat(String key, float def) {
        return ConfigManager.getInstance().getConfig().getFloat(key, def);
    }

    public static double getDouble(String key) {
        return ConfigManager.getInstance().getConfig().getDouble(key);
    }

    public static double getDouble(String key, double def) {
        return ConfigManager.getInstance().getConfig().getDouble(key, def);
    }

    public static boolean getBoolean(String key) {
        return ConfigManager.getInstance().getConfig().getBoolean(key);
    }

    public static boolean getBoolean(String key, boolean def) {
        return ConfigManager.getInstance().getConfig().getBoolean(key, def);
    }

    public static String[] getStringArray(String key) {
        return ConfigManager.getInstance().getConfig().getStringArray(key);
    }

    @SuppressWarnings("unchecked")
    public static List getList(String key) {
        return ConfigManager.getInstance().getConfig().getList(key);
    }

    @SuppressWarnings("unchecked")
    public static List getList(String key, List def) {
        return ConfigManager.getInstance().getConfig().getList(key, def);
    }

    public static void setProperty(String key, Object value) {
        ConfigManager.getInstance().getConfig().setProperty(key, value);
    }

}
