
document.addEventListener('DOMContentLoaded', () => {

    // -----------------------------------------
    // Gallery Image Handling
    // -----------------------------------------

    document.querySelectorAll('.photo-card img').forEach(image => {

        image.addEventListener('error', () => {
            console.warn(
                'Image could not be loaded:',
                image.currentSrc || image.src
            );

            image.classList.add('media-error');
            image.alt = 'Image unavailable';
        });

        image.addEventListener('load', () => {
            image.classList.add('media-loaded');
        });

    });


    // -----------------------------------------
    // Video Handling
    // -----------------------------------------

    document.querySelectorAll('.video-card video').forEach(video => {

        video.addEventListener('error', () => {
            console.warn(
                'Video could not be loaded:',
                video.currentSrc || video.src,
                'Check the S3 object, CloudFront URL and video codec.'
            );
        });

        video.addEventListener('loadedmetadata', () => {
            console.info(
                'Video metadata loaded:',
                video.currentSrc || video.src
            );
        });

        video.addEventListener('playing', () => {
            console.info('Video playback started.');
        });

    });

});
