Map<String, Object> ${mapName} = new HashMap<>();
<#list data?keys as key>
${mapName}.put("${key}", "${data["${key}"]}");
</#list>