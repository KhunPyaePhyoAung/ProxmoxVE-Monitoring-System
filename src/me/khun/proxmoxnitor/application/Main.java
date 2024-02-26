package me.khun.proxmoxnitor.application;

import java.util.LinkedHashMap;
import java.util.Map;

import me.khun.text.StringTemplate;

public class Main {
	public static void main(String[] args) {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("i", "JICA");
		params.put("a", "DAT");
		params.put("c", "CBM");
		params.put("serverName", "JICA");
		params.put("author", "Proxmoxnitor");
		params.put("nodeName", "node1");
		
		String templateString = "${c}!}%$%{a%}%!${i}!2000%%${a}!${a}!";
//		StringTemplate template = new StringTemplate(Main.class.getResourceAsStream("/template/server_node_down_email.txt"));
		StringTemplate template = new StringTemplate(templateString);
		String result = template.bind(params);
		System.out.println(result);
	}
}
