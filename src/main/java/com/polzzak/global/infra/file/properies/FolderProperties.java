package com.polzzak.global.infra.file.properies;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

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
