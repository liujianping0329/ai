begin;

-- Remove the field from the previous design if it exists.
alter table public.album_item
  drop column if exists sort_order;

-- Existing rows remain null because their image positions are unknown.
alter table public.album_item
  add column if not exists center_x_percent numeric(5, 2)
    check (center_x_percent between 0 and 100),
  add column if not exists center_y_percent numeric(5, 2)
    check (center_y_percent between 0 and 100);

comment on column public.album_item.center_x_percent is
  '物品中心点距离图片左侧的横向百分比，范围 0～100';
comment on column public.album_item.center_y_percent is
  '物品中心点距离图片顶部的纵向百分比，范围 0～100';

-- Keep the same access mode as a Dashboard-created table with RLS unchecked.
alter table public.album_item disable row level security;
grant usage on schema public
  to anon, authenticated, service_role;
grant all privileges on table public.album_item
  to anon, authenticated, service_role;
grant all privileges on sequence public.album_item_id_seq
  to anon, authenticated, service_role;

commit;
