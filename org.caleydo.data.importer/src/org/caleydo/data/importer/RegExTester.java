/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.data.importer;

import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.parser.ascii.TabularDataParser;
import org.caleydo.data.importer.tcga.TCGATestDataXMLGenerator;

/**
 * Test class for regular expressions applied to IDs. Uses the actual code which
 * is also used in caleydo. Can be used for replacement and substring
 * expressions
 * 
 * @author Alexander Lex
 */
public class RegExTester {

	public static void main(String[] args)

	{
		String inputString = "TCGA-02-0001";

		IDTypeParsingRules idTypeParsingRules = new IDTypeParsingRules();
		// idSpecification.setReplacementExpression(replacingExpression,
		// replacementString)
		idTypeParsingRules.setSubStringExpression(TCGATestDataXMLGenerator.TCGA_ID_SUBSTRING_REGEX);

		String outputString = TabularDataParser.convertID(inputString, idTypeParsingRules);

		System.out.println("Output: " + outputString);

	}
}