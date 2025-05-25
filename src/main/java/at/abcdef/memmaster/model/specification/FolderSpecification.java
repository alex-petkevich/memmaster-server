package at.abcdef.memmaster.model.specification;

import at.abcdef.memmaster.model.Folder;
import org.springframework.data.jpa.domain.Specification;

public class FolderSpecification
{
	private FolderSpecification() {
		// hide creation
	}

	public static Specification<Folder> hasName(String name) {
		return (root, query, criteriaBuilder) ->
				criteriaBuilder.like(root.get("name"), '%' + name + '%');
	}
	public static Specification<Folder> hasUuid(String uuid) {
		return (root, query, criteriaBuilder) ->
				criteriaBuilder.like(root.get("uuid"), '%' + uuid + '%');
	}
	public static Specification<Folder> hasParentId(Long parentId) {
		return (root, query, criteriaBuilder) ->
				criteriaBuilder.equal(root.get("parentId"), parentId);
	}
	public static Specification<Folder> hasUserId(Integer userId) {
		return (root, query, criteriaBuilder) ->
				criteriaBuilder.equal(root.get("user").get("id"), userId);
	}
	public static Specification<Folder> hasActive(Boolean active) {
		return (root, query, criteriaBuilder) ->
				criteriaBuilder.equal(root.get("active"), Boolean.TRUE.equals(active) ? 1 : 0);
	}
	public static Specification<Folder> isPublic(Boolean isPublic) {
		return (root, query, criteriaBuilder) ->
				criteriaBuilder.equal(root.get("isPublic"), Boolean.TRUE.equals(isPublic) ? 1 : 0);
	}
	public static Specification<Folder> hasIcon(String icon) {
		return (root, query, criteriaBuilder) ->
				criteriaBuilder.like(root.get("icon"), '%' + icon + '%');
	}

}
