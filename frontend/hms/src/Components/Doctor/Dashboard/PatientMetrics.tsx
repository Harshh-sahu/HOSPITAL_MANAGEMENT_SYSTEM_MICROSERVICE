import { AreaChart } from '@mantine/charts'
import React from 'react'


const PatientMetrics = () => {
  const data = [
    {date: 'January', patients: 30},
    {date: 'February', patients: 45},
    {date: 'March', patients: 28},
    {date: 'April', patients: 60},
    {date: 'May', patients: 50},
    {date: 'June', patients: 75},
    {date: 'July', patients: 65},
    {date: 'August', patients: 80},
    {date: 'September', patients: 70},
    {date: 'October', patients: 90},
    {date: 'November', patients: 85},
    {date: 'December', patients: 100},

  ]
  const getSum = (data:any[], key:string) => {
    return data.reduce((sum, item) => sum + item[key], 0);
  }
  return (
    <div className='bg-orange-50 rounded-xl border '>
      <div className='flex justify-between p-5 items-center'>
         <div>
          <div className='font-semibold'>Patients</div>
          <div className='text-xs text-gray-500'>{new Date().getFullYear()}</div>
         </div>
         <div className='text-2xl font-bold text-orange-500'>
           {getSum(data,"patients")}
         </div>

      </div>
          <AreaChart



                       strokeWidth={4}

             withGradient
             fillOpacity={0.70}
               h={280}
               data={data}
               dataKey="date"
               series={[{ name:  "patients" , color:  "orange"  }]}
               curveType="bump"
               tickLine="none"
               gridAxis="none"
               withXAxis={false}
               withYAxis={false}
               withDots={false}
             />
    </div>
  )
}

export default PatientMetrics;
