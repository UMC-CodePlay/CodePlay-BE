create event clean_input_data on schedule
    EVERY 1 DAY STARTS CURRENT_DATE + INTERVAL 1 DAY
    enable
    do
    BEGIN
        -- 2달 이상 지난 데이터 삭제
        DELETE FROM music WHERE DATE(created_at) < DATE_SUB(NOW(), INTERVAL 60 DAY);
        DELETE FROM remix WHERE DATE(updated_at) < DATE_SUB(NOW(), INTERVAL 60 DAY);
    END;

create event clean_result_data on schedule
    EVERY 1 DAY STARTS CURRENT_DATE + INTERVAL 1 DAY
    enable
    do
    BEGIN
        -- 2주 이상 지난 데이터 삭제
        DELETE FROM harmony WHERE DATE(updated_at) < DATE_SUB(NOW(), INTERVAL 14 DAY);
        DELETE FROM track WHERE DATE(updated_at) < DATE_SUB(NOW(), INTERVAL 14 DAY);
        UPDATE remix set deleted_at = now() WHERE DATE(updated_at) < DATE_SUB(NOW(), INTERVAL 14 DAY);
    END;