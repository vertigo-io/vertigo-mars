ALTER TABLE BASE ALTER COLUMN NAME SET NOT NULL;
ALTER TABLE BASE_TYPE ALTER COLUMN LABEL SET NOT NULL;
ALTER TABLE BUSINESS ALTER COLUMN NAME SET NOT NULL;
ALTER TABLE EQUIPMENT ALTER COLUMN NAME SET NOT NULL;
ALTER TABLE EQUIPMENT ALTER COLUMN CODE SET NOT NULL;
ALTER TABLE EQUIPMENT_CATEGORY ALTER COLUMN LABEL SET NOT NULL;
ALTER TABLE EQUIPMENT_CATEGORY ALTER COLUMN ACTIVE SET NOT NULL;
ALTER TABLE EQUIPMENT_FEATURE ALTER COLUMN NAME SET NOT NULL;
ALTER TABLE EQUIPMENT_TYPE ALTER COLUMN LABEL SET NOT NULL;
ALTER TABLE EQUIPMENT_TYPE ALTER COLUMN ACTIVE SET NOT NULL;
ALTER TABLE OPENDATA_SET ALTER COLUMN CODE SET NOT NULL;
ALTER TABLE OPENDATA_SET ALTER COLUMN TITLE SET NOT NULL;
ALTER TABLE PERSON ALTER COLUMN FIRST_NAME SET NOT NULL;
ALTER TABLE PERSON ALTER COLUMN LAST_NAME SET NOT NULL;
ALTER TABLE PERSON ALTER COLUMN EMAIL SET NOT NULL;
ALTER TABLE TAG ALTER COLUMN LABEL SET NOT NULL;
ALTER TABLE TICKET ALTER COLUMN CODE SET NOT NULL;
ALTER TABLE TICKET ALTER COLUMN TITLE SET NOT NULL;
ALTER TABLE WORK_ORDER ALTER COLUMN CODE SET NOT NULL;
ALTER TABLE WORK_ORDER ALTER COLUMN NAME SET NOT NULL;