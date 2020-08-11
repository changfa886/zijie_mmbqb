
CREATE TABLE IF NOT EXISTS zijie_admin (
id                      bigserial primary key,
admname                 varchar(16) not null unique,
loginpw                 varchar(64) not null,
role                    varchar(16) not null, -- system, ...
nickname                text
);
insert into zijie_admin (admname,loginpw,role,nickname) values ('zadmin', md5('111111Uk&s23^ruk@'), 'system', '管理员');

CREATE TABLE IF NOT EXISTS zijie_user (
id                      bigserial primary key,
created                 bigint not null,
updated                 bigint not null, -- 活跃时间
status                  integer default 0, -- 0:正常 -1:冻结
openid                  text unique,
nickname                text,
avatar                  text
);

CREATE TABLE IF NOT EXISTS zijie_catagory (
id                      bigserial primary key,
name                    text,
status                  integer default 0, -- 0:正常 1:删除
isort                   integer default 0 -- 排序系统 越大越靠前
);
insert into zijie_catagory (name) values ('首页');
insert into zijie_catagory (name) values ('创意');
insert into zijie_catagory (name) values ('简约');
insert into zijie_catagory (name) values ('卡通');
insert into zijie_catagory (name) values ('摄影');
insert into zijie_catagory (name) values ('颜控');
insert into zijie_catagory (name) values ('可爱');

CREATE TABLE IF NOT EXISTS zijie_goods (
id                      bigserial primary key,
status                  integer default 0,  -- 0正常 1删除
catagoryid              bigint not null,
isort                   integer default 0,  -- 排序系数 越大越靠前
title                   text not null, -- 套图标题
price                   integer default 0,
buytimes                integer default 0,  -- 购买次数
headimg                 text, -- 首图
imgs                    text, -- 多图 逗号分隔
remark                  text -- 套图备注
);
CREATE INDEX zijie_goods_catagoryid_index ON zijie_goods (catagoryid);
CREATE INDEX zijie_goods_isort_index ON zijie_goods (isort);


CREATE TABLE IF NOT EXISTS zijie_pay_online(
id                      bigserial primary key,
created                 bigint not null,
updated                 bigint not null,
uid                     bigint not null, --
status                  integer default 0, -- 0:预支付 待付款 1:成功 2:失败
out_trade_no            varchar(32) not null unique, -- 商户系统内部订单号，要求32个字符内
goods_id                bigint not null,
total_fee               integer default 0, -- 费用 分. 重复是为了防止支付过程中用户修改礼物配置
remark                  text
);
CREATE INDEX zijie_pay_online_uid_index ON zijie_pay_online (uid);


---- 购买记录
CREATE TABLE IF NOT EXISTS zijie_buy_record (
id              bigserial primary key,
created         bigint not null,
uid             bigint not null,
amount          integer default 0, -- 分
type            integer not null, -- 0:广告赠送 1:支付宝 2:微信
goodsid         bigint not null, -- 商品id
catagoryid      bigint not null, -- 冗余 简化二次查询
remark          text
);
CREATE INDEX zijie_buy_record_uid_index ON zijie_buy_record (uid);
CREATE INDEX zijie_buy_record_goodsid_index ON zijie_buy_record (goodsid);
CREATE INDEX zijie_buy_record_catagoryid_index ON zijie_buy_record (catagoryid);
CREATE INDEX zijie_buy_record_created_index ON zijie_buy_record (created);


CREATE TABLE IF NOT EXISTS zijie_globle (
id                      bigserial primary key,
name                    text not null unique,
type                    integer default 0, -- 0:str 1:integer 2:html
value                   text,
remark                  text
);
insert into zijie_globle (name,type,value,remark) values ('paytype', 1, '-1', '系统支付状态'); -- <0表示无需支付
insert into zijie_globle (name,type,value,remark) values ('payversion', 0, '1.0.5', '系统支付状态针对的版本'); -- 只有该版本下，才可能出现 paytype<0


--------------------------------------------------------------------------------

