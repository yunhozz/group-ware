package com.mailservice.persistence.repository;

import com.mailservice.common.enums.MailType;
import com.mailservice.common.enums.ReadStatus;
import com.mailservice.dto.query.MailFileQueryDto;
import com.mailservice.dto.query.MailWriteQueryDto;
import com.mailservice.dto.query.QMailFileQueryDto;
import com.mailservice.dto.query.QMailWriteQueryDto;
import com.mailservice.dto.response.MailResponseDto;
import com.mailservice.dto.response.MailSimpleResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mailservice.persistence.entity.QMailFile.mailFile;
import static com.mailservice.persistence.entity.QMailSecurity.mailSecurity;
import static com.mailservice.persistence.entity.QMailWrite.mailWrite;
import static com.mailservice.persistence.entity.mail.QMail.mail;

@Repository
@RequiredArgsConstructor
public class MailRepositoryCustomImpl implements MailRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MailSimpleResponseDto> findSimpleMailPageByTypeAndReadStatus(MailType mailType, ReadStatus readStatus, Pageable pageable) {
        List<MailSimpleResponseDto> mailList = queryFactory
                .select(Projections.constructor(
                        MailSimpleResponseDto.class,
                        mail.id,
                        mailWrite.id,
                        mail.title,
                        mail.isRed,
                        mail.createdAt
                ))
                .from(mailWrite)
                .join(mailWrite.mail, mail)
                .where(
                        mailTypeBy(mailType),
                        readStatusBy(readStatus)
                )
                .orderBy(mail.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> mailWriteIds = mailList.stream()
                .map(MailSimpleResponseDto::getMailWriteId)
                .toList();

        List<MailWriteQueryDto> mailWriteList = queryFactory
                .select(new QMailWriteQueryDto(
                        mailWrite.id,
                        mailWrite.writerEmail,
                        mailWrite.isImportant,
                        mailSecurity.rating,
                        mailSecurity.validity
                ))
                .from(mailWrite)
                .join(mailWrite.mailSecurity, mailSecurity)
                .where(mailWrite.id.in(mailWriteIds))
                .fetch();

        Map<Long, List<MailWriteQueryDto>> mailWriteMap = mailWriteList.stream()
                .collect(Collectors.groupingBy(MailWriteQueryDto::getId));
        mailList.forEach(mailSimpleResponseDto -> {
            MailWriteQueryDto extraInfo = mailWriteMap.get(mailSimpleResponseDto.getMailWriteId()).get(0);
            mailSimpleResponseDto.setExtraInfo(extraInfo);
        });

        return new PageImpl<>(mailList, pageable, getTotalCount());
    }

    @Override
    public MailResponseDto findMailDetailsById(Long id) {
        MailResponseDto mailResponseDto = queryFactory
                .select(Projections.constructor(
                        MailResponseDto.class,
                        mail.id,
                        mailWrite.id,
                        mail.title,
                        mail.content,
                        mail.createdAt
                ))
                .from(mailWrite)
                .join(mailWrite.mail, mail)
                .where(mail.id.eq(id))
                .fetchOne();

        if (mailResponseDto != null) {
            MailWriteQueryDto extraInfo = queryFactory
                    .select(new QMailWriteQueryDto(
                            mailWrite.id,
                            mailWrite.writerEmail,
                            mailWrite.isImportant,
                            mailSecurity.rating,
                            mailSecurity.validity
                    ))
                    .from(mailWrite)
                    .join(mailWrite.mailSecurity, mailSecurity)
                    .join(mailWrite.mail, mail)
                    .where(mail.id.eq(mailResponseDto.getMailId()))
                    .fetchOne();

            List<MailFileQueryDto> fileList = queryFactory
                    .select(new QMailFileQueryDto(
                            mailFile.fileId,
                            mailFile.originalName,
                            mailFile.savePath,
                            mailFile.createdAt
                    ))
                    .from(mailFile)
                    .join(mailFile.mail, mail)
                    .where(mail.id.eq(mailResponseDto.getMailId()))
                    .fetch();

            mailResponseDto.setExtraInfo(extraInfo);
            mailResponseDto.setFileList(fileList);
        }

        return mailResponseDto;
    }

    private BooleanExpression mailTypeBy(MailType mailType) {
        if (mailType != null) {
            if (mailType.equals(MailType.IMPORTANT))
                return mailWrite.isImportant.isTrue();
            else
                return mailWrite.isImportant.isFalse();
        }

        return null;
    }

    private BooleanExpression readStatusBy(ReadStatus readStatus) {
        if (readStatus != null) {
            if (readStatus.equals(ReadStatus.READ))
                return mail.isRed.isTrue();
            else
                return mail.isRed.isFalse();
        }

        return null;
    }

    private Long getTotalCount() {
        return queryFactory
                .select(mail.count())
                .from(mail)
                .fetchOne();
    }
}