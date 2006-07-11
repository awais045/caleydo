/**
 * 
 */
package cerberus.xml.parser.command;


//import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import cerberus.manager.GeneralManager;
import cerberus.manager.MenuManager;
import cerberus.manager.CommandManager;

import cerberus.xml.parser.CerberusDefaultSaxHandler;


/**
 * Create Menus in Frames from XML file.
 * 
 * @author java
 *
 */
public class CommandSaxHandler extends CerberusDefaultSaxHandler  {

	private final CommandManager refCommandManager;
	
	private boolean bCommandBuffer_isActive = false;
	
	private boolean bData_EnableMenu = true;
	
	protected boolean bApplicationActive = false;
	
	private int iData_MenuId = -1;
	private int iData_MenuParentId = -1;
	private int iData_TargetFrameId = -1;
	private int iData_CommandId = -1;
	
	private String sData_MenuType = "none";
	private String sData_MenuTitle = "none";
	private String sData_MenuTooltip = "";
	private String sData_MenuMemento = "-";
	
	protected int iDefaultFrameWidht = 100;
	protected int iDefaultFrameHeight = 100;
	protected int iDefaultFrameX = 0;
	protected int iDefaultFrameY = 0;
	protected int iDefaultFrameId = -1;
	
	/* XML Attributes */
	protected static final String sMenuKey_processType = "proecess";
	protected static final String sMenuKey_commandId = "cmdId";
	//protected static final String sMenuKey_parentMenuId = "parentMenuId";
	//protected static final String sMenuKey_enabled = "enabled";
	//protected static final String sMenuKey_title = "title";
	protected static final String sMenuKey_details = "tooltip";
	protected static final String sMenuKey_memento = "mementoId";
	protected static final String sCmdKey_type = "type";
	//protected static final String sMenuKey_objectId = "id";
		

	
	/* XML Tags */
	public static final String sTag_Application = "Application";	
	public static final String sTag_CommandBuffer = "CommandBuffer";	
	public static final String sTag_Command = "Cmd";
	
	
	
	/**
	 * <Application >
	 *  <CommandBuffer>
	 *    <Cmd />
	 *    <Cmd />
	 *  </CommandBuffer>
	 * </Application>
	 */
	public CommandSaxHandler( final GeneralManager setGeneralManager  ) {
		super( setGeneralManager );
		
		refCommandManager = 
			refGeneralManager.getSingelton().getCommandManager();

		assert refCommandManager != null : "CommandManager was not created by Singelton!";
	}
	
	/**
	 * Set state of application-tag.
	 * 
	 * @param stateApplication TRUE ot indicate, that teh application tag is opened, FALSE if it is closed.
	 */
	public final void setApplicationStatus( boolean stateApplication ) {
		this.bApplicationActive = stateApplication;
	}
	
	/**
	 * Get state of application tag.
	 * 
	 * @return TRUE if application tag is opened.
	 */
	public final boolean getApplicationStatus() {
		return this.bApplicationActive;
	}
	
	public String createXMLcloseingTag( final Object frame, final String sIndent ) {
		
		assert false : "not implemented!";
				
		return "";
	}
	
	public String createXML( final Object frame, final String sIndent ) {
		String result = sIndent;		

		
//		if ( frame.getClass().equals( SwingJoglJFrame.class )) {
//			SwingJoglJFrame jframe = (SwingJoglJFrame) frame;
//			
////			result += "<" + sMenuTag;
////			
////			iCurrentFrameId = jframe.getId();
////			dim = jframe.getSize();
////			location = jframe.getLocation();			
////			bIsVisible = jframe.isVisible();
////			sTypeName = jframe.getFrameType().getTypeNameForXML();
////			sTitle = jframe.getTitle();
////			sName = jframe.getName();
////			sClosingTag = ">\n";
//			
//		} else if ( frame.getClass().equals( SwingJoglJInternalFrame.class )) {
//			SwingJoglJInternalFrame jiframe = (SwingJoglJInternalFrame) frame;
//			
////			result += "<" + sInternalFrameTag;
////			
////			iCurrentFrameId = jiframe.getId();
////			dim = jiframe.getSize();
////			location = jiframe.getLocation();			
////			bIsVisible = jiframe.isVisible();
////			sTitle = jiframe.getTitle();
////			sTypeName = jiframe.getFrameType().getTypeNameForXML();
////			sName = jiframe.getName();
////			sClosingTag = "> </" + sInternalFrameTag + ">\n";
//			
//		} else {
//			throw new RuntimeException("Can not create XML string from class [" +
//					frame.getClass().getName() + "] ;only support SwingJoglJFrame and SwingJoglJInternalFrame");
//		}
//		
////		result +=          " " + sMenuKey_objectId + sArgumentBegin + Integer.toString(iData_MenuId);
//		result += sArgumentEnd + sMenuKey_processType + sArgumentBegin + Integer.toString(this.iData_TargetFrameId);
//		result += sArgumentEnd + sMenuKey_commandId + sArgumentBegin + Integer.toString(this.iData_CommandId);
////		result += sArgumentEnd + sMenuKey_parentMenuId + sArgumentBegin + Integer.toString(this.iData_MenuParentId);;
//		result += sArgumentEnd + sCmdKey_type + sArgumentBegin + sData_MenuType;
//		
////		result += sArgumentEnd + sMenuKey_enabled + sArgumentBegin + Boolean.toString(bData_EnableMenu);	
//		result += sArgumentEnd + sMenuKey_memento + sArgumentBegin + sData_MenuMemento;
//		
////		result += sArgumentEnd + sMenuKey_title + sArgumentBegin + sData_MenuTitle;
//		result += sArgumentEnd + sMenuKey_details + sArgumentBegin + sData_MenuTooltip;					
//				
//		
		return result;
	}

//	public void setApplicationValue( String sValue ) {
//		sApplicationValue = sValue;
//	}
	
	
	

	

	
	/**
	 * 
	 * Read values of class: iCurrentFrameId
	 * @param attrs
	 * @param bIsExternalFrame
	 */
	private void readFrameData( final Attributes attrs, boolean bIsExternalFrame ) {
		
		/* create new Frame */
		iData_TargetFrameId = assignIntValueIfValid( attrs, sMenuKey_processType, -1 );
		iData_CommandId = assignIntValueIfValid( attrs, sMenuKey_commandId, -1  );
								
//		iData_MenuParentId = assignIntValueIfValid( attrs, sMenuKey_parentMenuId, -1 );
//		iData_MenuId = assignIntValueIfValid( attrs, sMenuKey_objectId, -1 );
//		
//		bData_EnableMenu = assignBooleanValueIfValid( attrs, sMenuKey_enabled, true );											
		
		sData_MenuTooltip = attrs.getValue( sMenuKey_details );
		sData_MenuMemento = attrs.getValue( sMenuKey_memento );
			
		sData_MenuType = attrs.getValue( sCmdKey_type );					
	}
	
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attrs) throws SAXException {
		
		String eName = ("".equals(localName)) ? qName : localName;
		
		if (null != eName) {
			
			if ( ! bApplicationActive ) {
				if (eName.equalsIgnoreCase(sTag_Application)) {
						/* <sApplicationTag> */
						bApplicationActive = true;
						
				} //end: if (eName.equals(sApplicationTag)) {
			}
			else //end: if ( ! bApplicationActive ) {
			{
				
				if (eName.equals(sTag_CommandBuffer)) {
					/* <sFrameStateTag> */
					if ( bCommandBuffer_isActive ) {
						throw new SAXException ( "<" + sTag_CommandBuffer + "> already opened!");
					} else {
						bCommandBuffer_isActive = true;
						return;
					}
				} //end: if (eName.equals(sFrameStateTag)) {
				else if (eName.equals(sTag_Command)) {
					
					
					/**
					 *  <Cmd ...> 
					 */
					if ( bCommandBuffer_isActive ) {
						
						
						readFrameData( attrs, true );
						

						
					} else {
						throw new SAXException ( "<"+ sTag_Command + "> opens without <" + 
								sTag_CommandBuffer + "> being opened!");
					}
				}
				
				
			} //end: if ( ! bApplicationActive ) {
		}
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		
		if ( bApplicationActive ) {
			
			String eName = ("".equals(localName)) ? qName : localName;
		
			if (null != eName) {
				if (eName.equalsIgnoreCase(sTag_Application)) {
					/* </sApplicationTag> */
					if ( bCommandBuffer_isActive ) {
						throw new SAXException ( "<" + sTag_CommandBuffer + "> still opened while <" +
								sTag_Application + "> is closed.");
					}
					bApplicationActive = false;
					return;
				} 
				else if (eName.equals(sTag_CommandBuffer)) {	
					
					/* </CommandBuffer> */
					if ( bCommandBuffer_isActive ) {
						bCommandBuffer_isActive = false;
						return;
					} else {
						throw new SAXException ( "<" + sTag_CommandBuffer + "> still opened while <" +
								sTag_Application + "> is closed.");
					}	
					
				} 
				else if (eName.equals(sTag_Command)) {	
					
					/* </sFrameTag> */
					if ( ! bCommandBuffer_isActive ) {
						throw new SAXException ( "<" + sTag_Command + "> opens without " + 
								sTag_CommandBuffer + " being opened.");
					}	
					
				}
				// end:else if (eName.equals(...)) {	
			} //end: if (null != eName) {
			
		} //end: if ( bApplicationActive ) {
	}

//	public void characters(char[] buf, int offset, int len) throws SAXException {
//		if ( bApplicationActive ) {
//		
//		}
//	}

}
