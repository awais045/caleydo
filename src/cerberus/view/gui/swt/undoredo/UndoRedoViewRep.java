package cerberus.view.gui.swt.undoredo;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import cerberus.command.ICommand;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.ViewType;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

/**
 * UNDO/REDO view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class UndoRedoViewRep 
extends AViewRep 
implements IView {
	
	protected Composite refSWTContainer;
	
	protected Combo refUndoRedoCombo;
	
	/**
	 * Constructor. 
	 * 
	 * @param refGeneralManager
	 * @param iViewId
	 * @param iParentContainerId
	 * @param sLabel
	 */
	public UndoRedoViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_IMAGE_VIEWER);	
	}

	public void initView() {
		
		retrieveGUIContainer();
		
		refSWTContainer.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Label viewComboLabel = new Label(refSWTContainer, SWT.LEFT);
		viewComboLabel.setText("Undo/Redo:");
		viewComboLabel.setSize(300, 30);
				
		refUndoRedoCombo = new Combo(refSWTContainer, SWT.READ_ONLY);
	}

	public void drawView() {
		
//		refGeneralManager.getSingelton().logMsg(
//				this.getClass().getSimpleName() + 
//				": drawView(): Load "+sUrl, 
//				LoggerType.VERBOSE );		
	}

	/**
	 * Method uses the parent container ID to retrieve the 
	 * GUI widget by calling the createWidget method from
	 * the SWT GUI Manager.
	 * 
	 */
	private void retrieveGUIContainer() {
		
		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
						iParentContainerId, iWidth, iHeight);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();
	}
	
	public void setAttributes(int iWidth, int iHeight, String sImagePath) {
		
		super.setAttributes(iWidth, iHeight);
	}
	
	public void updateCommandList(Vector<ICommand> vecCommands) {
		
		refUndoRedoCombo.removeAll();
		
		Iterator<ICommand> iterCommands = vecCommands.iterator();
		
		while(iterCommands.hasNext())
		{
			//ICommand bufferCmd = iterCommands.next();
			refUndoRedoCombo.add( iterCommands.next().getInfoText() );
		}		
	}
	
	public void addCommand(final ICommand refCommand) {
	
		refSWTContainer.getDisplay().asyncExec(new Runnable() {
			public void run() {
				refUndoRedoCombo.add(refCommand.getInfoText());
				System.err.println( "DEBUG: " + refCommand.getInfoText() + " " + refCommand.toString() );
			}
		});
	}
}
