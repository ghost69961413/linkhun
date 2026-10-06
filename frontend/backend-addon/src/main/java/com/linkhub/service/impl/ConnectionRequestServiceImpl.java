package com.linkhub.service.impl;

import com.linkhub.dto.connection.ConnectionRequestDto;
import com.linkhub.dto.connection.ConnectionUserDto;
import com.linkhub.entity.ConnectionRequest;
import com.linkhub.entity.Profile;
import com.linkhub.entity.User;
import com.linkhub.enums.ConnectionStatus;
import com.linkhub.exception.BadRequestException;
import com.linkhub.exception.UserNotFoundException;
import com.linkhub.repository.ConnectionRequestRepository;
import com.linkhub.repository.UserRepository;
import com.linkhub.service.ConnectionRequestService;
import com.linkhub.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConnectionRequestServiceImpl implements ConnectionRequestService {
    private final ConnectionRequestRepository connectionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("Authentication is required");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));
    }

    @Override
    @Transactional
    public ConnectionRequestDto sendRequest(Long recipientId) {
        User sender = currentUser();
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (sender.getId().equals(recipient.getId())) throw new BadRequestException("You cannot connect with yourself");

        var existing = connectionRepository.findBySenderAndRecipient(sender, recipient);
        if (existing.isPresent()) {
            ConnectionRequest request = existing.get();
            if (request.getStatus() == ConnectionStatus.PENDING) throw new BadRequestException("A connection request is already pending");
            if (request.getStatus() == ConnectionStatus.ACCEPTED) throw new BadRequestException("You are already connected");
            request.setStatus(ConnectionStatus.PENDING);
            ConnectionRequest saved = connectionRepository.save(request);
            notify(recipient, sender, "CONNECTION_REQUEST", "sent you a connection request", saved.getId());
            return toDto(saved);
        }
        // If the other user has already invited the current user, accept that invitation
        // so one pair can never have two active requests in opposite directions.
        var reverse = connectionRepository.findBySenderAndRecipient(recipient, sender);
        if (reverse.isPresent() && reverse.get().getStatus() == ConnectionStatus.PENDING) {
            ConnectionRequest request = reverse.get();
            request.setStatus(ConnectionStatus.ACCEPTED);
            ConnectionRequest saved = connectionRepository.save(request);
            notify(recipient, sender, "CONNECTION_ACCEPTED", "accepted your connection request", saved.getId());
            return toDto(saved);
        }
        ConnectionRequest saved = connectionRepository.save(ConnectionRequest.builder()
                .sender(sender).recipient(recipient).status(ConnectionStatus.PENDING).build());
        notify(recipient, sender, "CONNECTION_REQUEST", "sent you a connection request", saved.getId());
        return toDto(saved);
    }

    @Override
    @Transactional
    public ConnectionRequestDto acceptRequest(Long requestId) {
        User recipient = currentUser();
        ConnectionRequest request = connectionRepository.findByIdAndRecipientAndStatus(requestId, recipient, ConnectionStatus.PENDING)
                .orElseThrow(() -> new BadRequestException("Pending connection request not found"));
        request.setStatus(ConnectionStatus.ACCEPTED);
        ConnectionRequest saved = connectionRepository.save(request);
        notify(request.getSender(), recipient, "CONNECTION_ACCEPTED", "accepted your connection request", requestId);
        return toDto(saved);
    }

    @Override
    @Transactional
    public ConnectionRequestDto rejectRequest(Long requestId) {
        User recipient = currentUser();
        ConnectionRequest request = connectionRepository.findByIdAndRecipientAndStatus(requestId, recipient, ConnectionStatus.PENDING)
                .orElseThrow(() -> new BadRequestException("Pending connection request not found"));
        request.setStatus(ConnectionStatus.REJECTED);
        return toDto(connectionRepository.save(request));
    }

    @Override
    @Transactional
    public void withdrawRequest(Long requestId) {
        User sender = currentUser();
        ConnectionRequest request = connectionRepository.findByIdAndSenderAndStatus(requestId, sender, ConnectionStatus.PENDING)
                .orElseThrow(() -> new BadRequestException("Pending sent request not found"));
        connectionRepository.delete(request);
    }

    @Override
    public List<ConnectionRequestDto> getPendingRequests() {
        User recipient = currentUser();
        return connectionRepository.findByRecipientAndStatusOrderByCreatedAtDesc(recipient, ConnectionStatus.PENDING)
                .stream().map(this::toDto).toList();
    }

    @Override
    public List<ConnectionRequestDto> getSentRequests() {
        User sender = currentUser();
        return connectionRepository.findBySenderAndStatusOrderByCreatedAtDesc(sender, ConnectionStatus.PENDING)
                .stream().map(this::toDto).toList();
    }

    @Override
    public List<ConnectionRequestDto> getConnections() {
        return connectionRepository.findConnections(currentUser(), ConnectionStatus.ACCEPTED)
                .stream().map(this::toDto).toList();
    }

    @Override
    public List<ConnectionUserDto> getMutualConnections(Long userId) {
        User current = currentUser();
        User other = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Set<Long> mine = new HashSet<>(connectionRepository.findConnectedUserIds(current, ConnectionStatus.ACCEPTED));
        Set<Long> theirs = new HashSet<>(connectionRepository.findConnectedUserIds(other, ConnectionStatus.ACCEPTED));
        mine.retainAll(theirs);
        return userRepository.findAllById(mine).stream().map(this::userDto).collect(Collectors.toList());
    }

    private void notify(User recipient, User actor, String type, String suffix, Long referenceId) {
        notificationService.createNotification(recipient.getId(), actor.getId(), type,
                actor.getFirstName() + " " + suffix, referenceId);
    }

    private ConnectionRequestDto toDto(ConnectionRequest request) {
        return ConnectionRequestDto.builder()
                .requestId(request.getId()).status(request.getStatus()).createdAt(request.getCreatedAt())
                .sender(userDto(request.getSender())).recipient(userDto(request.getRecipient())).build();
    }

    private ConnectionUserDto userDto(User user) {
        Profile profile = user.getProfile();
        return ConnectionUserDto.builder().userId(user.getId()).username(user.getUsername())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .headline(profile == null ? null : profile.getHeadline())
                .profileImage(profile != null && profile.getProfilePictureUrl() != null
                        ? profile.getProfilePictureUrl() : user.getProfilePicture())
                .build();
    }
}
