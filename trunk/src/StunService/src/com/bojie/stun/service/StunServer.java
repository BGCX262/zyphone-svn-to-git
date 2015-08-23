/*
 * This file is part of JSTUN. 
 * 
 * Copyright (c) 2005 Thomas King <king@t-king.de> - All rights
 * reserved.
 * 
 * This software is licensed under either the GNU Public License (GPL),
 * or the Apache 2.0 license. Copies of both license agreements are
 * included in this distribution.
 */

package com.bojie.stun.service;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

/*
 * This class implements a STUN server as described in RFC 3489.
 * The server requires a machine that is dual-homed to be functional. 
 */
public class StunServer {
	Vector<DatagramSocket> sockets;
	
	public StunServer(int primaryPort, InetAddress primary, int secondaryPort, InetAddress secondary) throws SocketException {
		sockets = new Vector<DatagramSocket>();
		sockets.add(new DatagramSocket(primaryPort, primary));
		sockets.add(new DatagramSocket(secondaryPort, primary));
		sockets.add(new DatagramSocket(primaryPort, secondary));
		sockets.add(new DatagramSocket(secondaryPort, secondary));
	}
	
	public void start() throws SocketException {
		for (DatagramSocket socket : sockets) {
			socket.setReceiveBufferSize(2000);
			StunServerReceiverThread ssrt = new StunServerReceiverThread(socket, sockets);
			ssrt.start();
			System.out.println("Service Started ... ");
			
		}
	}

	/*
	 * To invoke the STUN server two IP addresses and two ports are required.
	 */
	public static void load() {
		String args[] = new String[4];
		args[1] = new String("172.168.254.100") ;
		args[0] = new String("1001") ;
		args[3] = new String("172.168.254.101") ;
		args[2] = new String("1002") ;
		//
		try {
			if (args.length != 4) {
				System.out.println("usage: java de.javawi.jstun.test.demo.StunServer PORT1 IP1 PORT2 IP2");
				System.out.println();
				System.out.println(" PORT1 - the first port that should be used by the server");
				System.out.println("   IP1 - the first ip address that should be used by the server");
				System.out.println(" PORT2 - the second port that should be used by the server");
				System.out.println("   IP2 - the second ip address that should be used by the server");
				System.exit(0);
			}
			Handler fh = new FileHandler("logging_server.txt");
			fh.setFormatter(new SimpleFormatter());
			java.util.logging.Logger.getLogger("de.javawi.jstun").addHandler(fh);
			java.util.logging.Logger.getLogger("de.javawi.jstun").setLevel(Level.ALL);
			StunServer ss = new StunServer(Integer.parseInt(args[0]), InetAddress.getByName(args[1]),
										   Integer.parseInt(args[2]), InetAddress.getByName(args[3]));
			ss.start();
		} catch (SocketException se) {
			se.printStackTrace();
		} catch (UnknownHostException uhe) {
			uhe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	public static void main(String args[]) {
		load();
	
	}
}