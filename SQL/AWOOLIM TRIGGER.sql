--TRIGGER
--모임 등록시 모임장 모임멤버 테이블에 생성 트리거
CREATE OR REPLACE TRIGGER CLUBINSERT_TRG
    AFTER INSERT ON CLUB
    FOR EACH ROW
BEGIN
    INSERT INTO CLUBMEMBER VALUES(
    CLUBMEMBER_SEQ.NEXTVAL,:NEW.CLUBNO,:NEW.USERID,SYSDATE,1);
    INSERT INTO CHATROOM VALUES (CHATROOM_SEQ.NEXTVAL,:NEW.CLUBNO,:NEW.CLUBTITLE);
    IF :NEW.REGULARTYPE > 0 THEN
    INSERT INTO CLUBSCHEDULE VALUES(
    CLUBSCHEDULE_SEQ.NEXTVAL,:NEW.CLUBNO,:NEW.DDAY,'정기모임');
    ELSE
    INSERT INTO CLUBSCHEDULE VALUES(
    CLUBSCHEDULE_SEQ.NEXTVAL,:NEW.CLUBNO,:NEW.DDAY,'모임 D-DAY');
    END IF;
END;
/


--경고 5회 누적시 유저 삭제
CREATE OR REPLACE PROCEDURE process_warning (
    p_target_id IN NUMBER
) IS
BEGIN
    -- 대상 사용자의 경고 횟수를 1 증가시킴
    UPDATE MEMBER
    SET WARNINGCOUNT = WARNINGCOUNT + 1
    WHERE USERID = p_target_id;

    -- 경고 횟수가 5회 이상인 유저 삭제
    DELETE FROM MEMBER
    WHERE USERID = p_target_id AND WARNINGCOUNT >= 5;

    -- 명시적 커밋은 여기서 하지 않고, 호출하는 코드에서 트랜잭션 종료
END process_warning;
/


CREATE OR REPLACE TRIGGER trg_after_report_processed
AFTER UPDATE OF RESULT ON REPORT
FOR EACH ROW
WHEN (NEW.RESULT != 0)

BEGIN
    -- 공통 메시지 작성
    IF :OLD.TYPE = 'user' THEN
    INSERT INTO ALARM (ALARMNO, USERID, MESSAGE, ISREAD)
    VALUES (ALARM_SEQ.NEXTVAL, :NEW.USERID, '신고가 처리되었습니다.', 0);
        IF :NEW.RESULT = 1 THEN
        INSERT INTO ALARM (ALARMNO, USERID, MESSAGE, ISREAD)
        VALUES (ALARM_SEQ.NEXTVAL, :NEW.TARGETID, '부적절한 행동이 확인되어 제제 되었습니다.', 0);
        END IF;
    END IF;
END;
/


