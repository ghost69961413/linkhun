package com.linkhub.service.impl;

import com.linkhub.dto.profile.ProfileRequest;
import com.linkhub.dto.profile.ProfileResponse;
import com.linkhub.entity.*;
import com.linkhub.enums.ProfileType;
import com.linkhub.exception.BadRequestException;
import com.linkhub.exception.ProfileNotFoundException;
import com.linkhub.exception.ResourceAlreadyExistsException;
import com.linkhub.exception.UserNotFoundException;
import com.linkhub.mapper.ProfileMapper;
import com.linkhub.repository.*;
import com.linkhub.repository.FollowRepository;
import com.linkhub.service.MediaService;
import com.linkhub.service.ProfileService;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {
    private static final Pattern USERNAME = Pattern.compile("^[a-zA-Z0-9._-]{3,30}$");
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;
    private final MediaService mediaService;
    private final SkillRepository skillRepository;
    private final ObjectMapper objectMapper;
    private final FollowRepository followRepository;

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal()))
            throw new org.springframework.security.access.AccessDeniedException("Please log in again.");
        return userRepository.findByEmail(auth.getName()).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override @Transactional(readOnly = true)
    public ProfileResponse getMyProfile() {
        User user = currentUser();
        Profile profile = profileRepository.findByUserId(user.getId()).orElseThrow(() -> new ProfileNotFoundException("Profile not found"));
        return response(profile);
    }

    @Override @Transactional(readOnly = true)
    public ProfileResponse getProfile(String userId) {
        Profile profile;
        if (userId.matches("\\d+")) profile = profileRepository.findByUserId(Long.valueOf(userId)).orElseThrow(() -> new ProfileNotFoundException("Profile not found"));
        else profile = profileRepository.findByUserUsername(userId).orElseThrow(() -> new ProfileNotFoundException("Profile not found"));
        return response(profile);
    }

    @Override @Transactional(readOnly = true)
    public ProfileResponse getProfileByUsername(String username) { return getProfile(username); }

    @Override
    public ProfileResponse updateProfile(ProfileRequest request) {
        if (request == null) throw new BadRequestException("Profile details are required.");
        User user = currentUser();
        String fullName = clean(request.getFullName());
        if (fullName == null) fullName = String.join(" ", Arrays.asList(user.getFirstName(), user.getLastName())).trim();
        if (fullName.isBlank()) throw new BadRequestException("Full name is required.");
        String username = clean(request.getUsername());
        if (username == null) username = user.getUsername();
        if (!USERNAME.matcher(username).matches()) throw new BadRequestException("Use 3–30 letters, numbers, dots, underscores, or hyphens for username.");
        userRepository.findByUsername(username).filter(other -> !other.getId().equals(user.getId())).ifPresent(other -> { throw new ResourceAlreadyExistsException("Username already exists."); });
        user.setUsername(username);
        String[] names = fullName.trim().split("\\s+", 2);
        user.setFirstName(names[0].substring(0, Math.min(50, names[0].length())));
        user.setLastName(names.length > 1 ? names[1].substring(0, Math.min(50, names[1].length())) : " ");
        userRepository.save(user);

        Profile profile = profileRepository.findByUserId(user.getId()).orElseGet(() -> Profile.builder().user(user).followers(0).following(0).build());
        if (request.getProfileType() != null) profile.setProfileType(request.getProfileType());
        Map<String, Object> roleDetails = sanitizeRoleDetails(profile.getProfileType(), request.getRoleDetails());
        try {
            if (roleDetails.isEmpty()) profile.setRoleDetails(null);
            else if (profile.getRoleDetails() == null) profile.setRoleDetails(RoleProfileDetails.builder().profile(profile).detailsJson(objectMapper.writeValueAsString(roleDetails)).build());
            else profile.getRoleDetails().setDetailsJson(objectMapper.writeValueAsString(roleDetails));
        } catch (Exception e) { throw new BadRequestException("Role-specific profile details could not be saved."); }
        profileMapper.updateProfile(request, profile);
        profile.setUser(user);
        profile.setCoverImageUrl(clean(request.getCoverImage()));
        if (request.getSkills() != null) {
            Set<Skill> skills = new LinkedHashSet<>();
            for (String value : request.getSkills()) {
                String name = clean(value);
                if (name == null || name.length() > 100) continue;
                Skill skill = skillRepository.findByNameIgnoreCase(name).orElseGet(() -> skillRepository.save(Skill.builder().name(name).build()));
                skills.add(skill);
            }
            profile.setSkills(skills);
        }
        if (request.getExperience() != null) {
            profile.getExperiences().clear();
            for (ProfileRequest.ExperienceItem item : request.getExperience()) {
                Experience experience = Experience.builder().companyName(required(item.getCompany(), "Experience company is required.", 255)).designation(required(item.getTitle(), "Experience role is required.", 255)).employmentType(defaultText(item.getEmploymentType())).location(defaultText(item.getLocation())).startYear(year(item.getStartYear())).endYear(year(item.getEndYear())).currentlyWorking(Boolean.TRUE.equals(item.getCurrent())).description(limit(item.getDescription(), 1500)).profile(profile).build();
                if (experience.getCurrentlyWorking()) experience.setEndYear(null);
                profile.getExperiences().add(experience);
            }
        }
        if (request.getEducation() != null) {
            profile.getEducations().clear();
            for (ProfileRequest.EducationItem item : request.getEducation()) {
                Education education = Education.builder().collegeName(required(item.getSchool(), "Education school is required.", 255)).degree(required(item.getDegree(), "Education degree is required.", 255)).fieldOfStudy(defaultText(item.getFieldOfStudy())).startYear(year(item.getStartYear())).endYear(year(item.getEndYear())).grade(decimal(item.getGrade())).description(limit(item.getDescription(), 1000)).profile(profile).build();
                profile.getEducations().add(education);
            }
        }
        if (request.getCertifications() != null) {
            profile.getCertifications().clear();
            for (ProfileRequest.CertificationItem item : request.getCertifications()) {
                String name = clean(item.getName());
                if (name == null) continue;
                profile.getCertifications().add(Certification.builder().name(limit(name,255)).issuer(limit(item.getIssuer(),255)).issueDate(limit(item.getIssueDate(),100)).credentialUrl(limit(item.getCredentialUrl(),1000)).profile(profile).build());
            }
        }
        return response(profileRepository.save(profile));
    }

    @Override public void updateProfilePicture(MultipartFile image) { updateProfileImage(image, false); }

    @Override
    public String updateProfileImage(MultipartFile image, boolean cover) {
        if (image == null || image.isEmpty()) throw new BadRequestException("Choose an image to upload.");
        User user = currentUser();
        Profile profile = profileRepository.findByUserId(user.getId()).orElseGet(() -> Profile.builder().user(user).followers(0).following(0).build());
        String url = mediaService.uploadImage(image, "linkhub_profiles");
        if (cover) profile.setCoverImageUrl(url); else { profile.setProfilePictureUrl(url); user.setProfilePicture(url); userRepository.save(user); }
        profileRepository.save(profile);
        return url;
    }

    private ProfileResponse response(Profile profile) {
        ProfileResponse out = profileMapper.toResponse(profile);
        User user = profile.getUser();
        out.setUsername(user.getUsername());
        out.setUserId(user.getId());
        out.setFirstName(user.getFirstName());
        out.setLastName(user.getLastName());
        out.setProfilePicture(profile.getProfilePictureUrl());
        out.setCoverImage(profile.getCoverImageUrl());
        out.setFollowers(Math.toIntExact(followRepository.countByFollowing(user)));
        out.setFollowing(Math.toIntExact(followRepository.countByFollower(user)));
        out.setProfileType(profile.getProfileType() == null ? ProfileType.PROFESSIONAL : profile.getProfileType());
        if (profile.getRoleDetails() != null && profile.getRoleDetails().getDetailsJson() != null) {
            try { out.setRoleDetails(objectMapper.readValue(profile.getRoleDetails().getDetailsJson(), new TypeReference<>() {})); }
            catch (Exception ignored) { out.setRoleDetails(Map.of()); }
        } else out.setRoleDetails(Map.of());
        out.setSkills(profile.getSkills().stream().map(Skill::getName).sorted(String.CASE_INSENSITIVE_ORDER).toList());
        out.setExperience(profile.getExperiences().stream().map(e -> new ProfileRequest.ExperienceItem(e.getCompanyName(),e.getDesignation(),e.getEmploymentType(),e.getLocation(),str(e.getStartYear()),str(e.getEndYear()),e.getCurrentlyWorking(),e.getDescription())).toList());
        out.setEducation(profile.getEducations().stream().map(e -> new ProfileRequest.EducationItem(e.getCollegeName(),e.getDegree(),e.getFieldOfStudy(),str(e.getStartYear()),str(e.getEndYear()),e.getGrade()==null?"":e.getGrade().toString(),e.getDescription())).toList());
        out.setCertifications(profile.getCertifications().stream().map(c -> new ProfileRequest.CertificationItem(c.getName(),c.getIssuer(),c.getIssueDate(),c.getCredentialUrl())).toList());
        return out;
    }
    private static String clean(String s) { return s == null ? null : s.trim(); }
    private static String required(String s,String message,int max) { String v=clean(s); if(v==null||v.isEmpty()) throw new BadRequestException(message); return limit(v,max); }
    private static String limit(String s,int max) { if(s==null)return null; return s.length()<=max?s:s.substring(0,max); }
    private static String defaultText(String s) { String v=clean(s); return v==null||v.isEmpty()?"Not specified":limit(v,255); }
    private static Integer year(String s) { if(s==null||s.isBlank())return null; try { int v=Integer.parseInt(s.trim()); if(v<1900||v>2200)throw new NumberFormatException(); return v; } catch(NumberFormatException e){throw new BadRequestException("Enter a valid year.");} }
    private static Double decimal(String s) { if(s==null||s.isBlank())return null; try{return Double.valueOf(s.trim());}catch(NumberFormatException e){return null;} }
    private static String str(Integer v) { return v==null?"":v.toString(); }

    private static final Map<ProfileType, Set<String>> ROLE_FIELDS = Map.of(
        ProfileType.STUDENT, Set.of("college", "degree", "branch", "graduationYear", "cgpa", "projects", "certifications", "github"),
        ProfileType.TEACHER, Set.of("institution", "department", "designation", "subjects", "researchInterests", "publications", "experience"),
        ProfileType.DEVELOPER, Set.of("programmingLanguages", "frameworks", "github", "repositories", "projects", "experience", "developerScore"),
        ProfileType.HR, Set.of("company", "designation", "hiringFor", "experience", "openJobs"),
        ProfileType.RECRUITER, Set.of("company", "designation", "hiringFor", "experience", "openJobs"),
        ProfileType.CEO, Set.of("company", "companyWebsite", "industry", "companySize", "position", "experience", "foundedYear"),
        ProfileType.FOUNDER, Set.of("company", "companyWebsite", "industry", "companySize", "position", "experience", "foundedYear"),
        ProfileType.PROFESSIONAL, Set.of());

    private static Map<String, Object> sanitizeRoleDetails(ProfileType type, Map<String, Object> input) {
        if (input == null || input.isEmpty() || type == null) return Map.of();
        Set<String> allowed = ROLE_FIELDS.getOrDefault(type, Set.of());
        Map<String, Object> clean = new LinkedHashMap<>();
        input.forEach((key, value) -> {
            if (!allowed.contains(key) || value == null) return;
            if (value instanceof List<?> list) {
                List<String> items = list.stream().filter(Objects::nonNull).map(Object::toString).map(String::trim).filter(s -> !s.isEmpty()).limit(50).map(s -> s.substring(0, Math.min(500, s.length()))).toList();
                if (!items.isEmpty()) clean.put(key, items);
            } else {
                String text = value.toString().trim();
                if (!text.isEmpty()) clean.put(key, text.substring(0, Math.min(2000, text.length())));
            }
        });
        return clean;
    }
}
