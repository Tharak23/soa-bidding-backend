package com.bidding.common.env;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.Profiles;

/**
 * Loads {@code backend/.env} for Maven {@code spring-boot:run} / IDE launches,
 * which do not inject process env the way {@code start-all.sh} does.
 */
@SuppressWarnings("removal")
public class DotEnvEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

	static final String PROPERTY_SOURCE_NAME = "bidvelocityDotEnv";

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		if (environment.acceptsProfiles(Profiles.of("test"))) {
			return;
		}
		Path file = locateEnvFile();
		if (file == null) {
			return;
		}
		Map<String, Object> values = parse(file);
		if (values.isEmpty()) {
			return;
		}
		Object jdbcUrl = values.get("SUPABASE_DB_URL");
		if (jdbcUrl instanceof String url) {
			values.put("SUPABASE_DB_URL", toTransactionPooler(url));
		}
		// DodoPaymentsOkHttpClient.fromEnv() reads OS env / system properties, not Spring keys.
		Object dodoKey = values.get("DODO_PAYMENTS_API_KEY");
		if (dodoKey instanceof String key && !key.isBlank()) {
			System.setProperty("dodopayments.apiKey", key);
		}
		Object dodoBase = values.get("DODO_PAYMENTS_BASE_URL");
		if (dodoBase instanceof String base && !base.isBlank()) {
			System.setProperty("dodopayments.baseUrl", base);
		}
		environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, values));
	}

	private static Path locateEnvFile() {
		Path cwd = Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
		Path[] candidates = {
				cwd.resolve(".env.local"),
				cwd.resolve(".env"),
				cwd.resolve("backend/.env.local"),
				cwd.resolve("backend/.env"),
				cwd.getParent() != null ? cwd.getParent().resolve(".env.local") : null,
				cwd.getParent() != null ? cwd.getParent().resolve(".env") : null,
		};
		for (Path candidate : candidates) {
			if (candidate != null && Files.isRegularFile(candidate)) {
				return candidate;
			}
		}
		return null;
	}

	private static Map<String, Object> parse(Path file) {
		Map<String, Object> map = new LinkedHashMap<>();
		try {
			for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
				String trimmed = line.trim();
				if (trimmed.isEmpty() || trimmed.startsWith("#")) {
					continue;
				}
				if (trimmed.startsWith("export ")) {
					trimmed = trimmed.substring("export ".length()).trim();
				}
				int eq = trimmed.indexOf('=');
				if (eq <= 0) {
					continue;
				}
				String key = trimmed.substring(0, eq).trim();
				String value = trimmed.substring(eq + 1).trim();
				if (value.length() >= 2 && ((value.startsWith("\"") && value.endsWith("\""))
						|| (value.startsWith("'") && value.endsWith("'")))) {
					value = value.substring(1, value.length() - 1);
				}
				map.put(key, value);
			}
		}
		catch (IOException ignored) {
			return Map.of();
		}
		return map;
	}

	/**
	 * Supabase session pooler (port 5432) allows only ~15 clients. Four services
	 * exceed that. Transaction mode (6543) multiplexes connections.
	 */
	static String toTransactionPooler(String jdbcUrl) {
		if (jdbcUrl.contains("pooler.supabase.com") && jdbcUrl.contains(":5432")) {
			jdbcUrl = jdbcUrl.replaceFirst(":5432", ":6543");
			if (!jdbcUrl.contains("prepareThreshold=")) {
				jdbcUrl += (jdbcUrl.contains("?") ? "&" : "?") + "prepareThreshold=0";
			}
		}
		return jdbcUrl;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
}
