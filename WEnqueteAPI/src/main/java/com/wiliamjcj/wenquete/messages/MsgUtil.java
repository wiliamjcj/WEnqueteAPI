package com.wiliamjcj.wenquete.messages;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MsgUtil {

	@Autowired
	private MessageSource messages;

	private Locale locale = LocaleContextHolder.getLocale();

	public String msg(MessageProvider msgProv, Locale l) {
		return messages.getMessage(msgProv.getKey(), null, null == l ? locale : l);
	}

	public String msg(MessageProvider msgProv) {
		return messages.getMessage(msgProv.getKey(), null, locale);
	}
}
