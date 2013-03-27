# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table accounting (
  id                        bigint not null,
  year                      integer(4),
  user_id                   bigint,
  constraint pk_accounting primary key (id))
;

create table accounting_row (
  id                        bigint not null,
  accounting_id             bigint,
  label                     varchar(255),
  amount                    bigint,
  personal_withdrawal       bigint,
  row_type                  varchar(7),
  category_id               bigint,
  treasury_id               bigint,
  date                      timestamp,
  constraint ck_accounting_row_row_type check (row_type in ('EXPENSE','RECIPE')),
  constraint pk_accounting_row primary key (id))
;

create table category (
  id                        bigint not null,
  name                      varchar(255),
  row_type                  varchar(7),
  ordering                  bigint,
  parent_id                 bigint,
  constraint ck_category_row_type check (row_type in ('EXPENSE','RECIPE')),
  constraint pk_category primary key (id))
;

create table treasury (
  id                        bigint not null,
  name                      varchar(255),
  ordering                  bigint,
  row_type                  varchar(7),
  constraint ck_treasury_row_type check (row_type in ('EXPENSE','RECIPE')),
  constraint pk_treasury primary key (id))
;

create table user (
  id                        bigint not null,
  username                  varchar(255),
  password                  varchar(255),
  constraint pk_user primary key (id))
;

create sequence accounting_seq;

create sequence accounting_row_seq;

create sequence category_seq;

create sequence treasury_seq;

create sequence user_seq;

alter table accounting add constraint fk_accounting_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_accounting_user_1 on accounting (user_id);
alter table accounting_row add constraint fk_accounting_row_accounting_2 foreign key (accounting_id) references accounting (id) on delete restrict on update restrict;
create index ix_accounting_row_accounting_2 on accounting_row (accounting_id);
alter table accounting_row add constraint fk_accounting_row_category_3 foreign key (category_id) references category (id) on delete restrict on update restrict;
create index ix_accounting_row_category_3 on accounting_row (category_id);
alter table accounting_row add constraint fk_accounting_row_treasury_4 foreign key (treasury_id) references treasury (id) on delete restrict on update restrict;
create index ix_accounting_row_treasury_4 on accounting_row (treasury_id);
alter table category add constraint fk_category_parent_5 foreign key (parent_id) references category (id) on delete restrict on update restrict;
create index ix_category_parent_5 on category (parent_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists accounting;

drop table if exists accounting_row;

drop table if exists category;

drop table if exists treasury;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists accounting_seq;

drop sequence if exists accounting_row_seq;

drop sequence if exists category_seq;

drop sequence if exists treasury_seq;

drop sequence if exists user_seq;

