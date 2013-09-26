package br.com.caelum.vraptor.simplemail.template;

public interface BundleFormatter {
	
	String getMessage(String key, Object... params);

}
