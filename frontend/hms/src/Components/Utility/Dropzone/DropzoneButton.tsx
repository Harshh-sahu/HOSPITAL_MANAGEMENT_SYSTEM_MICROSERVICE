import { useRef, useState } from 'react';
import { IconCloudUpload, IconDownload, IconX } from '@tabler/icons-react';
import { Button, Group, Text, useMantineTheme } from '@mantine/core';
import { Dropzone, FileWithPath, MIME_TYPES } from '@mantine/dropzone';
import classes from './DropzoneButton.module.css';

import { uploadMedia } from '../../../Service/MediaService';
import { successNotification } from '../../../Utility/NotificationUtil';

export function DropzoneButton({close,form,id} :any) {
  const theme = useMantineTheme();
  const openRef = useRef<() => void>(null);
 const [file,setFile] = useState<FileWithPath | null>(null);
  const [fileId,setFileId] = useState<string | null>(null);

 const handleDrop = async(files:FileWithPath[])=>{
  setFile(files[0]);
  
  uploadMedia(files[0]).then((res)=>{
    console.log("File uploaded successfully:", res);

   setFileId(res.id);

  
    
  }).catch((err)=>{
    console.error("Error uploading file:", err);
  });

 }

 const handleSave=()=>{
form.setFieldValue("profilePictureId",fileId);
close();
 successNotification("File uploaded successfully");
 }
  return (
    <div className={classes.wrapper}>
     {
     !file ?
     <Dropzone
        openRef={openRef}
        onDrop={handleDrop}
        multiple={false}
        className={classes.dropzone}
        radius="md"
        accept={[MIME_TYPES.png, MIME_TYPES.jpeg]}
        maxSize={5 * 1024 ** 2}
      >
        <div style={{ pointerEvents: 'none' }}>
          <Group justify="center">
            <Dropzone.Accept>
              <IconDownload size={50} color={theme.colors.blue[6]} stroke={1.5} />
            </Dropzone.Accept>
            <Dropzone.Reject>
              <IconX size={50} color={theme.colors.red[6]} stroke={1.5} />
            </Dropzone.Reject>
            <Dropzone.Idle>
              <IconCloudUpload size={50} stroke={1.5} className={classes.icon} />
            </Dropzone.Idle>
          </Group>

          <Text ta="center" fw={700} fz="lg" mt="xl">
            <Dropzone.Accept>Drop photo here</Dropzone.Accept>
            <Dropzone.Reject>image file less than 5mb</Dropzone.Reject>
            <Dropzone.Idle>Upload picture</Dropzone.Idle>
          </Text>

          <Text className={classes.description}>
            Drag&apos;n&apos;drop files here to upload. We can accept only <i>.png and jpg</i> files that
            are less than 5mb in size.
          </Text>
        </div>
      </Dropzone>
:
      <img src={URL.createObjectURL(file)} alt="preview"
      className={classes.imagePreview} />
      }

     {!file? <Button className={classes.control} size="md" radius="xl" onClick={() => openRef.current?.()}>
      
      Select Photo
      </Button>
      :
      <div className='flex gap-3 mt-3 items-center justify-center'>
        <Button 
         color='red' size="md" radius="xl" onClick={() => setFile(null)}>
          Change Photo
        </Button><Button 
       color='green'
        size="md" radius="xl" onClick={handleSave}>
      Save 
      </Button>
      </div>
      }
    </div>
  );
}