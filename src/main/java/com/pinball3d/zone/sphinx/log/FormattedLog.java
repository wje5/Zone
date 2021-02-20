package com.pinball3d.zone.sphinx.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pinball3d.zone.sphinx.log.Log.Level;

import net.minecraft.client.resources.I18n;

public class FormattedLog {
	private List<LogComponent> components = new ArrayList<LogComponent>();

	private static SimpleDateFormat formatter = new SimpleDateFormat("[HH:mm:ss] ");
	private static Date date = new Date();

	public FormattedLog(long time, Level level, String key, Object... parms) {
		String prefix = formatter.format(date) + level.toString() + " ";
		date.setTime(time);
		components.add(new LogComponentString(prefix));
		String text = I18n.format(key);
		Matcher m = Pattern.compile("<[0-9]*>").matcher(text);
		String[] l = text.split("<[0-9]*>");
		for (String s : l) {
			components.add(LogComponent.of(s));
			if (m.find()) {
				s = m.group();
				int index = Integer.valueOf(s.substring(1, s.length() - 1));
				if (index > parms.length - 1) {
					components.add(LogComponent.of(s));
				} else {
					Object o = parms[index];
					if (o instanceof List) {
						for (int i = 0; i < ((List<?>) o).size(); i++) {
							components.add(LogComponent.of(((List<?>) o).get(i)));
							if (i < ((List<?>) o).size() - 1) {
								components.add(LogComponent.of(","));
							}
						}
					} else {
						components.add(LogComponent.of(o));
					}
				}
			}
		}
	}

	public List<LogComponent> getComponents() {
		return components;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		components.forEach(e -> sb.append(e));
		return sb.toString();
	}
}
