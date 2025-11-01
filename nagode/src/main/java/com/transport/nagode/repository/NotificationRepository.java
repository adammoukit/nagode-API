package com.transport.nagode.repository;

import com.transport.nagode.models.Notification;
import com.transport.nagode.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserOrderBySentAtDesc(User user);

    long countByUserAndRead(User user, boolean read);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user = :user AND n.read = false")
    int markAllAsReadForUser(@Param("user") User user);

    List<Notification> findBySentAtBefore(LocalDateTime date);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.sentAt DESC")
    List<Notification> findRecentNotificationsByUserId(@Param("userId") UUID userId);
}
