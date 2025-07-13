import ProfileMenu from './ProfileMenu'
import { ActionIcon, Button } from '@mantine/core'
import { IconBellRinging, IconLayoutSidebarLeftCollapse } from '@tabler/icons-react'
import { Link } from 'react-router-dom'

const Header = () => {
  return (
    <div className='w-full bg-light h-16 shadow-lg flex justify-between px-5 items-center'>
  <ActionIcon variant="transparent" size="lg" aria-label="Settings">
      <IconLayoutSidebarLeftCollapse style={{ width: '90%', height: '70%' }} stroke={1.5} />
    </ActionIcon>
    <div className='flex gap-5 items-center'>

      <Link to='login'>
            <Button> 
Login
      </Button>
      </Link>

          <ActionIcon variant="transparent" size="md" aria-label="Settings">
      <IconBellRinging style={{ width: '90%', height: '70%' }} stroke={2} />
    </ActionIcon>
        <ProfileMenu/>
    </div>
    </div>
  )

}

export default Header
