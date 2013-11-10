package pi.GUI.DTViewer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pi.graph.drawing.Graph;
import pi.inputs.drawing.Drawing;

public class Timeline extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private JSlider timeSlider;
	private JLabel currentTime;
	
	private JButton playButton;
	private JButton stopButton;
	
	private Graph graph;
	
	public Timeline(Dimension size, Graph graph)
	{
		this.setGraph(graph);
		
		this.setSize(size);
		this.setLayout(null);
		this.setBorder(BorderFactory.createTitledBorder("Timeline"));
		
		this.timeSlider = new JSlider();
		this.timeSlider.setSize(new Dimension(size.width - 20, 30));
		this.timeSlider.setLocation(10, 20);
		this.timeSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				applyNewLabel();
				getGraph().setCurrentTime(timeSlider.getValue());
			}
		});
		this.add(this.timeSlider);
		
		this.currentTime = new JLabel();
		this.currentTime.setLocation(15, 55);
		this.currentTime.setSize(new Dimension(size.width, 20));
		this.add(this.currentTime);
		
		this.playButton = new JButton("P");
		this.playButton.setLocation(size.width - 100, 55);
		this.playButton.setSize(new Dimension(43, 20));
		this.playButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
	
			}
		});
		this.add(this.playButton);
		
		this.stopButton = new JButton("S");
		this.stopButton.setLocation(size.width - 50, 55);
		this.stopButton.setSize(new Dimension(43, 20));
		this.stopButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
	
			}
		});
		this.add(this.stopButton);
		
		this.rebuild(graph.getDrawing());
	}
	
	public void rebuild(Drawing drawing)
	{
		this.timeSlider.setMinimum(0);
		
		if (drawing != null)
		{
			this.timeSlider.setMaximum(drawing.getTotalTime());
		}
		else
		{
			this.timeSlider.setMaximum(1);
		}
		
		this.timeSlider.setValue(this.timeSlider.getMaximum());
	
		this.applyNewLabel();
	}


	public void applyNewLabel()
	{
		this.currentTime.setText(String.format("%d/%d", this.timeSlider.getValue(), this.timeSlider.getMaximum()));
	}

	public Graph getGraph()
	{
		return graph;
	}

	public void setGraph(Graph graph)
	{
		this.graph = graph;
	}
}
