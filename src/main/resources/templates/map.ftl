Map<String, Object> ${name} = new HashMap<>();
<#list data?keys as key>
${name}.put("${key}", "${data["${key}"]}");
</#list>
