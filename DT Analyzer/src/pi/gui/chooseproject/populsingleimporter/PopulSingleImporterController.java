package pi.gui.chooseproject.populsingleimporter;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;

import pi.population.Population;
import pi.project.Project;
import pi.shared.SharedController;

public class PopulSingleImporterController implements ActionListener
{
	private PopulSingleImporterView view;

	public PopulSingleImporterController(PopulSingleImporterView view)
	{
		this.view = view;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		String action = arg0.getActionCommand();

		if (action.equals("OK"))
		{
			ArrayList<ArrayList<String>> paths = this.view.getPaths();
			
			int sizeFirstBefore = paths.get(0).size();
			int sizeSecondBefore = paths.get(1).size();
			
			if ((sizeFirstBefore > 0) && (sizeSecondBefore > 0) && (sizeFirstBefore == sizeSecondBefore))
			{
				Project project = new Project();
				project.setName("New Project");
				project.setPath(null);
				project.setType(Project.POPULATION_SINGLE);
				
				project.setFirstPopulation(new Population());
				project.setSecondPopulation(new Population());
			
				if (!project.create(paths.get(0), null, paths.get(1), null))
				{
					project = null;
				}
				else
				{
					SharedController.getInstance().getFrame().initProjectView(project);
					this.view.setVisible(false);
				}
			}
			else JOptionPane.showMessageDialog(null, "Size of lists should be equal and not empty!");

		} 
		else if (action.equals("ADD"))
		{
			this.view.getFc().setMultiSelectionEnabled(true);
			int returnVal = this.view.getFc().showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				DefaultListModel <String> model = this.view.getCurrentListModel();
				if (model == null) return;
				
				File [] file = this.view.getFc().getSelectedFiles();

				for (int i = 0; i < file.length; i++)
				{
					String path = file[i].getAbsolutePath();
					model.addElement(path);
				}
			}
		} 
		else if (action.equals("DEL"))
		{
			JList <String> list = this.view.getCurrentList();
			if (list == null) return;
			
			DefaultListModel <String> model = this.view.getCurrentListModel();
			if (model == null) return;
			
			int selection = list.getSelectedIndex();
			if (selection == -1) return;
			
			model.remove(selection);
		}
		else if (action.equals("UP"))
		{
			JList <String> list = this.view.getCurrentList();
			if (list == null) return;
			
			DefaultListModel <String> model = this.view.getCurrentListModel();
			if (model == null) return;
			
			int selection = list.getSelectedIndex();
			if (selection < 1) return;
			
			String temp = model.get(selection);
			model.set(selection, model.get(selection - 1));
			model.set(selection - 1, temp);
			
			list.setSelectedIndex(selection - 1);
			
		}
		else if (action.equals("DOWN"))
		{
			JList <String> list = this.view.getCurrentList();
			if (list == null) return;
			
			DefaultListModel <String> model = this.view.getCurrentListModel();
			if (model == null) return;
			
			int selection = list.getSelectedIndex();
			if (selection < 0) return;
			if (selection >= model.getSize() - 1) return;
			
			String temp = model.get(selection);
			model.set(selection, model.get(selection + 1));
			model.set(selection + 1, temp);
			
			list.setSelectedIndex(selection + 1);
		}
		else if (action.equals("CANCEL"))
		{
			SharedController.getInstance().getFrame().getMenuView()
			.setInChoose(false);
			view.setVisible(false);
		}
	}
}