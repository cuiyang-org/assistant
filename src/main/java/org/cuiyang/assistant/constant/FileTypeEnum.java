package org.cuiyang.assistant.constant;

/**
 * 文件类型
 *
 * @author cuiyang
 */
public enum FileTypeEnum {
    JSON(".json", "JSON"),
    HTML(".html", "HTML"),
    XML(".xml", "XML"),
    COOKIE(".cookie", "COOKIE"),
    FORM(".form", "FORM");

    private String suffix;
    private String desc;

    FileTypeEnum(String suffix, String desc) {
        this.suffix = suffix;
        this.desc = desc;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getDesc() {
        return desc;
    }

    public static FileTypeEnum parseOfDesc(String desc) {
        for (FileTypeEnum fileType : FileTypeEnum.values()) {
            if (fileType.desc.equalsIgnoreCase(desc)) {
                return fileType;
            }
        }
        return null;
    }
}
