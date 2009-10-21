package eu.sweetlygeek.handlers;

import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import eu.sweetlygeek.Utils;

public class TumblrHandler extends ImageHandler {
	
	private static final String POST_TAG = "post";
	private static final String URL_TAG = "post";
	
	private int number;
	
	private int miniSize;
	private int maxSize;
	private boolean isMinimum;
	private boolean isMaximum;
	
	public TumblrHandler(int number)
	{
		this.miniSize = Integer.MAX_VALUE;
		this.maxSize = Integer.MIN_VALUE;
		this.isMaximum = false;
		this.isMinimum = false;
		this.number = number;
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if (POST_TAG.equals(name))
		{
			inSection = true;
			currentId = attributes.getValue("id");
		}
		if (URL_TAG.equals(name))
		{
			buffer = new StringBuffer();
			inTextTag = true;
			int width = Integer.parseInt(attributes.getValue("max-width"));
			isMaximum = width > maxSize;
			isMinimum = width < miniSize;
		}
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (POST_TAG.equals(name))
		{
			inSection = false;
		}
		if (URL_TAG.equals(name))
		{
			if (isMinimum)
			{
				miniMap.put(currentId, buffer.toString());
			}
			if (isMaximum)
			{
				bigMap.put(currentId, buffer.toString());
			}
			inTextTag = false;
			isMaximum = false;
			isMinimum = false;
		}
	}

	@Override
	public Map<String, String> getBigMap() {
		return Utils.pickAtRandom(bigMap, number);
	}

	@Override
	public Map<String, String> getMiniMap() {
		return Utils.pickAtRandom(miniMap, number);
	}

}
