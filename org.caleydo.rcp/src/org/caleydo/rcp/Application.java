package org.caleydo.rcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import org.caleydo.core.application.core.CaleydoBootloader;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.rcp.core.bridge.RCPBridge;
import org.caleydo.rcp.progress.PathwayLoadingProgressIndicatorAction;
import org.caleydo.rcp.views.opengl.GLRemoteRenderingView;
import org.caleydo.rcp.views.swt.ToolBarView;
import org.caleydo.rcp.wizard.firststart.FirstStartWizard;
import org.caleydo.rcp.wizard.project.CaleydoProjectWizard;
import org.caleydo.rcp.wizard.project.DataImportWizard;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application
	implements IApplication
{
	private static String BOOTSTRAP_FILE_GENE_EXPRESSION_MODE = "data/bootstrap/shared/webstart/bootstrap_webstart_gene_expression.xml";
	private static String BOOTSTRAP_FILE_SAMPLE_DATA_MODE = "data/bootstrap/shared/sample/bootstrap_gene_expression_sample.xml";
	private static String BOOTSTRAP_FILE_PATHWAY_VIEWER_MODE = "data/bootstrap/shared/webstart/bootstrap_webstart_pathway_viewer.xml";

	private static String REAL_DATA_SAMPLE_FILE = "data/genome/microarray/sample/HCC_sample_dataset.csv";
	// private static String REAL_DATA_SAMPLE_FILE =
	// "data/genome/microarray/sample/sample_microarray_dataset.csv";

	public static CaleydoBootloader caleydoCore;

	public static ApplicationWorkbenchAdvisor applicationWorkbenchAdvisor;

	public static boolean bIsWebstart = false;
	public static boolean bDoExit = false;
	
	// When both boolean variables are false the loadPathwayData 
	// flag from the preference file is taken
	// The command line arguments overrule the preference store
	public static boolean bNoPathwayData = false;
	public static boolean bLoadPathwayData = false;
	public static boolean bIsWindowsOS = false;
	
	public static EApplicationMode applicationMode = EApplicationMode.STANDARD;

	public static String sCaleydoXMLfile = "";

	public static ArrayList<EStartViewType> alStartViews;

	public RCPBridge rcpGuiBridge;

	@Override
	@SuppressWarnings("unchecked")
	public Object start(IApplicationContext context) throws Exception
	{
		// System.out.println("Start Caleydo...");
		// System.out.println("OS Name:" +System.getProperty("os.name"));
		
		if (System.getProperty("os.name").contains("Win"))
			bIsWindowsOS = true;
		
		alStartViews = new ArrayList<EStartViewType>();

		Map<String, Object> map = (Map<String, Object>) context.getArguments();

		if (map.size() > 0)
		{
			String[] sArParam = (String[]) map.get("application.args");

			if (sArParam != null)
			{
				for (int iParamIndex = 0; iParamIndex < sArParam.length; iParamIndex++)
				{
					if (sArParam[iParamIndex].equals("webstart"))
					{
						bIsWebstart = true;
					}
					else if (sArParam[iParamIndex].equals("no_pathways"))
					{
						bNoPathwayData = true;
					}
					else if (sArParam[iParamIndex].equals("load_pathways"))
					{
						bLoadPathwayData = true;
					}
					else if (sArParam[iParamIndex].equals(EStartViewType.PARALLEL_COORDINATES
							.getCommandLineArgument()))
					{
						alStartViews.add(EStartViewType.PARALLEL_COORDINATES);
					}
					else if (sArParam[iParamIndex].equals(EStartViewType.HEATMAP
							.getCommandLineArgument()))
					{
						alStartViews.add(EStartViewType.HEATMAP);
					}
					else if (sArParam[iParamIndex].equals(EStartViewType.GLYPHVIEW
							.getCommandLineArgument()))
					{
						alStartViews.add(EStartViewType.GLYPHVIEW);
					}
					else if (sArParam[iParamIndex].equals(EStartViewType.BROWSER
							.getCommandLineArgument()))
					{
						alStartViews.add(EStartViewType.BROWSER);
					}
					else if (sArParam[iParamIndex].equals(EStartViewType.REMOTE
							.getCommandLineArgument()))
					{
						alStartViews.add(EStartViewType.REMOTE);
					}
					else if (sArParam[iParamIndex].equals(EStartViewType.TABULAR
							.getCommandLineArgument()))
					{
						alStartViews.add(EStartViewType.TABULAR);
					}
					else
					{
						sCaleydoXMLfile = sArParam[iParamIndex];
					}
				}
			}
		}

		// System.setProperty("network.proxy_host", "webproxy.kages.at");
		// System.setProperty("network.proxy_port", "80");

		rcpGuiBridge = new RCPBridge();

		// Create Caleydo core
		caleydoCore = new CaleydoBootloader(bIsWebstart, rcpGuiBridge);

		Display display = PlatformUI.createDisplay();

		// Check if Caleydo will be started the first time
		if (!caleydoCore.getGeneralManager().getPreferenceStore().getBoolean(
				PreferenceConstants.PATHWAY_DATA_OK))
		{
			WizardDialog firstStartWizard = new WizardDialog(display.getActiveShell(),
					new FirstStartWizard());
			firstStartWizard.open();
		}

		// if (bIsWebstart && !bDoExit)
		// {
		// startCaleydoCore();
		// }
		try
		{
			applicationWorkbenchAdvisor = new ApplicationWorkbenchAdvisor();

			int returnCode = PlatformUI.createAndRunWorkbench(display,
					applicationWorkbenchAdvisor);

			if (returnCode == PlatformUI.RETURN_RESTART)
			{
				return IApplication.EXIT_RESTART;
			}
			else
				return IApplication.EXIT_OK;
		}
		finally
		{
			if (!bDoExit)
			{
				// Save preferences before shutdown
				try
				{
					GeneralManager.get().getLogger().log(Level.INFO,
							"Save Caleydo preferences...");
					GeneralManager.get().getPreferenceStore().setValue("firstStart", false);
					GeneralManager.get().getPreferenceStore().save();
				}
				catch (IOException e)
				{
					throw new IllegalStateException("Unable to save preference file.");
				}
			}

			GeneralManager.get().getLogger().log(Level.INFO, "Bye bye!");
			display.dispose();
		}
	}

	@Override
	public void stop()
	{
		final IWorkbench workbench = PlatformUI.getWorkbench();

		if (workbench == null)
			return;

		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable()
		{
			public void run()
			{
				if (!display.isDisposed())
				{
					workbench.close();
				}
			}
		});
	}

	public static void startCaleydoCore()
	{
		// If no file is provided as command line argument a XML file open
		// dialog is opened
		if (sCaleydoXMLfile.equals(""))
		{
			Display display = PlatformUI.createDisplay();
			Shell shell = new Shell(display);
			// shell.setActive();
			// shell.setFocus();

			WizardDialog projectWizardDialog = new WizardDialog(shell,
					new CaleydoProjectWizard(shell));

			if (WizardDialog.CANCEL == projectWizardDialog.open())
			{
				bDoExit = true;
				return;
			}

			switch (applicationMode)
			{
				case PATHWAY_VIEWER:
					sCaleydoXMLfile = BOOTSTRAP_FILE_PATHWAY_VIEWER_MODE;
					break;
				case SAMPLE_DATA_RANDOM:
					sCaleydoXMLfile = BOOTSTRAP_FILE_SAMPLE_DATA_MODE;
					break;
				default:
					sCaleydoXMLfile = BOOTSTRAP_FILE_GENE_EXPRESSION_MODE;

			}
			caleydoCore.setXmlFileName(sCaleydoXMLfile);
			caleydoCore.start();

			if (applicationMode == EApplicationMode.STANDARD)
			{
				WizardDialog dataImportWizard = new WizardDialog(shell, new DataImportWizard(
						shell));

				if (WizardDialog.CANCEL == dataImportWizard.open())
				{
					bDoExit = true;
				}
			}
			else if (applicationMode == EApplicationMode.SAMPLE_DATA_REAL)
			{
				WizardDialog dataImportWizard = new WizardDialog(shell, new DataImportWizard(
						shell, REAL_DATA_SAMPLE_FILE));

				if (WizardDialog.CANCEL == dataImportWizard.open())
				{
					bDoExit = true;
				}
			}

			shell.dispose();
		}
		else
		{
			caleydoCore.setXmlFileName(sCaleydoXMLfile);
			caleydoCore.start();
		}

		initializeColorMapping();
		// initializeViewSettings();

		openViewsInRCP();

		if (!bDoExit && (!bNoPathwayData || bLoadPathwayData))
		{
			// Trigger pathway loading
			new PathwayLoadingProgressIndicatorAction().run(null);

			// ((ToolBarView)PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			// .getActivePage().getViewReferences()[0].getView(false)).addPathwayLoadingProgress();
		}

		if (GeneralManager.get().isStandalone())
		{
			// Start OpenGL rendering
			GeneralManager.get().getViewGLCanvasManager().startAnimator();
			GeneralManager.get().getSWTGUIManager().runApplication();
		}
	}

	public static void initializeColorMapping()
	{
		// The next two lines are a hack FIXME which need to be replaces once we
		// can call initializeDefaultPreferences() in PreferenceIntializer
		// ourselves
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.getInt("");

		ColorMappingManager.get().initiFromPreferenceStore();
	}

	private static void openViewsInRCP()
	{
		if (applicationMode == EApplicationMode.PATHWAY_VIEWER)
		{
			// Filter all views except remote and browser in case of pathway
			// viewer mode
			Iterator<EStartViewType> iterStartViewsType = alStartViews.iterator();
			EStartViewType type;
			while (iterStartViewsType.hasNext())
			{
				type = iterStartViewsType.next();
				if (type != EStartViewType.REMOTE && type != EStartViewType.BROWSER)
				{
					iterStartViewsType.remove();
				}
			}
		}

		// Open Views in RCP
		try
		{
			// Open toolbar view
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
					ToolBarView.ID);

			if (alStartViews.contains(EStartViewType.REMOTE))
			{
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
						GLRemoteRenderingView.ID);

				alStartViews.remove(EStartViewType.REMOTE);
			}

			for (EStartViewType startViewsMode : alStartViews)
			{
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
						startViewsMode.getRCPViewID());
			}
		}
		catch (PartInitException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
