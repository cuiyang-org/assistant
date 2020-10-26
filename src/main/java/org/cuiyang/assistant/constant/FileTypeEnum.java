package org.cuiyang.assistant.constant;

import java.io.File;

/**
 * 文件类型
 *
 * @author cuiyang
 */
public enum FileTypeEnum {
    JSON(".json", "JSON"),
    HTML(".html", "HTML"),
    XML(".xml", "XML"),
    COOKIE(".cookie", "Cookie"),
    FORM(".form", "Form");

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

    public static FileTypeEnum parseOfFile(File file) {
        for (FileTypeEnum fileType : FileTypeEnum.values()) {
            if (file.getName().endsWith(fileType.suffix)) {
                return fileType;
            }
        }
        return null;
    }
}
