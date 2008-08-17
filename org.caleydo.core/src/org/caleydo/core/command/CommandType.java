package org.caleydo.core.command;


/**
 * Type of Command Queue "tag's" and "key's" Example:
 * LOAD_DATA_FILE("cmd","type") in XML: <cmd type="LOAD_DATA_FILE" /> Example 2:
 * LOAD_ON_DEMAND("cmd","process") in XML: <cmd process="LOAD_ON_DEMAND" />
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public enum CommandType
{
	/**
	 * XML-value ( XML-Tag , XML-key )
	 */
	LOAD_DATA_FILE("cmd", "type", "", "No description available!"),
	LOAD_LOOKUP_TABLE_FILE("cmd", "type", "", "Load a lookup table"),
	LOAD_ON_DEMAND("cmd", "process", "LOAD_ON_DEMAND", "No description available!"),
	LOAD_URL_IN_BROWSER("cmd", "type", "-1", "Load URL in browser"),

	FETCH_PATHWAY_DATA("cmd", "type", "", "No description available!"),
	
	DATA_FILTER_MATH("cmd", "type", "-1", "Filter data by using math operations"),
	DATA_FILTER_MIN_MAX("cmd", "type", "-1", "Evaluate min and max of an entity"),

	CREATE_EVENT_MEDIATOR("cmd", "type", null, "Create Event Mediator"),
	EVENT_MEDIATOR_ADD_OBJECT("cmd", "type", null, "Add Objects ad sender or receiver to Event Mediator"),

	CREATE_GL_HEAT_MAP_3D("cmd", "type", "-1", "Create Heat Map"),
	CREATE_GL_GLYPH("cmd", "type", "-1", "Create Glyph"),
	CREATE_GL_GLYPH_SLIDER("cmd", "type", "-1", "Create Glyph Slider View"),
	CREATE_GL_PATHWAY_3D("cmd", "type", "-1", "Create Pathway 3D"),
	CREATE_GL_PARALLEL_COORDINATES_3D("cmd", "type", "-1", "Create Parallel Coordinates 3D"),
	CREATE_GL_BUCKET_3D("cmd", "type", "-1", "Create Bucket 3D"),
	CREATE_GL_JUKEBOX_3D( "cmd", "type", "-1", "Create Jukebox 3D"),
	CREATE_GL_WII_TEST("cmd", "type", "-1", "No description available!"),
	CREATE_GL_REMOTE_GLYPH("cmd", "type", "-1", "Create Remote Glyph"),

	EXTERNAL_FLAG_SETTER("cmd", "type", "-1", "External flag setter"),
	EXTERNAL_ACTION_TRIGGER("cmd", "type", "-1", "External action trigger"),

	CREATE_SET_DATA("cmd", "type", "-1", "Create SET"),
	CREATE_STORAGE("cmd", "type", "-1", "Create Storage"),
	CREATE_VIRTUAL_ARRAY("cmd", "type", "-1", "Create VirtualArray"),

	CREATE_SWT_WINDOW("cmd", "type", "-1", "Create SWT window"),
	CREATE_SWT_CONTAINER("cmd", "type", "-1", "Create SWTContainer"),
	CREATE_VIEW_PATHWAY("cmd", "type", "-1", "Create Pathway 2D"),
	CREATE_VIEW_GEARS("cmd", "type", "-1", "Create Gears Demo"),
	CREATE_VIEW_DATA_EXPLORER("cmd", "type", "-1", "Create Data Explorer"),
	CREATE_VIEW_DATA_EXCHANGER("cmd", "type", "-1", "Create Data Exchanger"),
	CREATE_VIEW_MIXER("cmd", "type", "-1", "Create Mixer"),
	CREATE_VIEW_GLYPHCONFIG("cmd", "type", "-1", "Create Mixer"),
	CREATE_VIEW_BROWSER("cmd", "type", "-1", "Create Browser"),
	CREATE_VIEW_IMAGE("cmd", "type", "-1", "Create Image"),
	CREATE_VIEW_SET_EDITOR("cmd", "type", "-1", "No description available!"),
	CREATE_VIEW_UNDO_REDO("cmd", "type", "-1", "Create UNDO/REDO"),
	CREATE_VIEW_DATA_ENTITY_SEARCHER("cmd", "type", null, "Create Data Entity Searcher"),

	CREATE_VIEW_SWT_GLCANVAS("cmd", "type", "-1", "Create SWT GL Canvas"),
	CREATE_VIEW_RCP_GLCANVAS("cmd", "type", "-1", "Create RCP GL Canvas"),

	/**
	 * Set path for pathway XML files, images and imagemaps.
	 */
	SET_SYSTEM_PATH_PATHWAYS("cmd", "type", "-1", "Set path to pathway files"),

	/**
	 * Load data definition for glyph view
	 */
	LOAD_GLYPH_DEFINITIONS("cmd", "type", "-1", "load definition for the glyphs"),

	LOAD_PATHWAY_DATA("cmd", "type", "-1", "load pathway data"),

	RUN_CMD_NOW("cmd", "process", "RUN_CMD_NOW", "No description available!"),
	MEMENTO("cmd", "process", null, "No description available!"),

	/*
	 * ------- COMMAND QUEUE --------
	 */
	COMMAND_QUEUE_OPEN("cmdqueue", "type", null, "Open a command queue"),
	COMMAND_QUEUE_RUN("cmdqueue", "type", null, "execute a command queue"),

	CMD_ID("cmdqueue", "cmdId", "-1", "No description available!"),
	CMDQUEUE_ID("cmdqueue", "cmdQueueId", "-1", "No description available!"),

	RUN_QUEUE_ON_DEMAND("cmdqueue", "process", "RUN_QUEUE_ON_DEMAND", "No description available!"),
	RUN_QUEUE("cmdqueue", "process", "RUN_QUEUE", "No description available!"),

	CMD_THREAD_POOL_ID("cmdqueue", "queue_thread", "-1", "No description available!"),
	CMD_THREAD_POOL_WAIT_ID("cmdqueue", "queue_thread_wait", "-1", "No description available!"),

	/*
	 * ================================================= 
	 * Import from former Type "CommandType" 
	 * =================================================
	 */
	SYSTEM_SHUT_DOWN("cmd", "type", "-1", "Caleydo system shut down"),

	/*
	 * ================================================== TAG's used only while
	 * parsing XML files ==================================================
	 */
	TAG_CMD("cmd", "Cmd", ""),
	TAG_CMD_QUEUE("cmd", "CmdQueue", ""),
	TAG_CMD_ID("cmd", "cmdId", "-1"),
	TAG_UNIQUE_ID("cmd", "uniqueId", "-1"),
	TAG_MEMENTO_ID("cmd", "mementoId", "-1"),
	TAG_TYPE("cmd", "type", "NO_OPERATION"),
	TAG_ATTRIBUTE1("cmd", "attrib1", ""),
	TAG_ATTRIBUTE2("cmd", "attrib2", ""),
	TAG_ATTRIBUTE3("cmd", "attrib3", ""),
	TAG_ATTRIBUTE4("cmd", "attrib4", ""),
	TAG_DETAIL("cmd", "detail", ""),
	TAG_PARENT("cmd", "parent", "-1"),
	TAG_PROCESS("cmd", "process", "RUN_CMD_NOW"),
	TAG_LABEL("cmd", "label", ""),
	TAG_POS_GL_ORIGIN("cmd", "gl_origin", "0 0 0"),
	
	/** Values indicate axis: (X,Y,Z) and rotation-angle (ALPHA) in (radiant). */
	TAG_POS_GL_ROTATION("cmd", "gl_rotation", "0 0 1 0.0");

	/**
	 * * Tag: Example: <CmdQueue type="COMMAND_QUEUE_OPEN"> <br>
	 * "type" is the Key.<br>
	 * "CmdQueue" is the Tag.<br>
	 * "COMMAND_QUEUE_OPEN" is the attribute.<br>
	 */
	private String sXmlTag;

	/**
	 * * Tag: Example: <CmdQueue type="COMMAND_QUEUE_OPEN"> <br>
	 * "type" is the Key.<br>
	 * "CmdQueue" is the Tag.<br>
	 * "COMMAND_QUEUE_OPEN" is the attribute.<br>
	 */
	private String sXmlKey;

	private String sDefaultValue;

	/**
	 * Text that should describe the command. This is mainly used for the
	 * UNDO/REDO function for showing extra information to the commands.
	 */
	private String sInfoText;

	/**
	 * Constructor.
	 * 
	 * @param sXmlTag
	 * @param sXmlKey
	 * @param sDefaultValue
	 * @param sInfoText
	 */
	private CommandType(String sXmlTag, String sXmlKey,
			String sDefaultValue, String sInfoText)
	{
		this.sXmlTag = sXmlTag;
		this.sXmlKey = sXmlKey;
		this.sDefaultValue = sDefaultValue;
		this.sInfoText = sInfoText;
	}
	
	private CommandType(String sXmlTag, String sXmlKey, String sDefaultValue)
	{
		this(sXmlTag, sXmlKey, sDefaultValue,
				"Description is not valid! This is a TAG.");
	}
	
	/**
	 * * Tag: Example: <CmdQueue type="COMMAND_QUEUE_OPEN"> <br>
	 * "type" is the Key.<br>
	 * "CmdQueue" is the Tag.<br>
	 * "COMMAND_QUEUE_OPEN" is the attribute.<br>
	 * 
	 * @return key
	 */
	public String getXmlKey()
	{

		return this.sXmlKey;
	}

	/**
	 * Tag: Example: <CmdQueue type="COMMAND_QUEUE_OPEN"> <br>
	 * "CmdQueue" is the Tag.<br>
	 * "type" is the Key.<br>
	 * "COMMAND_QUEUE_OPEN" is the attribute.
	 * 
	 * @return tag
	 */
	public String getXmlTag()
	{

		return this.sXmlTag;
	}

	/**
	 * Return the default value, if it is known.
	 * 
	 * @return default value
	 */
	public String getDefault()
	{

		return this.sDefaultValue;
	}

	public String getInfoText()
	{

		return this.sInfoText;
	}
}
