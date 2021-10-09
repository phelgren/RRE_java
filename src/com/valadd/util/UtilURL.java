/* ============================================================================
   
   Copyright (c) 2008 - Value Added Software, Inc.  All rights reserved.
  
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ============================================================================
 */
package com.valadd.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class Name: UtilURL.java
 * Description: Simple Class for flexibly working with properties files 
 * Create Date: 
 * Author: <a href="mailto:pete@valadd.com">Pete Helgren</a>
 * ----------------------------------------------------------------------------
 * Change Log:
 * ----------------------------------------------------------------------------
 *     Date:
 *     Number:
 *     Author:
 *     Description:
 */
public class UtilURL
{

	public static URL fromClass(Class contextClass)
	{
		String resourceName = contextClass.getName();
		int dotIndex = resourceName.lastIndexOf('.');

		if (dotIndex != -1) resourceName = resourceName.substring(0, dotIndex);
		resourceName += ".properties";

		return fromResource(contextClass, resourceName);
	}

	public static URL fromResource(String resourceName)
	{
		return fromResource(resourceName, null);
	}

	public static URL fromResource(Class contextClass, String resourceName)
	{
		if (contextClass == null)
			return fromResource(resourceName, null);
		else
			return fromResource(resourceName, contextClass.getClassLoader());
	}

	public static URL fromResource(String resourceName, ClassLoader loader)
	{
		URL url = null;

		if (loader != null && url == null) url = loader.getResource(resourceName);
		if (loader != null && url == null) url = loader.getResource(resourceName + ".properties");

		if (loader == null && url == null)
		{
			try
			{
				loader = Thread.currentThread().getContextClassLoader();
			}
			catch (SecurityException e)
			{
				UtilURL utilURL = new UtilURL();
				loader = utilURL.getClass().getClassLoader();
			}
		}

		if (url == null) url = loader.getResource(resourceName);
		if (url == null) url = loader.getResource(resourceName + ".properties");

		if (url == null) url = ClassLoader.getSystemResource(resourceName);
		if (url == null) url = ClassLoader.getSystemResource(resourceName + ".properties");

		if (url == null) url = fromFilename(resourceName);
		if (url == null) url = fromHomePath();
		if (url == null) url = fromUrlString(resourceName);

		// Debug.log("[fromResource] got URL " + url + " from resourceName " +
		// resourceName);
		return url;
	}

	public static URL fromFilename(String filename)
	{
		if(File.separator.equals("\\"))
			filename = "C:\\" + filename; // Arbitrarily set it to C: until I can figure out a better way
		if (filename == null) 
			return null;
		File file = new File(filename);
		URL url = null;

		try
		{
			if (file.exists()) 
				url = file.toURL();
		}
		catch (java.net.MalformedURLException e)
		{
			e.printStackTrace();
			url = null;
		}
		return url;
	}

	public static URL fromUrlString(String urlString)
	{
		URL url = null;
		try
		{
			url = new URL(urlString);
		}
		catch (MalformedURLException e)
		{
		}

		return url;
	}

	public static URL fromHomePath()
	{
		String home = System.getProperty("VALADD_HOME");
		if(home!=null){
			String newFilename = home;
				if (!home.endsWith("/") && !home.startsWith("/"))
					{
						home = home + "/";
					}
				}
		return fromFilename(home);
	}
}

