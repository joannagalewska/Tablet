package pi.statistics.logic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;

import pi.project.Project;
import pi.shared.SharedController;
import pi.statistics.functions.Average;
import pi.statistics.functions.Variance;
import pi.statistics.tests.LillieforsNormality;

public class ProjectResult
{
	private Project project;
	private Map<String, PopulationResult> value;

	private boolean projectLvPaired = false;
	private double signiLillie = 0.05d;
	private int rangesLillie = 5;

	// 1. before/after
	// 2. figure
	// 3. attribute
	// 4. statistic
	private Map<String, Map<String, Map<String, Map<String, LinkedList<Double>>>>> testResult;

	public ProjectResult(Project project)
	{
		this.project = project;
	}

	public void clearMemory()
	{
		if (this.testResult != null)
		{
			this.testResult = null;
		}

		if (this.value != null)
		{
			for (Map.Entry<String, PopulationResult> entry : this.value
					.entrySet())
			{
				entry.getValue().clearMemory();
			}

			this.value = null;
		}
	}

	public void doTests()
	{
		this.testResult = null;
		// 1. before/after
		// 2. figure
		// 3. attribute
		// 4. statistic

		boolean after = false;
		int type = SharedController.getInstance().getProject().getType();
		if ((type == Project.POPULATION_PAIR)
				|| (type == Project.SPECIMEN_PAIR))
			after = true;

		if (after)
		{
			String[] maps =
			{ "P1AB", "P2AB", "BB", "AA", "dAB", };
			this.testResult = StatMapper.getMap(maps);
			this.performTest("First", "Before", "First", "After",
					this.testResult.get("P1AB"));
			this.performTest("Second", "Before", "Second", "After",
					this.testResult.get("P2AB"));
			this.performTest("First", "Before", "Second", "Before",
					this.testResult.get("BB"));
			this.performTest("First", "After", "Second", "After",
					this.testResult.get("AA"));
			this.performTest("First", "Diff", "Second", "Diff",
					this.testResult.get("dAB"));

		} else
		{
			String[] maps =
			{ "BB" };
			this.testResult = StatMapper.getMap(maps);
			this.performTest("First", "Before", "Second", "Before",
					this.testResult.get("BB"));
		}
	}

	public void performTest(String fP, String fS, String sP, String sS,
			Map<String, Map<String, Map<String, LinkedList<Double>>>> map)
	{
		PopulationResult first = this.value.get(fP);
		PopulationResult second = this.value.get(sP);

		Map<String, Map<String, Map<String, LinkedList<Double>>>> fMap = first
				.getData().get(fS);
		Map<String, Map<String, Map<String, LinkedList<Double>>>> sMap = second
				.getData().get(sS);

		LinkedList<Double> fList;
		LinkedList<Double> sList;
		LinkedList<Double> result;

		for (int i = 0; i < StatMapper.figureNames.length; i++)
		{
			for (int j = 0; j < StatMapper.attributeNames.length; j++)
			{
				//SharedController.getInstance().getProgressView().increase();

				for (int k = 0; k < StatMapper.statisticNames.length; k++)
				{
					fList = fMap.get(StatMapper.figureNames[i])
							.get(StatMapper.attributeNames[j])
							.get(StatMapper.statisticNames[k]);
					sList = sMap.get(StatMapper.figureNames[i])
							.get(StatMapper.attributeNames[j])
							.get(StatMapper.statisticNames[k]);

					if ((fList.size() < 2) || (sList.size() < 2))
						continue;

					// ----- PERFORM ACTION WITH THIS LISTS :D
					result = map.get(StatMapper.figureNames[i])
							.get(StatMapper.attributeNames[j])
							.get(StatMapper.statisticNames[k]);

					// To [] double

					double[] left;
					double[] right;

					if (fList.size() != sList.size())
					{
						int length = fList.size();
						if (sList.size() < length)
							length = sList.size();

						left = this.listToDouble(fList, length);
						right = this.listToDouble(sList, length);
					} else
					{
						left = this.listToDouble(fList);
						right = this.listToDouble(sList);
					}

					// ------------------------
					// LICZ AVG I DEV

					this.calculateStatistics(result, left);
					this.calculateStatistics(result, right);

					// ------------------------

					LillieforsNormality.compute(left, this.rangesLillie, false);
					boolean normal = LillieforsNormality
							.isTrueForAlpha(this.signiLillie);

					if (normal == true)
					{
						LillieforsNormality.compute(right, this.rangesLillie,
								false);
						normal = LillieforsNormality
								.isTrueForAlpha(this.signiLillie);

					}

					if (normal == true)
					{
						result.add(1.0d);
						TTest test = new TTest();

						boolean paired = true;
						if (!fP.equals(sP))
							paired = this.projectLvPaired;

						double pval = 0.0d;

						if (paired)
							pval = test.pairedTTest(left, right);
						else
							pval = test.tTest(left, right);

						if (paired)
							result.add(1.0d);
						else
							result.add(-1.0d);

						result.add(pval);

					} else
					{
						result.add(-1.0d);

						boolean paired = true;
						if (!fP.equals(sP))
							paired = this.projectLvPaired;

						double pval = 0.0d;

						if (paired)
						{
							WilcoxonSignedRankTest test = new WilcoxonSignedRankTest();
							pval = test.wilcoxonSignedRankTest(left, right,
									true);
						} else
						{
							MannWhitneyUTest test = new MannWhitneyUTest();
							pval = test.mannWhitneyUTest(left, right);
						}

						if (paired)
							result.add(1.0d);
						else
							result.add(-1.0d);

						result.add(pval);

					}

					// ----
				}
			}
		}
	}

	public void calculateStatistics(LinkedList<Double> result, double[] list)
	{
		StatisticResult avg = new StatisticResult();
		StatisticResult var = new StatisticResult();

		Average.init(avg);
		for (int i = 0; i < list.length; i++)
			Average.iterate(list[i]);
		Average.finish();

		Variance.init(var, avg.getValue().get(0));

		for (int i = 0; i < list.length; i++)
			Variance.iterate(list[i]);

		Variance.finish();

		result.add(avg.getValue().get(0));
		double sd = Math.sqrt(var.getValue().get(0));
		result.add(sd);
	}

	public double[] listToDouble(LinkedList<Double> list, int length)
	{
		double[] result = new double[length];
		Iterator<Double> it = list.iterator();
		Double value = 0.0d;
		int place = 0;
		while (it.hasNext())
		{
			value = it.next();
			result[place] = value;
			place++;
			if (place >= length)
				break;
		}

		return result;
	}

	public double[] listToDouble(LinkedList<Double> list)
	{
		return this.listToDouble(list, list.size());
	}

	public void calculateResult()
	{
		this.value = new HashMap<String, PopulationResult>();

		PopulationResult first = new PopulationResult(
				this.project.getFirstPopulation());
		first.calculateResult();

		value.put("First", first);

		if (this.project.getSecondPopulation() != null)
		{
			PopulationResult second = new PopulationResult(
					this.project.getSecondPopulation());
			second.calculateResult();
			value.put("Second", second);
		}

		this.project.setResult(this);
		this.doTests();
	}

	public Map<String, Map<String, Map<String, Map<String, LinkedList<Double>>>>> getTestResult()
	{
		return testResult;
	}

	public void setTestResult(
			Map<String, Map<String, Map<String, Map<String, LinkedList<Double>>>>> testResult)
	{
		this.testResult = testResult;
	}

	public Map<String, PopulationResult> getValue()
	{
		return value;
	}

	public void setValue(Map<String, PopulationResult> value)
	{
		this.value = value;
	}

}
