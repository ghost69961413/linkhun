package com.linkhub.service.impl;

import com.linkhub.dto.ChatDto.ChatRequest;
import com.linkhub.dto.ChatDto.ChatResponse;
import com.linkhub.dto.ChatDto.MessageRequest;
import com.linkhub.dto.ChatDto.MessageResponse;
import com.linkhub.entity.Chat;
import com.linkhub.entity.Message;
import com.linkhub.entity.User;
import com.linkhub.exception.BadRequestException;
import com.linkhub.exception.UserNotFoundException;
import com.linkhub.mapper.ChatMapper;
import com.linkhub.repository.ChatRepository;
import com.linkhub.repository.MessageRepository;
import com.linkhub.repository.UserRepository;
import com.linkhub.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;


    // =====================================================
    // CURRENT USER
    // =====================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));
    }


    // =====================================================
    // FIND CHAT
    // =====================================================

    private Chat findChat(Long chatId) {

        return chatRepository.findById(chatId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Chat not found"
                        ));
    }


    // =====================================================
    // CHECK CHAT PARTICIPANT
    // =====================================================

    private void checkParticipant(
            Chat chat,
            User user) {

        boolean participant =
                chat.getParticipants()
                        .stream()
                        .anyMatch(
                                participantUser ->
                                        participantUser
                                                .getId()
                                                .equals(user.getId())
                        );

        if (!participant) {

            throw new BadRequestException(
                    "You are not a participant of this chat"
            );
        }
    }


    // =====================================================
    // CREATE CHAT
    // =====================================================

    @Override
    @Transactional
    public ChatResponse createChat(
            ChatRequest request) {

        User currentUser = getCurrentUser();

        User targetUser =
                userRepository.findById(
                        request.getUserId()
                ).orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));


        if (currentUser.getId()
                .equals(targetUser.getId())) {

            throw new BadRequestException(
                    "You cannot create a chat with yourself"
            );
        }


        // Check whether chat already exists
        List<Chat> currentUserChats =
                chatRepository
                        .findByParticipantsContaining(
                                currentUser
                        );


        for (Chat chat : currentUserChats) {

            boolean hasTargetUser =
                    chat.getParticipants()
                            .stream()
                            .anyMatch(
                                    user ->
                                            user.getId()
                                                    .equals(
                                                            targetUser.getId()
                                                    )
                            );

            if (hasTargetUser
                    && chat.getParticipants().size() == 2) {

                return chatMapper.toChatResponse(chat);
            }
        }


        Set<User> participants =
                new HashSet<>();

        participants.add(currentUser);
        participants.add(targetUser);


        Chat chat = Chat.builder()
                .participants(participants)
                .build();


        Chat savedChat =
                chatRepository.save(chat);

        return chatMapper.toChatResponse(
                savedChat
        );
    }


    // =====================================================
    // GET MY CHATS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<ChatResponse> getMyChats() {

        User currentUser = getCurrentUser();

        return chatRepository
                .findByParticipantsContaining(
                        currentUser
                )
                .stream()
                .map(chatMapper::toChatResponse)
                .toList();
    }


    // =====================================================
    // GET CHAT
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public ChatResponse getChat(Long chatId) {

        User currentUser = getCurrentUser();

        Chat chat = findChat(chatId);

        checkParticipant(
                chat,
                currentUser
        );

        return chatMapper.toChatResponse(
                chat
        );
    }


    // =====================================================
    // DELETE CHAT
    // =====================================================

    @Override
    @Transactional
    public void deleteChat(Long chatId) {

        User currentUser = getCurrentUser();

        Chat chat = findChat(chatId);

        checkParticipant(
                chat,
                currentUser
        );

        chatRepository.delete(chat);
    }


    // =====================================================
    // SEND MESSAGE
    // =====================================================

    @Override
    @Transactional
    public MessageResponse sendMessage(
            Long chatId,
            MessageRequest request) {

        User currentUser = getCurrentUser();

        Chat chat = findChat(chatId);

        checkParticipant(
                chat,
                currentUser
        );


        Message message = Message.builder()
                .chat(chat)
                .sender(currentUser)
                .content(request.getContent())
                .read(false)
                .build();


        Message savedMessage =
                messageRepository.save(message);


        return chatMapper.toMessageResponse(
                savedMessage
        );
    }


    // =====================================================
    // GET MESSAGES
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> getMessages(
            Long chatId,
            Pageable pageable) {

        User currentUser = getCurrentUser();

        Chat chat = findChat(chatId);

        checkParticipant(
                chat,
                currentUser
        );


        return messageRepository
                .findByChat(
                        chat,
                        pageable
                )
                .map(chatMapper::toMessageResponse);
    }


    // =====================================================
    // MARK MESSAGES AS READ
    // =====================================================

    @Override
    @Transactional
    public void markMessagesAsRead(
            Long chatId) {

        User currentUser = getCurrentUser();

        Chat chat = findChat(chatId);

        checkParticipant(
                chat,
                currentUser
        );


        Page<Message> messages =
                messageRepository.findByChat(
                        chat,
                        Pageable.unpaged()
                );


        messages.getContent()
                .forEach(message -> {

                    if (!message.getSender()
                            .getId()
                            .equals(
                                    currentUser.getId()
                            )) {

                        message.setRead(true);
                    }
                });


        messageRepository.saveAll(
                messages.getContent()
        );
    }
}