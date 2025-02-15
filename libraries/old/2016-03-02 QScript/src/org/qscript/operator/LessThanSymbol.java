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

package org.qscript.operator;

import org.qscript.Argument;
import org.qscript.Script;
import org.qscript.Token;
import org.qscript.errors.EvaluationException;

/**
 * &lt; logical comparison
 * 
 * @author Peter Lager
 *
 */
public class LessThanSymbol extends Operator {

	public LessThanSymbol(String symbol, int nbrArgs, int priority, int type) {
		super(symbol, nbrArgs, priority, type);
	}

	@Override
	public Argument resolve(Script script, Token token, Argument[] args, Object...objects) 
			throws EvaluationException {
		testForUninitialisedVars(script, args);
		Argument a0 = args[0];
		Argument a1 = args[1];
		// Operator overloading
		if(a0.isNumeric && a1.isNumeric)
			return new Argument(a0.toDouble() < a1.toDouble());
		if(a0.isString && a1.isString){
			int r = a0.toString().compareTo(a1.toString());
			return new Argument(r < 0);
		}

		// If we get here then the arguments are invalid		
		handleInvalidArguments(script, token);
		return null;
	}
}
