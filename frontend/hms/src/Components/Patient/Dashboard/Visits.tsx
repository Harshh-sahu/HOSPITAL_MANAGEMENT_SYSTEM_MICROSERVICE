import { AreaChart } from '@mantine/charts'
import React, { useEffect } from 'react'
import { useSelector } from 'react-redux';
import { countAppointmentByPatient } from '../../../Service/AppointmentService';
import { addZeroMonths } from '../../../Utility/OtherUtility';


const Appointments = () => {
  const [appointments,setAppointments] = React.useState<any[]>([]);
  
  const user= useSelector((state:any)=>state.user);
    useEffect(()=>{
      
      countAppointmentByPatient(user.profileId).then((res)=>{
        setAppointments(addZeroMonths(res,"month","count"));
      }).catch((err)=>{
        console.error("Error fetching appointment metrics:",err);
      });
  
    })
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
           {getSum(appointments,"count")}
         </div>

      </div>
          <AreaChart



                       strokeWidth={4}

             withGradient
             fillOpacity={0.70}
               h={150}
               data={appointments}
               dataKey="month"
               series={[{ name:  "count" , color:  "violet"  }]}
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
