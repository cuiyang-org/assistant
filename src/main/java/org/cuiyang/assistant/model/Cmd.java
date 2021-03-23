package org.cuiyang.assistant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 命令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cmd {
    private String name;
    private String path;
    private String args;
}
