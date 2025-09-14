import React from 'react'
import Header from '../Components/Header/Header'
import { Outlet } from 'react-router-dom'
import Sidebar from '../Components/Admin/Sidebar/Sidebar'

const AdminDashboard = () => {
  return (
<div className="flex">
      <Sidebar />

      <div className="w-full flex flex-col">
        <Header />
       <Outlet />
      </div>
    </div>
  )
}

export default AdminDashboard
