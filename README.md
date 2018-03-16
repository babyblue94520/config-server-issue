# Config Server Issues

[issues demo]()

## issues
1.If git.uri is ssh uri, then all git.repos must be ssh uri?
	I set the ssh key, then git.repos can not use the local repository, it will throw this exception.
	
exception:

	Reason: Property 'spring.cloud.config.server.git.privateKey' must be set when 'spring.cloud.config.server.git.ignoreLocalSshSettings' is specified

[application-issue1]()


2.Set up multiple ssh keys to access git repository.
	I have a lot of private git repositories and they have different deploy keys,because I think it should not be accessed by my account's ssh key, but when I set a different deploy key, it will throw this exception.

access url: http://127.0.0.1:8080/Config/config-repo1/dev

exception:

	org.springframework.cloud.config.server.environment.NoSuchRepositoryException: Cannot clone or checkout repository: git@github.com:istar588/config-repo3.git
	
[application-issue2]()