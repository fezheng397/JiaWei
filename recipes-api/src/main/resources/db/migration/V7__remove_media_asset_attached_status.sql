update media_assets
set status = 'UPLOADED'
where status = 'ATTACHED';

alter table media_assets
  drop constraint media_assets_status_check;

alter table media_assets
  add constraint media_assets_status_check
  check (status in ('PENDING_UPLOAD', 'UPLOADED', 'DELETED'));

comment on constraint media_assets_status_check on media_assets
  is 'Must match MediaAssetStatus enum values.';
