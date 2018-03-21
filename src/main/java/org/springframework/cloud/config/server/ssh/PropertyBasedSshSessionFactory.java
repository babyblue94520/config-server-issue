package org.springframework.cloud.config.server.ssh;

import org.eclipse.jgit.transport.OpenSshConfig.Host;

import java.util.Map;

import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.util.Base64;
import org.eclipse.jgit.util.FS;

public class PropertyBasedSshSessionFactory extends JschConfigSessionFactory {

	private static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
	private static final String PREFERRED_AUTHENTICATIONS = "PreferredAuthentications";
	private static final String YES_OPTION = "yes";
	private static final String NO_OPTION = "no";
	private static final String SERVER_HOST_KEY = "server_host_key";
	private final Map<String, SshUri> sshKeysByHostname;
	private final JSch jSch;

	public PropertyBasedSshSessionFactory(Map<String, SshUri> sshKeysByHostname, JSch jSch) {
		this.sshKeysByHostname = sshKeysByHostname;
		this.jSch = jSch;
	}

	@Override
	protected void configure(Host hc, Session session) {
		SshUri sshProperties = sshKeysByHostname.get(hc.getHostName());
		String hostKeyAlgorithm = sshProperties.getHostKeyAlgorithm();
		if (hostKeyAlgorithm != null) {
			session.setConfig(SERVER_HOST_KEY, hostKeyAlgorithm);
		}
		if (sshProperties.getHostKey() == null || !sshProperties.isStrictHostKeyChecking()) {
			session.setConfig(STRICT_HOST_KEY_CHECKING, NO_OPTION);
		} else {
			session.setConfig(STRICT_HOST_KEY_CHECKING, YES_OPTION);
		}
		String preferredAuthentications = sshProperties.getPreferredAuthentications();
		if (preferredAuthentications != null) {
			session.setConfig(PREFERRED_AUTHENTICATIONS, preferredAuthentications);
		}
	}

	@Override
	protected Session createSession(Host hc, String user, String uri, int port, FS fs) throws JSchException {
		if (sshKeysByHostname.containsKey(uri)) {
			SshUri sshUriProperties = sshKeysByHostname.get(uri);
			jSch.addIdentity(hc.getHostName(), sshUriProperties.getPrivateKey().getBytes(), null, null);
			if (sshUriProperties.getKnownHostsFile() != null) {
				jSch.setKnownHosts(sshUriProperties.getKnownHostsFile());
			}
			if (sshUriProperties.getHostKey() != null) {
				HostKey hostkey = new HostKey(hc.getHostName(), Base64.decode(sshUriProperties.getHostKey()));
				jSch.getHostKeyRepository().add(hostkey, null);
			}
			return jSch.getSession(user, hc.getHostName(), port);
		}
		throw new JSchException("no keys configured for hostname " + hc.getHostName());
	}

	@Override
	protected void configure(String uri, Session session) {
		SshUri sshProperties = sshKeysByHostname.get(uri);
		String hostKeyAlgorithm = sshProperties.getHostKeyAlgorithm();
		if (hostKeyAlgorithm != null) {
			session.setConfig(SERVER_HOST_KEY, hostKeyAlgorithm);
		}
		if (sshProperties.getHostKey() == null || !sshProperties.isStrictHostKeyChecking()) {
			session.setConfig(STRICT_HOST_KEY_CHECKING, NO_OPTION);
		} else {
			session.setConfig(STRICT_HOST_KEY_CHECKING, YES_OPTION);
		}
		String preferredAuthentications = sshProperties.getPreferredAuthentications();
		if (preferredAuthentications != null) {
			session.setConfig(PREFERRED_AUTHENTICATIONS, preferredAuthentications);
		}
	}

}
