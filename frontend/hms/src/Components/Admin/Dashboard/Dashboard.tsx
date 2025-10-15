import React from 'react'
import TopCards from './TopCards'
import DiseaseChart from './DiseaseChart'
import Appointment from './Appointment'
import MedicineChart from './MedicineChart'
import PatientChart from './PatientChart'
import DoctorChart from './DoctorChart'


const Dashboard = () => {
  return (
    <div className='flex flex-col gap-5'>
      <TopCards/>
      <div className='grid grid-cols-3 gap-5'>
        <DiseaseChart/>
        <Appointment/>
        <MedicineChart/>
      </div>
      <div className='grid grid-cols-2 gap-5'>

 <PatientChart/>
 <DoctorChart/>
      </div>
    </div>
  )
}

export default Dashboard
