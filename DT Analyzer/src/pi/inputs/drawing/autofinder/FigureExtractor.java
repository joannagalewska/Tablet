package pi.inputs.drawing.autofinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import pi.inputs.drawing.Drawing;
import pi.inputs.drawing.Figure;
import pi.inputs.drawing.PacketData;
import pi.inputs.drawing.Segment;
import pi.utilities.Range;

public class FigureExtractor
{
	public void extract(Drawing drawing)
	{
		ArrayList <PacketData> packet = drawing.getPacket();
		int size = packet.size();
		int pressureAvoid = drawing.getPressureAvoid();
		
		LinkedList <Range> range = new LinkedList <Range> ();
		double maxDist = drawing.getBreakFigureDistance();
		
		int pointer = 0;
		int last = 0;
		
		
		packet.get(0).setBroken(false);
		for (int i = 1; i < size; i++)
		{
			packet.get(i).setBroken(false);
			if ((i != size - 1) && (packet.get(i).getPkPressure() < pressureAvoid))
				continue;

			if ((this.getDistance(packet.get(last), packet.get(i)) > maxDist)
					|| (i == size - 1))
			{
				range.add(new Range(pointer, last));
				pointer = i;
			}
			
			last = i;
		}
		
		ArrayList <Figure> figure = new ArrayList <Figure> (range.size());
		drawing.setFigure(figure);
		
		Iterator <Range> it = range.iterator();
		Range value;
		
		while (it.hasNext())
		{
			Figure newFigure = new Figure();
			LinkedList <Segment> segment = new LinkedList <Segment> ();
			newFigure.setSegment(segment);
			
			value = it.next();
			
			int left = 0;
			boolean firstAccept = true;
			
			for (int i = value.getLeft(); i <= value.getRight(); i++)
			{
				
				if ((i < value.getRight()) && (packet.get(i).getPkPressure() >= pressureAvoid)
						&& (packet.get(i + 1).getPkPressure() < pressureAvoid) )
				{
					packet.get(i).setBroken(true);
				}
				
				if (packet.get(i).getPkPressure() < pressureAvoid) continue;
				
				if (firstAccept)
				{
					firstAccept = false;
					left = i;
				}
				
				if ((packet.get(i).isBroken()) || (i == value.getRight()))
				{
					firstAccept = true;
					if (i - left + 1 != 0)
					{
						Segment newSegment = new Segment();
						ArrayList <PacketData> copyPacket = new ArrayList <PacketData> (i - left + 1);
						for (int j = left; j <= i; j++)
						{
							copyPacket.add(packet.get(j).getCopy());
						}
						if (copyPacket.size() > 2)
						{
							newSegment.setPacket(copyPacket);
							segment.add(newSegment);	
						}
					}

				}
			}
			
			newFigure.calculateBounds();
			figure.add(newFigure);			
		}
		
	}
	
	public double getDistance(PacketData A, PacketData B)
	{
		double dx = A.getPkX() - B.getPkX();
		dx = dx * dx;
		
		double dy = A.getPkY() - B.getPkY();
		dy = dy * dy;
		
		double result = Math.sqrt(dx + dy);
		return result;
	}
}
