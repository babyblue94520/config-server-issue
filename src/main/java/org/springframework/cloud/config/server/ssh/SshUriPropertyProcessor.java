package org.springframework.cloud.config.server.ssh;

import static org.springframework.cloud.config.server.ssh.SshPropertyValidator.isSshUri;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.transport.URIish;
import org.springframework.cloud.config.server.ssh.SshUriProperties.SshUriNestedRepoProperties;

public class SshUriPropertyProcessor {

	private final SshUriProperties sshUriProperties;

	public SshUriPropertyProcessor(SshUriProperties sshUriProperties) {
		this.sshUriProperties = sshUriProperties;
	}

	public Map<String, SshUri> getSshKeysByHostname() {
		return extractNestedProperties(sshUriProperties);
	}

	private Map<String, SshUri> extractNestedProperties(SshUriProperties uriProperties) {
		Map<String, SshUri> sshUriPropertyMap = new HashMap<>();
		String parentUri = uriProperties.getUri();
		if (isSshUri(parentUri) && getHostname(parentUri) != null) {
			// old
//			sshUriPropertyMap.put(getHostname(parentUri), uriProperties);
			sshUriPropertyMap.put(parentUri, uriProperties);
		}
		Map<String, SshUriNestedRepoProperties> repos = uriProperties.getRepos();
		if(repos != null) {
			for (SshUriNestedRepoProperties repoProperties : repos.values()) {
				String repoUri = repoProperties.getUri();
				if (isSshUri(repoUri) && getHostname(repoUri) != null) {
//					sshUriPropertyMap.put(getHostname(repoUri), repoProperties);
					sshUriPropertyMap.put(repoUri, repoProperties);
				}
			}
		}
		return sshUriPropertyMap;
	}

	protected static String getHostname(String uri) {
		try {
			URIish urIish = new URIish(uri);
			return urIish.getHost();
		} catch (URISyntaxException e) {
			return null;
		}
	}
}
