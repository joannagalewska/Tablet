package pi.inputs.drawing.autofinder;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import pi.inputs.drawing.Drawing;
import pi.inputs.drawing.Figure;
import pi.inputs.drawing.PacketData;
import pi.inputs.drawing.Segment;

public class FigureInterpreter
{
	private int figureCnt = -1;
	private Rectangle bounds;

	public void interprate(Drawing drawing)
	{
		if (drawing.getFigure() == null)
			return;

		ArrayList<Figure> figure = drawing.getFigure();
		Iterator<Figure> it = figure.iterator();

		Figure fig;
		this.figureCnt = -1;
		this.bounds = drawing.getContent();

		while (it.hasNext())
		{
			fig = it.next();
			this.interprateFigure(fig);
		}

	}

	public void interprateFigure(Figure figure)
	{
		if (figure.getSegment() == null)
			return;
		if (figure.getBounds() == null)
			return;

		int test = this.lineTest(figure);
		if (test != -1)
		{
			figure.setType(test);
			return;
		}
		test = this.brokenLineTest(figure);
		if (test != -1)
		{
			figure.setType(test);
			return;
		}
		test = this.circleTest(figure);
		if (test != -1)
		{
			figure.setType(test);
			return;
		}
		test = this.spiralTest(figure);
		if (test != -1)
		{
			figure.setType(test);
			return;
		}

		figure.setType(Figure.ZIGZAG);
	}

	public int brokenLineTest(Figure figure)
	{
		if (figure.getSegment().size() < 5)
			return -1;

		if (!this.isStraight(figure))
			return -1;

		ArrayList<PacketData> packet = figure.getParent().getPacket();
		if (packet == null)
			return -1;

		Iterator<Segment> it = figure.getSegment().iterator();
		Segment seg;

		int packets = figure.getTotalPoints();
		int packetsDiv = 6;
		int dimensionDiv = 2 * packetsDiv - 1;

		int x = 0, y = 0;
		int positive = 0;

		boolean height = true;

		double dDimension = figure.getBounds().height / (double) dimensionDiv;
		if (figure.getBounds().width > figure.getBounds().height)
		{
			height = false;
			dDimension = figure.getBounds().width / dimensionDiv;
		}

		double base = figure.getBounds().y - figure.getBounds().height;

		it = figure.getSegment().iterator();
		while (it.hasNext())
		{
			seg = it.next();

			for (int i = seg.getRange().getLeft(); i <= seg.getRange()
					.getRight(); i++)
			{
				x = packet.get(i).getPkX();
				y = packet.get(i).getPkY();

				if (height)
				{
					for (int j = 1; j < packetsDiv; j++)
					{
						if (y < base + (j - 1) * (dDimension * 2.0d))
							continue;
						if (y > base + (j) * (dDimension * 2.0d) - dDimension)
							continue;
						positive++;
					}

				} else
				{
					for (int j = 1; j < packetsDiv; j++)
					{
						if (x < figure.getBounds().x + (j - 1) * dDimension)
							continue;
						if (x >= figure.getBounds().x + (j) * dDimension)
							continue;
						positive++;
					}
				}

			}
		}

		double accPercent = 0.5d;
		double result = (double) positive / (double) packets;

		if (result > accPercent)
			return Figure.BROKENLINE;

		return -1;
	}

	public int lineTest(Figure figure)
	{
		if (!this.isStraight(figure))
			return -1;

		ArrayList<PacketData> packet = figure.getParent().getPacket();
		Iterator<Segment> it = figure.getSegment().iterator();
		Segment seg;

		int total = figure.getTotalPoints();

		double accX = 0.0d;
		double accY = 0.0d;

		double bX = 0.0d, bY = 0.0d;

		while (it.hasNext())
		{
			seg = it.next();
			for (int i = seg.getRange().getLeft() + 1; i <= seg.getRange()
					.getRight(); i++)
			{
				accX += (packet.get(i).getPkX() - packet.get(i - 1).getPkX());
				accY += (packet.get(i).getPkY() - packet.get(i - 1).getPkY());

				bX += packet.get(i).getPkX();
				bY += packet.get(i).getPkY();
			}
		}

		bX /= (double) (total - 1);
		bY /= (double) (total - 1);

		int correct = 0;
		double dist = 0.0d;
		if (figure.getBounds().height > figure.getBounds().width)
			dist = figure.getBounds().height / 70.0d;
		else
			dist = figure.getBounds().width / 70.0d;

		if (accX == 0)
		{
			System.out.printf("UPS\n");
		} else
		{
			double a = accY / accX;
			double b = bY - a * bX;

			it = figure.getSegment().iterator();
			while (it.hasNext())
			{
				seg = it.next();

				for (int i = seg.getRange().getLeft() + 1; i <= seg.getRange()
						.getRight(); i++)
				{
					if (this.getDistanceFromLine(a, b, packet.get(i).getPkX(),
							packet.get(i).getPkY()) < dist)
						correct++;
				}
			}
		}

		double accept = 0.8d;

		double result = (double) correct / (double) total;

		if (result > accept)
		{
			this.figureCnt++;
			if (this.figureCnt == 2)
				return -1;

			return Figure.FIRSTLINE + this.figureCnt;
		}

		return -1;
	}

	public double getDistanceFromLine(double a, double b, double x, double y)
	{
		double result = 0.0d;
		result = (a * x - y + b) / Math.sqrt(a * a + 1);
		if (result < 0)
			return -result;
		return result;
	}

	public int spiralTest(Figure figure)
	{
		if (!this.isSquare(figure))
			return -1;

		Point m = new Point(0, 0);
		int div = 0;

		ArrayList<PacketData> packet = figure.getParent().getPacket();
		;
		Iterator<Segment> it = figure.getSegment().iterator();
		Segment seg;

		int total = figure.getTotalPoints();

		while (it.hasNext())
		{
			seg = it.next();
			for (int i = seg.getRange().getLeft(); i <= seg.getRange()
					.getRight(); i++)
			{
				m.x += packet.get(i).getPkX();
				m.y += packet.get(i).getPkY();
				div++;
			}
		}

		if (div != 0)
		{
			m.x /= div;
			m.y /= div;
		}

		double base = (figure.getBounds().width + figure.getBounds().height) / 14;
		double[] distDown = new double[7];
		double[] partDown = new double[7];
		double[] distUp = new double[7];
		double[] partUp = new double[7];

		for (int i = 0; i < 7; i++)
		{
			distDown[i] = (3.5d - (double) i * 0.5);
			if (i == 0)
				partDown[i] = distDown[i];
			else
				partDown[i] = distDown[i] + partDown[i - 1];

			distUp[i] = (double) (i + 1) * 0.5;
			if (i == 0)
				partUp[i] = distUp[i];
			else
				partUp[i] = distUp[i] + partUp[i - 1];
		}
		for (int i = 0; i < 7; i++)
		{
			partUp[i] = (double) total * (partUp[i]) / 14.0d;
			distUp[i] *= base;

			partDown[i] = (double) total * (partDown[i]) / 14.0d;
			distDown[i] *= base;
		}

		Double accRange = (base * 0.5d);
		Double accPercent = 0.45d;
		int downAccu = 0;
		int upAccu = 0;
		int current = 0;

		Double rad;
		it = figure.getSegment().iterator();
		while (it.hasNext())
		{
			seg = it.next();
			for (int i = seg.getRange().getLeft(); i <= seg.getRange()
					.getRight(); i++)
			{
				current++;
				rad = this.getDistance(m, new Point(packet.get(i).getPkX(),
						packet.get(i).getPkY()));

				for (int j = 0; j < 7; j++)
					if ((current < partDown[j]) || (j == 6))
					{
						if (this.isInRange(rad, distDown[j], accRange))
							downAccu++;
						break;
					}

				for (int j = 0; j < 7; j++)
					if ((current < partUp[j]) || (j == 6))
					{
						if (this.isInRange(rad, distUp[j], accRange))
							upAccu++;
						break;
					}
			}
		}

		double resultDown = (double) downAccu / (double) total;
		double resultUp = (double) upAccu / (double) total;

		if ((resultDown > resultUp) && (resultDown > accPercent))
		{
			return Figure.SPIRALIN;
		} else if ((resultUp > resultDown) && (resultUp > accPercent))
		{
			return Figure.SPIRALOUT;
		}

		return -1;
	}

	public int circleTest(Figure figure)
	{

		if (!this.isSquare(figure))
		{
			return -1;
		}

		double scale = 1.0d;
		scale = (double) figure.getBounds().width
				/ (double) figure.getBounds().height;

		Double dist = 0.0d;
		Point m = new Point(0, 0);
		int div = 0;

		ArrayList<PacketData> packet = figure.getParent().getPacket();
		Iterator<Segment> it = figure.getSegment().iterator();
		Segment seg;

		int total = figure.getTotalPoints();
		double scalledY = 0.0d;

		while (it.hasNext())
		{
			seg = it.next();

			for (int i = seg.getRange().getLeft(); i <= seg.getRange()
					.getRight(); i++)
			{
				m.x += packet.get(i).getPkX();
				m.y += packet.get(i).getPkY();
				div++;
			}
		}

		if (div != 0)
		{
			m.x /= div;
			m.y /= div;
		}

		dist = 0.0d;
		div = 0;
		it = figure.getSegment().iterator();
		while (it.hasNext())
		{
			seg = it.next();

			for (int i = seg.getRange().getLeft(); i <= seg.getRange()
					.getRight(); i++)
			{
				scalledY = (double) m.y + (packet.get(i).getPkY() - m.y)
						* scale;
				dist += this.getDistance(m, new Point(packet.get(i).getPkX(),
						(int) scalledY));
				div++;
			}
		}

		if (div != 0)
		{
			dist /= div;
		}

		div = 0;
		int accepted = 0;
		int accRange = (int) (dist * 0.20d);
		Double accPercent = 0.5d;

		int current = 0;
		int[][] accu = new int[4][4];
		int[] visited = new int[3];

		Double rad = 0.0d;
		it = figure.getSegment().iterator();
		while (it.hasNext())
		{
			seg = it.next();
			for (int i = seg.getRange().getLeft(); i <= seg.getRange()
					.getRight(); i++)
			{
				scalledY = (double) m.y + (packet.get(i).getPkY() - m.y)
						* scale;
				rad = this.getDistance(m, new Point(packet.get(i).getPkX(),
						(int) scalledY));
				if ((rad > dist - accRange) && (rad < dist + accRange))
				{
					accepted++;
				}
				div++;
				current++;

				int where = 0;
				if (current < (double) total * 0.25d)
					where = 0;
				else if (current < (double) total * 0.5d)
					where = 1;
				else if (current < (double) total * 0.75d)
					where = 2;
				else
					where = 3;

				if (packet.get(i).getPkX() < m.x)
				{
					if (packet.get(i).getPkY() < m.y)
						accu[where][0]++;
					else
						accu[where][1]++;
				} else
				{
					if (packet.get(i).getPkY() < m.y)
						accu[where][3]++;
					else
						accu[where][2]++;
				}
			}
		}

		double result = (double) accepted / (double) div;

		if (result > accPercent)
		{
			for (int i = 0; i < 3; i++)
			{
				int p = 0;
				int max = -1;
				for (int j = 0; j < 4; j++)
				{
					if (accu[i][j] > max)
					{
						max = accu[i][j];
						p = j;
					}
				}
				visited[i] = p;
			}

			if ((visited[1] == (visited[0] + 1) % 4)
					&& (visited[2] == (visited[1] + 1) % 4))
			{
				return Figure.CIRCLELEFT;
			} else
				return Figure.CIRCLERIGHT;

		}

		return -1;
	}

	public boolean isStraight(Figure figure)
	{
		if (this.bounds == null)
			return false;
		if ((figure.getBounds().height < 0.65d * this.bounds.height)
				&& (figure.getBounds().width < 0.65d * this.bounds.width))
		{
			return false;
		}

		return true;
	}

	public boolean isSquare(Figure figure)
	{
		if ((!this.isInRange(figure.getBounds().width,
				figure.getBounds().height, 0.33d * figure.getBounds().height))
				|| (!this.isInRange(figure.getBounds().height,
						figure.getBounds().width,
						0.33d * figure.getBounds().width)))
		{
			return false;
		}

		return true;
	}

	public boolean isInRange(double value, double base, double dev)
	{
		if ((value > base - dev) && (value < base + dev))
			return true;
		return false;
	}

	public double getDistance(Point A, Point B)
	{
		double dx = A.x - B.x;
		dx *= dx;

		double dy = A.y - B.y;
		dy *= dy;

		double result = Math.sqrt(dx + dy);
		return result;
	}

	public double getAngle(double dx, double dy)
	{
		if ((dx > -0.001d) && (dx < 0.001d))
		{
			if (dy >= 0)
				return 90.0d;
			else
				return 270.0d;
		} else
		{
			double x = (double) dx;
			if (x < 0)
				x *= -1.0d;

			double y = (double) dy;
			if (y < 0)
				y *= -1.0d;

			double angle = Math.atan(y / x);
			angle /= (Math.PI / 180.0d);

			if (dx < 0)
			{
				if (dy > 0)
					return (180.0d - angle);
				else
					return 180.0d + angle;
			} else
			{
				if (dy > 0)
					return angle;
				else
					return 360.0d - angle;
			}
		}
	}

}
