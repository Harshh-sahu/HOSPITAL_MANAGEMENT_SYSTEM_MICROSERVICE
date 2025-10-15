import { DonutChart } from '@mantine/charts'
import React from 'react'
import { diseaseData } from '../../../Data/DashboardData'

const DiseaseChart = () => {
  return (
    <div className='p-3 border rounded-xl bg-green-50 shadow-xl flex flex-col gap-3'>
        <div className='text-xl font-semibold'>
            Disease Distribution
        </div>

        <div className='flex justify-center'>
               <DonutChart withLabelsLine labelsType="percent" withLabels data={diseaseData}
              chartLabel="Disease" 
              thickness={25}
              size={200}
              paddingAngle={10} />

        </div>
      
      
    </div>
  )
}

export default DiseaseChart
