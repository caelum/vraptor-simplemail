package br.com.caelum.vraptor.simplemail.template;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.inject.Inject;

public class DefaultBundleFormatter implements BundleFormatter{

	private ResourceBundle bundle;

	
	@Inject
	public DefaultBundleFormatter(ResourceBundle bundle){
		this.bundle = bundle;
	}
	
	@Override
	public String getMessage(String key, Object... params) {
		return MessageFormat.format(getMessage(key), params);
	}

	@Override
	public String getMessage(String key) {
		return bundle.getString(key);
	}

	@Override
	public String getMessage(String key, Object first) {
		return MessageFormat.format(getMessage(key), first);
	}
}
