# Darjeeling Trip Memories

A travel-memory website built with **Java 21, Spring Boot, Thymeleaf, Amazon S3 and CloudFront**. It displays trip photographs and videos stored in S3.

The project contains two deliverables:

1. **Spring Boot application** — server-rendered pages and an API that lists media objects from S3.
2. **`s3-static-site/`** — a separate static HTML/CSS/JS edition that can be hosted directly by Amazon S3 to demonstrate the Module 03 static website hosting objective.

> The Google Drive folder is not automatically copied by this starter project. Download the media you want to publish, review permissions/consent, and upload the selected files to S3. Do not make private media public without permission.

## 1. Requirements

- Java 21+
- Maven 3.9+
- AWS account
- An S3 bucket
- A CloudFront distribution (recommended for HTTPS and private S3 origin)

## 2. S3 folder structure

Upload media using this structure:

```text
your-bucket/
├── photos/
│   ├── mountain-view.jpg
│   ├── tea-garden.jpg
│   └── ...
└── videos/
    ├── trip-highlight.mp4
    └── ...
```

Supported photo extensions: JPG, JPEG, PNG, WEBP, GIF.

Supported video extensions: MP4, WEBM, MOV, M4V.

Keep large video files compressed to a sensible size. For a large collection, consider S3 multipart uploads and CloudFront caching.

## 3. Configure AWS

Set these environment variables before running the app:

**Windows PowerShell**
```powershell
$env:AWS_REGION="ap-south-1"
$env:AWS_S3_BUCKET="your-unique-bucket-name"
$env:AWS_CLOUDFRONT_BASE_URL="https://YOUR_DISTRIBUTION.cloudfront.net"
```

**Linux / macOS**
```bash
export AWS_REGION=ap-south-1
export AWS_S3_BUCKET=your-unique-bucket-name
export AWS_CLOUDFRONT_BASE_URL=https://YOUR_DISTRIBUTION.cloudfront.net
```

Authentication uses the AWS SDK default credential provider chain. For local development, configure the AWS CLI (`aws configure`) or use environment credentials. On EC2, attach an IAM instance role instead of storing access keys on the server.

The application role/user needs `s3:ListBucket` for the configured bucket and `s3:GetObject` for the relevant objects if your chosen delivery design requires it. With CloudFront OAC, visitors receive media through CloudFront while the S3 origin remains private.

## 4. Run locally

```bash
mvn clean spring-boot:run
```

Open:
- http://localhost:8080/
- http://localhost:8080/gallery
- http://localhost:8080/videos
- http://localhost:8080/about
- http://localhost:8080/api/media

If bucket or CloudFront settings are missing, the pages show a helpful empty state. Once configured, the application lists objects under `photos/` and `videos/`.

## 5. Build executable JAR

```bash
mvn clean package
java -jar target/darjeeling-trip-memories.jar
```

## 6. Deploy the Spring Boot app

You can deploy the JAR to EC2 (for example, behind Apache or Nginx) or another Java hosting service. Set the three AWS environment variables on the server and attach an IAM role with least-privilege S3 access.

This Spring Boot server is **not** executed by S3 static website hosting. S3 hosts static files; Spring Boot needs a Java application runtime such as EC2.

## 7. S3 static website deliverable (Module 03)

The `s3-static-site/` folder is a separate static edition for the internship's S3 website-hosting objective.

1. Create a bucket for the static website.
2. Copy the contents of `s3-static-site/` into the bucket root.
3. Enable Static website hosting and set `index.html` as the index document.
4. For the simplest learning exercise, follow AWS's static website hosting permissions guidance. For a production-safe HTTPS setup, use CloudFront with Origin Access Control and a private S3 REST origin instead of exposing the bucket website endpoint.
5. Configure the static site to use a CloudFront media base URL.

### Generate the static gallery manifest

Make a local media directory:

```text
media/
├── photos/
└── videos/
```

Put your selected files in those folders, then run:

```bash
python scripts/generate_manifest.py --media-dir media --base-url https://YOUR_DISTRIBUTION.cloudfront.net
```

This creates `s3-static-site/media/manifest.json`. Upload the `photos/` and `videos/` folders to the media bucket using the same object keys, then upload the static website files. The manifest contains CloudFront URLs and is used by the static site's JavaScript to render the gallery.

Example AWS CLI commands (replace bucket names):

```bash
aws s3 sync media/photos/ s3://YOUR-MEDIA-BUCKET/photos/
aws s3 sync media/videos/ s3://YOUR-MEDIA-BUCKET/videos/
aws s3 sync s3-static-site/ s3://YOUR-WEBSITE-BUCKET/
```

## 8. Suggested least-privilege IAM policy for the Spring Boot application

Replace `YOUR-MEDIA-BUCKET` with your actual bucket name:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "ListTripMedia",
      "Effect": "Allow",
      "Action": ["s3:ListBucket"],
      "Resource": "arn:aws:s3:::YOUR-MEDIA-BUCKET"
    }
  ]
}
```

The application lists object keys; CloudFront serves the actual media. If you later add application-side uploads or downloads, add only the specific object permissions required for those operations.

## 9. Internship deliverables

- GitHub repository with source code and README.
- S3 bucket configured with photo/video objects.
- S3 static website deployment (or CloudFront distribution backed by S3).
- Live website URL.
- LinkedIn post with screenshots and project description.

## 10. Before publishing

- Confirm everyone shown in the photos/videos is comfortable with public sharing.
- Do not upload confidential files or personal documents.
- Do not commit AWS access keys, secret keys, `.env` files or credentials.
- Keep the S3 bucket private when using CloudFront OAC.
- Add a custom domain only if you own/control it.
