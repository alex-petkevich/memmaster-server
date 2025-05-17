package at.abcdef.memmaster.service;

import static at.abcdef.memmaster.util.GlobUtil.settingValue;

import at.abcdef.memmaster.config.ApplicationProperties;
import at.abcdef.memmaster.model.Settings;
import at.abcdef.memmaster.model.User;
import at.abcdef.memmaster.repository.SettingsRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
public class SettingsService
{
	private final SettingsRepository settingsRepository;

	private final ApplicationProperties applicationProperties;

	public SettingsService(SettingsRepository settingsRepository, ApplicationProperties applicationProperties) {
		this.settingsRepository = settingsRepository;
		this.applicationProperties = applicationProperties;
	}

	public void saveSettings(User user, Map<String, String> values)
	{
		values.forEach((name, value) -> {
			this.setSettingValue(user, name, value);
		});

	}

	public Settings setSettingValue(User user, String name, String value) {
		Settings currentSetting = settingsRepository.getByUserIdAndName(user.getId(), name);
		if (currentSetting == null) {
			currentSetting = new Settings();
			currentSetting.setName(name);
			currentSetting.setCreatedAt(OffsetDateTime.now());
			currentSetting.setUser(user);
		}
		currentSetting.setValue(value);
		currentSetting.setLastModifiedAt(OffsetDateTime.now());
		return settingsRepository.save(currentSetting);
	}

	public List<Settings> getUserSettings(Integer userId)
	{

		return settingsRepository.getByUserId(userId);
	}

	public List<User> getMailSettingsByUsers() {
		List<User> users = settingsRepository.getUserWithEmailSettings();

		return users.stream().filter(it -> {
			String lastTimeProcessed = settingValue(it, "lasttime_mail_processed");
			String period = settingValue(it, "period");
			return "".equals(lastTimeProcessed) 
                    || null == lastTimeProcessed
					|| OffsetDateTime.now().toEpochSecond() > Long.parseLong(lastTimeProcessed) + Long.parseLong(period) * 60;
		}).toList();
	}
}
