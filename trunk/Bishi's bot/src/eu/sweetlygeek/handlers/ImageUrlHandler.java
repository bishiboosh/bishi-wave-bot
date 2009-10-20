package eu.sweetlygeek.handlers;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** Image URL Sax Handler
 * @author bishiboosh
 *
 */
public class ImageUrlHandler extends DefaultHandler {
	
	private static final String IMAGE_TAG = "image_big";
	private static final String IMAGE_MINI_TAG = "image_small";
	private static final String DROP_TAG = "drop";
	private static final String ID_TAG = "drop_id";
	private StringBuffer buffer;
	private Map<String, String> bigMap;
	private Map<String, String> miniMap;
	private String currentId; 
	private boolean inDrop;
	private boolean inTextTag;
	
	public ImageUrlHandler()
	{
		this.buffer = new StringBuffer();
		this.bigMap = new HashMap<String, String>();
		this.miniMap = new HashMap<String, String>();
		this.inDrop = false;
		this.inTextTag = false;
		this.currentId = null;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (inDrop && inTextTag)
		{
			buffer.append(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (DROP_TAG.equals(name))
		{
			inDrop = false;
		}
		if (ID_TAG.equals(name))
		{
			currentId = buffer.toString();
			inTextTag = false;
		}
		if (IMAGE_TAG.equals(name) && this.currentId != null)
		{
			this.bigMap.put(currentId, buffer.toString());
			inTextTag = false;
		}
		if (IMAGE_MINI_TAG.equals(name) && this.currentId != null)
		{
			this.miniMap.put(currentId, buffer.toString());
			inTextTag = false;
		}
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if (DROP_TAG.equals(name))
		{
			inDrop = true;
			currentId = null;
		}
		if (ID_TAG.equals(name) || IMAGE_TAG.equals(name) || IMAGE_MINI_TAG.equals(name))
		{
			buffer = new StringBuffer();
			inTextTag = true;
		}
	}

	public Map<String, String> getBigMap() {
		return bigMap;
	}

	public Map<String, String> getMiniMap() {
		return miniMap;
	}

}
