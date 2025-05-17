package at.abcdef.memmaster.service;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TranslateService
{
	private final MessageSource messageSource;

	public TranslateService(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String get(String key) {
		return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
	}
}
