SET SCHEMA async_service;

ALTER TABLE async_task
    ALTER COLUMN task_type SET DATA TYPE VARCHAR(255);
