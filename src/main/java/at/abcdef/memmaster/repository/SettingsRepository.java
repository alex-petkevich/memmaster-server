package at.abcdef.memmaster.repository;

import at.abcdef.memmaster.model.Settings;
import at.abcdef.memmaster.model.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Long>
{
    String SETTINGS_EMAIL_CACHE = "settingsEmailCache";

    String SETTINGS_GLOBAL_CACHE = "settingsGlobalCache";

    List<Settings> getByUserId(Integer userId);

    Settings getByUserIdAndName(Integer userId, String name);

    @Cacheable(cacheNames = SETTINGS_EMAIL_CACHE)
    @Query("SELECT user FROM Settings WHERE name = 'period' AND value != ''")
    List<User> getUserWithEmailSettings();

    @Cacheable(cacheNames = SETTINGS_GLOBAL_CACHE)
    List<Settings> findAllByUserIdIsNull();

}
