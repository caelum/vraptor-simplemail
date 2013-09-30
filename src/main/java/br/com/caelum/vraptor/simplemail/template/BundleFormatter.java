package br.com.caelum.vraptor.simplemail.template;

public interface BundleFormatter {
	
	String getMessage(String key);
	
	String getMessage(String key, Object param);

	String getMessage(String key, Object... params);

}
