@Data
public class ${className} {
<#list fields as field>
    /** ${field.fieldName} */
    private ${field.fieldType} ${field.fieldName};
</#list>
}