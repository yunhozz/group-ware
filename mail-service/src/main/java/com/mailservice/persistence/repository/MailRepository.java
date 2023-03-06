package com.mailservice.persistence.repository;

import com.mailservice.persistence.entity.mail.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MailRepository<T extends Mail> extends JpaRepository<T, Long>, MailRepositoryCustom {

    @Query("select m.id from MailWrite mw join mw.mail m join mw.mailSecurity ms where ms.validity is not null and ms.validity >= :now")
    List<Long> findIdsAfterNow(@Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true)
    @Query("update Mail m set m.isDeleted = true where m.id in :ids")
    void deleteListInIds(@Param("ids") List<Long> ids);

    default void deleteExpiredMailList(LocalDateTime now) {
        List<Long> ids = findIdsAfterNow(now);
        deleteListInIds(ids);
    }
}