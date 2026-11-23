#!/usr/bin/env python3
"""Generate the JSON manifest used by the S3-only static website.

Usage:
  python scripts/generate_manifest.py --media-dir media --base-url https://YOUR-CLOUDFRONT-DOMAIN

Expected local folders:
  media/photos/
  media/videos/

The generated file is written to s3-static-site/media/manifest.json.
"""
import argparse, json
from pathlib import Path
from urllib.parse import quote

IMAGE_EXT = {'.jpg', '.jpeg', '.png', '.webp', '.gif'}
VIDEO_EXT = {'.mp4', '.webm', '.mov', '.m4v'}

parser = argparse.ArgumentParser()
parser.add_argument('--media-dir', default='media')
parser.add_argument('--base-url', required=True, help='CloudFront base URL, without trailing slash')
args = parser.parse_args()

root = Path(args.media_dir)
base = args.base_url.rstrip('/')

def collect(folder, allowed, prefix):
    results = []
    path = root / folder
    if not path.exists():
        return results
    for file in sorted(path.rglob('*')):
        if file.is_file() and file.suffix.lower() in allowed:
            relative = file.relative_to(root).as_posix()
            encoded = '/'.join(quote(part) for part in relative.split('/'))
            results.append({'name': file.name, 'url': f'{base}/{encoded}'})
    return results

manifest = {
    'photos': collect('photos', IMAGE_EXT, 'photos/'),
    'videos': collect('videos', VIDEO_EXT, 'videos/')
}
output = Path('s3-static-site/media/manifest.json')
output.parent.mkdir(parents=True, exist_ok=True)
output.write_text(json.dumps(manifest, indent=2), encoding='utf-8')
print(f'Generated {output}: {len(manifest["photos"])} photos, {len(manifest["videos"])} videos')
