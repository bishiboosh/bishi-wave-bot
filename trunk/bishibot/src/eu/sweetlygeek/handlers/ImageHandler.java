package eu.sweetlygeek.handlers;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** SAX Handler for images
 * @author bishiboosh
 *
 */
public abstract class ImageHandler extends DefaultHandler {
	
	protected Map<String, String> bigMap;
	protected Map<String, String> miniMap;
	protected StringBuffer buffer;
	protected String currentId;
	protected boolean inSection;
	protected boolean inTextTag;
	
	protected ImageHandler()
	{
		this.bigMap = new HashMap<String, String>();
		this.miniMap = new HashMap<String, String>();
		this.buffer = new StringBuffer();
		this.currentId = null;
		this.inSection = false;
		this.inTextTag = false;
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (inSection && inTextTag)
		{
			buffer.append(ch, start, length);
		}
	}

	public Map<String, String> getBigMap() {
		return bigMap;
	}

	public Map<String, String> getMiniMap() {
		return miniMap;
	}
}
