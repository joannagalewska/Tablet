package pi.gui.project.population.specimen;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import pi.gui.project.population.PopulationView;
import pi.gui.project.population.specimen.drawingtest.DrawingTestView;
import pi.gui.project.population.specimen.toolbar.ToolbarView;
import pi.population.Specimen;
import pi.project.Project;

public class SpecimenView extends JPanel
{
	private static final long serialVersionUID = 1L;

	private SpecimenController controller;
	private Specimen specimen;

	private ToolbarView toolbar;

	private DrawingTestView before;
	private DrawingTestView after;

	private JSplitPane splitPane;
	private int type;

	private PopulationView populationView;

	public SpecimenView(PopulationView populationView, int type)
	{
		this.populationView = populationView;
		this.specimen = null;
		this.setLayout(new GridBagLayout());
		this.type = type;

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.gridy = 1;
		c.weighty = 1.0d;

		if ((type == Project.POPULATION_SINGLE)
				|| (type == Project.SPECIMEN_SINGLE))
		{
			this.before = new DrawingTestView();
			before.setDrawing(null, null, true);
			this.add(this.before, c);
		} else
		{
			this.before = new DrawingTestView();
			before.setDrawing(null, null, true);

			this.after = new DrawingTestView();
			after.setDrawing(null, null, true);

			this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					this.before, this.after);

			this.splitPane.setOneTouchExpandable(true);

			Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
			this.splitPane.setDividerLocation(dimension.height);
			this.add(this.splitPane, c);
		}

		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0d;
		c.weighty = 0.0d;

		this.toolbar = new ToolbarView(this);
		this.add(this.toolbar, c);

	}

	public void setSpecimen(Specimen specimen)
	{

		this.specimen = specimen;
		if ((type == Project.POPULATION_SINGLE)
				|| (type == Project.SPECIMEN_SINGLE))
		{

			if (specimen != null)
				this.before.setDrawing(specimen.getBefore(), specimen, true);
			else
				this.before.setDrawing(null, null, true);
		} else
		{
			if (specimen != null)
			{
				this.before.setDrawing(specimen.getBefore(), specimen, true);
				this.after.setDrawing(specimen.getAfter(), specimen, false);
			} else
			{
				this.before.setDrawing(null, null, true);
				this.after.setDrawing(null, null, true);
			}

		}
	}

	public void recalculate()
	{
		if (this.splitPane != null)
			this.splitPane.setDividerLocation(0.5d);
	}

	public void redraw()
	{
		this.before.redraw();
		if (this.after != null)
		{
			this.after.redraw();
		}
	}

	public JSplitPane getSplitPane()
	{
		return this.splitPane;
	}

	public DrawingTestView getBefore()
	{
		return before;
	}

	public void setBefore(DrawingTestView before)
	{
		this.before = before;
	}

	public DrawingTestView getAfter()
	{
		return after;
	}

	public void setAfter(DrawingTestView after)
	{
		this.after = after;
	}

	public Specimen getSpecimen()
	{
		return specimen;
	}

	public SpecimenController getController()
	{
		return controller;
	}

	public void setController(SpecimenController controller)
	{
		this.controller = controller;
	}

	public PopulationView getPopulationView()
	{
		return populationView;
	}

	public void setPopulationView(PopulationView populationView)
	{
		this.populationView = populationView;
	}

}
