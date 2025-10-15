import React from 'react'
import Welcome from './Welcome'
import DiseaseChart from './DiseaseChart'
import Visits from './Visits'
import Appointment from './Appointment'
import MedicineChart from './MedicineChart'

const Dashboard = () => {
  return (
    <div className='flex flex-col gap-5'>
          <div className='grid grid-cols-2 gap-5'>
            <Welcome/>
            <Visits/>
          </div>
          <div className='grid grid-cols-3 gap-5'>
            <DiseaseChart/>
    <Appointment/>
    <MedicineChart/>
          </div>
    </div>
  )
}

export default Dashboard
