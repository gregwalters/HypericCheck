/*
 *  HypericCheck Written by Greg Walters
 *  Copyright (C) 2011, Contegix, LLC, www.contegix.com
 *
 *  This is free software; you can redistribute it and/or modify
 *  it under the terms version 2 of the GNU General Public License as
 *  published by the Free Software Foundation. This program is distributed
 *  in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *  PARTICULAR PURPOSE. See the GNU General Public License for more
 *  details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA.

 *  About Contegix:
 *  Contegix provides high-level managed hosting solutions for enterprise 
 *  applications and infrastructure.  The company delivers proactive, 
 *  passionate support that is unparalleled in the industry. All Contegix 
 *  solutions encompass supporting dedicated hardware and operating system 
 *  management, deploying and configuring software, and offering complete  
 *  licensing management. Contegix\u2019s award-winning service is delivered 
 *  by a staff of Tier-3 engineers from its global headquarters in St. Louis, 
 *  MO. Current clients and partners include Six Apart, ReadWriteWeb, VMware
 *  and Atlassian. For additional information, visit www.contegix.com or call 
 *  1(877) 426-6834.
*/

import java.util.Properties;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class GetConfig {
	private String password;
	private String host = "localhost";
	private String string = "MYPASSWORD";
	private int PORT = 10127;
	private Properties props = new Properties();

	public GetConfig(String file) {
		try {
			FileInputStream in = new FileInputStream(file);
			props.load(in);
			in.close();
		} catch (IOException e) {
			System.err.println("Could not load properties file " + file + ".");
			System.exit(1);
		}
	}

	public String getHost() {
		return props.getProperty("host");
	}

	public String getUser() {
		return props.getProperty("user");
	}

	public int getPort() {
		return Integer.parseInt(props.getProperty("port"));
	}

	public boolean getSecure() {
		return Boolean.parseBoolean(props.getProperty("secure"));
	}

	public String getDefaultMetrics() {
		return props.getProperty("hc.metricset", "default");
	}

	public String getpdkDir() {
		return props.getProperty("hc.pdkDir", "./lib/pdk/");
	}

	public String getpluginDir() {
		return props.getProperty("hc.pluginDir", "./lib/plugins");
	}

	public String getPassword() {
		if (props.getProperty("hc.password.source", "").equals("pb")) {
			if ( props.getProperty("hc.password.port", null) != null ) {
				PORT = Integer.parseInt(props.getProperty("hc.password.port"));
			} 
			if ( props.getProperty("hc.password.host", null) != null ) {
				host = props.getProperty("hc.password.host");
			}
			if ( props.getProperty("hc.password.string", null) != null ) {
				string = props.getProperty("hc.password.string");
			}
			try {
				Socket socket = openSocket(host, PORT);
				password = writeToReadFromSocket(socket, string + "\n").replace("\n", "");
			} catch (Exception e) {
				System.err.println("Unable to retrieve password from " + host + ":" + PORT);
				System.exit(1);
			} 
		} else {
			password = props.getProperty("password");
		}
		return password;
	}

/**
 * 
 * Socket in/out classes written by:
 * @author alvin alexander, devdaily.com.
 * http://www.devdaily.com/blog/post/java/simple-java-socket-client-class-program
 */

        private Socket openSocket(String server, int port) throws Exception {
                Socket socket;
                try {
                        InetAddress inetAddress = InetAddress.getByName(server);
                        SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);

                        socket = new Socket();

                        int timeoutInMs = 10*1000;
                        socket.connect(socketAddress, timeoutInMs);

                        return socket;
                } catch (SocketTimeoutException e) {
                        e.printStackTrace();
                        throw e;
                }
        }

        private String writeToReadFromSocket(Socket socket, String writeTo) throws Exception {
                try {
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        bufferedWriter.write(writeTo);
                        bufferedWriter.flush();

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String str;
                        while ((str = bufferedReader.readLine()) != null) {
                                sb.append(str + "\n");
                        }

                        bufferedReader.close();
                        return sb.toString();
                } catch (IOException e) {
                        e.printStackTrace();
                        throw e;

                }
        }
}
