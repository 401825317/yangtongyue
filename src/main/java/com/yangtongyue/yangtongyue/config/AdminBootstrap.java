package com.yangtongyue.yangtongyue.config;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.yangtongyue.yangtongyue.domain.AppUser;
import com.yangtongyue.yangtongyue.repo.AppUserRepository;

/** Creates the first admin account when no users exist yet. */
@Component
public class AdminBootstrap implements ApplicationRunner {

	private static final Logger LOG = LoggerFactory.getLogger(AdminBootstrap.class);

	private final AppUserRepository users;
	private final PasswordEncoder encoder;
	private final AppProperties props;

	public AdminBootstrap(AppUserRepository users, PasswordEncoder encoder, AppProperties props) {
		this.users = users;
		this.encoder = encoder;
		this.props = props;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (users.count() > 0) {
			return;
		}
		String username = props.admin().username();
		String password = props.admin().password();
		boolean generated = !StringUtils.hasText(password);
		if (generated) {
			password = UUID.randomUUID().toString().substring(0, 12);
		}
		AppUser admin = new AppUser();
		admin.setUsername(username);
		admin.setDisplayName(username);
		admin.setRole(AppUser.ROLE_ADMIN);
		admin.setPasswordHash(encoder.encode(password));
		users.save(admin);
		if (generated) {
			LOG.warn("Created admin user '{}' with generated password: {} (set ADMIN_PASSWORD to choose one)",
					username, password);
		} else {
			LOG.info("Created admin user '{}'", username);
		}
	}
}
