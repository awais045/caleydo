/**
 * 
 */
package cerberus.manager.parser;

import java.util.Iterator;
//import java.util.Collection;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
//import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager;
import cerberus.manager.IXmlParserManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.parser.xml.sax.handler.IXmlParserHandler;
import cerberus.parser.xml.sax.handler.command.CommandSaxHandler;
import cerberus.parser.xml.sax.handler.kegg.KgmlSaxHandler;
import cerberus.parser.xml.sax.handler.kegg.PathwayImageMapSaxHandler;
import cerberus.parser.xml.sax.handler.recursion.OpenExternalXmlFileSaxHandler;
import cerberus.util.system.CerberusInputStream;
import cerberus.util.exception.GeneViewRuntimeExceptionType;
import cerberus.util.exception.GeneViewRuntimeException;

/**
 * Administer several XML-SaxHandelers.
 * Switches between several XML-SaxHandeler automatical, based by a registered tag.
 * Acts as proxy for other derived objects from IXmlParserManager
 * 
 * @see cerberus.parser.xml.sax.handler.IXmlParserHandler
 * 
 * @author Michael Kalkusch
 *
 */
public class XmlParserManager 
extends AXmlParserManager
implements IXmlParserManager {

//  /**
//   * necessary to catch the special pathway XML file case
//	 * when the pathway tag needs to be parsed again.
//   */
//  private static final String xml_tag_for_pathways = "pathway";
  
	protected final ILoggerManager refLoggerManager;

	/** Define log level for log inforamtion */
	protected final LoggerType logLevel;
	
	/**
	 * Define maximum number of recursions
	 */
	public final int iCountMaximumOpenedFile = 513;

	/**
	 * count number of recusrions in order to detect misbehaviour.
	 */
	private int iCountOpenedFiles = 0;		
	
	protected boolean bUnloadSaxHandlerAfterBootstraping = false;
	
	/**
	 * Default constructor.
	 * 
	 * @param generalManager reference to IGeneralManager
	 * @param bUseCascadingHandler TRUE enabeld cascading handlers and slows down parsing speed.
	 */
	public XmlParserManager(final IGeneralManager generalManager) {
		
		super( generalManager );
		
		refLoggerManager = generalManager.getSingelton().getLoggerManager();
		
		this.logLevel = LoggerType.VERBOSE;
		
		OpenExternalXmlFileSaxHandler externalFileHandler =
			new OpenExternalXmlFileSaxHandler( generalManager, this );
						
		KgmlSaxHandler kgmlParser = 
			new KgmlSaxHandler( generalManager, this );	
		
		PathwayImageMapSaxHandler pathwayImageMapParser =
			new PathwayImageMapSaxHandler ( generalManager, this );
		
		CommandSaxHandler cmdHandler = 
			new CommandSaxHandler( generalManager, this );
		
		registerAndInitSaxHandler( externalFileHandler );		
		registerAndInitSaxHandler( kgmlParser );
		registerAndInitSaxHandler( pathwayImageMapParser );
		registerAndInitSaxHandler( cmdHandler );
		
		//openCurrentTag( cmdHandler );
	}


	/**
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public final void startDocument() throws SAXException {
		
		setXmlFileProcessedNow( true );
	}

	/**
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public final void endDocument() throws SAXException {
		
		setXmlFileProcessedNow( false );	
		
		if ( currentHandler != null ) 
		{
			refLoggerManager.logMsg( "XmlParserManager.endDocument()  key=[" +
					currentHandler.getXmlActivationTag() + "]  call " +
					currentHandler.getClass().getSimpleName() + 
					".endDocument() ...",
					LoggerType.FULL );
			
			currentHandler.endDocument();
		} // if ( currentHandler != null ) 
		else 
		{
			if ( bUnloadSaxHandlerAfterBootstraping ) {				
				this.destroyHandler();
			}
			
		} // else .. if ( currentHandler != null ) 
	}


	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startElement(Stringt, Stringt, Stringt, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, 
			String localName, 
			String qName,
			Attributes attrib) throws SAXException {
		
		if ( currentHandler == null ) 
		{
			refLoggerManager.logMsg( " < TAG= " + qName,
					LoggerType.FULL );
			
			startElement_search4Tag(uri,
					localName, 
					qName,
					attrib);
			
			if ( currentHandler != null )
			{
				/* forwared event if currentHandler was set inside startElement_search4Tag(..) */
				currentHandler.startElement( uri, 
						localName, 
						qName,
						attrib );
			} //if ( currentHandler != null )
			
			/* early return from if ()*/
			return;
			
		} // if ( currentHandler == null ) 
		
		/* else; regular case with valid current Handler */ 			
		/* test, if new Handler has to be activated. */
		startElement_search4Tag(uri,
				localName, 
				qName,
				attrib);	
		
		currentHandler.startElement( uri, 
				localName, 
				qName,
				attrib );		
	}

	/**
	 * @see cerberus.manager.IXmlParserManager#startElement_search4Tag(Stringt, Stringt, Stringt, org.xml.sax.Attributes)
	 */
	public void startElement_search4Tag(String uri, 
			String localName, 
			String qName,
			Attributes attrib) {
		
		if ( hashTag2XmlParser.containsKey( qName ) ) 
		{
			/**
			 * Get handler registered to this "qName" ..
			 */
			IXmlParserHandler handler = hashTag2XmlParser.get( qName );
			
			try // catch (SAXException se) 
			{
				/**
				 * Register handler only if it is not 
				 * the OpenExternalXmlFileSaxHandler ...
				 */
				if ( handler.getClass().getName().equals( 
						OpenExternalXmlFileSaxHandler.class.getName()) ) 
				{
					/**
					 * Special case: 
					 * 
					 * Open new file, but do not register new handler...
					 * Attention: do not call  sectionFinishedByHandler() from FileLoaderSaxHandler !
					 */
						/**
						 * 
						 * pass event to current handler
						 */
						handler.startElement( uri,
								localName,
								qName,
								attrib );
						
						/* early exit from try-catch block and if */
						return;
						
				} // if ( handler.getClass().getName().equals(OpenExternalXmlFileSaxHandler.class.getName()) ) 						
				
				
				/**
				 * Regular case: register new handler ...
				 */
				
				refGeneralManager.getSingelton().logMsg(
						"AXmlParserManager.openCurrentTag( key=[" + 
						handler.getXmlActivationTag() + "] " +
						handler.getClass().getSimpleName() +	" )",
						LoggerType.VERBOSE_EXTRA );
				
				/**
				 * register new handler ...
				 */
				llXmlParserStack.add( handler );				
				currentHandler = handler;	
				
			} // try
			catch (SAXException se) 
			{
				refLoggerManager.logMsg( "XmlParserManager.startElement_search4Tag() SAX error: " +
						se.toString(),
						LoggerType.ERROR_ONLY );
				
			} // try .. catch (SAXException se) 		
			
		} // if ( hashTag2XmlParser.containsKey( qName ) ) 
		
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endElement(Stringt, Stringt, Stringt)
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		refLoggerManager.logMsg( "        " + qName + " TAG -->",
				LoggerType.FULL );
		
		if ( currentHandler != null ) {
//			if ( sCurrentClosingTag.equals( qName ) ) {
//				this.closeCurrentTag();
//				return;
//			}
			
			currentHandler.endElement( uri, 
					localName,
					qName);
		}

	}
	
	
	/**
	 * @see cerberus.manager.IXmlParserManager#endElement_search4Tag(Stringt, Stringt, Stringt)
	 */
	public void endElement_search4Tag(String uri, 
			String localName, 
			String qName) {
		
		assert false : "should not be called but overloaded by derived class.";
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) 
		throws SAXException {
		
		if ( currentHandler != null ) {				
			currentHandler.characters(ch, start, length);
		}
	}

	
	public final void sectionFinishedByHandler( IXmlParserHandler handler ) {
		
		assert handler != null : "Can not handel null pointer!";
		
		/**
		 * allow unregistering of handler
		 */
		setXmlFileProcessedNow( false );
		
		/**
		 * 
		 */
		if ( currentHandler != handler ) {
			throw new GeneViewRuntimeException("sectionFinishedByHandler() called by wrong handler!",
					GeneViewRuntimeExceptionType.SAXPARSER);
		}
				
		closeCurrentTag();				
		
		/**
		 * enable processing flag again. Return "token".		 
		 */
		setXmlFileProcessedNow( true );
	}

	
	/**
	 * @see cerberus.manager.IXmlParserManager#parseXmlFileByName(Stringt)
	 */
	public boolean parseXmlFileByName( final String filename ) {
		
		iCountOpenedFiles++;
		InputSource inSource = 
			CerberusInputStream.openInputStreamFromFile( filename,
					refLoggerManager );
		
		refLoggerManager.logMsg("XmlParserManager.parseXmlFileByName( " + filename + ") parse...",
				LoggerType.VERBOSE_EXTRA );
		
		boolean status = CerberusInputStream.parseOnce( inSource ,
				filename,
				this,
				refLoggerManager );
		
		refLoggerManager.logMsg("XmlParserManager.parseXmlFileByName( " + filename + ") done.",
				LoggerType.STATUS );
		
		return 	status;
	}
	
	/**
	 * @see cerberus.manager.IXmlParserManager#parseXmlFileByName(Stringt)
	 */
	public boolean parseXmlFileByNameAndHandler( final String filename, 
			final OpenExternalXmlFileSaxHandler openFileHandler ) {
		
		// this.swapXmlParserHandler( currentHandler, openFileHandler );
		
		iCountOpenedFiles++;
		InputSource inSource = 
			CerberusInputStream.openInputStreamFromFile( filename,
					refLoggerManager );
		
		return CerberusInputStream.parseOnce( inSource , 
				filename,
				this,
				refLoggerManager );		
	}
	
	
	/**
	 * @see cerberus.manager.IXmlParserManager#parseXmlFileByInputStream(org.xml.sax.InputSource)
	 */
	public boolean parseXmlFileByInputStream( InputSource inputStream,
			final String inputStreamText ) {
		
		iCountOpenedFiles++;
		return CerberusInputStream.parseOnce( inputStream ,
				inputStreamText,
				this,
				refLoggerManager );	
	}


	public void destroyHandler() {
		
		refLoggerManager.logMsg( "XmlParserManager.destoryHandler() ... ",
				LoggerType.VERBOSE );
		
		/**
		 * Linked list...
		 */
		
		if ( llXmlParserStack == null )
		{
			refLoggerManager.logMsg( "XmlParserManager.destoryHandler() llXmlParserStack is null",
					LoggerType.FULL );
		} // if ( llXmlParserStack == null )
		else 
		{
			refLoggerManager.logMsg( "XmlParserManager.destoryHandler() llXmlParserStack remove objects..",
					LoggerType.FULL );
			
			if ( ! llXmlParserStack.isEmpty() ) 
			{
				Iterator <IXmlParserHandler> iterParserHandler = 
					llXmlParserStack.iterator();
				
				while ( iterParserHandler.hasNext() ) 
				{
					IXmlParserHandler handler = iterParserHandler.next();
					
					unregisterSaxHandler( handler.getXmlActivationTag() );
					handler.destroyHandler();
				} // while ( iterParserHandler.hasNext() ) 
				
				llXmlParserStack.clear();	
				
			} // if ( ! llXmlParserStack.isEmpty() ) 
			
			llXmlParserStack = null;
		} // else .. if ( llXmlParserStack == null )
		
		
		/**
		 * Hashtable ...
		 */
		
		if ( hashTag2XmlParser == null )
		{
			refLoggerManager.logMsg( "XmlParserManager.destoryHandler() hashTag2XmlParser is null",
					LoggerType.FULL );
		} // if ( hashTag2XmlParser == null )
		else
		{
			refLoggerManager.logMsg( "XmlParserManager.destoryHandler() hashTag2XmlParser remove objects..",
					LoggerType.FULL );
			
			if ( ! hashTag2XmlParser.isEmpty() ) {
				Iterator <IXmlParserHandler> iterHandler =  hashTag2XmlParser.values().iterator();
										
				while ( iterHandler.hasNext() ) 
				{
					IXmlParserHandler refHandler = iterHandler.next(); 
					
					if ( refHandler != null )
					{
						refHandler.destroyHandler();
						refHandler = null;
					}
					
				} // while ( iterHandler.hasNext() ) 
			
				hashTag2XmlParser.clear();	
				
			} // if ( ! hashTag2XmlParser.isEmpty() ) {
			hashTag2XmlParser = null;
			
		} // else .. if ( hashTag2XmlParser == null )
		
		refLoggerManager.logMsg( "XmlParserManager.destoryHandler() ... done!",
				LoggerType.FULL );
		
		refLoggerManager.logMsg( "XML file was read sucessfully.",
				LoggerType.STATUS );
		
	}
}
