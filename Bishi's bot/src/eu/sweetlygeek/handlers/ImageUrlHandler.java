package eu.sweetlygeek.handlers;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** Image URL Sax Handler
 * @author bishiboosh
 *
 */
public class ImageUrlHandler extends DefaultHandler {
	
	private static final String IMAGE_TAG = "image_big";
	private StringBuffer urlBuf;
	private List<String> urls;
	private boolean inTag;
	
	public ImageUrlHandler()
	{
		this.urlBuf = new StringBuffer();
		this.urls = new ArrayList<String>();
		this.inTag = false;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (inTag)
		{
			urlBuf.append(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (IMAGE_TAG.equals(name))
		{
			urls.add(urlBuf.toString());
			inTag = false;
		}
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if (IMAGE_TAG.equals(name))
		{
			urlBuf = new StringBuffer();
			inTag = true;
		}
	}
	
	public List<String> getUrls()
	{
		return this.urls;
	}

}
