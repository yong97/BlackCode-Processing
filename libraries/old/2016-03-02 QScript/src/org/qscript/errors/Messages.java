/*
 * Copyright (c) 2014 Peter Lager
 * <quark(a)lagers.org.uk> http:www.lagers.org.uk
 * 
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it freely,
 * subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented;
 * you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 * 
 * 2. Altered source versions must be plainly marked as such,
 * and must not be misrepresented as being the original software.
 * 
 * 3. This notice may not be removed or altered from any source distribution.
 */

package org.qscript.errors;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Build strings on user format
 * 
 * @author Peter Lager
 *
 */
public class Messages {

	/**
	 * Build a string for the patterns and values passed.
	 * 
	 * @param pattern the layout to use
	 * @param arguments the values to fill the layout
	 * @return the formatted string
	 */
	public static String build(String pattern, Object ... arguments){
		return (pattern == null) ? "" : new MessageFormat(pattern, Locale.UK).format(arguments);        
	}
	
	/**
	 * Same as the build method except to display the string in the console.
	 * 
	 * @param pattern the layout to use
	 * @param arguments the values to fill the layout
	 * @return the formatted string
	 */
	public static String println(String pattern, Object ... arguments){
		String s = build(pattern, arguments);
		System.out.println(s);
		return s;
	}
	
	
}
