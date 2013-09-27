package br.com.caelum.vraptor.simplemail.template;

import java.text.MessageFormat;
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
		
		String message = bundle.getString(key);
		
		if(params.length > 0){
			message = MessageFormat.format(message,params);
		}
		
		return message;
	}
	
}
