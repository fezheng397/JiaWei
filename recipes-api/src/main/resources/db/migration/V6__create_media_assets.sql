create table media_assets (
  id uuid primary key default gen_random_uuid(),
  public_id varchar(26) unique not null
    constraint media_assets_public_id_check
    check (public_id ~ '^[0-9A-HJKMNP-TV-Z]{26}$'),
  media_type text not null
    constraint media_assets_media_type_check
    check (media_type in ('IMAGE')),
  object_key text not null unique,
  public_url text not null,
  original_filename text,
  content_type text not null,
  size_bytes bigint not null,
  status text not null
    constraint media_assets_status_check
    check (status in ('PENDING_UPLOAD', 'UPLOADED', 'ATTACHED', 'DELETED')),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  created_by uuid,
  updated_by uuid,
  version integer not null default 0
);

create index idx_media_assets_status on media_assets(status);
create index idx_media_assets_media_type on media_assets(media_type);
create index idx_media_assets_object_key on media_assets(object_key);

comment on constraint media_assets_media_type_check on media_assets
  is 'Must match MediaType enum values.';
comment on constraint media_assets_status_check on media_assets
  is 'Must match MediaAssetStatus enum values.';
