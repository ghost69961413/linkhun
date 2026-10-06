package com.linkhub.service.impl;

import com.linkhub.dto.SearchDto.UserSearchResponse;
import com.linkhub.mapper.UserSearchMapper;
import com.linkhub.repository.UserRepository;
import com.linkhub.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.linkhub.enums.ProfileType;
import com.linkhub.entity.Profile;
import com.linkhub.entity.Skill;
import com.linkhub.entity.User;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final UserRepository userRepository;
    private final UserSearchMapper userSearchMapper;

    @Override
    public Page<UserSearchResponse> searchUsers(
            String query,
            ProfileType profileType,
            Pageable pageable) {

        String normalized = query == null ? "" : query.trim().toUpperCase(java.util.Locale.ROOT);
        try {
            ProfileType namedType = ProfileType.valueOf(normalized);
            if (profileType != null && profileType != namedType) return Page.empty(pageable);
            return userRepository.findByProfileProfileType(namedType, pageable).map(userSearchMapper::toResponse);
        } catch (IllegalArgumentException ignored) {
            // It is a normal text search, so continue matching names, headlines,
            // profile skills, and role-specific profile values.
        }

        String searchText = query == null ? "" : query.trim().toLowerCase(java.util.Locale.ROOT);
        Specification<User> specification = (root, criteriaQuery, criteriaBuilder) -> {
            Join<User, Profile> profile = root.join("profile", JoinType.LEFT);
            Predicate conditions = criteriaBuilder.conjunction();
            if (profileType != null) {
                conditions = criteriaBuilder.and(conditions,
                        criteriaBuilder.equal(profile.get("profileType"), profileType));
            }
            if (!searchText.isEmpty()) {
                String pattern = "%" + searchText + "%";
                Join<User, Skill> skill = profile.join("skills", JoinType.LEFT);
                Join<Profile, ?> roleDetails = profile.join("roleDetails", JoinType.LEFT);
                Predicate textMatch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.coalesce(profile.get("headline"), "")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.coalesce(skill.get("name"), "")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.coalesce(roleDetails.get("detailsJson"), "")), pattern)
                );
                conditions = criteriaBuilder.and(conditions, textMatch);
            }
            criteriaQuery.distinct(true);
            return conditions;
        };
        return userRepository.findAll(specification, pageable).map(userSearchMapper::toResponse);
    }
}
