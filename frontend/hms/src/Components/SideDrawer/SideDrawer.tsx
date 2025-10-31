import { ActionIcon, Drawer } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { IconMenu2 } from '@tabler/icons-react';
import DoctorSidebar from '../Doctor/Sidebar/Sidebar';
import AdminSidebar from '../Admin/Sidebar/Sidebar';
import Sidebar from '../Patient/Sidebar/Sidebar';
import { useSelector } from 'react-redux';

const SideDrawer = () => {
   const [opened, { open, close }] = useDisclosure(false);
   const user  = useSelector((state:any)=>state.user);

  return (
    <>
      <Drawer size="auto" opened={opened} withCloseButton={false} onClose={close} padding={0} overlayProps={{backgroundOpacity: 0.5,blur: 4}} >
     { user.role === 'PATIENT' ?<Sidebar/>: user.role === 'DOCTOR' ? <DoctorSidebar/> : <AdminSidebar/>}
      </Drawer>

           <ActionIcon onClick={open} variant="filled" size="lg" aria-label="Menu">
             <IconMenu2 style={{ width: '90%', height: '70%' }} stroke={1.5} />
           </ActionIcon>
     
    </>);
}

export default SideDrawer
