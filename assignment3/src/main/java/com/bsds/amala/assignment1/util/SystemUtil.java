package com.bsds.amala.assignment1.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SystemUtil {
    public static final String HOSTNAME = getHostname();

    private static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
