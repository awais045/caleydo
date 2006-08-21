/**
 * 
 */
package cerberus.xml.parser.manager;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import cerberus.manager.IGeneralManager;
import cerberus.util.system.CerberusInputStream;
import cerberus.util.exception.CerberusExceptionType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.xml.parser.handler.IXmlParserHandler;
import cerberus.xml.parser.handler.importer.OpenExternalXmlFileSaxHandler;
import cerberus.xml.parser.handler.importer.kegg.KgmlSaxHandler2;
import cerberus.xml.parser.manager.AXmlParserManager;

/**
 * Administer several XML-SaxHandelers.
 * Switches between several XML-SaxHandeler automatical, based by a registerd tag.
 * 
 * @see cerberus.xml.parser.handler.IXmlParserHandler
 * 
 * @author kalkusch
 *
 */
public class XmlParserManager 
extends AXmlParserManager
implements IXmlParserManager
{

	/**
	 * Define maximum number of recursions
	 */
	public final int iCountMaximumOpenedFile = 513;

	/**
	 * count number of recusrions in order to detect misbehaviour.
	 */
	private int iCountOpenedFiles = 0;		
	
	/**
	 * TRUE defines, that handles may be cascaded.
	 * Optimization!
	 */
	protected boolean bUseCascadingHandler = false;
	
	/**
	 * Default constructor.
	 * 
	 * @param generalManager reference to IGeneralManager
	 * @param bUseCascadingHandler TRUE enabeld cascading handlers and slows down parsing speed.
	 */
	public XmlParserManager( final IGeneralManager generalManager,
			final boolean bUseCascadingHandler )
	{
		super( generalManager );
		
		this.bUseCascadingHandler = bUseCascadingHandler;
		
		OpenExternalXmlFileSaxHandler externalFileHandler =
			new OpenExternalXmlFileSaxHandler( generalManager, this );
						
		KgmlSaxHandler2 kgmlParser = 
			new KgmlSaxHandler2( generalManager, this );		
		
		registerSaxHandler( externalFileHandler );		
		registerSaxHandler( kgmlParser );
	}


	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public final void startDocument() throws SAXException
	{
		setXmlFileProcessedNow( true );
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public final void endDocument() throws SAXException
	{
		setXmlFileProcessedNow( false );
	}


	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, 
			String localName, 
			String qName,
			Attributes attrib) throws SAXException
	{
		if ( currentHandler == null ) 
		{
			
			System.out.println(" < TAG= " + qName);
			
			if ( hashTag2XmlParser.containsKey( qName ) ) 
			{
				IXmlParserHandler handler = hashTag2XmlParser.get( qName );
				
				/**
				 * Register handly onyl if it is not 
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
					handler.startElement( uri,
							localName,
							qName,
							attrib );															
				
					currentHandler.startElement(  uri,
							localName,
							qName,
							attrib );		
				}								
				else {
					
					/**
					 * Regular case: register new handler ...
					 */
					
					this.openCurrentTag( handler );
				
					handler.startElement( uri,
							localName,
							qName,
							attrib );
				
				}
				
				
				
//				if ( hander.getClass().getName().equals( 
//						OpenExternalXmlFileSaxHandler.class.getName()) ) 
//				{
//					
//					/**
//					 * execute opening tag..
//					 */
//					hander.startElement( uri,
//							localName,
//							qName,
//							attrib );
//				}
			}
			
			return;
		}
		else 
		{
			if ( bUseCascadingHandler ) {				
				if ( this.hashTag2XmlParser.containsKey( qName ) ) {
					openCurrentTag( hashTag2XmlParser.get( qName ) );
					return;
				}
			}
			
			currentHandler.startElement( uri, 
					localName, 
					qName,
					attrib );
		}

	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
		System.out.println("        " + qName + " TAG -->");
		
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

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		if ( currentHandler != null ) {				
			currentHandler.characters(ch, start, length);
		}
	}

	
	public final void sectionFinishedByHandler(IXmlParserHandler handler)
	{
		assert handler != null : "Can not handel null pointer!";
		
		/**
		 * allow unregistering of handler
		 */
		setXmlFileProcessedNow( false );
		
		/**
		 * Special case: OpenExternalXmlFileSaxHandler
		 */
		if ( handler.getClass().getName().equals( 
				OpenExternalXmlFileSaxHandler.class.getName()) ) 
		{
			throw new CerberusRuntimeException(
					"sectionFinishedByHandler() must not be called by OpenExternalXmlFileSaxHandler, because this handler is notregistered by default!",
					CerberusExceptionType.SAXPARSER);
		}
		
		/**
		 * 
		 */
		if ( currentHandler != handler ) {
			throw new CerberusRuntimeException("sectionFinishedByHandler() called by wrong handler!",
					CerberusExceptionType.SAXPARSER);
		}
		
		
		closeCurrentTag();		
		
		
		/**
		 * enable processing flag again. Return "token".		 
		 */
		setXmlFileProcessedNow( true );
	}

	
	/**
	 * @see cerberus.xml.parser.manager.IXmlParserManager#parseXmlFileByName(java.lang.String)
	 */
	public boolean parseXmlFileByName( String filename ) {
		
		iCountOpenedFiles++;
		InputSource inSource = 
			CerberusInputStream.openInputStreamFromFile( filename );
		
		return CerberusInputStream.parseOnce( inSource , this );		
	}
	
	
	/**
	 * @see cerberus.xml.parser.manager.IXmlParserManager#parseXmlFileByInputStream(org.xml.sax.InputSource)
	 */
	public boolean parseXmlFileByInputStream( InputSource inputStream ) {
		iCountOpenedFiles++;
		return CerberusInputStream.parseOnce( inputStream , this );	
	}


}
