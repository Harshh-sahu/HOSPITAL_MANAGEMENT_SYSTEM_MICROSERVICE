import { AreaChart } from '@mantine/charts'
import React from 'react'


const Appointments = () => {
  const data = [
    {date: 'January', Visits: 30},
    {date: 'February', Visits: 45},
    {date: 'March', Visits: 28},
    {date: 'April', Visits: 60},
    {date: 'May', Visits: 50},
    {date: 'June', Visits: 75},
    {date: 'July', Visits: 65},
    {date: 'August', Visits: 80},
    {date: 'September', Visits: 70},
    {date: 'October', Visits: 90},
    {date: 'November', Visits: 85},
    {date: 'December', Visits: 100},

  ]
  const getSum = (data:any[], key:string) => {
    return data.reduce((sum, item) => sum + item[key], 0);
  }
  return (
    <div className='bg-violet-50 rounded-xl border '>
      <div className='flex justify-between p-5 items-center'>
         <div>
          <div className='font-semibold'>Visits</div>
          <div className='text-xs text-gray-500'>{new Date().getFullYear()}</div>
         </div>
         <div className='text-2xl font-bold text-violet-500'>
           {getSum(data,"Visits")}
         </div>

      </div>
          <AreaChart



                       strokeWidth={4}

             withGradient
             fillOpacity={0.70}
               h={150}
               data={data}
               dataKey="date"
               series={[{ name:  "Visits" , color:  "violet"  }]}
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

export default Appointments;
