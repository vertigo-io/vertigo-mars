-- ============================================================
--   Table : EQUIPMENT_CATEGORY                                        
-- ============================================================

alter table EQUIPMENT_CATEGORY
	add column EFO_ID		NUMERIC;

comment on column EQUIPMENT_CATEGORY.EFO_ID is
'Equipment Category Meta Form';

alter table EQUIPMENT_CATEGORY
	add constraint FK_A_EQUIPMENT_TYPE_EASY_FORM_EASY_FORM foreign key (EFO_ID)
	references EASY_FORM (EFO_ID);
	
create index A_EQUIPMENT_TYPE_EASY_FORM_EASY_FORM_FK on EQUIPMENT_CATEGORY (EFO_ID asc);

-- ============================================================
--   Table : EQUIPMENT_SURVEY                                        
-- ============================================================
create sequence SEQ_EQUIPMENT_SURVEY
	start with 1000 cache 1; 
	
create table EQUIPMENT_SURVEY
(
    ESU_ID      	 NUMERIC     	not null,
    DATE_ANSWER 	 TIMESTAMP   	not null,
    FORMULAIRE  	 JSONB       	not null,
    EQUIPMENT_ID	 NUMERIC     	not null,
    PERSON_ID   	 NUMERIC     	not null,
    constraint PK_EQUIPMENT_SURVEY primary key (ESU_ID)
);

comment on column EQUIPMENT_SURVEY.ESU_ID is
'Id';

comment on column EQUIPMENT_SURVEY.DATE_ANSWER is
'Date';

comment on column EQUIPMENT_SURVEY.FORMULAIRE is
'Informations';

comment on column EQUIPMENT_SURVEY.EQUIPMENT_ID is
'Equipment';

comment on column EQUIPMENT_SURVEY.PERSON_ID is
'Respondent';


alter table EQUIPMENT_SURVEY
	add constraint FK_A_EQUIPMENT_SURVEY_EQUIPMENT_EQUIPMENT foreign key (EQUIPMENT_ID)
	references EQUIPMENT (EQUIPMENT_ID);

create index A_EQUIPMENT_SURVEY_EQUIPMENT_EQUIPMENT_FK on EQUIPMENT_SURVEY (EQUIPMENT_ID asc);

alter table EQUIPMENT_SURVEY
	add constraint FK_A_EQUIPMENT_SURVEY_PERSON_PERSON foreign key (PERSON_ID)
	references PERSON (PERSON_ID);

create index A_EQUIPMENT_SURVEY_PERSON_PERSON_FK on EQUIPMENT_SURVEY (PERSON_ID asc);

