import React from 'react'
import TopCards from './TopCards'
import DiseaseChart from './DiseaseChart'
import Appointment from './Appointment'
import MedicineChart from './MedicineChart'
import PatientChart from './PatientChart'
import DoctorChart from './DoctorChart'
import AdminQuickStats from './AdminQuickStats'
import StockAlertCard from './StockAlertCard'
import SalesOverview from './SalesOverview'
import AIAdminInsights from './AIAdminInsights'

const Dashboard = () => {
  return (
    <div className='flex flex-col gap-5'>
      <TopCards/>
      <AdminQuickStats />
      <StockAlertCard />
      <div className='grid lg:grid-cols-3 gap-5'>
        <DiseaseChart/>
        <Appointment/>
        <MedicineChart/>
      </div>
      <div className='grid lg:grid-cols-2 gap-5'>
        <SalesOverview />
        <AIAdminInsights />
      </div>
      <div className='grid lg:grid-cols-2 gap-5'>
        <PatientChart/>
        <DoctorChart/>
      </div>
    </div>
  )
}

export default Dashboard
