package com.nardah;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public final class Ping {

    private static void getPing(String hostAddress, int port) {
        String usage = "java Probe <address> [<port>]";
        try {
            Client.ping = test(hostAddress, port);
        } catch (NumberFormatException e) {
            System.out.println("Problem with arguments, usage: " + usage);
            e.printStackTrace();
        }
    }

    public static void runPing() {
        Thread t = new Thread(new Runnable() {

            //@Override
            public void run() {
                while (true) {
                    if (Settings.DISPLAY_PING) {
                        getPing(Client.server, 43594);
                    }
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        t.start();
    }

    /**
     * Connect using layer3
     */
    static long test(String hostAddress) {
        InetAddress inetAddress = null;
        Date start, stop;
        try {
            inetAddress = InetAddress.getByName(hostAddress);
        } catch (UnknownHostException e) {
            System.out.println("Problem, unknown host:");
            e.printStackTrace();
        }
        try {
            start = new Date();
            if (inetAddress.isReachable(5000)) {
                stop = new Date();
                return (stop.getTime() - start.getTime());
            }
        } catch (IOException e1) {
            System.out.println("Problem, a network error has occurred:");
            e1.printStackTrace();
        } catch (IllegalArgumentException e1) {
            System.out.println("Problem, timeout was invalid:");
            e1.printStackTrace();
        }
        return -1;

    }

    /**
     * Connect using layer4 (sockets)
     */
    static long test(String hostAddress, int port) {
        InetAddress inetAddress = null;
        InetSocketAddress socketAddress = null;
        Socket sc = null;
        long timeToRespond = -1;
        long start, stop;

        try {
            inetAddress = InetAddress.getByName(hostAddress);
        } catch (UnknownHostException e) {
            System.out.println("Problem, unknown host:");
            e.printStackTrace();
        }

        try {
            socketAddress = new InetSocketAddress(inetAddress, port);
        } catch (IllegalArgumentException e) {
            System.out.println("Problem, port may be invalid:");
            e.printStackTrace();
        }

        try {
            sc = new Socket();
            start = System.currentTimeMillis();
            sc.connect(socketAddress, 2000);
            stop = System.currentTimeMillis();
            timeToRespond = (stop - start);
        } catch (IOException e) {

        }

        try {
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return timeToRespond;
    }

}