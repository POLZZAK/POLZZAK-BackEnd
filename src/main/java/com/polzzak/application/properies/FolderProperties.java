package com.polzzak.application.properies;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "folder")
public class FolderProperties {
    private final String etcFolderName;
    private final String profileFolderName;

    public FolderProperties(final String etcFolderName, final String profileFolderName) {
        this.etcFolderName = etcFolderName == null ? "" : etcFolderName;
        this.profileFolderName = profileFolderName == null ? "" : profileFolderName;
    }
}
