import React from 'react'

import Header from '../Components/Header/Header'
import { Outlet } from 'react-router-dom'
import Sidebar from '../Components/Patient/Sidebar/Sidebar'
import { useMediaQuery } from '@mantine/hooks'
import ChatWidget from '../Components/ChatBot/ChatWidget'

const PatientDashboard = () => {
  
    const matches = useMediaQuery('(max-width: 768px)');
  return (
<div className="flex">
      { !matches && <Sidebar />}

      <div className="w-full overflow-hidden flex flex-col">
        <Header />
       <Outlet />
      </div>
      <ChatWidget />
    </div>
  )
}

export default PatientDashboard;
