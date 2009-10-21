package eu.sweetlygeek.handlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/** Image URL Sax Handler
 * @author bishiboosh
 *
 */
public class DropularHandler extends ImageHandler {
	
	private static final String IMAGE_TAG = "image_big";
	private static final String IMAGE_MINI_TAG = "image_small";
	private static final String DROP_TAG = "drop";
	private static final String ID_TAG = "drop_id";
	
	public DropularHandler()
	{
		super();
		this.inSection = false;
		this.inTextTag = false;
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if (DROP_TAG.equals(name))
		{
			inSection = true;
			currentId = null;
		}
		if (ID_TAG.equals(name) || IMAGE_TAG.equals(name) || IMAGE_MINI_TAG.equals(name))
		{
			buffer = new StringBuffer();
			inTextTag = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (DROP_TAG.equals(name))
		{
			inSection = false;
		}
		if (ID_TAG.equals(name))
		{
			currentId = buffer.toString();
			inTextTag = false;
		}
		if (IMAGE_TAG.equals(name) && this.currentId != null)
		{
			bigMap.put(currentId, buffer.toString());
			inTextTag = false;
		}
		if (IMAGE_MINI_TAG.equals(name) && this.currentId != null)
		{
			miniMap.put(currentId, buffer.toString());
			inTextTag = false;
		}
	}
}
