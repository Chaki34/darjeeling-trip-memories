async function loadManifest(){
  const photoGrid=document.getElementById('photo-grid');
  const videoGrid=document.getElementById('video-grid');
  try{
    const response=await fetch('media/manifest.json');
    if(!response.ok) throw new Error('Manifest not found');
    const data=await response.json();
    if(data.photos?.length){
      photoGrid.innerHTML='';
      data.photos.forEach(item=>{
        const img=document.createElement('img'); img.src=item.url; img.alt=item.name||'Darjeeling trip photo'; img.loading='lazy';
        photoGrid.appendChild(img);
      });
    }
    if(data.videos?.length){
      videoGrid.innerHTML='';
      data.videos.forEach(item=>{
        const video=document.createElement('video'); video.src=item.url; video.controls=true; video.preload='metadata'; video.playsInline=true;
        videoGrid.appendChild(video);
      });
    }
  }catch(error){console.info('Static gallery is awaiting media/manifest.json. Generate it after uploading media.',error.message);}
}
loadManifest();
