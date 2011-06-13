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

import java.util.*;
import java.lang.Throwable;

public class ParseOptions {
	private String DEFAULT_FILE = System.getProperty("user.home")+"/.hq/client.properties";
	private boolean DEBUG = false;
	private boolean HAS_SERVICES = false;
	private boolean HAS_RESOURCES = false;

	private ArrayList<Object> resources = new ArrayList<Object>();
	private ArrayList<Object> services = new ArrayList<Object>();

	//public void init(String args[]) {
	public ParseOptions(String args[]) {
		int id;
		if (args.length == 0) {
			printHelp();
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-d")) {
				DEBUG = true;
			} else if (args[i].equals("-h")) {
				printHelp();
			} else if (args[i].equals("-s") && ( i+1 < args.length )) {
				HAS_SERVICES = true;
				id = Integer.parseInt(args[i+1].trim());
				services.add(id);
			} else if (args[i].equals("-r") && ( i+1 < args.length )) {
				HAS_RESOURCES = true;
				id = Integer.parseInt(args[i+1].trim());
				resources.add(id);
			} else if (args[i].equals("-p") && ( i+1 < args.length )) {
					DEFAULT_FILE = args[i+1];
			}
		}
	}

	public boolean getDebug() {
		return DEBUG;
	}

	public boolean hasServices() {
		return HAS_SERVICES;
	}

	public boolean hasResources() {
		return HAS_RESOURCES;
	}

	public String getDefaultFile() {
		return DEFAULT_FILE;
	}

	public ArrayList getServices() {
		return services;
	}

	public ArrayList getResources() {
		return resources;
	}

	public void printHelp() {
		System.out.println("NAME");
		System.out.println("\tHypericChecker - resource checker for Hyperic resources\n");

		System.out.println("SYNOPSIS");
		System.out.println("\tHypericChecker [-d] [-h] [-p defaults-file] [-s AEID] [-r RID]\n");

		System.out.println("DESCRIPTION");
		System.out.println("\tHypericChecker is a java wrapper for Hyperic measurement plugins specifically intended to perform on-demand checks of netservices plugin resources. The wrapper uses several classes and methods from Hyperic to call the actual code that the Hyperic agent uses to collect metrics on a resource in a consistent and reproducable manner. Multiple AEID's and RID's can be specified on the command line to check multiple resources with one invokation.\n");

		System.out.println("OPTIONS");
		System.out.println("\t-d\tEnable DEBUG logging\n");

		System.out.println("\t-h\tThis help message\n");

		System.out.println("\t-p\tdefaults-file");
		System.out.println("\t\tUse an alternate properties file as the configuration source. Defaults to ~/.hq/client.properties\n");

		System.out.println("\t-s\tAEID");
		System.out.println("\t\tSpecify a resource to collect metrics on via AEID. AEID's can be found as the last 5 digits on a resource's URL within the Hyperic GUI\n");
	
		System.out.println("\t-r\tRID");
		System.out.println("\t\tSpecify a resource to collect metrics on via RID. RID's can be found using the hqapi.\n");

		System.out.println("FILES");
		System.out.println("\t~/.hq/client.properties");
		System.out.println("\t\tThe default configuration file. Expects the file to conform to Java's syntax for properties files and takes values in the <key>=<value> form.\n");
		System.out.println("\t\tSample config:\n");

		System.out.println("\t\t\thost=monitor.mynetwork.com");
		System.out.println("\t\t\tport=7443");
		System.out.println("\t\t\tsecure=true");
		System.out.println("\t\t\tuser=hqadmin");
		System.out.println("\t\t\tpassword=hqadmin\n");

		System.out.println("\t\tAside from the default options for hqapi in the client.properties file, the following HypericCheck specific options are expected:\n");

		System.out.println("\t\t\thc.pdkDir\tFull path to the lib/pdk directory that came with this utility");
		System.out.println("\t\t\thc.pluginDir\tFull path to the lib/plugins directory that came with this utility\n");

		System.out.println("\t\tThe following options are optional and apply to the usage of the PasswordBouncer utility so that no passwords need to be on your filesystem. If you use these options, the password key need not have a value.\n");

		System.out.println("\t\t\thc.password.source\tOnly accepts \"pb\".");
		System.out.println("\t\t\thc.password.port\tPort that PasswordBouncer is listening on. Defaults to 10127.");
		System.out.println("\t\t\thc.password.host\tAddress that PasswordBouncer is listening on. Defaults to localhost.");
		System.out.println("\t\t\thc.password.string\tString that PasswordBouncer expects prior to returning a password. Defaults to MYPASSWORD.\n");

		System.out.println("\t\thc.metricset\tSet this to \"all\", \"availability\", \"throughput\", or \"utilization\" to see metrics of a different category.\n");

		System.out.println("BUGS");
		System.out.println("\tNone found yet :)\n");

		System.out.println("AUTHOR");
		System.out.println("\tGreg Walters <allrightname at gmail>");
		System.exit(0);
	}
}
