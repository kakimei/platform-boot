insert into meta_info(interval_unit, day, hour_begin, minute_begin, hour_end, minute_end, times, meta_type, deleted)
values('WEEK', 2, 15, 0, 16, 0, 1, 'TEAM', 0);
insert into meta_info(interval_unit, day, hour_begin, minute_begin, hour_end, minute_end, times, meta_type, deleted)
values('WEEK', 2, 15, 0, 16, 0, 15, 'SINGLE', 0);

insert into bo_user(bo_user_name, bo_password, bo_role_type, active)
values('admin', 'admin', 'ADMIN', 1)