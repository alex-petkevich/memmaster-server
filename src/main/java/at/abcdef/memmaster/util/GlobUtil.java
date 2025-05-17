package at.abcdef.memmaster.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;

import at.abcdef.memmaster.model.Settings;
import at.abcdef.memmaster.model.User;

public class GlobUtil
{
	private GlobUtil() {
		// empty
	}

	public static Sort getSortOrder(String sort)
	{
		List<Sort.Order> result = new ArrayList<>();
		String[] inpSortList = sort.split(",");
		for (String order: inpSortList)
		{
			String[] inpSort = order.split("-");
			Sort.Order sortOrder = new Sort.Order(
					inpSort.length > 1 && "desc".equals(inpSort[1]) ? Sort.Direction.DESC : Sort.Direction.ASC, inpSort[0]);
			result.add(sortOrder);
		}
		return Sort.by(result);
	}

    public static String settingValue(User user, String name) {
        Optional<Settings> sett = user.getSettings().stream().filter(it -> name.equals(it.getName())).findFirst();
        if (sett.isPresent()) {
            return sett.get().getValue();
        }
        return "";
    }

}
