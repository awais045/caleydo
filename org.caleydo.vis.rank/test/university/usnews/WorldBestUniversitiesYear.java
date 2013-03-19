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
package university.usnews;

import static demo.RankTableDemo.toFloat;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.data.AFloatFunction;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class WorldBestUniversitiesYear {
	public static final int COL_overall = 1;
	public static final int COL_academic = 2;
	public static final int COL_employer = 3;
	public static final int COL_faculty = 4;
	public static final int COL_international = 5;
	public static final int COL_internationalstudents = 6;
	public static final int COL_citations = 7;

	private final float overall;
	private final float academic;
	private final float employer;
	private final float faculty;
	private final float international;
	private final float internationalstudents;
	private final float citations;

	public WorldBestUniversitiesYear(String[] l) {
		// Rank;School;Country;Overall Score;Academic Reputation Score;Employer Reputation Score;Faculty-Student Ratio
		// Score;International Faculty Score;International Students Score;Citations per Faculty Score
		overall = toFloat(l, 3);
		academic = toFloat(l, 4);
		employer = toFloat(l, 5);
		faculty = toFloat(l, 6);
		international = toFloat(l, 7);
		internationalstudents = toFloat(l, 8);
		citations = toFloat(l, 9);
	}

	public float get(int index) {
		switch (index) {
		case COL_academic:
			return academic;
		case COL_citations:
			return citations;
		case COL_employer:
			return employer;
		case COL_faculty:
			return faculty;
		case COL_international:
			return international;
		case COL_internationalstudents:
			return internationalstudents;
		case COL_overall:
			return overall;
		}
		return 0;
	}

	public static void addYear(RankTableModel table, String title, Function<IRow, WorldBestUniversitiesYear> year) {
		final StackedRankColumnModel stacked = new StackedRankColumnModel();
		stacked.setTitle(title);
		table.add(stacked);
		// 40% ACADEMIC REPUTATION from global survey
		// 10% EMPLOYER REPUTATION from global survey
		// 20% CITATIONS PER FACULTY from SciVerse Scopus
		// 20% FACULTY STUDENT Ratio
		// 5% Proportion of INTERNATIONAL STUDENTS
		// 5% Proportion of INTERNATIONAL FACULTY
		stacked.add(col(year, COL_academic, "Academic reputation", "#FC9272", "#FEE0D2"));
		stacked.add(col(year, COL_employer, "Employer reputation", "#9ECAE1", "#DEEBF7"));
		stacked.add(col(year, COL_citations, "Citations per faculty", "#C994C7", "#E7E1EF"));
		stacked.add(col(year, COL_faculty, "Faculty/student ratio", "#A1D99B", "#E5F5E0"));
		stacked.add(col(year, COL_international, "International faculty ratio", "#FDBB84", "#FEE8C8"));
		stacked.add(col(year, COL_internationalstudents, "International student ratio", "#DFC27D", "#F6E8C3"));

		stacked.setDistributions(new float[] { 40, 10, 20, 20, 5, 5 });
		stacked.setWidth(300);
	}

	private static FloatRankColumnModel col(Function<IRow, WorldBestUniversitiesYear> year, int col, String text,
			String color, String bgColor) {
		return new FloatRankColumnModel(new ValueGetter(year, col), GLRenderers.drawText(text, VAlign.CENTER),
				Color.decode(color), Color.decode(bgColor), percentage(), FloatInferrers.MEDIAN);
	}

	protected static PiecewiseMapping percentage() {
		return new PiecewiseMapping(0, 100);
	}

	public static Map<String, WorldBestUniversitiesYear[]> readData(int... years) throws IOException {
		Map<String, WorldBestUniversitiesYear[]> data = new LinkedHashMap<>();
		for (int i = 0; i < years.length; ++i) {
			String year = String.format("usnews%4d.txt", years[i]);
			try (BufferedReader r = new BufferedReader(new InputStreamReader(
					WorldBestUniversitiesYear.class.getResourceAsStream(year), Charset.forName("UTF-8")))) {
				String line;
				r.readLine(); // header
				while ((line = r.readLine()) != null) {
					if (line.isEmpty())
						continue;
					String[] l = line.split(";");
					String school = l[1];

					WorldBestUniversitiesYear universityYear = new WorldBestUniversitiesYear(l);
					if (!data.containsKey(school)) {
						data.put(school, new WorldBestUniversitiesYear[years.length]);
					}
					data.get(school)[i] = universityYear;
				}
			}
		}
		return data;
	}

	static class ValueGetter extends AFloatFunction<IRow> {
		private final Function<IRow, WorldBestUniversitiesYear> year;
		private final int subindex;

		public ValueGetter(Function<IRow, WorldBestUniversitiesYear> year, int column) {
			this.year = year;
			this.subindex = column;
		}

		@Override
		public float applyPrimitive(IRow in) {
			WorldBestUniversitiesYear y = year.apply(in);
			if (y == null)
				return Float.NaN;
			return y.get(subindex);
		}
	}
}
