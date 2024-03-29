package pi.statistics.logic;

import java.util.HashMap;
import java.util.Map;

import pi.population.Specimen;
import pi.shared.SharedController;

public class SpecimenResult
{
	public Specimen specimen;
	private Map<String, DrawingResult> value;

	public void clearMemory()
	{
		if (this.value != null)
		{
			for (Map.Entry<String, DrawingResult> entry : this.value.entrySet())
			{
				entry.getValue().clearMemory();
			}

			this.value = null;
		}
	}

	public SpecimenResult(Specimen specimen)
	{
		this.specimen = specimen;
	}

	public void calculateResult(boolean projectLevel)
	{
		this.value = new HashMap<String, DrawingResult>();

		DrawingResult before = new DrawingResult(this.specimen.getBefore());
		before.calculateResult(projectLevel);
		getValue().put("Before", before);

		if (this.specimen.getAfter() != null)
		{
			DrawingResult after = new DrawingResult(this.specimen.getAfter());
			after.calculateResult(projectLevel);
			getValue().put("After", after);
		}
		SharedController.getInstance().getProgressView().increase();

		this.specimen.setResult(this);
	}

	public Map<String, DrawingResult> getValue()
	{
		return value;
	}

	public void setValue(Map<String, DrawingResult> value)
	{
		this.value = value;
	}

}
