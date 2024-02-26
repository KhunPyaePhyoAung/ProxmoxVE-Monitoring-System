package me.khun.text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class StringTemplate {

	private String template;

	private Set<String> parameterNames;

	private static final String OPENING = "${";
	private static final String CLOSING = "}!";
	private static final String VARIABLE_NAME_PATTERN = "^[a-zA-Z_][a-zA-Z0-9_]*$";

	public StringTemplate(InputStream inputStream) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		this.template = reader.lines().collect(Collectors.joining("\n"));
		init();
	}
	
	public StringTemplate(String template) {
		this.template = template;
		init();
	}

	private void init() {
		this.parameterNames = new LinkedHashSet<String>();
		Pattern pattern = Pattern.compile(VARIABLE_NAME_PATTERN);

		int index = 0;
		while (index < this.template.length()) {
			int openIndex = this.template.indexOf(OPENING, index);
			int closeIndex = this.template.indexOf(CLOSING, Math.max(openIndex, index));
			int flagCloseIndex = this.template.indexOf(CLOSING, index);

			boolean error = false;
			String message = null;

			if (openIndex == -1 && closeIndex == -1) {
				break;
			} else if (openIndex != -1 && closeIndex == -1) {
				error = true;
				message = String.format("Parameter closing not found at index %d", openIndex);
			} else if (openIndex == -1 && closeIndex != -1) {
				error = true;
				message = String.format("Parameter opening not found at index %d", closeIndex);
			} else if (openIndex + 2 >= closeIndex) {
				error = true;
				message = String.format("Parameter opening not found at index %d", closeIndex);
			} else if (flagCloseIndex < closeIndex) {
				error = true;
				message = String.format("Parameter opening not found at index %d", flagCloseIndex);
			}

			String param = this.template.substring(openIndex + OPENING.length(), closeIndex).trim();
			Matcher matcher = pattern.matcher(param);
			if (!error && !matcher.matches()) {
				error = true;
				message = String.format("Invalid variable name : %s", param);
			}
			
			if (error) {
				throw new IllegalArgumentException(message);
			}

			parameterNames.add(param);
			index = closeIndex + CLOSING.length();
		}
	}

	public String bind(Map<String, Object> parameters) {
		String result = template;
		for (String paramName : parameterNames) {
			if (!parameters.containsKey(paramName)) {
				throw new IllegalArgumentException("No parameter value for : " + paramName);
			}
			Object paramValue = parameters.get(paramName);
			result = result.replaceAll(String.format("\\$\\{\\s*%s\\s*\\}!", paramName), String.valueOf(paramValue));
		}
		
		result = result.replaceAll("%!", "!")
					   .replaceAll("%\\$", "\\$")
					   .replaceAll("%\\{", "\\{")
					   .replaceAll("%\\}", "\\}")
					   .replaceAll("%%", "%");
		
		return result;
	}
}
