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

import org.hyperic.hq.hqapi1.*;
import org.hyperic.hq.hqapi1.types.*;
import org.hyperic.hq.product.util.PluginDumper;
import org.hyperic.hq.product.ServiceTypeInfo;
import org.hyperic.hq.product.ServerTypeInfo;
import org.hyperic.util.config.ConfigResponse;

import java.io.*;
import java.util.*;
import java.util.Properties;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.BasicConfigurator;

class HypericCheck {
	public static void main(String args[]) {

		ParseOptions options = new ParseOptions(args);
		boolean debug = options.getDebug();
		boolean has_services = options.hasServices();
		boolean has_resources = options.hasResources();
		String properties_file = options.getDefaultFile();
		Properties sprops = System.getProperties();

		Logger logger = Logger.getLogger("org.apache");
		Logger logger2 = Logger.getLogger("org.hyperic.hq.product");
		BasicConfigurator.configure();
		if (options.getDebug()) {
			Logger.getRootLogger().setLevel(Level.DEBUG);
			logger2.setLevel(Level.DEBUG);
			sprops.setProperty("log","DEBUG");
		} else {
			Logger.getRootLogger().setLevel(Level.ERROR);
			logger2.setLevel(Level.ERROR);
		}

		GetConfig config = new GetConfig(properties_file);

		sprops.setProperty("metric-collect", config.getDefaultMetrics());
		sprops.setProperty("pdkDir", config.getpdkDir());
		sprops.setProperty("pluginDir", config.getpluginDir());
		System.setProperties(sprops);

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf;
		sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss zzz");

		System.out.println("#\tHypericChecker v1.0");
		System.out.println("#\tStarted on " + sdf.format(cal.getTime()) + "\n");

		HQApi api = new HQApi(config.getHost(), config.getPort(), config.getSecure(), config.getUser(), config.getPassword());
		ResourceApi r_api = api.getResourceApi();
		

		if (has_services) {
			ArrayList<Object> services = options.getServices();
			for (int i = 0; i < services.size(); i++) {
				doCheck(services.get(i), r_api, sprops, true);
			}
		}

		if (has_resources) {
			ArrayList<Object> resources = options.getResources();
			for (int i = 0; i < resources.size(); i++) {
				doCheck(resources.get(i), r_api, sprops, false);
			}
		}
	}

	private static void doCheck(Object oid, ResourceApi r_api, Properties sprops, boolean aeid) {
		int id = Integer.parseInt(oid.toString());
		try {
			ResourceResponse r_response = new ResourceResponse();
			if (aeid) {
				r_response = r_api.getResource("3:" + Integer.toString(id), true, false);
			} else {
				r_response = r_api.getResource(id, true, false);
			}
			if (!r_response.getStatus().equals(ResponseStatus.SUCCESS)) {
				System.err.println("Error :" + r_response.getError().getReasonText());
				System.exit(1);
			}

			Resource resource = r_response.getResource();
			List<ResourceConfig> config = new ArrayList<ResourceConfig>();
			config = resource.getResourceConfig();
			ResourcePrototype type = resource.getResourcePrototype();
			ConfigResponse configr = new ConfigResponse();

			System.out.println("#\tResource config:");
			System.out.println("**************************************");

			for (int i = 0; i < config.size(); i++) {
				ResourceConfig rconfig = config.get(i);
				String key = rconfig.getKey();
				String value = rconfig.getValue();
				if ( !key.contains("service.log_") ) {
					System.out.println("#\t" + key + " : " + value );
				}
				configr.setValue(key, value);
			}

			System.out.println("**************************************\n");

			ServerTypeInfo serverti = new ServerTypeInfo();
			ServiceTypeInfo serviceti = new ServiceTypeInfo();

			PluginDumper pd = new PluginDumper(sprops.getProperty("pdkDir"), sprops.getProperty("pluginDir"));
			serviceti.setServerTypeInfo(serverti);
			serviceti.setName(type.getName().replace("Remote Server ", ""));
			pd.init();
			pd.fetchMetrics(serviceti, false, configr);

			System.out.println();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
