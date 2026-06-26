import React from 'react'
import Welcome from './Welcome'
import DiseaseChart from './DiseaseChart'
import Visits from './Visits'
import Appointment from './Appointment'
import MedicineChart from './MedicineChart'
import HealthSummary from './HealthSummary'
import AIHealthInsights from './AIHealthInsights'

const Dashboard = () => {
  return (
    <div className='flex flex-col gap-5'>
          <div className='grid lg:grid-cols-2 gap-5'>
            <Welcome/>
            <Visits/>
          </div>
          <HealthSummary />
          <div className='grid lg:grid-cols-3 gap-5'>
            <DiseaseChart/>
    <Appointment/>
    <MedicineChart/>
          </div>
          <AIHealthInsights />
    </div>
  )
}

export default Dashboard
