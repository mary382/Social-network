package com.bootcamp.socialnetwork.util;

import com.bootcamp.socialnetwork.domain.Friendship;
import com.bootcamp.socialnetwork.repository.FriendshipRepository;
import com.bootcamp.socialnetwork.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class ScheduledTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private MessageService messageService;


    @Scheduled(cron = "*/60 * * * * *")
    public void unblockUser() {

        List<Friendship> friendships =
                friendshipRepository.findAllByTimeUnblock12LessThanEqual(System.currentTimeMillis());
        for (Friendship friendship : friendships) {
            friendship.setBlock12(false);
            friendship.setTimeUnblock12(null);
            Friendship friendship1 =
                    friendshipRepository.findByUserIdAndFriendId(friendship.getFriendId(), friendship.getUserId());
            friendship1.setBlock21(false);
            friendship1.setTimeUnblock21(null);
            messageService.unblock(friendship.getFriendId(), friendship.getUserId());
            LOGGER.info("User with id {} removed from blacklist of user with id {}.",
                    friendship.getFriendId(), friendship.getUserId());
        }
    }
}