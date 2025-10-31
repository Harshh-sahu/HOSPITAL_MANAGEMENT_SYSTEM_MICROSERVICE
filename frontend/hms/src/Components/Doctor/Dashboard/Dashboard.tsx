import React from 'react'
import Welcome from './Welcome'
import Metrices from './Metrices'
import DiseaseChart from './DiseaseChart'
import PatientMetrics from './PatientMetrics'
import PatientChart from './PatientChart'
import Appointment from './Appointment'

const Dashboard = () => {
  return (
    <div className='flex flex-col gap-5'>
          <div className='grid lg:grid-cols-2 gap-5'>
            <Welcome/>
            <Metrices/>

          </div>
          <div className='grid lg:grid-cols-3 gap-5'>
            <DiseaseChart/>
            <div className='lg:col-span-2'>
              
  <PatientMetrics/>
            </div>
          </div>
          <div className='grid lg:grid-cols-2 gap-5'>
            <PatientChart/>
            <Appointment/>
          </div>
    </div>
  )
}

export default Dashboard
