Map<String, Object> ${mapName} = new HashMap<>();
<#list data?keys as key>
<#if data["${key}"]??>
${mapName}.put("${key}", "${data["${key}"]}");
<#else>
${mapName}.put("${key}", null);
</#if>
</#list>