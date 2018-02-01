package com.ald.fanbei.api.ioc.start;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class PlaceHolderReplace {
	public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
	public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";
	public static final int SYSTEM_PROPERTIES_MODE_NEVER = 0;
	public static final int SYSTEM_PROPERTIES_MODE_FALLBACK = 1;
	public static final int SYSTEM_PROPERTIES_MODE_OVERRIDE = 2;
	private String placeholderPrefix = "${";

	private String placeholderSuffix = "}";

	private int systemPropertiesMode = 1;

	private boolean searchSystemEnvironment = true;

	private boolean ignoreUnresolvablePlaceholders = true;

	private static List<String> suffixes = new ArrayList();

	static {
		String[] toAdd = {".conf", ".properties", ".config", ".property", ".xml"};
		for (String t : toAdd) {
			suffixes.add(t);
		}

		String needReplaceStr = System.getProperty("rjrNeedReplace");
		if (needReplaceStr != null) {
			String[] tokens = needReplaceStr.split(",");
			for (String t : tokens)
				if ((t != null) && (!(t.isEmpty())))
					suffixes.add(t.trim());
		}
	}

	public static void main(String[] args) {
		PlaceHolderReplace place = new PlaceHolderReplace();
		Properties props = new Properties();
		props.put("bbbb", "FFFF");

		String strVal = "aagaljgklajgkljalglaafaf,,bkkkk";
		Set visitedPlaceholders = new HashSet();
		String s = place.parseStringValue(strVal, props, visitedPlaceholders);
		System.out.println(s);
	}

	public boolean hasPlaceHolder(String strVal) {
		int startIndex = strVal.indexOf(this.placeholderPrefix);
		if (startIndex == -1) {
			return false;
		}
		int endIndex = findPlaceholderEndIndex(strVal, startIndex);

		return (endIndex > 0);
	}

	public String parseStringValue(String strVal, Properties props, Set visitedPlaceholders) {
		StringBuffer buf = new StringBuffer(strVal);

		int startIndex = strVal.indexOf(this.placeholderPrefix);
		while (startIndex != -1) {
			int endIndex = findPlaceholderEndIndex(buf, startIndex);
			if (endIndex != -1) {
				String placeholder = buf.substring(startIndex + this.placeholderPrefix.length(), endIndex);
				visitedPlaceholders.add(placeholder);

				placeholder = parseStringValue(placeholder, props, visitedPlaceholders);

				String propVal = resolvePlaceholder(placeholder, props, this.systemPropertiesMode);
				if (propVal != null) {
					propVal = parseStringValue(propVal, props, visitedPlaceholders);
					buf.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);

					startIndex = buf.indexOf(this.placeholderPrefix, startIndex + propVal.length());
				} else if (this.ignoreUnresolvablePlaceholders) {
					startIndex = buf.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
				}

				visitedPlaceholders.remove(placeholder);
			} else {
				startIndex = -1;
			}
		}

		return buf.toString();
	}

	private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
		int index = startIndex + this.placeholderPrefix.length();
		int withinNestedPlaceholder = 0;
		while (index < buf.length()) {
			if (StringUtils.substringMatch(buf, index, this.placeholderSuffix)) {
				if (withinNestedPlaceholder > 0) {
					--withinNestedPlaceholder;
					index += this.placeholderSuffix.length();
					continue;
				}

				return index;
			}

			if (StringUtils.substringMatch(buf, index, this.placeholderPrefix)) {
				++withinNestedPlaceholder;
				index += this.placeholderPrefix.length();
			} else {
				++index;
			}
		}
		return -1;
	}

	protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
		String propVal = null;
		if (systemPropertiesMode == 2) {
			propVal = resolveSystemProperty(placeholder);
		}
		if (propVal == null) {
			propVal = resolvePlaceholder(placeholder, props);
		}
		if ((propVal == null) && (systemPropertiesMode == 1)) {
			propVal = resolveSystemProperty(placeholder);
		}
		return propVal;
	}

	protected String resolvePlaceholder(String placeholder, Properties props) {
		return props.getProperty(placeholder);
	}

	protected String resolveSystemProperty(String key) {
		try {
			String value = System.getProperty(key);
			if ((value == null) && (this.searchSystemEnvironment)) {
				value = System.getenv(key);
			}
			return value;
		} catch (Throwable ex) {
		}
		return null;
	}

	public static boolean needReplace(String resourceName) {
		for (String suffix : suffixes) {
			if (resourceName.endsWith(suffix)) {
				return true;
			}
		}
		return false;
	}
}