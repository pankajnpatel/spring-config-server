package org.springframework.cloud.config.server.rdbms.environment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.StringUtils;

public class RDBMSEnvironmentRepository implements EnvironmentRepository {

	private static final String LABEL = "label";
	private static final String PROFILE = "profile";
	private static final String DEFAULT = "default";
	private static final String DEFAULT_PROFILE = null;
	private static final String DEFAULT_LABEL = null;

	private JdbcTemplate jdbcTemplate;
	//private MapFlattener mapFlattener;

	public RDBMSEnvironmentRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		//this.mapFlattener = new MapFlattener();
	}

	@Override
	public Environment findOne(String name, String profile, String label) {
		
		String[] profilesArr = StringUtils.commaDelimitedListToStringArray(profile);
		
		List<String> profiles = new ArrayList<String>(Arrays.asList(profilesArr.clone()));
		
		for (int i = 0; i < profiles.size(); i++) {
			if (DEFAULT.equals(profiles.get(i))) {
				profiles.set(i, DEFAULT_PROFILE);
			}
		}
		profiles.add(DEFAULT_PROFILE); // Default configuration will have 'null' profile
		profiles = sortedUnique(profiles);

		List<String> labels = Arrays.asList(label, DEFAULT_LABEL); // Default configuration will have 'null' label
		labels = sortedUnique(labels);

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(PROFILE, profiles);
		parameters.addValue(LABEL, labels);
		
		System.out.println("Labels: " + labels);
		System.out.println("Profiles: " + profiles);
		
		Environment environment = jdbcTemplate.query("SELECT label, profile, source FROM gateway WHERE profile IN ('prod') AND label IN ('master') ORDER BY label ASC, profile ASC;",
				
				new ResultSetExtractor<Environment>() {

					Environment environment = new Environment(name, profilesArr, label, null, null);
					
					@Override
					public Environment extractData(ResultSet rs) throws SQLException, DataAccessException {
						
						while(rs.next()){
							
							String lbl = rs.getString("label");
							String src = rs.getString("source");
							String profile = rs.getString("source");
							
							System.out.println("Label: " + lbl);
							System.out.println("Profile: " + profile);
							System.out.println("Source: " + src);
							
							Map map = new HashMap<>();
							map.put("src", src);
							PropertySource propSource = new PropertySource(rs.getString("label"), map);
							environment.add(propSource);
						
						}
						
						return environment;
					}
		});
				
		/*Query query = new Query();
		query.addCriteria(Criteria.where(PROFILE).in(profiles.toArray()));
		query.addCriteria(Criteria.where(LABEL).in(labels.toArray()));*/

//		Environment environment = null;
		/*try {
			List<MongoPropertySource> sources = restTemplate.find(query, MongoPropertySource.class, name);
			sortSourcesByLabel(sources, labels);
			sortSourcesByProfile(sources, profiles);
			environment = new Environment(name, profilesArr, label, null, null);
			for (MongoPropertySource propertySource : sources) {
				String sourceName = generateSourceName(name, propertySource);
				Map<String, Object> flatSource = mapFlattener.flatten(propertySource.getSource());
				PropertySource propSource = new PropertySource(sourceName, flatSource);
				environment.add(propSource);
			}
		}
		catch (Exception e) {
			throw new IllegalStateException("Cannot load environment", e);
		}*/

		return environment;
	}

	private ArrayList<String> sortedUnique(List<String> values) {
		return new ArrayList<String>(new LinkedHashSet<String>(values));
	}

	/*private void sortSourcesByLabel(List<MongoPropertySource> sources, final List<String> labels) {
		Collections.sort(sources, new Comparator<MongoPropertySource>() {

			@Override
			public int compare(MongoPropertySource s1, MongoPropertySource s2) {
				int i1 = labels.indexOf(s1.getLabel());
				int i2 = labels.indexOf(s2.getLabel());
				return Integer.compare(i1, i2);
			}

		});
	}

	private void sortSourcesByProfile(List<MongoPropertySource> sources, final List<String> profiles) {
		Collections.sort(sources, new Comparator<MongoPropertySource>() {

			@Override
			public int compare(MongoPropertySource s1, MongoPropertySource s2) {
				int i1 = profiles.indexOf(s1.getProfile());
				int i2 = profiles.indexOf(s2.getProfile());
				return Integer.compare(i1, i2);
			}

		});
	}

	private String generateSourceName(String environmentName, MongoPropertySource source) {
		String sourceName;
		String profile = source.getProfile() != null ? source.getProfile() : DEFAULT;
		String label = source.getLabel();
		if (label != null) {
			sourceName = String.format("%s-%s-%s", environmentName, profile, label);
		} else {
			sourceName = String.format("%s-%s", environmentName, profile);
		}
		return sourceName;
	}*/

	public static class MongoPropertySource {

		private String profile;
		private String label;
		private LinkedHashMap<String, Object> source = new LinkedHashMap<String, Object>();

		public String getProfile() {
			return profile;
		}

		public void setProfile(String profile) {
			this.profile = profile;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public LinkedHashMap<String, Object> getSource() {
			return source;
		}

		public void setSource(LinkedHashMap<String, Object> source) {
			this.source = source;
		}

	}

	private static class MapFlattener extends YamlProcessor {

		public Map<String, Object> flatten(Map<String, Object> source) {
			return getFlattenedMap(source);
		}

	}

}
