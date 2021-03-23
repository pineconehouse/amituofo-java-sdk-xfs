package com.amituofo.xfs.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtils {

	public static InetAddress[] getIP(String host) throws UnknownHostException {
		InetAddress[] addresses = new InetAddress[0];
		addresses = InetAddress.getAllByName(host);
		return addresses;
	}


}
