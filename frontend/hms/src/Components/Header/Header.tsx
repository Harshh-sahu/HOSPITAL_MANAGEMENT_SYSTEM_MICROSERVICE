import ProfileMenu from './ProfileMenu';
import { ActionIcon, Button } from '@mantine/core';
import { IconBellRinging, IconLayoutSidebarLeftCollapse } from '@tabler/icons-react';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { removeJwt } from '../../Slices/JwtSlice';
import { removeUser } from '../../Slices/UserSlice';


const Header = () => {
  const dispatch = useDispatch();
  const jwt = useSelector((state: any) => state.jwt);
  const handleLogout=()=>{
    console.log("Logging out...");
    dispatch(removeJwt())
    dispatch(removeUser())

  }
  return (
    <div className="w-full bg-light h-16 shadow-lg flex justify-between px-5 items-center">
      <ActionIcon variant="transparent" size="lg" aria-label="Toggle Sidebar">
        <IconLayoutSidebarLeftCollapse style={{ width: '90%', height: '70%' }} stroke={1.5} />
      </ActionIcon>

      <div className="flex gap-5 items-center">
        {jwt ? (
          <Button color="red"  onClick={handleLogout}>
            Logout
          </Button>
        ) : (
          <Link to="/login">
            <Button>Login</Button>
          </Link>
        )}

         {  jwt&&<><ActionIcon variant="transparent" size="md" aria-label="Notifications">
          <IconBellRinging style={{ width: '90%', height: '70%' }} stroke={2} />
        </ActionIcon>

     <ProfileMenu /></>}
      </div>
    </div>
  );
};

export default Header;
