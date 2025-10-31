import React from 'react'
import Header from '../Components/Header/Header'
import { Outlet } from 'react-router-dom'
import Sidebar from '../Components/Admin/Sidebar/Sidebar'
import { useMediaQuery } from '@mantine/hooks'

const AdminDashboard = () => {
  
    const matches = useMediaQuery('(max-width: 768px)');
  return (
<div className="flex">
  { !matches && <Sidebar />}

      <div className="w-full flex flex-col">
        <Header />
       <Outlet />
      </div>
    </div>
  )
}

export default AdminDashboard
